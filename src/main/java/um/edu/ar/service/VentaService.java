package um.edu.ar.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import um.edu.ar.config.Constants;
import um.edu.ar.domain.User;
import um.edu.ar.domain.Venta;
import um.edu.ar.repository.UserRepository;
import um.edu.ar.repository.VentaRepository;
import um.edu.ar.service.dto.VentaDTO;
import um.edu.ar.service.dto.VentaProfeDTO;
import um.edu.ar.service.mapper.UserMapper;
import um.edu.ar.service.mapper.VentaMapper;

/**
 * Service Implementation for managing {@link Venta}.
 */
@Service
@Transactional
public class VentaService {

    private static final Logger LOG = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;
    private final UserRepository userRepository;
    private final VentaMapper ventaMapper;

    private static final RestTemplate restTemplate = new RestTemplate();
    //    private static final String postUrl = "http://192.168.194.254:8080/api/catedra/";
    private static final String postUrl = Constants.API_URL;

    public VentaService(VentaRepository ventaRepository, UserRepository userRepository, UserMapper userMapper, VentaMapper ventaMapper) {
        LOG.info("Initializing VentaService");
        this.ventaRepository = ventaRepository;
        this.userRepository = userRepository;
        this.ventaMapper = ventaMapper;
    }

    /**
     * Save a venta.
     *
     * @param ventaDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaDTO save(VentaDTO ventaDTO) {
        LOG.debug("Request to save Sale : {}", ventaDTO);
        LOG.debug("Converting DTO to entity");
        Venta venta = ventaMapper.toEntity(ventaDTO);
        LOG.debug("Saving sale entity");
        venta = ventaRepository.save(venta);
        LOG.info("Sale successfully saved with ID: {}", venta.getId());
        return ventaMapper.toDto(venta);
    }

    /**
     * Update a venta.
     *
     * @param ventaDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaDTO update(VentaDTO ventaDTO) {
        LOG.debug("Request to update Sale : {}", ventaDTO);
        LOG.debug("Converting DTO to entity for update");
        Venta venta = ventaMapper.toEntity(ventaDTO);
        LOG.debug("Updating sale entity");
        venta = ventaRepository.save(venta);
        LOG.info("Sale successfully updated with ID: {}", venta.getId());
        return ventaMapper.toDto(venta);
    }

    /**
     * Partially update a venta.
     *
     * @param ventaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VentaDTO> partialUpdate(VentaDTO ventaDTO) {
        LOG.debug("Request to partially update Sale : {}", ventaDTO);
        LOG.debug("Looking up existing sale with ID: {}", ventaDTO.getId());

        return ventaRepository
            .findById(ventaDTO.getId())
            .map(existingVenta -> {
                LOG.debug("Found existing sale, applying partial update");
                ventaMapper.partialUpdate(existingVenta, ventaDTO);
                return existingVenta;
            })
            .map(venta -> {
                LOG.debug("Saving partially updated sale");
                return ventaRepository.save(venta);
            })
            .map(venta -> {
                LOG.info("Sale partially updated successfully. ID: {}", venta.getId());
                return ventaMapper.toDto(venta);
            });
    }

    /**
     * Get all the ventas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VentaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Sales with pagination: {}", pageable);
        Page<VentaDTO> result = ventaRepository.findAll(pageable).map(ventaMapper::toDto);
        LOG.info("Retrieved {} sales", result.getTotalElements());
        return result;
    }

    /**
     * Get one venta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VentaDTO> findOne(Long id) {
        LOG.debug("Request to get Sale by ID: {}", id);
        Optional<VentaDTO> result = ventaRepository.findById(id).map(ventaMapper::toDto);
        if (result.isPresent()) {
            LOG.info("Sale found with ID: {}", id);
        } else {
            LOG.warn("Sale not found with ID: {}", id);
        }
        return result;
    }

    /**
     * Delete the venta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Sale with ID: {}", id);
        LOG.debug("Executing deletion");
        ventaRepository.deleteById(id);
        LOG.info("Sale successfully deleted with ID: {}", id);
    }

    public Venta realizarVenta(VentaDTO ventaDTO) {
        LOG.debug("Request to process new Sale: {}", ventaDTO);
        LOG.debug("Loading authentication token");
        String token = loadTokenFromFile();

        LOG.debug("Looking up user with ID: {}", ventaDTO.getUser().getId());
        User user = userRepository
            .findById(ventaDTO.getUser().getId())
            .orElseThrow(() -> {
                LOG.error("User not found with ID: {}", ventaDTO.getUser().getId());
                return new RuntimeException("User not found");
            });

        LOG.debug("Preparing HTTP request to external service");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<VentaDTO> entity = new HttpEntity<>(ventaDTO, headers);

        LOG.debug("Sending sale request to external service");
        ResponseEntity<VentaProfeDTO> response = restTemplate.exchange(postUrl + "/vender", HttpMethod.POST, entity, VentaProfeDTO.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            LOG.debug("External service request successful, creating local sale record");
            Venta venta = new Venta();
            venta.setId(Objects.requireNonNull(response.getBody()).getIdVenta());
            venta.setFechaVenta(ventaDTO.getFechaVenta());
            venta.setGanancia(ventaDTO.getPrecioFinal());
            venta.setUser(user);

            LOG.debug("Saving sale to local database");
            venta = ventaRepository.save(venta);
            LOG.info("Sale successfully processed and saved with ID: {}", venta.getId());
            return venta;
        } else {
            LOG.error("External service request failed with status code: {}", response.getStatusCode());
            throw new RuntimeException("Error during sale request");
        }
    }

    public List<VentaDTO> getVentasByUserId(Long userId) {
        LOG.debug("Request to get all Sales for user with ID: {}", userId);
        List<Venta> ventas = ventaRepository.findByUserId(userId);
        List<VentaDTO> result = ventas.stream().map(ventaMapper::toDto).collect(Collectors.toList());
        LOG.info("Found {} sales for user ID: {}", result.size(), userId);
        return result;
    }

    private String loadTokenFromFile() {
        LOG.debug("Attempting to load authentication token from file");
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("token.json"));
            JSONObject jsonObject = new JSONObject(new String(bytes));
            LOG.debug("Authentication token successfully loaded");
            return jsonObject.getString("token");
        } catch (IOException e) {
            LOG.error("Failed to load authentication token from file", e);
            throw new RuntimeException("Error loading token from file", e);
        }
    }
}
