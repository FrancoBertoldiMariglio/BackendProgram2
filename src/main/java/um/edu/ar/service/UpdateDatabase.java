package edu.um.alumno.service;

import edu.um.alumno.service.dto.DispositivoDTO;
import java.io.FileReader;
import java.io.IOException;
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
import um.edu.ar.service.DispositivoService;
import um.edu.ar.service.dto.DispositivoDTO;
import um.edu.ar.service.dto.MapperDispositivoDTO;

@Service
public class UpdateDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateDatabase.class);

    private static final String DEVICES_URL = "http://192.168.194.254:8080/api/catedra/dispositivos";

    @Autowired
    private DispositivoService dispositivoService;

    @Autowired
    private RestTemplate restTemplate;

    // Cargar token desde el archivo token.json
    private String loadTokenFromFile() {
        try (FileReader reader = new FileReader("token.json")) {
            JSONObject jsonObject = new JSONObject(new String(reader.readAllBytes()));
            return jsonObject.getString("token");
        } catch (IOException e) {
            LOG.error("Error loading token from file", e);
            throw new RuntimeException("Error loading token from file", e);
        }
    }

    // Método que se ejecuta al iniciar la aplicación
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        LOG.info("Initializing UpdateDatabase service");
        syncData();
        startScheduledSync();
    }

    // Sincronizar datos con la API usando el token
    public void syncData() {
        LOG.info("Starting data sync");

        String token = loadTokenFromFile(); // Cargar el token desde el archivo
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Añadir el token como Authorization: Bearer
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Realizar la solicitud GET a la URL de dispositivos
            ResponseEntity<List<DispositivoDTO>> response = restTemplate.exchange(
                DEVICES_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<DispositivoDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<DispositivoDTO> devices = response.getBody();
                LOG.info("Data sync successful, {} devices retrieved", devices.size());
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

    // Iniciar la sincronización programada cada 15 minutos
    private void startScheduledSync() {
        LOG.info("Starting scheduled data sync every 15 minutes");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::syncData, 0, 15, TimeUnit.MINUTES);
    }

    // Actualizar la base de datos local con los dispositivos obtenidos de la API
    private void updateLocalDatabase(List<DispositivoDTO> devices) {
        LOG.info("Updating local database with {} devices", devices.size());

        List<MapperDispositivoDTO> localDevices = dispositivoService.findAllNoPag();
        Map<Long, MapperDispositivoDTO> localDeviceMap = localDevices
            .stream()
            .collect(Collectors.toMap(MapperDispositivoDTO::getId, dispositivo -> dispositivo));

        for (DispositivoDTO remoteDevice : devices) {
            MapperDispositivoDTO localDevice = localDeviceMap.get(remoteDevice.getId());
            if (localDevice == null || !localDevice.equals(remoteDevice)) {
                dispositivoService.save(remoteDevice);
                LOG.info("Device updated: {}", remoteDevice.getId());
            }
        }

        LOG.info("Local database updated successfully");
    }
}
