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
import um.edu.ar.domain.Venta;
import um.edu.ar.repository.VentaRepository;
import um.edu.ar.service.VentaService;
import um.edu.ar.service.dto.VentaDTO;
import um.edu.ar.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link um.edu.ar.domain.Venta}.
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaResource {

    private static final Logger LOG = LoggerFactory.getLogger(VentaResource.class);
    private static final String ENTITY_NAME = "venta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VentaService ventaService;
    private final VentaRepository ventaRepository;

    public VentaResource(VentaService ventaService, VentaRepository ventaRepository) {
        this.ventaService = ventaService;
        this.ventaRepository = ventaRepository;
    }

    /**
     * {@code POST  /ventas} : Create a new venta.
     *
     * @param ventaDTO the ventaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ventaDTO, or with status {@code 400 (Bad Request)} if the venta has already an ID.
     */
    @PostMapping("")
    public ResponseEntity<VentaDTO> createVenta(@Valid @RequestBody VentaDTO ventaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Sale: {}", ventaDTO);
        LOG.debug("Validating that sale has no existing ID");
        if (ventaDTO.getId() != null) {
            LOG.error("Attempt to create sale with existing ID: {}", ventaDTO.getId());
            throw new BadRequestAlertException("A new venta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LOG.debug("Processing sale through service");
        VentaDTO result = ventaService.realizarVenta(ventaDTO);
        LOG.info("Sale successfully created with ID: {}", result.getId());
        return ResponseEntity.created(new URI("/api/ventas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ventas/:id} : Updates an existing venta.
     *
     * @param id the id of the ventaDTO to save.
     * @param ventaDTO the ventaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ventaDTO,
     * or with status {@code 400 (Bad Request)} if the ventaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ventaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VentaDTO> updateVenta(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VentaDTO ventaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Sale. ID: {}, Data: {}", id, ventaDTO);
        LOG.debug("Validating sale ID");
        if (ventaDTO.getId() == null) {
            LOG.error("Attempt to update sale without ID");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ventaDTO.getId())) {
            LOG.error("Path ID ({}) does not match DTO ID ({})", id, ventaDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        LOG.debug("Verifying existence of sale with ID: {}", id);
        if (!ventaRepository.existsById(id)) {
            LOG.error("Sale not found with ID: {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LOG.debug("Updating sale through service");
        ventaDTO = ventaService.update(ventaDTO);
        LOG.info("Sale successfully updated. ID: {}", ventaDTO.getId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ventaDTO.getId().toString()))
            .body(ventaDTO);
    }

    /**
     * {@code PATCH  /ventas/:id} : Partial updates given fields of an existing venta, field will ignore if it is null
     *
     * @param id the id of the ventaDTO to save.
     * @param ventaDTO the ventaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ventaDTO,
     * or with status {@code 400 (Bad Request)} if the ventaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ventaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ventaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VentaDTO> partialUpdateVenta(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VentaDTO ventaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Sale. ID: {}, Data: {}", id, ventaDTO);
        LOG.debug("Validating sale ID");
        if (ventaDTO.getId() == null) {
            LOG.error("Attempt to partial update without ID");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ventaDTO.getId())) {
            LOG.error("Path ID ({}) does not match DTO ID ({})", id, ventaDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        LOG.debug("Verifying existence of sale with ID: {}", id);
        if (!ventaRepository.existsById(id)) {
            LOG.error("Sale not found with ID: {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LOG.debug("Performing partial update of sale");
        Optional<VentaDTO> result = ventaService.partialUpdate(ventaDTO);
        LOG.info("Partial update completed for sale ID: {}", id);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ventaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ventas} : get all the ventas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ventas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VentaDTO>> getAllVentas(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all Sales. Pageable: {}", pageable);
        LOG.debug("Retrieving page of sales");
        Page<VentaDTO> page = ventaService.findAll(pageable);
        LOG.debug("Total elements found: {}", page.getTotalElements());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        LOG.info("Returning {} sales", page.getNumberOfElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ventas/:id} : get the "id" venta.
     *
     * @param id the id of the ventaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ventaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> getVenta(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Sale with ID: {}", id);
        LOG.debug("Looking up sale in service");
        Optional<VentaDTO> ventaDTO = ventaService.findOne(id);
        if (ventaDTO.isPresent()) {
            LOG.info("Sale found with ID: {}", id);
        } else {
            LOG.warn("Sale not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(ventaDTO);
    }

    /**
     * {@code DELETE  /ventas/:id} : delete the "id" venta.
     *
     * @param id the id of the ventaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Sale with ID: {}", id);
        LOG.debug("Starting deletion process");
        ventaService.delete(id);
        LOG.info("Sale successfully deleted. ID: {}", id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /user/:userId/ventas} : get all the ventas by user.
     * @param userId the id of the owner of the sale to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ventas in body
     */
    @GetMapping("/user/{userId}/ventas")
    public ResponseEntity<List<VentaDTO>> getAllVentasByUserId(@PathVariable Long userId) {
        LOG.debug("REST request to get all Sales for user with ID: {}", userId);
        LOG.debug("Retrieving sales for user");
        List<VentaDTO> ventas = ventaService.getVentasByUserId(userId);
        LOG.info("Found {} sales for user ID: {}", ventas.size(), userId);
        return ResponseEntity.ok().body(ventas);
    }
}
