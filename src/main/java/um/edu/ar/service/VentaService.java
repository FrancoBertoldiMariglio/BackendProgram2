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

    private static final String postUrl = "http://192.168.194.254:8080/api/catedra/";

    public VentaService(VentaRepository ventaRepository, UserRepository userRepository, UserMapper userMapper, VentaMapper ventaMapper) {
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
        LOG.debug("Request to save Venta : {}", ventaDTO);
        Venta venta = ventaMapper.toEntity(ventaDTO);
        venta = ventaRepository.save(venta);
        return ventaMapper.toDto(venta);
    }

    /**
     * Update a venta.
     *
     * @param ventaDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaDTO update(VentaDTO ventaDTO) {
        LOG.debug("Request to update Venta : {}", ventaDTO);
        Venta venta = ventaMapper.toEntity(ventaDTO);
        venta = ventaRepository.save(venta);
        return ventaMapper.toDto(venta);
    }

    /**
     * Partially update a venta.
     *
     * @param ventaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VentaDTO> partialUpdate(VentaDTO ventaDTO) {
        LOG.debug("Request to partially update Venta : {}", ventaDTO);

        return ventaRepository
            .findById(ventaDTO.getId())
            .map(existingVenta -> {
                ventaMapper.partialUpdate(existingVenta, ventaDTO);

                return existingVenta;
            })
            .map(ventaRepository::save)
            .map(ventaMapper::toDto);
    }

    /**
     * Get all the ventas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VentaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Ventas");
        return ventaRepository.findAll(pageable).map(ventaMapper::toDto);
    }

    /**
     * Get one venta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VentaDTO> findOne(Long id) {
        LOG.debug("Request to get Venta : {}", id);
        return ventaRepository.findById(id).map(ventaMapper::toDto);
    }

    /**
     * Delete the venta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Venta : {}", id);
        ventaRepository.deleteById(id);
    }

    public Venta realizarVenta(VentaDTO ventaDTO) {
        LOG.debug("Request to save Venta : {}", ventaDTO);
        String token = loadTokenFromFile();

        User user = userRepository.findById(ventaDTO.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<VentaDTO> entity = new HttpEntity<>(ventaDTO, headers);

        ResponseEntity<VentaProfeDTO> response = restTemplate.exchange(postUrl + "/vender", HttpMethod.POST, entity, VentaProfeDTO.class);
        Venta venta = new Venta();

        if (response.getStatusCode().is2xxSuccessful()) {
            venta.setId(Objects.requireNonNull(response.getBody()).getIdVenta());
            venta.setFechaVenta(ventaDTO.getFechaVenta());
            venta.setGanancia(ventaDTO.getPrecioFinal());
            venta.setUser(user);
            venta = ventaRepository.save(venta);
        } else {
            LOG.error("Error during venta request, status code: {}", response.getStatusCode());
            throw new RuntimeException("Error during venta request");
        }
        return venta;
    }

    public List<VentaDTO> getVentasByUserId(Long userId) {
        List<Venta> ventas = ventaRepository.findByUserId(userId);
        return ventas.stream().map(ventaMapper::toDto).collect(Collectors.toList());
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
}
