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

    @Autowired
    private DispositivoService dispositivoService;

    private final RestTemplate restTemplate;

    public UpdateDatabase() {
        this.restTemplate = new RestTemplate();
    }

    private String loadTokenFromFile() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("token.json"));
            JSONObject jsonObject = new JSONObject(new String(bytes));
            return jsonObject.getString("token");
        } catch (IOException e) {
            LOG.error("Error loading token from file", e);
            throw new RuntimeException("Error loading token from file", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        LOG.info("Initializing UpdateDatabase service");
        syncData();
        startScheduledSync();
    }

    public void syncData() {
        LOG.info("Starting data sync");

        String token = loadTokenFromFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<DispositivoDTO>> response = restTemplate.exchange(
                DEVICES_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<DispositivoDTO> devices = response.getBody();
                LOG.info("Data sync successful, {} devices retrieved", devices.size());
                LOG.info(devices.toString());
                updateLocalDatabase(devices);
            } else {
                LOG.error("Failed to sync data, status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to sync data");
            }
        } catch (Exception e) {
            LOG.error("Error during data sync", e);
            throw new RuntimeException("Error during data sync", e);
        }
    }

    private void startScheduledSync() {
        LOG.info("Starting scheduled data sync every 15 minutes");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::syncData, 0, 15, TimeUnit.MINUTES);
    }

    void updateLocalDatabase(List<DispositivoDTO> devices) {
        LOG.info("Updating local database with {} devices", devices.size());
        List<DispositivoDTO> localDevices = dispositivoService.findAllNoPag();
        Map<Long, DispositivoDTO> localDeviceMap = localDevices
            .stream()
            .collect(Collectors.toMap(DispositivoDTO::getId, dispositivo -> dispositivo));

        for (DispositivoDTO remoteDevice : devices) {
            DispositivoDTO localDevice = localDeviceMap.get(remoteDevice.getId());
            if (localDevice == null || !localDevice.equals(remoteDevice)) {
                dispositivoService.save(remoteDevice);
                LOG.info("Device updated: {}", remoteDevice.getId());
            }
        }
        LOG.info("Local database updated successfully");
    }
}
