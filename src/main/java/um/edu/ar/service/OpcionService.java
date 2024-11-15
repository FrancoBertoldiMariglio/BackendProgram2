package um.edu.ar.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Opcion;
import um.edu.ar.repository.OpcionRepository;
import um.edu.ar.service.dto.OpcionDTO;
import um.edu.ar.service.mapper.OpcionMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Opcion}.
 */
@Service
@Transactional
public class OpcionService {

    private static final Logger LOG = LoggerFactory.getLogger(OpcionService.class);

    private final OpcionRepository opcionRepository;
    private final OpcionMapper opcionMapper;

    public OpcionService(OpcionRepository opcionRepository, OpcionMapper opcionMapper) {
        this.opcionRepository = opcionRepository;
        this.opcionMapper = opcionMapper;
    }

    /**
     * Save a opcion.
     *
     * @param opcionDTO the entity to save.
     * @return the persisted entity.
     */
    public OpcionDTO save(OpcionDTO opcionDTO) {
        LOG.debug("Request to save Option: {}", opcionDTO);
        LOG.debug("Converting DTO to entity");
        Opcion opcion = opcionMapper.toEntity(opcionDTO);
        LOG.debug("Saving option entity");
        opcion = opcionRepository.save(opcion);
        LOG.info("Successfully saved option with ID: {}", opcion.getId());
        return opcionMapper.toDto(opcion);
    }

    /**
     * Update a opcion.
     *
     * @param opcionDTO the entity to save.
     * @return the persisted entity.
     */
    public OpcionDTO update(OpcionDTO opcionDTO) {
        LOG.debug("Request to update Option: {}", opcionDTO);
        LOG.debug("Converting DTO to entity for update");
        Opcion opcion = opcionMapper.toEntity(opcionDTO);
        LOG.debug("Updating option entity");
        opcion = opcionRepository.save(opcion);
        LOG.info("Successfully updated option with ID: {}", opcion.getId());
        return opcionMapper.toDto(opcion);
    }

    /**
     * Partially update a opcion.
     *
     * @param opcionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OpcionDTO> partialUpdate(OpcionDTO opcionDTO) {
        LOG.debug("Request to partially update Option: {}", opcionDTO);
        LOG.debug("Looking up existing option with ID: {}", opcionDTO.getId());

        return opcionRepository
            .findById(opcionDTO.getId())
            .map(existingOpcion -> {
                LOG.debug("Found existing option, applying partial update");
                opcionMapper.partialUpdate(existingOpcion, opcionDTO);
                return existingOpcion;
            })
            .map(opcion -> {
                LOG.debug("Saving partially updated option");
                return opcionRepository.save(opcion);
            })
            .map(opcion -> {
                LOG.info("Successfully completed partial update of option with ID: {}", opcion.getId());
                return opcionMapper.toDto(opcion);
            });
    }

    /**
     * Get all the opcions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OpcionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Options with pageable: {}", pageable);
        Page<OpcionDTO> result = opcionRepository.findAll(pageable).map(opcionMapper::toDto);
        LOG.info("Retrieved {} options", result.getTotalElements());
        return result;
    }

    /**
     * Get one opcion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OpcionDTO> findOne(Long id) {
        LOG.debug("Request to get Option by ID: {}", id);
        Optional<OpcionDTO> result = opcionRepository.findById(id).map(opcionMapper::toDto);
        if (result.isPresent()) {
            LOG.info("Found option with ID: {}", id);
        } else {
            LOG.warn("Option not found with ID: {}", id);
        }
        return result;
    }

    /**
     * Delete the opcion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Option with ID: {}", id);
        LOG.debug("Executing deletion");
        opcionRepository.deleteById(id);
        LOG.info("Successfully deleted option with ID: {}", id);
    }
}
