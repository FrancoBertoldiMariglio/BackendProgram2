package um.edu.ar.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static um.edu.ar.domain.CaracteristicaAsserts.*;
import static um.edu.ar.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.IntegrationTest;
import um.edu.ar.domain.Caracteristica;
import um.edu.ar.repository.CaracteristicaRepository;
import um.edu.ar.service.dto.CaracteristicaDTO;
import um.edu.ar.service.mapper.CaracteristicaMapper;

/**
 * Integration tests for the {@link CaracteristicaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CaracteristicaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/caracteristicas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    @Autowired
    private CaracteristicaMapper caracteristicaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCaracteristicaMockMvc;

    private Caracteristica caracteristica;

    private Caracteristica insertedCaracteristica;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Caracteristica createEntity() {
        return new Caracteristica().nombre(DEFAULT_NOMBRE).descripcion(DEFAULT_DESCRIPCION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Caracteristica createUpdatedEntity() {
        return new Caracteristica().nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION);
    }

    @BeforeEach
    public void initTest() {
        caracteristica = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCaracteristica != null) {
            caracteristicaRepository.delete(insertedCaracteristica);
            insertedCaracteristica = null;
        }
    }

    @Test
    @Transactional
    void createCaracteristica() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);
        var returnedCaracteristicaDTO = om.readValue(
            restCaracteristicaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(caracteristicaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CaracteristicaDTO.class
        );

        // Validate the Caracteristica in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCaracteristica = caracteristicaMapper.toEntity(returnedCaracteristicaDTO);
        assertCaracteristicaUpdatableFieldsEquals(returnedCaracteristica, getPersistedCaracteristica(returnedCaracteristica));

        insertedCaracteristica = returnedCaracteristica;
    }

    @Test
    @Transactional
    void createCaracteristicaWithExistingId() throws Exception {
        // Create the Caracteristica with an existing ID
        caracteristica.setId(1L);
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCaracteristicaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(caracteristicaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        caracteristica.setNombre(null);

        // Create the Caracteristica, which fails.
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        restCaracteristicaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(caracteristicaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescripcionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        caracteristica.setDescripcion(null);

        // Create the Caracteristica, which fails.
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        restCaracteristicaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(caracteristicaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCaracteristicas() throws Exception {
        // Initialize the database
        insertedCaracteristica = caracteristicaRepository.saveAndFlush(caracteristica);

        // Get all the caracteristicaList
        restCaracteristicaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(caracteristica.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }

    @Test
    @Transactional
    void getCaracteristica() throws Exception {
        // Initialize the database
        insertedCaracteristica = caracteristicaRepository.saveAndFlush(caracteristica);

        // Get the caracteristica
        restCaracteristicaMockMvc
            .perform(get(ENTITY_API_URL_ID, caracteristica.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(caracteristica.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    void getNonExistingCaracteristica() throws Exception {
        // Get the caracteristica
        restCaracteristicaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCaracteristica() throws Exception {
        // Initialize the database
        insertedCaracteristica = caracteristicaRepository.saveAndFlush(caracteristica);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the caracteristica
        Caracteristica updatedCaracteristica = caracteristicaRepository.findById(caracteristica.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCaracteristica are not directly saved in db
        em.detach(updatedCaracteristica);
        updatedCaracteristica.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION);
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(updatedCaracteristica);

        restCaracteristicaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, caracteristicaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(caracteristicaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCaracteristicaToMatchAllProperties(updatedCaracteristica);
    }

    @Test
    @Transactional
    void putNonExistingCaracteristica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        caracteristica.setId(longCount.incrementAndGet());

        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, caracteristicaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(caracteristicaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCaracteristica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        caracteristica.setId(longCount.incrementAndGet());

        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(caracteristicaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCaracteristica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        caracteristica.setId(longCount.incrementAndGet());

        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(caracteristicaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCaracteristicaWithPatch() throws Exception {
        // Initialize the database
        insertedCaracteristica = caracteristicaRepository.saveAndFlush(caracteristica);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the caracteristica using partial update
        Caracteristica partialUpdatedCaracteristica = new Caracteristica();
        partialUpdatedCaracteristica.setId(caracteristica.getId());

        partialUpdatedCaracteristica.descripcion(UPDATED_DESCRIPCION);

        restCaracteristicaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCaracteristica.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCaracteristica))
            )
            .andExpect(status().isOk());

        // Validate the Caracteristica in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCaracteristicaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCaracteristica, caracteristica),
            getPersistedCaracteristica(caracteristica)
        );
    }

    @Test
    @Transactional
    void fullUpdateCaracteristicaWithPatch() throws Exception {
        // Initialize the database
        insertedCaracteristica = caracteristicaRepository.saveAndFlush(caracteristica);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the caracteristica using partial update
        Caracteristica partialUpdatedCaracteristica = new Caracteristica();
        partialUpdatedCaracteristica.setId(caracteristica.getId());

        partialUpdatedCaracteristica.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION);

        restCaracteristicaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCaracteristica.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCaracteristica))
            )
            .andExpect(status().isOk());

        // Validate the Caracteristica in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCaracteristicaUpdatableFieldsEquals(partialUpdatedCaracteristica, getPersistedCaracteristica(partialUpdatedCaracteristica));
    }

    @Test
    @Transactional
    void patchNonExistingCaracteristica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        caracteristica.setId(longCount.incrementAndGet());

        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, caracteristicaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(caracteristicaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCaracteristica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        caracteristica.setId(longCount.incrementAndGet());

        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(caracteristicaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCaracteristica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        caracteristica.setId(longCount.incrementAndGet());

        // Create the Caracteristica
        CaracteristicaDTO caracteristicaDTO = caracteristicaMapper.toDto(caracteristica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(caracteristicaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Caracteristica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCaracteristica() throws Exception {
        // Initialize the database
        insertedCaracteristica = caracteristicaRepository.saveAndFlush(caracteristica);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the caracteristica
        restCaracteristicaMockMvc
            .perform(delete(ENTITY_API_URL_ID, caracteristica.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return caracteristicaRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Caracteristica getPersistedCaracteristica(Caracteristica caracteristica) {
        return caracteristicaRepository.findById(caracteristica.getId()).orElseThrow();
    }

    protected void assertPersistedCaracteristicaToMatchAllProperties(Caracteristica expectedCaracteristica) {
        assertCaracteristicaAllPropertiesEquals(expectedCaracteristica, getPersistedCaracteristica(expectedCaracteristica));
    }

    protected void assertPersistedCaracteristicaToMatchUpdatableProperties(Caracteristica expectedCaracteristica) {
        assertCaracteristicaAllUpdatablePropertiesEquals(expectedCaracteristica, getPersistedCaracteristica(expectedCaracteristica));
    }
}
