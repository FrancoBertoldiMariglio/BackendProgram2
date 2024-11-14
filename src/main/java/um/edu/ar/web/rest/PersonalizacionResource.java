package um.edu.ar.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import um.edu.ar.repository.PersonalizacionRepository;
import um.edu.ar.service.PersonalizacionService;
import um.edu.ar.service.dto.PersonalizacionDTO;
import um.edu.ar.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link um.edu.ar.domain.Personalizacion}.
 */
@RestController
@RequestMapping("/api/personalizacions")
public class PersonalizacionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalizacionResource.class);

    private static final String ENTITY_NAME = "personalizacion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonalizacionService personalizacionService;

    private final PersonalizacionRepository personalizacionRepository;

    public PersonalizacionResource(PersonalizacionService personalizacionService, PersonalizacionRepository personalizacionRepository) {
        this.personalizacionService = personalizacionService;
        this.personalizacionRepository = personalizacionRepository;
    }

    /**
     * {@code POST  /personalizacions} : Create a new personalizacion.
     *
     * @param personalizacionDTO the personalizacionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personalizacionDTO, or with status {@code 400 (Bad Request)} if the personalizacion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PersonalizacionDTO> createPersonalizacion(@Valid @RequestBody PersonalizacionDTO personalizacionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to create new Personalizacion: {}", personalizacionDTO);
        LOG.debug("Validating personalization data");
        if (personalizacionDTO.getId() != null) {
            LOG.error("Attempt to create personalization with existing ID: {}", personalizacionDTO.getId());
            throw new BadRequestAlertException("A new personalizacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LOG.debug("Saving new personalization");
        personalizacionDTO = personalizacionService.save(personalizacionDTO);
        LOG.info("Personalization created successfully with ID: {}", personalizacionDTO.getId());
        return ResponseEntity.created(new URI("/api/personalizacions/" + personalizacionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, personalizacionDTO.getId().toString()))
            .body(personalizacionDTO);
    }

    /**
     * {@code PUT  /personalizacions/:id} : Updates an existing personalizacion.
     *
     * @param id the id of the personalizacionDTO to save.
     * @param personalizacionDTO the personalizacionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalizacionDTO,
     * or with status {@code 400 (Bad Request)} if the personalizacionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personalizacionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonalizacionDTO> updatePersonalizacion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PersonalizacionDTO personalizacionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Personalizacion. ID: {}, Data: {}", id, personalizacionDTO);
        LOG.debug("Validating personalization ID");
        if (personalizacionDTO.getId() == null) {
            LOG.error("Attempt to update personalization without ID");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalizacionDTO.getId())) {
            LOG.error("Path ID ({}) does not match DTO ID ({})", id, personalizacionDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        LOG.debug("Checking if personalization exists with ID: {}", id);
        if (!personalizacionRepository.existsById(id)) {
            LOG.error("Personalization not found with ID: {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LOG.debug("Updating personalization through service");
        personalizacionDTO = personalizacionService.update(personalizacionDTO);
        LOG.info("Personalization updated successfully with ID: {}", personalizacionDTO.getId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalizacionDTO.getId().toString()))
            .body(personalizacionDTO);
    }

    /**
     * {@code PATCH  /personalizacions/:id} : Partial updates given fields of an existing personalizacion, field will ignore if it is null
     *
     * @param id the id of the personalizacionDTO to save.
     * @param personalizacionDTO the personalizacionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalizacionDTO,
     * or with status {@code 400 (Bad Request)} if the personalizacionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the personalizacionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the personalizacionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PersonalizacionDTO> partialUpdatePersonalizacion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PersonalizacionDTO personalizacionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Personalizacion. ID: {}, Data: {}", id, personalizacionDTO);
        LOG.debug("Validating personalization data");
        if (personalizacionDTO.getId() == null) {
            LOG.error("Attempt to partially update without ID");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalizacionDTO.getId())) {
            LOG.error("Path ID ({}) does not match DTO ID ({})", id, personalizacionDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        LOG.debug("Checking if personalization exists with ID: {}", id);
        if (!personalizacionRepository.existsById(id)) {
            LOG.error("Personalization not found with ID: {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LOG.debug("Processing partial update");
        Optional<PersonalizacionDTO> result = personalizacionService.partialUpdate(personalizacionDTO);
        LOG.info("Partial update completed for personalization ID: {}", id);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalizacionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /personalizacions} : get all the personalizacions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personalizacions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PersonalizacionDTO>> getAllPersonalizacions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all Personalizations. Pageable: {}", pageable);
        LOG.debug("Retrieving page of personalizations");
        Page<PersonalizacionDTO> page = personalizacionService.findAll(pageable);
        LOG.debug("Total elements found: {}", page.getTotalElements());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        LOG.info("Returning {} personalizations", page.getNumberOfElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /personalizacions/:id} : get the "id" personalizacion.
     *
     * @param id the id of the personalizacionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personalizacionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonalizacionDTO> getPersonalizacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Personalization with ID: {}", id);
        LOG.debug("Looking up personalization in service");
        Optional<PersonalizacionDTO> personalizacionDTO = personalizacionService.findOne(id);
        if (personalizacionDTO.isPresent()) {
            LOG.info("Personalization found with ID: {}", id);
        } else {
            LOG.warn("Personalization not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(personalizacionDTO);
    }

    /**
     * {@code DELETE  /personalizacions/:id} : delete the "id" personalizacion.
     *
     * @param id the id of the personalizacionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalizacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Personalization with ID: {}", id);
        LOG.debug("Starting deletion process");
        personalizacionService.delete(id);
        LOG.info("Personalization successfully deleted with ID: {}", id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
