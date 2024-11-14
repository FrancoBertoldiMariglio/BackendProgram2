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
import um.edu.ar.repository.OpcionRepository;
import um.edu.ar.service.OpcionService;
import um.edu.ar.service.dto.OpcionDTO;
import um.edu.ar.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link um.edu.ar.domain.Opcion}.
 */
@RestController
@RequestMapping("/api/opcions")
public class OpcionResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpcionResource.class);

    private static final String ENTITY_NAME = "opcion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OpcionService opcionService;

    private final OpcionRepository opcionRepository;

    public OpcionResource(OpcionService opcionService, OpcionRepository opcionRepository) {
        this.opcionService = opcionService;
        this.opcionRepository = opcionRepository;
    }

    /**
     * {@code POST  /opcions} : Create a new opcion.
     *
     * @param opcionDTO the opcionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new opcionDTO, or with status {@code 400 (Bad Request)} if the opcion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OpcionDTO> createOpcion(@Valid @RequestBody OpcionDTO opcionDTO) throws URISyntaxException {
        LOG.debug("REST request to create new Option: {}", opcionDTO);
        LOG.debug("Validating option data");
        if (opcionDTO.getId() != null) {
            LOG.error("Attempt to create option with existing ID: {}", opcionDTO.getId());
            throw new BadRequestAlertException("A new option cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LOG.debug("Saving new option");
        opcionDTO = opcionService.save(opcionDTO);
        LOG.info("Option created successfully with ID: {}", opcionDTO.getId());
        return ResponseEntity.created(new URI("/api/opcions/" + opcionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, opcionDTO.getId().toString()))
            .body(opcionDTO);
    }

    /**
     * {@code PUT  /opcions/:id} : Updates an existing opcion.
     *
     * @param id the id of the opcionDTO to save.
     * @param opcionDTO the opcionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated opcionDTO,
     * or with status {@code 400 (Bad Request)} if the opcionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the opcionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OpcionDTO> updateOpcion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OpcionDTO opcionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Option. ID: {}, Data: {}", id, opcionDTO);
        LOG.debug("Validating option ID");
        if (opcionDTO.getId() == null) {
            LOG.error("Attempt to update option without ID");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, opcionDTO.getId())) {
            LOG.error("Path ID ({}) does not match DTO ID ({})", id, opcionDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        LOG.debug("Checking if option exists with ID: {}", id);
        if (!opcionRepository.existsById(id)) {
            LOG.error("Option not found with ID: {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LOG.debug("Updating option through service");
        opcionDTO = opcionService.update(opcionDTO);
        LOG.info("Option updated successfully with ID: {}", opcionDTO.getId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, opcionDTO.getId().toString()))
            .body(opcionDTO);
    }

    /**
     * {@code PATCH  /opcions/:id} : Partial updates given fields of an existing opcion, field will ignore if it is null
     *
     * @param id the id of the opcionDTO to save.
     * @param opcionDTO the opcionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated opcionDTO,
     * or with status {@code 400 (Bad Request)} if the opcionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the opcionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the opcionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OpcionDTO> partialUpdateOpcion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OpcionDTO opcionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Option. ID: {}, Data: {}", id, opcionDTO);
        LOG.debug("Validating option data");
        if (opcionDTO.getId() == null) {
            LOG.error("Attempt to partially update without ID");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, opcionDTO.getId())) {
            LOG.error("Path ID ({}) does not match DTO ID ({})", id, opcionDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        LOG.debug("Checking if option exists with ID: {}", id);
        if (!opcionRepository.existsById(id)) {
            LOG.error("Option not found with ID: {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LOG.debug("Processing partial update");
        Optional<OpcionDTO> result = opcionService.partialUpdate(opcionDTO);
        LOG.info("Partial update completed for option ID: {}", id);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, opcionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /opcions} : get all the opcions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of opcions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OpcionDTO>> getAllOpcions(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all Options. Pageable: {}", pageable);
        LOG.debug("Retrieving page of options");
        Page<OpcionDTO> page = opcionService.findAll(pageable);
        LOG.debug("Total elements found: {}", page.getTotalElements());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        LOG.info("Returning {} options", page.getNumberOfElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /opcions/:id} : get the "id" opcion.
     *
     * @param id the id of the opcionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the opcionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpcionDTO> getOpcion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Option with ID: {}", id);
        LOG.debug("Looking up option in service");
        Optional<OpcionDTO> opcionDTO = opcionService.findOne(id);
        if (opcionDTO.isPresent()) {
            LOG.info("Option found with ID: {}", id);
        } else {
            LOG.warn("Option not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(opcionDTO);
    }

    /**
     * {@code DELETE  /opcions/:id} : delete the "id" opcion.
     *
     * @param id the id of the opcionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpcion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Option with ID: {}", id);
        LOG.debug("Starting deletion process");
        opcionService.delete(id);
        LOG.info("Option successfully deleted with ID: {}", id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
