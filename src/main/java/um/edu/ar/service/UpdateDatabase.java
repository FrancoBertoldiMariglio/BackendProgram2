package um.edu.ar.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.edu.ar.config.Constants;
import um.edu.ar.service.dto.DispositivoDTO;

@Service
public class UpdateDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateDatabase.class);
    //    private static final String DEVICES_URL = "http://192.168.194.254:8080/api/catedra/dispositivos";
    private static final String DEVICES_URL = Constants.API_URL + "/dispositivos";

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
            syncData();
            LOG.info("UpdateDatabase service initialized successfully");
        } catch (Exception e) {
            LOG.error("Failed to initialize UpdateDatabase service: {}", e.getMessage());
            throw new RuntimeException("Service initialization failed", e);
        }
    }

    @Scheduled(fixedRate = 600000)
    public void scheduledSync() {
        try {
            syncData();
        } catch (Exception e) {
            LOG.error("Scheduled sync failed: {}", e.getMessage());
            LOG.debug("Detailed error in scheduled sync: ", e);
        }
    }

    private void syncData() {
        LOG.info("Starting data synchronization process");
        try {
            String token = loadTokenFromFile();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<DispositivoDTO>> response = restTemplate.exchange(
                DEVICES_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<DispositivoDTO> devices = response.getBody();
                LOG.info("Successfully retrieved {} devices from external API", devices.size());
                updateLocalDatabase(devices);
            } else {
                LOG.error("External API request failed with status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to sync data: Invalid response");
            }
        } catch (Exception e) {
            LOG.error("Data synchronization failed: {}", e.getMessage());
            throw new RuntimeException("Error during data sync", e);
        }
    }

    private void updateLocalDatabase(List<DispositivoDTO> devices) {
        LOG.info("Starting local database update with {} devices", devices.size());
        try {
            List<DispositivoDTO> localDevices = dispositivoService.findAllNoPag();
            Map<Long, DispositivoDTO> localDeviceMap = localDevices
                .stream()
                .collect(Collectors.toMap(DispositivoDTO::getId, device -> device));

            int updatedCount = 0;
            int unchangedCount = 0;

            for (DispositivoDTO remoteDevice : devices) {
                DispositivoDTO localDevice = localDeviceMap.get(remoteDevice.getId());
                if (localDevice == null || !localDevice.equals(remoteDevice)) {
                    dispositivoService.save(remoteDevice);
                    updatedCount++;
                } else {
                    unchangedCount++;
                }
            }

            LOG.info("Database update completed - Updated: {}, Unchanged: {}", updatedCount, unchangedCount);
        } catch (Exception e) {
            LOG.error("Failed to update local database: {}", e.getMessage());
            throw new RuntimeException("Database update failed", e);
        }
    }
}
