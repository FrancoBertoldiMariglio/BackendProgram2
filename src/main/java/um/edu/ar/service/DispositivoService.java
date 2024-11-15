package um.edu.ar.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Dispositivo;
import um.edu.ar.repository.DispositivoRepository;
import um.edu.ar.service.dto.DispositivoDTO;
import um.edu.ar.service.mapper.DispositivoMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Dispositivo}.
 */
@Service
@Transactional
public class DispositivoService {

    private static final Logger LOG = LoggerFactory.getLogger(DispositivoService.class);

    private final DispositivoRepository dispositivoRepository;
    private final DispositivoMapper dispositivoMapper;

    public DispositivoService(DispositivoRepository dispositivoRepository, DispositivoMapper dispositivoMapper) {
        LOG.info("Initializing DeviceService");
        this.dispositivoRepository = dispositivoRepository;
        this.dispositivoMapper = dispositivoMapper;
    }

    /**
     * Save a dispositivo.
     *
     * @param dispositivoDTO the entity to save.
     * @return the persisted entity.
     */
    public DispositivoDTO save(DispositivoDTO dispositivoDTO) {
        LOG.debug("Request to save Device: {}", dispositivoDTO);
        LOG.debug("Converting DTO to entity");
        Dispositivo dispositivo = dispositivoMapper.toEntity(dispositivoDTO);
        LOG.debug("Saving device entity");
        dispositivo = dispositivoRepository.save(dispositivo);
        LOG.info("Successfully saved device with ID: {}", dispositivo.getId());
        return dispositivoMapper.toDto(dispositivo);
    }

    /**
     * Update a dispositivo.
     *
     * @param dispositivoDTO the entity to save.
     * @return the persisted entity.
     */
    public DispositivoDTO update(DispositivoDTO dispositivoDTO) {
        LOG.debug("Request to update Device: {}", dispositivoDTO);
        LOG.debug("Converting DTO to entity for update");
        Dispositivo dispositivo = dispositivoMapper.toEntity(dispositivoDTO);
        LOG.debug("Updating device entity");
        dispositivo = dispositivoRepository.save(dispositivo);
        LOG.info("Successfully updated device with ID: {}", dispositivo.getId());
        return dispositivoMapper.toDto(dispositivo);
    }

    /**
     * Partially update a dispositivo.
     *
     * @param dispositivoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DispositivoDTO> partialUpdate(DispositivoDTO dispositivoDTO) {
        LOG.debug("Request to partially update Device: {}", dispositivoDTO);
        LOG.debug("Looking up existing device with ID: {}", dispositivoDTO.getId());

        return dispositivoRepository
            .findById(dispositivoDTO.getId())
            .map(existingDispositivo -> {
                LOG.debug("Found existing device, applying partial update");
                dispositivoMapper.partialUpdate(existingDispositivo, dispositivoDTO);
                return existingDispositivo;
            })
            .map(dispositivo -> {
                LOG.debug("Saving partially updated device");
                return dispositivoRepository.save(dispositivo);
            })
            .map(dispositivo -> {
                LOG.info("Successfully completed partial update of device with ID: {}", dispositivo.getId());
                return dispositivoMapper.toDto(dispositivo);
            });
    }

    /**
     * Get all the dispositivos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DispositivoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Devices with pageable: {}", pageable);
        Page<DispositivoDTO> result = dispositivoRepository.findAll(pageable).map(dispositivoMapper::toDto);
        LOG.info("Retrieved {} devices", result.getTotalElements());
        return result;
    }

    @Transactional(readOnly = true)
    public List<DispositivoDTO> findAllNoPag() {
        LOG.debug("Request to get all Devices without pagination");
        List<DispositivoDTO> devices = dispositivoRepository.findAll().stream().map(dispositivoMapper::toDto).collect(Collectors.toList());
        LOG.info("Retrieved {} devices without pagination", devices.size());
        return devices;
    }

    /**
     * Get all the dispositivos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DispositivoDTO> findAllWithEagerRelationships(Pageable pageable) {
        LOG.debug("Request to get all Devices with eager load of relationships. Pageable: {}", pageable);
        Page<DispositivoDTO> result = dispositivoRepository.findAllWithEagerRelationships(pageable).map(dispositivoMapper::toDto);
        LOG.info("Retrieved {} devices with eager relationships", result.getTotalElements());
        return result;
    }

    /**
     * Get one dispositivo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DispositivoDTO> findOne(Long id) {
        LOG.debug("Request to get Device by ID: {}", id);
        Optional<DispositivoDTO> result = dispositivoRepository.findOneWithEagerRelationships(id).map(dispositivoMapper::toDto);
        if (result.isPresent()) {
            LOG.info("Found device with ID: {}", id);
        } else {
            LOG.warn("Device not found with ID: {}", id);
        }
        return result;
    }

    /**
     * Delete the dispositivo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Device with ID: {}", id);
        LOG.debug("Executing deletion");
        dispositivoRepository.deleteById(id);
        LOG.info("Successfully deleted device with ID: {}", id);
    }
}
