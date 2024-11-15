package um.edu.ar.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Caracteristica;
import um.edu.ar.repository.CaracteristicaRepository;
import um.edu.ar.service.dto.CaracteristicaDTO;
import um.edu.ar.service.mapper.CaracteristicaMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Caracteristica}.
 */
@Service
@Transactional
public class CaracteristicaService {

    private static final Logger LOG = LoggerFactory.getLogger(CaracteristicaService.class);

    private final CaracteristicaRepository caracteristicaRepository;
    private final CaracteristicaMapper caracteristicaMapper;

    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository, CaracteristicaMapper caracteristicaMapper) {
        LOG.info("Initializing CharacteristicService");
        this.caracteristicaRepository = caracteristicaRepository;
        this.caracteristicaMapper = caracteristicaMapper;
    }

    /**
     * Save a caracteristica.
     *
     * @param caracteristicaDTO the entity to save.
     * @return the persisted entity.
     */
    public CaracteristicaDTO save(CaracteristicaDTO caracteristicaDTO) {
        LOG.debug("Request to save Characteristic: {}", caracteristicaDTO);
        LOG.debug("Converting DTO to entity");
        Caracteristica caracteristica = caracteristicaMapper.toEntity(caracteristicaDTO);
        LOG.debug("Saving characteristic entity");
        caracteristica = caracteristicaRepository.save(caracteristica);
        LOG.info("Successfully saved characteristic with ID: {}", caracteristica.getId());
        return caracteristicaMapper.toDto(caracteristica);
    }

    /**
     * Update a caracteristica.
     *
     * @param caracteristicaDTO the entity to save.
     * @return the persisted entity.
     */
    public CaracteristicaDTO update(CaracteristicaDTO caracteristicaDTO) {
        LOG.debug("Request to update Characteristic: {}", caracteristicaDTO);
        LOG.debug("Converting DTO to entity for update");
        Caracteristica caracteristica = caracteristicaMapper.toEntity(caracteristicaDTO);
        LOG.debug("Updating characteristic entity");
        caracteristica = caracteristicaRepository.save(caracteristica);
        LOG.info("Successfully updated characteristic with ID: {}", caracteristica.getId());
        return caracteristicaMapper.toDto(caracteristica);
    }

    /**
     * Partially update a caracteristica.
     *
     * @param caracteristicaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CaracteristicaDTO> partialUpdate(CaracteristicaDTO caracteristicaDTO) {
        LOG.debug("Request to partially update Characteristic: {}", caracteristicaDTO);
        LOG.debug("Looking up existing characteristic with ID: {}", caracteristicaDTO.getId());

        return caracteristicaRepository
            .findById(caracteristicaDTO.getId())
            .map(existingCaracteristica -> {
                LOG.debug("Found existing characteristic, applying partial update");
                caracteristicaMapper.partialUpdate(existingCaracteristica, caracteristicaDTO);
                return existingCaracteristica;
            })
            .map(caracteristica -> {
                LOG.debug("Saving partially updated characteristic");
                return caracteristicaRepository.save(caracteristica);
            })
            .map(caracteristica -> {
                LOG.info("Successfully completed partial update of characteristic with ID: {}", caracteristica.getId());
                return caracteristicaMapper.toDto(caracteristica);
            });
    }

    /**
     * Get all the caracteristicas.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CaracteristicaDTO> findAll() {
        LOG.debug("Request to get all Characteristics");
        List<CaracteristicaDTO> results = caracteristicaRepository
            .findAll()
            .stream()
            .map(caracteristicaMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
        LOG.info("Retrieved {} characteristics", results.size());
        return results;
    }

    /**
     * Get one caracteristica by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CaracteristicaDTO> findOne(Long id) {
        LOG.debug("Request to get Characteristic by ID: {}", id);
        Optional<CaracteristicaDTO> result = caracteristicaRepository.findById(id).map(caracteristicaMapper::toDto);
        if (result.isPresent()) {
            LOG.info("Found characteristic with ID: {}", id);
        } else {
            LOG.warn("Characteristic not found with ID: {}", id);
        }
        return result;
    }

    /**
     * Delete the caracteristica by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Characteristic with ID: {}", id);
        LOG.debug("Executing deletion");
        caracteristicaRepository.deleteById(id);
        LOG.info("Successfully deleted characteristic with ID: {}", id);
    }
}
