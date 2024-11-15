package um.edu.ar.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Adicional;
import um.edu.ar.repository.AdicionalRepository;
import um.edu.ar.service.dto.AdicionalDTO;
import um.edu.ar.service.mapper.AdicionalMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Adicional}.
 */
@Service
@Transactional
public class AdicionalService {

    private static final Logger LOG = LoggerFactory.getLogger(AdicionalService.class);

    private final AdicionalRepository adicionalRepository;
    private final AdicionalMapper adicionalMapper;

    public AdicionalService(AdicionalRepository adicionalRepository, AdicionalMapper adicionalMapper) {
        LOG.info("Initializing AdditionalService");
        this.adicionalRepository = adicionalRepository;
        this.adicionalMapper = adicionalMapper;
    }

    /**
     * Save a adicional.
     *
     * @param adicionalDTO the entity to save.
     * @return the persisted entity.
     */
    public AdicionalDTO save(AdicionalDTO adicionalDTO) {
        LOG.debug("Request to save Additional: {}", adicionalDTO);
        LOG.debug("Converting DTO to entity");
        Adicional adicional = adicionalMapper.toEntity(adicionalDTO);
        LOG.debug("Saving additional entity");
        adicional = adicionalRepository.save(adicional);
        LOG.info("Successfully saved additional with ID: {}", adicional.getId());
        return adicionalMapper.toDto(adicional);
    }

    /**
     * Update a adicional.
     *
     * @param adicionalDTO the entity to save.
     * @return the persisted entity.
     */
    public AdicionalDTO update(AdicionalDTO adicionalDTO) {
        LOG.debug("Request to update Additional: {}", adicionalDTO);
        LOG.debug("Converting DTO to entity for update");
        Adicional adicional = adicionalMapper.toEntity(adicionalDTO);
        LOG.debug("Updating additional entity");
        adicional = adicionalRepository.save(adicional);
        LOG.info("Successfully updated additional with ID: {}", adicional.getId());
        return adicionalMapper.toDto(adicional);
    }

    /**
     * Partially update a adicional.
     *
     * @param adicionalDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AdicionalDTO> partialUpdate(AdicionalDTO adicionalDTO) {
        LOG.debug("Request to partially update Additional: {}", adicionalDTO);
        LOG.debug("Looking up existing additional with ID: {}", adicionalDTO.getId());

        return adicionalRepository
            .findById(adicionalDTO.getId())
            .map(existingAdicional -> {
                LOG.debug("Found existing additional, applying partial update");
                adicionalMapper.partialUpdate(existingAdicional, adicionalDTO);
                return existingAdicional;
            })
            .map(adicional -> {
                LOG.debug("Saving partially updated additional");
                return adicionalRepository.save(adicional);
            })
            .map(adicional -> {
                LOG.info("Successfully completed partial update of additional with ID: {}", adicional.getId());
                return adicionalMapper.toDto(adicional);
            });
    }

    /**
     * Get all the adicionals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AdicionalDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Additionals with pageable: {}", pageable);
        Page<AdicionalDTO> result = adicionalRepository.findAll(pageable).map(adicionalMapper::toDto);
        LOG.info("Retrieved {} additionals", result.getTotalElements());
        return result;
    }

    /**
     * Get one adicional by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AdicionalDTO> findOne(Long id) {
        LOG.debug("Request to get Additional by ID: {}", id);
        Optional<AdicionalDTO> result = adicionalRepository.findById(id).map(adicionalMapper::toDto);
        if (result.isPresent()) {
            LOG.info("Found additional with ID: {}", id);
        } else {
            LOG.warn("Additional not found with ID: {}", id);
        }
        return result;
    }

    /**
     * Delete the adicional by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Additional with ID: {}", id);
        LOG.debug("Executing deletion");
        adicionalRepository.deleteById(id);
        LOG.info("Successfully deleted additional with ID: {}", id);
    }
}
