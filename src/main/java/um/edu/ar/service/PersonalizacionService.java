package um.edu.ar.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Personalizacion;
import um.edu.ar.repository.PersonalizacionRepository;
import um.edu.ar.service.dto.PersonalizacionDTO;
import um.edu.ar.service.mapper.PersonalizacionMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Personalizacion}.
 */
@Service
@Transactional
public class PersonalizacionService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalizacionService.class);

    private final PersonalizacionRepository personalizacionRepository;
    private final PersonalizacionMapper personalizacionMapper;

    public PersonalizacionService(PersonalizacionRepository personalizacionRepository, PersonalizacionMapper personalizacionMapper) {
        this.personalizacionRepository = personalizacionRepository;
        this.personalizacionMapper = personalizacionMapper;
    }

    /**
     * Save a personalizacion.
     *
     * @param personalizacionDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonalizacionDTO save(PersonalizacionDTO personalizacionDTO) {
        LOG.debug("Request to save Personalization: {}", personalizacionDTO);
        LOG.debug("Converting DTO to entity");
        Personalizacion personalizacion = personalizacionMapper.toEntity(personalizacionDTO);
        LOG.debug("Saving personalization entity");
        personalizacion = personalizacionRepository.save(personalizacion);
        LOG.info("Successfully saved personalization with ID: {}", personalizacion.getId());
        return personalizacionMapper.toDto(personalizacion);
    }

    /**
     * Update a personalizacion.
     *
     * @param personalizacionDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonalizacionDTO update(PersonalizacionDTO personalizacionDTO) {
        LOG.debug("Request to update Personalization: {}", personalizacionDTO);
        LOG.debug("Converting DTO to entity for update");
        Personalizacion personalizacion = personalizacionMapper.toEntity(personalizacionDTO);
        LOG.debug("Updating personalization entity");
        personalizacion = personalizacionRepository.save(personalizacion);
        LOG.info("Successfully updated personalization with ID: {}", personalizacion.getId());
        return personalizacionMapper.toDto(personalizacion);
    }

    /**
     * Partially update a personalizacion.
     *
     * @param personalizacionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PersonalizacionDTO> partialUpdate(PersonalizacionDTO personalizacionDTO) {
        LOG.debug("Request to partially update Personalization: {}", personalizacionDTO);
        LOG.debug("Looking up existing personalization with ID: {}", personalizacionDTO.getId());

        return personalizacionRepository
            .findById(personalizacionDTO.getId())
            .map(existingPersonalizacion -> {
                LOG.debug("Found existing personalization, applying partial update");
                personalizacionMapper.partialUpdate(existingPersonalizacion, personalizacionDTO);
                return existingPersonalizacion;
            })
            .map(personalizacion -> {
                LOG.debug("Saving partially updated personalization");
                return personalizacionRepository.save(personalizacion);
            })
            .map(personalizacion -> {
                LOG.info("Successfully completed partial update of personalization with ID: {}", personalizacion.getId());
                return personalizacionMapper.toDto(personalizacion);
            });
    }

    /**
     * Get all the personalizacions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PersonalizacionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Personalizations with pageable: {}", pageable);
        Page<PersonalizacionDTO> result = personalizacionRepository.findAll(pageable).map(personalizacionMapper::toDto);
        LOG.info("Retrieved {} personalizations", result.getTotalElements());
        return result;
    }

    /**
     * Get one personalizacion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PersonalizacionDTO> findOne(Long id) {
        LOG.debug("Request to get Personalization by ID: {}", id);
        Optional<PersonalizacionDTO> result = personalizacionRepository.findById(id).map(personalizacionMapper::toDto);
        if (result.isPresent()) {
            LOG.info("Found personalization with ID: {}", id);
        } else {
            LOG.warn("Personalization not found with ID: {}", id);
        }
        return result;
    }

    /**
     * Delete the personalizacion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Personalization with ID: {}", id);
        LOG.debug("Executing deletion");
        personalizacionRepository.deleteById(id);
        LOG.info("Successfully deleted personalization with ID: {}", id);
    }
}
