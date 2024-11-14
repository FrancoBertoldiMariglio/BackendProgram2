package um.edu.ar.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.edu.ar.service.dto.DispositivoDTO;

@Service
public class UpdateDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateDatabase.class);
    private static final String DEVICES_URL = "http://192.168.194.254:8080/api/catedra/dispositivos";
    private ScheduledExecutorService scheduler;

    @Autowired
    private DispositivoService dispositivoService;

    private final RestTemplate restTemplate;

    public UpdateDatabase() {
        this.restTemplate = new RestTemplate();
    }

    private String loadTokenFromFile() {
        LOG.debug("Attempting to load authentication token from file");
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("token.json"));
            JSONObject jsonObject = new JSONObject(new String(bytes));
            LOG.debug("Token loaded successfully");
            return jsonObject.getString("token");
        } catch (IOException e) {
            LOG.error("Failed to load authentication token from file: {}", e.getMessage());
            throw new RuntimeException("Error loading token from file", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        LOG.info("Initializing UpdateDatabase service");
        try {
            LOG.debug("Performing initial data synchronization");
            syncData();
            LOG.debug("Starting scheduled synchronization");
            startScheduledSync();
            LOG.info("UpdateDatabase service initialized successfully");
        } catch (Exception e) {
            LOG.error("Failed to initialize UpdateDatabase service: {}", e.getMessage());
            throw new RuntimeException("Service initialization failed", e);
        }
    }

    public void syncData() {
        LOG.info("Starting data synchronization process");

        try {
            LOG.debug("Loading authentication token");
            String token = loadTokenFromFile();

            LOG.debug("Preparing HTTP request headers");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            LOG.debug("Sending request to external API: {}", DEVICES_URL);
            ResponseEntity<List<DispositivoDTO>> response = restTemplate.exchange(
                DEVICES_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<DispositivoDTO> devices = response.getBody();
                LOG.info("Successfully retrieved {} devices from external API", devices.size());
                LOG.debug("Retrieved devices: {}", devices);
                updateLocalDatabase(devices);
            } else {
                LOG.error("External API request failed with status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to sync data: Invalid response");
            }
        } catch (Exception e) {
            LOG.error("Data synchronization failed: {}", e.getMessage());
            LOG.debug("Detailed error: ", e);
            throw new RuntimeException("Error during data sync", e);
        }
    }

    private void startScheduledSync() {
        LOG.info("Initializing scheduled synchronization task");
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                LOG.warn("Existing scheduler detected. Shutting down previous scheduler.");
                stopScheduledSync();
            }

            LOG.debug("Creating new scheduled executor");
            scheduler = Executors.newScheduledThreadPool(1);

            LOG.debug("Scheduling synchronization task (15-minute interval)");
            scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        syncData();
                    } catch (Exception e) {
                        LOG.error("Scheduled sync failed: {}", e.getMessage());
                        LOG.debug("Detailed error in scheduled sync: ", e);
                    }
                },
                0,
                15,
                TimeUnit.MINUTES
            );

            LOG.info("Scheduled synchronization task initialized successfully");
        } catch (Exception e) {
            LOG.error("Failed to start scheduled synchronization: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize scheduler", e);
        }
    }

    public void stopScheduledSync() {
        if (scheduler != null && !scheduler.isShutdown()) {
            LOG.info("Stopping scheduled synchronization");
            try {
                scheduler.shutdown();
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    LOG.warn("Scheduler did not terminate in time. Forcing shutdown.");
                    scheduler.shutdownNow();
                }
                LOG.info("Scheduled synchronization stopped successfully");
            } catch (InterruptedException e) {
                LOG.error("Error while shutting down scheduler: {}", e.getMessage());
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    void updateLocalDatabase(List<DispositivoDTO> devices) {
        LOG.info("Starting local database update with {} devices", devices.size());

        try {
            LOG.debug("Retrieving current devices from local database");
            List<DispositivoDTO> localDevices = dispositivoService.findAllNoPag();
            LOG.debug("Found {} devices in local database", localDevices.size());

            Map<Long, DispositivoDTO> localDeviceMap = localDevices
                .stream()
                .collect(Collectors.toMap(DispositivoDTO::getId, device -> device));

            int updatedCount = 0;
            int unchangedCount = 0;

            LOG.debug("Processing devices for update");
            for (DispositivoDTO remoteDevice : devices) {
                DispositivoDTO localDevice = localDeviceMap.get(remoteDevice.getId());
                if (localDevice == null) {
                    LOG.debug("New device found with ID: {}", remoteDevice.getId());
                    dispositivoService.save(remoteDevice);
                    updatedCount++;
                } else if (!localDevice.equals(remoteDevice)) {
                    LOG.debug("Updating existing device with ID: {}", remoteDevice.getId());
                    dispositivoService.save(remoteDevice);
                    updatedCount++;
                } else {
                    LOG.debug("Device unchanged with ID: {}", remoteDevice.getId());
                    unchangedCount++;
                }
            }

            LOG.info("Database update completed - Updated: {}, Unchanged: {}", updatedCount, unchangedCount);
        } catch (Exception e) {
            LOG.error("Failed to update local database: {}", e.getMessage());
            LOG.debug("Detailed error in database update: ", e);
            throw new RuntimeException("Database update failed", e);
        }
    }
}
