package org.mgoulene.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mgoulene.IntegrationTest;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.repository.StockMarketDataRepository;
import org.mgoulene.repository.search.StockMarketDataSearchRepository;
import org.mgoulene.service.criteria.StockMarketDataCriteria;
import org.mgoulene.service.dto.StockMarketDataDTO;
import org.mgoulene.service.mapper.StockMarketDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockMarketDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockMarketDataResourceIT {

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_DATE = LocalDate.ofEpochDay(-1L);

    private static final Float DEFAULT_CLOSE_VALUE = 0F;
    private static final Float UPDATED_CLOSE_VALUE = 1F;
    private static final Float SMALLER_CLOSE_VALUE = 0F - 1F;

    private static final String ENTITY_API_URL = "/api/stock-market-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stock-market-data";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockMarketDataRepository stockMarketDataRepository;

    @Autowired
    private StockMarketDataMapper stockMarketDataMapper;

    @Autowired
    private StockMarketDataSearchRepository stockMarketDataSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMarketDataMockMvc;

    private StockMarketData stockMarketData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMarketData createEntity(EntityManager em) {
        StockMarketData stockMarketData = new StockMarketData()
            .symbol(DEFAULT_SYMBOL)
            .dataDate(DEFAULT_DATA_DATE)
            .closeValue(DEFAULT_CLOSE_VALUE);
        return stockMarketData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMarketData createUpdatedEntity(EntityManager em) {
        StockMarketData stockMarketData = new StockMarketData()
            .symbol(UPDATED_SYMBOL)
            .dataDate(UPDATED_DATA_DATE)
            .closeValue(UPDATED_CLOSE_VALUE);
        return stockMarketData;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        stockMarketDataSearchRepository.deleteAll();
        assertThat(stockMarketDataSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        stockMarketData = createEntity(em);
    }

    @Test
    @Transactional
    void createStockMarketData() throws Exception {
        int databaseSizeBeforeCreate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);
        restStockMarketDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        StockMarketData testStockMarketData = stockMarketDataList.get(stockMarketDataList.size() - 1);
        assertThat(testStockMarketData.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testStockMarketData.getDataDate()).isEqualTo(DEFAULT_DATA_DATE);
        assertThat(testStockMarketData.getCloseValue()).isEqualTo(DEFAULT_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void createStockMarketDataWithExistingId() throws Exception {
        // Create the StockMarketData with an existing ID
        stockMarketData.setId(1L);
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        int databaseSizeBeforeCreate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMarketDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        // set the field null
        stockMarketData.setSymbol(null);

        // Create the StockMarketData, which fails.
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        restStockMarketDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDataDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        // set the field null
        stockMarketData.setDataDate(null);

        // Create the StockMarketData, which fails.
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        restStockMarketDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCloseValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        // set the field null
        stockMarketData.setCloseValue(null);

        // Create the StockMarketData, which fails.
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        restStockMarketDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStockMarketData() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList
        restStockMarketDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMarketData.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].dataDate").value(hasItem(DEFAULT_DATA_DATE.toString())))
            .andExpect(jsonPath("$.[*].closeValue").value(hasItem(DEFAULT_CLOSE_VALUE.doubleValue())));
    }

    @Test
    @Transactional
    void getStockMarketData() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get the stockMarketData
        restStockMarketDataMockMvc
            .perform(get(ENTITY_API_URL_ID, stockMarketData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockMarketData.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.dataDate").value(DEFAULT_DATA_DATE.toString()))
            .andExpect(jsonPath("$.closeValue").value(DEFAULT_CLOSE_VALUE.doubleValue()));
    }

    @Test
    @Transactional
    void getStockMarketDataByIdFiltering() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        Long id = stockMarketData.getId();

        defaultStockMarketDataShouldBeFound("id.equals=" + id);
        defaultStockMarketDataShouldNotBeFound("id.notEquals=" + id);

        defaultStockMarketDataShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockMarketDataShouldNotBeFound("id.greaterThan=" + id);

        defaultStockMarketDataShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockMarketDataShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockMarketDataBySymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where symbol equals to DEFAULT_SYMBOL
        defaultStockMarketDataShouldBeFound("symbol.equals=" + DEFAULT_SYMBOL);

        // Get all the stockMarketDataList where symbol equals to UPDATED_SYMBOL
        defaultStockMarketDataShouldNotBeFound("symbol.equals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockMarketDataBySymbolIsInShouldWork() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where symbol in DEFAULT_SYMBOL or UPDATED_SYMBOL
        defaultStockMarketDataShouldBeFound("symbol.in=" + DEFAULT_SYMBOL + "," + UPDATED_SYMBOL);

        // Get all the stockMarketDataList where symbol equals to UPDATED_SYMBOL
        defaultStockMarketDataShouldNotBeFound("symbol.in=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockMarketDataBySymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where symbol is not null
        defaultStockMarketDataShouldBeFound("symbol.specified=true");

        // Get all the stockMarketDataList where symbol is null
        defaultStockMarketDataShouldNotBeFound("symbol.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMarketDataBySymbolContainsSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where symbol contains DEFAULT_SYMBOL
        defaultStockMarketDataShouldBeFound("symbol.contains=" + DEFAULT_SYMBOL);

        // Get all the stockMarketDataList where symbol contains UPDATED_SYMBOL
        defaultStockMarketDataShouldNotBeFound("symbol.contains=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockMarketDataBySymbolNotContainsSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where symbol does not contain DEFAULT_SYMBOL
        defaultStockMarketDataShouldNotBeFound("symbol.doesNotContain=" + DEFAULT_SYMBOL);

        // Get all the stockMarketDataList where symbol does not contain UPDATED_SYMBOL
        defaultStockMarketDataShouldBeFound("symbol.doesNotContain=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate equals to DEFAULT_DATA_DATE
        defaultStockMarketDataShouldBeFound("dataDate.equals=" + DEFAULT_DATA_DATE);

        // Get all the stockMarketDataList where dataDate equals to UPDATED_DATA_DATE
        defaultStockMarketDataShouldNotBeFound("dataDate.equals=" + UPDATED_DATA_DATE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsInShouldWork() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate in DEFAULT_DATA_DATE or UPDATED_DATA_DATE
        defaultStockMarketDataShouldBeFound("dataDate.in=" + DEFAULT_DATA_DATE + "," + UPDATED_DATA_DATE);

        // Get all the stockMarketDataList where dataDate equals to UPDATED_DATA_DATE
        defaultStockMarketDataShouldNotBeFound("dataDate.in=" + UPDATED_DATA_DATE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate is not null
        defaultStockMarketDataShouldBeFound("dataDate.specified=true");

        // Get all the stockMarketDataList where dataDate is null
        defaultStockMarketDataShouldNotBeFound("dataDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate is greater than or equal to DEFAULT_DATA_DATE
        defaultStockMarketDataShouldBeFound("dataDate.greaterThanOrEqual=" + DEFAULT_DATA_DATE);

        // Get all the stockMarketDataList where dataDate is greater than or equal to UPDATED_DATA_DATE
        defaultStockMarketDataShouldNotBeFound("dataDate.greaterThanOrEqual=" + UPDATED_DATA_DATE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate is less than or equal to DEFAULT_DATA_DATE
        defaultStockMarketDataShouldBeFound("dataDate.lessThanOrEqual=" + DEFAULT_DATA_DATE);

        // Get all the stockMarketDataList where dataDate is less than or equal to SMALLER_DATA_DATE
        defaultStockMarketDataShouldNotBeFound("dataDate.lessThanOrEqual=" + SMALLER_DATA_DATE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsLessThanSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate is less than DEFAULT_DATA_DATE
        defaultStockMarketDataShouldNotBeFound("dataDate.lessThan=" + DEFAULT_DATA_DATE);

        // Get all the stockMarketDataList where dataDate is less than UPDATED_DATA_DATE
        defaultStockMarketDataShouldBeFound("dataDate.lessThan=" + UPDATED_DATA_DATE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByDataDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where dataDate is greater than DEFAULT_DATA_DATE
        defaultStockMarketDataShouldNotBeFound("dataDate.greaterThan=" + DEFAULT_DATA_DATE);

        // Get all the stockMarketDataList where dataDate is greater than SMALLER_DATA_DATE
        defaultStockMarketDataShouldBeFound("dataDate.greaterThan=" + SMALLER_DATA_DATE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue equals to DEFAULT_CLOSE_VALUE
        defaultStockMarketDataShouldBeFound("closeValue.equals=" + DEFAULT_CLOSE_VALUE);

        // Get all the stockMarketDataList where closeValue equals to UPDATED_CLOSE_VALUE
        defaultStockMarketDataShouldNotBeFound("closeValue.equals=" + UPDATED_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsInShouldWork() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue in DEFAULT_CLOSE_VALUE or UPDATED_CLOSE_VALUE
        defaultStockMarketDataShouldBeFound("closeValue.in=" + DEFAULT_CLOSE_VALUE + "," + UPDATED_CLOSE_VALUE);

        // Get all the stockMarketDataList where closeValue equals to UPDATED_CLOSE_VALUE
        defaultStockMarketDataShouldNotBeFound("closeValue.in=" + UPDATED_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue is not null
        defaultStockMarketDataShouldBeFound("closeValue.specified=true");

        // Get all the stockMarketDataList where closeValue is null
        defaultStockMarketDataShouldNotBeFound("closeValue.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue is greater than or equal to DEFAULT_CLOSE_VALUE
        defaultStockMarketDataShouldBeFound("closeValue.greaterThanOrEqual=" + DEFAULT_CLOSE_VALUE);

        // Get all the stockMarketDataList where closeValue is greater than or equal to UPDATED_CLOSE_VALUE
        defaultStockMarketDataShouldNotBeFound("closeValue.greaterThanOrEqual=" + UPDATED_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue is less than or equal to DEFAULT_CLOSE_VALUE
        defaultStockMarketDataShouldBeFound("closeValue.lessThanOrEqual=" + DEFAULT_CLOSE_VALUE);

        // Get all the stockMarketDataList where closeValue is less than or equal to SMALLER_CLOSE_VALUE
        defaultStockMarketDataShouldNotBeFound("closeValue.lessThanOrEqual=" + SMALLER_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsLessThanSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue is less than DEFAULT_CLOSE_VALUE
        defaultStockMarketDataShouldNotBeFound("closeValue.lessThan=" + DEFAULT_CLOSE_VALUE);

        // Get all the stockMarketDataList where closeValue is less than UPDATED_CLOSE_VALUE
        defaultStockMarketDataShouldBeFound("closeValue.lessThan=" + UPDATED_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void getAllStockMarketDataByCloseValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        // Get all the stockMarketDataList where closeValue is greater than DEFAULT_CLOSE_VALUE
        defaultStockMarketDataShouldNotBeFound("closeValue.greaterThan=" + DEFAULT_CLOSE_VALUE);

        // Get all the stockMarketDataList where closeValue is greater than SMALLER_CLOSE_VALUE
        defaultStockMarketDataShouldBeFound("closeValue.greaterThan=" + SMALLER_CLOSE_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockMarketDataShouldBeFound(String filter) throws Exception {
        restStockMarketDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMarketData.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].dataDate").value(hasItem(DEFAULT_DATA_DATE.toString())))
            .andExpect(jsonPath("$.[*].closeValue").value(hasItem(DEFAULT_CLOSE_VALUE.doubleValue())));

        // Check, that the count call also returns 1
        restStockMarketDataMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockMarketDataShouldNotBeFound(String filter) throws Exception {
        restStockMarketDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockMarketDataMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockMarketData() throws Exception {
        // Get the stockMarketData
        restStockMarketDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockMarketData() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        stockMarketDataSearchRepository.save(stockMarketData);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());

        // Update the stockMarketData
        StockMarketData updatedStockMarketData = stockMarketDataRepository.findById(stockMarketData.getId()).get();
        // Disconnect from session so that the updates on updatedStockMarketData are not directly saved in db
        em.detach(updatedStockMarketData);
        updatedStockMarketData.symbol(UPDATED_SYMBOL).dataDate(UPDATED_DATA_DATE).closeValue(UPDATED_CLOSE_VALUE);
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(updatedStockMarketData);

        restStockMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMarketDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        StockMarketData testStockMarketData = stockMarketDataList.get(stockMarketDataList.size() - 1);
        assertThat(testStockMarketData.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testStockMarketData.getDataDate()).isEqualTo(UPDATED_DATA_DATE);
        assertThat(testStockMarketData.getCloseValue()).isEqualTo(UPDATED_CLOSE_VALUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<StockMarketData> stockMarketDataSearchList = IterableUtils.toList(stockMarketDataSearchRepository.findAll());
                StockMarketData testStockMarketDataSearch = stockMarketDataSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testStockMarketDataSearch.getSymbol()).isEqualTo(UPDATED_SYMBOL);
                assertThat(testStockMarketDataSearch.getDataDate()).isEqualTo(UPDATED_DATA_DATE);
                assertThat(testStockMarketDataSearch.getCloseValue()).isEqualTo(UPDATED_CLOSE_VALUE);
            });
    }

    @Test
    @Transactional
    void putNonExistingStockMarketData() throws Exception {
        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        stockMarketData.setId(count.incrementAndGet());

        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMarketDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockMarketData() throws Exception {
        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        stockMarketData.setId(count.incrementAndGet());

        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockMarketData() throws Exception {
        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        stockMarketData.setId(count.incrementAndGet());

        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStockMarketDataWithPatch() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();

        // Update the stockMarketData using partial update
        StockMarketData partialUpdatedStockMarketData = new StockMarketData();
        partialUpdatedStockMarketData.setId(stockMarketData.getId());

        partialUpdatedStockMarketData.dataDate(UPDATED_DATA_DATE);

        restStockMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMarketData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockMarketData))
            )
            .andExpect(status().isOk());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        StockMarketData testStockMarketData = stockMarketDataList.get(stockMarketDataList.size() - 1);
        assertThat(testStockMarketData.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testStockMarketData.getDataDate()).isEqualTo(UPDATED_DATA_DATE);
        assertThat(testStockMarketData.getCloseValue()).isEqualTo(DEFAULT_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateStockMarketDataWithPatch() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);

        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();

        // Update the stockMarketData using partial update
        StockMarketData partialUpdatedStockMarketData = new StockMarketData();
        partialUpdatedStockMarketData.setId(stockMarketData.getId());

        partialUpdatedStockMarketData.symbol(UPDATED_SYMBOL).dataDate(UPDATED_DATA_DATE).closeValue(UPDATED_CLOSE_VALUE);

        restStockMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMarketData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockMarketData))
            )
            .andExpect(status().isOk());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        StockMarketData testStockMarketData = stockMarketDataList.get(stockMarketDataList.size() - 1);
        assertThat(testStockMarketData.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testStockMarketData.getDataDate()).isEqualTo(UPDATED_DATA_DATE);
        assertThat(testStockMarketData.getCloseValue()).isEqualTo(UPDATED_CLOSE_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingStockMarketData() throws Exception {
        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        stockMarketData.setId(count.incrementAndGet());

        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockMarketDataDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockMarketData() throws Exception {
        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        stockMarketData.setId(count.incrementAndGet());

        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockMarketData() throws Exception {
        int databaseSizeBeforeUpdate = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        stockMarketData.setId(count.incrementAndGet());

        // Create the StockMarketData
        StockMarketDataDTO stockMarketDataDTO = stockMarketDataMapper.toDto(stockMarketData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockMarketDataDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMarketData in the database
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStockMarketData() throws Exception {
        // Initialize the database
        stockMarketDataRepository.saveAndFlush(stockMarketData);
        stockMarketDataRepository.save(stockMarketData);
        stockMarketDataSearchRepository.save(stockMarketData);

        int databaseSizeBeforeDelete = stockMarketDataRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the stockMarketData
        restStockMarketDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockMarketData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockMarketData> stockMarketDataList = stockMarketDataRepository.findAll();
        assertThat(stockMarketDataList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockMarketDataSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStockMarketData() throws Exception {
        // Initialize the database
        stockMarketData = stockMarketDataRepository.saveAndFlush(stockMarketData);
        stockMarketDataSearchRepository.save(stockMarketData);

        // Search the stockMarketData
        restStockMarketDataMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockMarketData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMarketData.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].dataDate").value(hasItem(DEFAULT_DATA_DATE.toString())))
            .andExpect(jsonPath("$.[*].closeValue").value(hasItem(DEFAULT_CLOSE_VALUE.doubleValue())));
    }
}
