package com.udea.laboratorio.web.rest;

import com.udea.laboratorio.LabcarroApp;
import com.udea.laboratorio.domain.Carro;
import com.udea.laboratorio.repository.CarroRepository;
import com.udea.laboratorio.repository.search.CarroSearchRepository;
import com.udea.laboratorio.service.CarroService;
import com.udea.laboratorio.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.udea.laboratorio.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CarroResource} REST controller.
 */
@SpringBootTest(classes = LabcarroApp.class)
public class CarroResourceIT {

    private static final String DEFAULT_FABRICANTE = "AAAAAAAAAA";
    private static final String UPDATED_FABRICANTE = "BBBBBBBBBB";

    private static final String DEFAULT_MODELO = "AAAAAAAAAA";
    private static final String UPDATED_MODELO = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRECIO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECIO = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRECIO = new BigDecimal(1 - 1);

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private CarroService carroService;

    /**
     * This repository is mocked in the com.udea.laboratorio.repository.search test package.
     *
     * @see com.udea.laboratorio.repository.search.CarroSearchRepositoryMockConfiguration
     */
    @Autowired
    private CarroSearchRepository mockCarroSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCarroMockMvc;

    private Carro carro;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CarroResource carroResource = new CarroResource(carroService);
        this.restCarroMockMvc = MockMvcBuilders.standaloneSetup(carroResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carro createEntity(EntityManager em) {
        Carro carro = new Carro()
            .fabricante(DEFAULT_FABRICANTE)
            .modelo(DEFAULT_MODELO)
            .precio(DEFAULT_PRECIO);
        return carro;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carro createUpdatedEntity(EntityManager em) {
        Carro carro = new Carro()
            .fabricante(UPDATED_FABRICANTE)
            .modelo(UPDATED_MODELO)
            .precio(UPDATED_PRECIO);
        return carro;
    }

    @BeforeEach
    public void initTest() {
        carro = createEntity(em);
    }

    @Test
    @Transactional
    public void createCarro() throws Exception {
        int databaseSizeBeforeCreate = carroRepository.findAll().size();

        // Create the Carro
        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isCreated());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeCreate + 1);
        Carro testCarro = carroList.get(carroList.size() - 1);
        assertThat(testCarro.getFabricante()).isEqualTo(DEFAULT_FABRICANTE);
        assertThat(testCarro.getModelo()).isEqualTo(DEFAULT_MODELO);
        assertThat(testCarro.getPrecio()).isEqualTo(DEFAULT_PRECIO);

        // Validate the Carro in Elasticsearch
        verify(mockCarroSearchRepository, times(1)).save(testCarro);
    }

    @Test
    @Transactional
    public void createCarroWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = carroRepository.findAll().size();

        // Create the Carro with an existing ID
        carro.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isBadRequest());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeCreate);

        // Validate the Carro in Elasticsearch
        verify(mockCarroSearchRepository, times(0)).save(carro);
    }


    @Test
    @Transactional
    public void checkFabricanteIsRequired() throws Exception {
        int databaseSizeBeforeTest = carroRepository.findAll().size();
        // set the field null
        carro.setFabricante(null);

        // Create the Carro, which fails.

        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isBadRequest());

        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkModeloIsRequired() throws Exception {
        int databaseSizeBeforeTest = carroRepository.findAll().size();
        // set the field null
        carro.setModelo(null);

        // Create the Carro, which fails.

        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isBadRequest());

        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPrecioIsRequired() throws Exception {
        int databaseSizeBeforeTest = carroRepository.findAll().size();
        // set the field null
        carro.setPrecio(null);

        // Create the Carro, which fails.

        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isBadRequest());

        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCarros() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList
        restCarroMockMvc.perform(get("/api/carros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carro.getId().intValue())))
            .andExpect(jsonPath("$.[*].fabricante").value(hasItem(DEFAULT_FABRICANTE.toString())))
            .andExpect(jsonPath("$.[*].modelo").value(hasItem(DEFAULT_MODELO.toString())))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.intValue())));
    }
    
    @Test
    @Transactional
    public void getCarro() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get the carro
        restCarroMockMvc.perform(get("/api/carros/{id}", carro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(carro.getId().intValue()))
            .andExpect(jsonPath("$.fabricante").value(DEFAULT_FABRICANTE.toString()))
            .andExpect(jsonPath("$.modelo").value(DEFAULT_MODELO.toString()))
            .andExpect(jsonPath("$.precio").value(DEFAULT_PRECIO.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCarro() throws Exception {
        // Get the carro
        restCarroMockMvc.perform(get("/api/carros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCarro() throws Exception {
        // Initialize the database
        carroService.save(carro);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCarroSearchRepository);

        int databaseSizeBeforeUpdate = carroRepository.findAll().size();

        // Update the carro
        Carro updatedCarro = carroRepository.findById(carro.getId()).get();
        // Disconnect from session so that the updates on updatedCarro are not directly saved in db
        em.detach(updatedCarro);
        updatedCarro
            .fabricante(UPDATED_FABRICANTE)
            .modelo(UPDATED_MODELO)
            .precio(UPDATED_PRECIO);

        restCarroMockMvc.perform(put("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCarro)))
            .andExpect(status().isOk());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeUpdate);
        Carro testCarro = carroList.get(carroList.size() - 1);
        assertThat(testCarro.getFabricante()).isEqualTo(UPDATED_FABRICANTE);
        assertThat(testCarro.getModelo()).isEqualTo(UPDATED_MODELO);
        assertThat(testCarro.getPrecio()).isEqualTo(UPDATED_PRECIO);

        // Validate the Carro in Elasticsearch
        verify(mockCarroSearchRepository, times(1)).save(testCarro);
    }

    @Test
    @Transactional
    public void updateNonExistingCarro() throws Exception {
        int databaseSizeBeforeUpdate = carroRepository.findAll().size();

        // Create the Carro

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarroMockMvc.perform(put("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isBadRequest());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Carro in Elasticsearch
        verify(mockCarroSearchRepository, times(0)).save(carro);
    }

    @Test
    @Transactional
    public void deleteCarro() throws Exception {
        // Initialize the database
        carroService.save(carro);

        int databaseSizeBeforeDelete = carroRepository.findAll().size();

        // Delete the carro
        restCarroMockMvc.perform(delete("/api/carros/{id}", carro.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Carro in Elasticsearch
        verify(mockCarroSearchRepository, times(1)).deleteById(carro.getId());
    }

    @Test
    @Transactional
    public void searchCarro() throws Exception {
        // Initialize the database
        carroService.save(carro);
        when(mockCarroSearchRepository.search(queryStringQuery("id:" + carro.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(carro), PageRequest.of(0, 1), 1));
        // Search the carro
        restCarroMockMvc.perform(get("/api/_search/carros?query=id:" + carro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carro.getId().intValue())))
            .andExpect(jsonPath("$.[*].fabricante").value(hasItem(DEFAULT_FABRICANTE)))
            .andExpect(jsonPath("$.[*].modelo").value(hasItem(DEFAULT_MODELO)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Carro.class);
        Carro carro1 = new Carro();
        carro1.setId(1L);
        Carro carro2 = new Carro();
        carro2.setId(carro1.getId());
        assertThat(carro1).isEqualTo(carro2);
        carro2.setId(2L);
        assertThat(carro1).isNotEqualTo(carro2);
        carro1.setId(null);
        assertThat(carro1).isNotEqualTo(carro2);
    }
}
