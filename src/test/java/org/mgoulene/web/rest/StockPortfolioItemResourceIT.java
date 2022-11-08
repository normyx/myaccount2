package org.mgoulene.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mgoulene.IntegrationTest;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.domain.enumeration.Currency;
import org.mgoulene.domain.enumeration.StockType;
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.mgoulene.repository.search.StockPortfolioItemSearchRepository;
import org.mgoulene.service.StockPortfolioItemService;
import org.mgoulene.service.criteria.StockPortfolioItemCriteria;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.mgoulene.service.mapper.StockPortfolioItemMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Integration tests for the {@link StockPortfolioItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockPortfolioItemResourceIT {

    private static final String DEFAULT_STOCK_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_STOCK_SYMBOL = "BBBBBBBBBB";

    private static final Currency DEFAULT_STOCK_CURRENCY = Currency.EUR;
    private static final Currency UPDATED_STOCK_CURRENCY = Currency.USD;

    private static final LocalDate DEFAULT_STOCK_ACQUISITION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_STOCK_ACQUISITION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_STOCK_ACQUISITION_DATE = LocalDate.ofEpochDay(-1L);

    private static final Float DEFAULT_STOCK_SHARES_NUMBER = 0F;
    private static final Float UPDATED_STOCK_SHARES_NUMBER = 1F;
    private static final Float SMALLER_STOCK_SHARES_NUMBER = 0F - 1F;

    private static final Float DEFAULT_STOCK_ACQUISITION_PRICE = 0F;
    private static final Float UPDATED_STOCK_ACQUISITION_PRICE = 1F;
    private static final Float SMALLER_STOCK_ACQUISITION_PRICE = 0F - 1F;

    private static final Float DEFAULT_STOCK_CURRENT_PRICE = 0F;
    private static final Float UPDATED_STOCK_CURRENT_PRICE = 1F;
    private static final Float SMALLER_STOCK_CURRENT_PRICE = 0F - 1F;

    private static final LocalDate DEFAULT_STOCK_CURRENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_STOCK_CURRENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_STOCK_CURRENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final Float DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR = 0F;
    private static final Float UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR = 1F;
    private static final Float SMALLER_STOCK_ACQUISITION_CURRENCY_FACTOR = 0F - 1F;

    private static final Float DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR = 0F;
    private static final Float UPDATED_STOCK_CURRENT_CURRENCY_FACTOR = 1F;
    private static final Float SMALLER_STOCK_CURRENT_CURRENCY_FACTOR = 0F - 1F;

    private static final Float DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE = 0F;
    private static final Float UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE = 1F;
    private static final Float SMALLER_STOCK_PRICE_AT_ACQUISITION_DATE = 0F - 1F;

    private static final StockType DEFAULT_STOCK_TYPE = StockType.STOCK;
    private static final StockType UPDATED_STOCK_TYPE = StockType.CRYPTO;

    private static final Instant DEFAULT_LAST_STOCK_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_STOCK_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_CURRENCY_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_CURRENCY_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stock-portfolio-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stock-portfolio-items";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockPortfolioItemRepository stockPortfolioItemRepository;

    @Mock
    private StockPortfolioItemRepository stockPortfolioItemRepositoryMock;

    @Autowired
    private StockPortfolioItemMapper stockPortfolioItemMapper;

    @Mock
    private StockPortfolioItemService stockPortfolioItemServiceMock;

    @Autowired
    private StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockPortfolioItemMockMvc;

    private StockPortfolioItem stockPortfolioItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockPortfolioItem createEntity(EntityManager em) {
        StockPortfolioItem stockPortfolioItem = new StockPortfolioItem()
            .stockSymbol(DEFAULT_STOCK_SYMBOL)
            .stockCurrency(DEFAULT_STOCK_CURRENCY)
            .stockAcquisitionDate(DEFAULT_STOCK_ACQUISITION_DATE)
            .stockSharesNumber(DEFAULT_STOCK_SHARES_NUMBER)
            .stockAcquisitionPrice(DEFAULT_STOCK_ACQUISITION_PRICE)
            .stockCurrentPrice(DEFAULT_STOCK_CURRENT_PRICE)
            .stockCurrentDate(DEFAULT_STOCK_CURRENT_DATE)
            .stockAcquisitionCurrencyFactor(DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR)
            .stockCurrentCurrencyFactor(DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR)
            .stockPriceAtAcquisitionDate(DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE)
            .stockType(DEFAULT_STOCK_TYPE)
            .lastStockUpdate(DEFAULT_LAST_STOCK_UPDATE)
            .lastCurrencyUpdate(DEFAULT_LAST_CURRENCY_UPDATE);
        return stockPortfolioItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockPortfolioItem createUpdatedEntity(EntityManager em) {
        StockPortfolioItem stockPortfolioItem = new StockPortfolioItem()
            .stockSymbol(UPDATED_STOCK_SYMBOL)
            .stockCurrency(UPDATED_STOCK_CURRENCY)
            .stockAcquisitionDate(UPDATED_STOCK_ACQUISITION_DATE)
            .stockSharesNumber(UPDATED_STOCK_SHARES_NUMBER)
            .stockAcquisitionPrice(UPDATED_STOCK_ACQUISITION_PRICE)
            .stockCurrentPrice(UPDATED_STOCK_CURRENT_PRICE)
            .stockCurrentDate(UPDATED_STOCK_CURRENT_DATE)
            .stockAcquisitionCurrencyFactor(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR)
            .stockCurrentCurrencyFactor(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR)
            .stockPriceAtAcquisitionDate(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE)
            .stockType(UPDATED_STOCK_TYPE)
            .lastStockUpdate(UPDATED_LAST_STOCK_UPDATE)
            .lastCurrencyUpdate(UPDATED_LAST_CURRENCY_UPDATE);
        return stockPortfolioItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        stockPortfolioItemSearchRepository.deleteAll();
        assertThat(stockPortfolioItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        stockPortfolioItem = createEntity(em);
    }

    @Test
    @Transactional
    void createStockPortfolioItem() throws Exception {
        int databaseSizeBeforeCreate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);
        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        StockPortfolioItem testStockPortfolioItem = stockPortfolioItemList.get(stockPortfolioItemList.size() - 1);
        assertThat(testStockPortfolioItem.getStockSymbol()).isEqualTo(DEFAULT_STOCK_SYMBOL);
        assertThat(testStockPortfolioItem.getStockCurrency()).isEqualTo(DEFAULT_STOCK_CURRENCY);
        assertThat(testStockPortfolioItem.getStockAcquisitionDate()).isEqualTo(DEFAULT_STOCK_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockSharesNumber()).isEqualTo(DEFAULT_STOCK_SHARES_NUMBER);
        assertThat(testStockPortfolioItem.getStockAcquisitionPrice()).isEqualTo(DEFAULT_STOCK_ACQUISITION_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentPrice()).isEqualTo(DEFAULT_STOCK_CURRENT_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentDate()).isEqualTo(DEFAULT_STOCK_CURRENT_DATE);
        assertThat(testStockPortfolioItem.getStockAcquisitionCurrencyFactor()).isEqualTo(DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockCurrentCurrencyFactor()).isEqualTo(DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockPriceAtAcquisitionDate()).isEqualTo(DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockType()).isEqualTo(DEFAULT_STOCK_TYPE);
        assertThat(testStockPortfolioItem.getLastStockUpdate()).isEqualTo(DEFAULT_LAST_STOCK_UPDATE);
        assertThat(testStockPortfolioItem.getLastCurrencyUpdate()).isEqualTo(DEFAULT_LAST_CURRENCY_UPDATE);
    }

    @Test
    @Transactional
    void createStockPortfolioItemWithExistingId() throws Exception {
        // Create the StockPortfolioItem with an existing ID
        stockPortfolioItem.setId(1L);
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        int databaseSizeBeforeCreate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockSymbol(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockCurrency(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockAcquisitionDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockAcquisitionDate(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockSharesNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockSharesNumber(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockAcquisitionPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockAcquisitionPrice(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockCurrentPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockCurrentPrice(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockCurrentDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockCurrentDate(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockAcquisitionCurrencyFactorIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockAcquisitionCurrencyFactor(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockCurrentCurrencyFactorIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockCurrentCurrencyFactor(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockPriceAtAcquisitionDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockPriceAtAcquisitionDate(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStockTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        // set the field null
        stockPortfolioItem.setStockType(null);

        // Create the StockPortfolioItem, which fails.
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItems() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockPortfolioItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockSymbol").value(hasItem(DEFAULT_STOCK_SYMBOL)))
            .andExpect(jsonPath("$.[*].stockCurrency").value(hasItem(DEFAULT_STOCK_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].stockAcquisitionDate").value(hasItem(DEFAULT_STOCK_ACQUISITION_DATE.toString())))
            .andExpect(jsonPath("$.[*].stockSharesNumber").value(hasItem(DEFAULT_STOCK_SHARES_NUMBER.doubleValue())))
            .andExpect(jsonPath("$.[*].stockAcquisitionPrice").value(hasItem(DEFAULT_STOCK_ACQUISITION_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockCurrentPrice").value(hasItem(DEFAULT_STOCK_CURRENT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockCurrentDate").value(hasItem(DEFAULT_STOCK_CURRENT_DATE.toString())))
            .andExpect(
                jsonPath("$.[*].stockAcquisitionCurrencyFactor").value(hasItem(DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR.doubleValue()))
            )
            .andExpect(jsonPath("$.[*].stockCurrentCurrencyFactor").value(hasItem(DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR.doubleValue())))
            .andExpect(jsonPath("$.[*].stockPriceAtAcquisitionDate").value(hasItem(DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockType").value(hasItem(DEFAULT_STOCK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastStockUpdate").value(hasItem(DEFAULT_LAST_STOCK_UPDATE.toString())))
            .andExpect(jsonPath("$.[*].lastCurrencyUpdate").value(hasItem(DEFAULT_LAST_CURRENCY_UPDATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockPortfolioItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockPortfolioItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockPortfolioItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockPortfolioItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockPortfolioItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockPortfolioItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockPortfolioItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockPortfolioItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockPortfolioItem() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get the stockPortfolioItem
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_API_URL_ID, stockPortfolioItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockPortfolioItem.getId().intValue()))
            .andExpect(jsonPath("$.stockSymbol").value(DEFAULT_STOCK_SYMBOL))
            .andExpect(jsonPath("$.stockCurrency").value(DEFAULT_STOCK_CURRENCY.toString()))
            .andExpect(jsonPath("$.stockAcquisitionDate").value(DEFAULT_STOCK_ACQUISITION_DATE.toString()))
            .andExpect(jsonPath("$.stockSharesNumber").value(DEFAULT_STOCK_SHARES_NUMBER.doubleValue()))
            .andExpect(jsonPath("$.stockAcquisitionPrice").value(DEFAULT_STOCK_ACQUISITION_PRICE.doubleValue()))
            .andExpect(jsonPath("$.stockCurrentPrice").value(DEFAULT_STOCK_CURRENT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.stockCurrentDate").value(DEFAULT_STOCK_CURRENT_DATE.toString()))
            .andExpect(jsonPath("$.stockAcquisitionCurrencyFactor").value(DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR.doubleValue()))
            .andExpect(jsonPath("$.stockCurrentCurrencyFactor").value(DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR.doubleValue()))
            .andExpect(jsonPath("$.stockPriceAtAcquisitionDate").value(DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE.doubleValue()))
            .andExpect(jsonPath("$.stockType").value(DEFAULT_STOCK_TYPE.toString()))
            .andExpect(jsonPath("$.lastStockUpdate").value(DEFAULT_LAST_STOCK_UPDATE.toString()))
            .andExpect(jsonPath("$.lastCurrencyUpdate").value(DEFAULT_LAST_CURRENCY_UPDATE.toString()));
    }

    @Test
    @Transactional
    void getStockPortfolioItemsByIdFiltering() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        Long id = stockPortfolioItem.getId();

        defaultStockPortfolioItemShouldBeFound("id.equals=" + id);
        defaultStockPortfolioItemShouldNotBeFound("id.notEquals=" + id);

        defaultStockPortfolioItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockPortfolioItemShouldNotBeFound("id.greaterThan=" + id);

        defaultStockPortfolioItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockPortfolioItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSymbol equals to DEFAULT_STOCK_SYMBOL
        defaultStockPortfolioItemShouldBeFound("stockSymbol.equals=" + DEFAULT_STOCK_SYMBOL);

        // Get all the stockPortfolioItemList where stockSymbol equals to UPDATED_STOCK_SYMBOL
        defaultStockPortfolioItemShouldNotBeFound("stockSymbol.equals=" + UPDATED_STOCK_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSymbolIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSymbol in DEFAULT_STOCK_SYMBOL or UPDATED_STOCK_SYMBOL
        defaultStockPortfolioItemShouldBeFound("stockSymbol.in=" + DEFAULT_STOCK_SYMBOL + "," + UPDATED_STOCK_SYMBOL);

        // Get all the stockPortfolioItemList where stockSymbol equals to UPDATED_STOCK_SYMBOL
        defaultStockPortfolioItemShouldNotBeFound("stockSymbol.in=" + UPDATED_STOCK_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSymbol is not null
        defaultStockPortfolioItemShouldBeFound("stockSymbol.specified=true");

        // Get all the stockPortfolioItemList where stockSymbol is null
        defaultStockPortfolioItemShouldNotBeFound("stockSymbol.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSymbolContainsSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSymbol contains DEFAULT_STOCK_SYMBOL
        defaultStockPortfolioItemShouldBeFound("stockSymbol.contains=" + DEFAULT_STOCK_SYMBOL);

        // Get all the stockPortfolioItemList where stockSymbol contains UPDATED_STOCK_SYMBOL
        defaultStockPortfolioItemShouldNotBeFound("stockSymbol.contains=" + UPDATED_STOCK_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSymbolNotContainsSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSymbol does not contain DEFAULT_STOCK_SYMBOL
        defaultStockPortfolioItemShouldNotBeFound("stockSymbol.doesNotContain=" + DEFAULT_STOCK_SYMBOL);

        // Get all the stockPortfolioItemList where stockSymbol does not contain UPDATED_STOCK_SYMBOL
        defaultStockPortfolioItemShouldBeFound("stockSymbol.doesNotContain=" + UPDATED_STOCK_SYMBOL);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrency equals to DEFAULT_STOCK_CURRENCY
        defaultStockPortfolioItemShouldBeFound("stockCurrency.equals=" + DEFAULT_STOCK_CURRENCY);

        // Get all the stockPortfolioItemList where stockCurrency equals to UPDATED_STOCK_CURRENCY
        defaultStockPortfolioItemShouldNotBeFound("stockCurrency.equals=" + UPDATED_STOCK_CURRENCY);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrency in DEFAULT_STOCK_CURRENCY or UPDATED_STOCK_CURRENCY
        defaultStockPortfolioItemShouldBeFound("stockCurrency.in=" + DEFAULT_STOCK_CURRENCY + "," + UPDATED_STOCK_CURRENCY);

        // Get all the stockPortfolioItemList where stockCurrency equals to UPDATED_STOCK_CURRENCY
        defaultStockPortfolioItemShouldNotBeFound("stockCurrency.in=" + UPDATED_STOCK_CURRENCY);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrency is not null
        defaultStockPortfolioItemShouldBeFound("stockCurrency.specified=true");

        // Get all the stockPortfolioItemList where stockCurrency is null
        defaultStockPortfolioItemShouldNotBeFound("stockCurrency.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate equals to DEFAULT_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionDate.equals=" + DEFAULT_STOCK_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockAcquisitionDate equals to UPDATED_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.equals=" + UPDATED_STOCK_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate in DEFAULT_STOCK_ACQUISITION_DATE or UPDATED_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound(
            "stockAcquisitionDate.in=" + DEFAULT_STOCK_ACQUISITION_DATE + "," + UPDATED_STOCK_ACQUISITION_DATE
        );

        // Get all the stockPortfolioItemList where stockAcquisitionDate equals to UPDATED_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.in=" + UPDATED_STOCK_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is not null
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionDate.specified=true");

        // Get all the stockPortfolioItemList where stockAcquisitionDate is null
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is greater than or equal to DEFAULT_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionDate.greaterThanOrEqual=" + DEFAULT_STOCK_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is greater than or equal to UPDATED_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.greaterThanOrEqual=" + UPDATED_STOCK_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is less than or equal to DEFAULT_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionDate.lessThanOrEqual=" + DEFAULT_STOCK_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is less than or equal to SMALLER_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.lessThanOrEqual=" + SMALLER_STOCK_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is less than DEFAULT_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.lessThan=" + DEFAULT_STOCK_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is less than UPDATED_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionDate.lessThan=" + UPDATED_STOCK_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is greater than DEFAULT_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionDate.greaterThan=" + DEFAULT_STOCK_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockAcquisitionDate is greater than SMALLER_STOCK_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionDate.greaterThan=" + SMALLER_STOCK_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber equals to DEFAULT_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.equals=" + DEFAULT_STOCK_SHARES_NUMBER);

        // Get all the stockPortfolioItemList where stockSharesNumber equals to UPDATED_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.equals=" + UPDATED_STOCK_SHARES_NUMBER);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber in DEFAULT_STOCK_SHARES_NUMBER or UPDATED_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.in=" + DEFAULT_STOCK_SHARES_NUMBER + "," + UPDATED_STOCK_SHARES_NUMBER);

        // Get all the stockPortfolioItemList where stockSharesNumber equals to UPDATED_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.in=" + UPDATED_STOCK_SHARES_NUMBER);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber is not null
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.specified=true");

        // Get all the stockPortfolioItemList where stockSharesNumber is null
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber is greater than or equal to DEFAULT_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.greaterThanOrEqual=" + DEFAULT_STOCK_SHARES_NUMBER);

        // Get all the stockPortfolioItemList where stockSharesNumber is greater than or equal to UPDATED_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.greaterThanOrEqual=" + UPDATED_STOCK_SHARES_NUMBER);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber is less than or equal to DEFAULT_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.lessThanOrEqual=" + DEFAULT_STOCK_SHARES_NUMBER);

        // Get all the stockPortfolioItemList where stockSharesNumber is less than or equal to SMALLER_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.lessThanOrEqual=" + SMALLER_STOCK_SHARES_NUMBER);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber is less than DEFAULT_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.lessThan=" + DEFAULT_STOCK_SHARES_NUMBER);

        // Get all the stockPortfolioItemList where stockSharesNumber is less than UPDATED_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.lessThan=" + UPDATED_STOCK_SHARES_NUMBER);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockSharesNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockSharesNumber is greater than DEFAULT_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldNotBeFound("stockSharesNumber.greaterThan=" + DEFAULT_STOCK_SHARES_NUMBER);

        // Get all the stockPortfolioItemList where stockSharesNumber is greater than SMALLER_STOCK_SHARES_NUMBER
        defaultStockPortfolioItemShouldBeFound("stockSharesNumber.greaterThan=" + SMALLER_STOCK_SHARES_NUMBER);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice equals to DEFAULT_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionPrice.equals=" + DEFAULT_STOCK_ACQUISITION_PRICE);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice equals to UPDATED_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.equals=" + UPDATED_STOCK_ACQUISITION_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice in DEFAULT_STOCK_ACQUISITION_PRICE or UPDATED_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldBeFound(
            "stockAcquisitionPrice.in=" + DEFAULT_STOCK_ACQUISITION_PRICE + "," + UPDATED_STOCK_ACQUISITION_PRICE
        );

        // Get all the stockPortfolioItemList where stockAcquisitionPrice equals to UPDATED_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.in=" + UPDATED_STOCK_ACQUISITION_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is not null
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionPrice.specified=true");

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is null
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is greater than or equal to DEFAULT_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionPrice.greaterThanOrEqual=" + DEFAULT_STOCK_ACQUISITION_PRICE);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is greater than or equal to UPDATED_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.greaterThanOrEqual=" + UPDATED_STOCK_ACQUISITION_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is less than or equal to DEFAULT_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionPrice.lessThanOrEqual=" + DEFAULT_STOCK_ACQUISITION_PRICE);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is less than or equal to SMALLER_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.lessThanOrEqual=" + SMALLER_STOCK_ACQUISITION_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is less than DEFAULT_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.lessThan=" + DEFAULT_STOCK_ACQUISITION_PRICE);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is less than UPDATED_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionPrice.lessThan=" + UPDATED_STOCK_ACQUISITION_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is greater than DEFAULT_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionPrice.greaterThan=" + DEFAULT_STOCK_ACQUISITION_PRICE);

        // Get all the stockPortfolioItemList where stockAcquisitionPrice is greater than SMALLER_STOCK_ACQUISITION_PRICE
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionPrice.greaterThan=" + SMALLER_STOCK_ACQUISITION_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice equals to DEFAULT_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.equals=" + DEFAULT_STOCK_CURRENT_PRICE);

        // Get all the stockPortfolioItemList where stockCurrentPrice equals to UPDATED_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.equals=" + UPDATED_STOCK_CURRENT_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice in DEFAULT_STOCK_CURRENT_PRICE or UPDATED_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.in=" + DEFAULT_STOCK_CURRENT_PRICE + "," + UPDATED_STOCK_CURRENT_PRICE);

        // Get all the stockPortfolioItemList where stockCurrentPrice equals to UPDATED_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.in=" + UPDATED_STOCK_CURRENT_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice is not null
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.specified=true");

        // Get all the stockPortfolioItemList where stockCurrentPrice is null
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice is greater than or equal to DEFAULT_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.greaterThanOrEqual=" + DEFAULT_STOCK_CURRENT_PRICE);

        // Get all the stockPortfolioItemList where stockCurrentPrice is greater than or equal to UPDATED_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.greaterThanOrEqual=" + UPDATED_STOCK_CURRENT_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice is less than or equal to DEFAULT_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.lessThanOrEqual=" + DEFAULT_STOCK_CURRENT_PRICE);

        // Get all the stockPortfolioItemList where stockCurrentPrice is less than or equal to SMALLER_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.lessThanOrEqual=" + SMALLER_STOCK_CURRENT_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice is less than DEFAULT_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.lessThan=" + DEFAULT_STOCK_CURRENT_PRICE);

        // Get all the stockPortfolioItemList where stockCurrentPrice is less than UPDATED_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.lessThan=" + UPDATED_STOCK_CURRENT_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentPrice is greater than DEFAULT_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentPrice.greaterThan=" + DEFAULT_STOCK_CURRENT_PRICE);

        // Get all the stockPortfolioItemList where stockCurrentPrice is greater than SMALLER_STOCK_CURRENT_PRICE
        defaultStockPortfolioItemShouldBeFound("stockCurrentPrice.greaterThan=" + SMALLER_STOCK_CURRENT_PRICE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate equals to DEFAULT_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.equals=" + DEFAULT_STOCK_CURRENT_DATE);

        // Get all the stockPortfolioItemList where stockCurrentDate equals to UPDATED_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.equals=" + UPDATED_STOCK_CURRENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate in DEFAULT_STOCK_CURRENT_DATE or UPDATED_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.in=" + DEFAULT_STOCK_CURRENT_DATE + "," + UPDATED_STOCK_CURRENT_DATE);

        // Get all the stockPortfolioItemList where stockCurrentDate equals to UPDATED_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.in=" + UPDATED_STOCK_CURRENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate is not null
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.specified=true");

        // Get all the stockPortfolioItemList where stockCurrentDate is null
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate is greater than or equal to DEFAULT_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.greaterThanOrEqual=" + DEFAULT_STOCK_CURRENT_DATE);

        // Get all the stockPortfolioItemList where stockCurrentDate is greater than or equal to UPDATED_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.greaterThanOrEqual=" + UPDATED_STOCK_CURRENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate is less than or equal to DEFAULT_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.lessThanOrEqual=" + DEFAULT_STOCK_CURRENT_DATE);

        // Get all the stockPortfolioItemList where stockCurrentDate is less than or equal to SMALLER_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.lessThanOrEqual=" + SMALLER_STOCK_CURRENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate is less than DEFAULT_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.lessThan=" + DEFAULT_STOCK_CURRENT_DATE);

        // Get all the stockPortfolioItemList where stockCurrentDate is less than UPDATED_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.lessThan=" + UPDATED_STOCK_CURRENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentDate is greater than DEFAULT_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentDate.greaterThan=" + DEFAULT_STOCK_CURRENT_DATE);

        // Get all the stockPortfolioItemList where stockCurrentDate is greater than SMALLER_STOCK_CURRENT_DATE
        defaultStockPortfolioItemShouldBeFound("stockCurrentDate.greaterThan=" + SMALLER_STOCK_CURRENT_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor equals to DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionCurrencyFactor.equals=" + DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor equals to UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionCurrencyFactor.equals=" + UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor in DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR or UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound(
            "stockAcquisitionCurrencyFactor.in=" +
            DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR +
            "," +
            UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        );

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor equals to UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionCurrencyFactor.in=" + UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is not null
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionCurrencyFactor.specified=true");

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is null
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionCurrencyFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is greater than or equal to DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound(
            "stockAcquisitionCurrencyFactor.greaterThanOrEqual=" + DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        );

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is greater than or equal to UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound(
            "stockAcquisitionCurrencyFactor.greaterThanOrEqual=" + UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is less than or equal to DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound(
            "stockAcquisitionCurrencyFactor.lessThanOrEqual=" + DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        );

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is less than or equal to SMALLER_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound(
            "stockAcquisitionCurrencyFactor.lessThanOrEqual=" + SMALLER_STOCK_ACQUISITION_CURRENCY_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is less than DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockAcquisitionCurrencyFactor.lessThan=" + DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is less than UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionCurrencyFactor.lessThan=" + UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockAcquisitionCurrencyFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is greater than DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound(
            "stockAcquisitionCurrencyFactor.greaterThan=" + DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR
        );

        // Get all the stockPortfolioItemList where stockAcquisitionCurrencyFactor is greater than SMALLER_STOCK_ACQUISITION_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockAcquisitionCurrencyFactor.greaterThan=" + SMALLER_STOCK_ACQUISITION_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor equals to DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockCurrentCurrencyFactor.equals=" + DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor equals to UPDATED_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.equals=" + UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor in DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR or UPDATED_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound(
            "stockCurrentCurrencyFactor.in=" + DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR + "," + UPDATED_STOCK_CURRENT_CURRENCY_FACTOR
        );

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor equals to UPDATED_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.in=" + UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is not null
        defaultStockPortfolioItemShouldBeFound("stockCurrentCurrencyFactor.specified=true");

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is null
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is greater than or equal to DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockCurrentCurrencyFactor.greaterThanOrEqual=" + DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is greater than or equal to UPDATED_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.greaterThanOrEqual=" + UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is less than or equal to DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockCurrentCurrencyFactor.lessThanOrEqual=" + DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is less than or equal to SMALLER_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.lessThanOrEqual=" + SMALLER_STOCK_CURRENT_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is less than DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.lessThan=" + DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is less than UPDATED_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockCurrentCurrencyFactor.lessThan=" + UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockCurrentCurrencyFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is greater than DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldNotBeFound("stockCurrentCurrencyFactor.greaterThan=" + DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR);

        // Get all the stockPortfolioItemList where stockCurrentCurrencyFactor is greater than SMALLER_STOCK_CURRENT_CURRENCY_FACTOR
        defaultStockPortfolioItemShouldBeFound("stockCurrentCurrencyFactor.greaterThan=" + SMALLER_STOCK_CURRENT_CURRENCY_FACTOR);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate equals to DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockPriceAtAcquisitionDate.equals=" + DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate equals to UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockPriceAtAcquisitionDate.equals=" + UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate in DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE or UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound(
            "stockPriceAtAcquisitionDate.in=" + DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE + "," + UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        );

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate equals to UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockPriceAtAcquisitionDate.in=" + UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is not null
        defaultStockPortfolioItemShouldBeFound("stockPriceAtAcquisitionDate.specified=true");

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is null
        defaultStockPortfolioItemShouldNotBeFound("stockPriceAtAcquisitionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is greater than or equal to DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockPriceAtAcquisitionDate.greaterThanOrEqual=" + DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is greater than or equal to UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound(
            "stockPriceAtAcquisitionDate.greaterThanOrEqual=" + UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        );
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is less than or equal to DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockPriceAtAcquisitionDate.lessThanOrEqual=" + DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is less than or equal to SMALLER_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockPriceAtAcquisitionDate.lessThanOrEqual=" + SMALLER_STOCK_PRICE_AT_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is less than DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockPriceAtAcquisitionDate.lessThan=" + DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is less than UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockPriceAtAcquisitionDate.lessThan=" + UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockPriceAtAcquisitionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is greater than DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldNotBeFound("stockPriceAtAcquisitionDate.greaterThan=" + DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE);

        // Get all the stockPortfolioItemList where stockPriceAtAcquisitionDate is greater than SMALLER_STOCK_PRICE_AT_ACQUISITION_DATE
        defaultStockPortfolioItemShouldBeFound("stockPriceAtAcquisitionDate.greaterThan=" + SMALLER_STOCK_PRICE_AT_ACQUISITION_DATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockType equals to DEFAULT_STOCK_TYPE
        defaultStockPortfolioItemShouldBeFound("stockType.equals=" + DEFAULT_STOCK_TYPE);

        // Get all the stockPortfolioItemList where stockType equals to UPDATED_STOCK_TYPE
        defaultStockPortfolioItemShouldNotBeFound("stockType.equals=" + UPDATED_STOCK_TYPE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockTypeIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockType in DEFAULT_STOCK_TYPE or UPDATED_STOCK_TYPE
        defaultStockPortfolioItemShouldBeFound("stockType.in=" + DEFAULT_STOCK_TYPE + "," + UPDATED_STOCK_TYPE);

        // Get all the stockPortfolioItemList where stockType equals to UPDATED_STOCK_TYPE
        defaultStockPortfolioItemShouldNotBeFound("stockType.in=" + UPDATED_STOCK_TYPE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByStockTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where stockType is not null
        defaultStockPortfolioItemShouldBeFound("stockType.specified=true");

        // Get all the stockPortfolioItemList where stockType is null
        defaultStockPortfolioItemShouldNotBeFound("stockType.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByLastStockUpdateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where lastStockUpdate equals to DEFAULT_LAST_STOCK_UPDATE
        defaultStockPortfolioItemShouldBeFound("lastStockUpdate.equals=" + DEFAULT_LAST_STOCK_UPDATE);

        // Get all the stockPortfolioItemList where lastStockUpdate equals to UPDATED_LAST_STOCK_UPDATE
        defaultStockPortfolioItemShouldNotBeFound("lastStockUpdate.equals=" + UPDATED_LAST_STOCK_UPDATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByLastStockUpdateIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where lastStockUpdate in DEFAULT_LAST_STOCK_UPDATE or UPDATED_LAST_STOCK_UPDATE
        defaultStockPortfolioItemShouldBeFound("lastStockUpdate.in=" + DEFAULT_LAST_STOCK_UPDATE + "," + UPDATED_LAST_STOCK_UPDATE);

        // Get all the stockPortfolioItemList where lastStockUpdate equals to UPDATED_LAST_STOCK_UPDATE
        defaultStockPortfolioItemShouldNotBeFound("lastStockUpdate.in=" + UPDATED_LAST_STOCK_UPDATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByLastStockUpdateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where lastStockUpdate is not null
        defaultStockPortfolioItemShouldBeFound("lastStockUpdate.specified=true");

        // Get all the stockPortfolioItemList where lastStockUpdate is null
        defaultStockPortfolioItemShouldNotBeFound("lastStockUpdate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByLastCurrencyUpdateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where lastCurrencyUpdate equals to DEFAULT_LAST_CURRENCY_UPDATE
        defaultStockPortfolioItemShouldBeFound("lastCurrencyUpdate.equals=" + DEFAULT_LAST_CURRENCY_UPDATE);

        // Get all the stockPortfolioItemList where lastCurrencyUpdate equals to UPDATED_LAST_CURRENCY_UPDATE
        defaultStockPortfolioItemShouldNotBeFound("lastCurrencyUpdate.equals=" + UPDATED_LAST_CURRENCY_UPDATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByLastCurrencyUpdateIsInShouldWork() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where lastCurrencyUpdate in DEFAULT_LAST_CURRENCY_UPDATE or UPDATED_LAST_CURRENCY_UPDATE
        defaultStockPortfolioItemShouldBeFound(
            "lastCurrencyUpdate.in=" + DEFAULT_LAST_CURRENCY_UPDATE + "," + UPDATED_LAST_CURRENCY_UPDATE
        );

        // Get all the stockPortfolioItemList where lastCurrencyUpdate equals to UPDATED_LAST_CURRENCY_UPDATE
        defaultStockPortfolioItemShouldNotBeFound("lastCurrencyUpdate.in=" + UPDATED_LAST_CURRENCY_UPDATE);
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByLastCurrencyUpdateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        // Get all the stockPortfolioItemList where lastCurrencyUpdate is not null
        defaultStockPortfolioItemShouldBeFound("lastCurrencyUpdate.specified=true");

        // Get all the stockPortfolioItemList where lastCurrencyUpdate is null
        defaultStockPortfolioItemShouldNotBeFound("lastCurrencyUpdate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockPortfolioItemsByBankAccountIsEqualToSomething() throws Exception {
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);
            bankAccount = BankAccountResourceIT.createEntity(em);
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        em.persist(bankAccount);
        em.flush();
        stockPortfolioItem.setBankAccount(bankAccount);
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);
        Long bankAccountId = bankAccount.getId();

        // Get all the stockPortfolioItemList where bankAccount equals to bankAccountId
        defaultStockPortfolioItemShouldBeFound("bankAccountId.equals=" + bankAccountId);

        // Get all the stockPortfolioItemList where bankAccount equals to (bankAccountId + 1)
        defaultStockPortfolioItemShouldNotBeFound("bankAccountId.equals=" + (bankAccountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockPortfolioItemShouldBeFound(String filter) throws Exception {
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockPortfolioItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockSymbol").value(hasItem(DEFAULT_STOCK_SYMBOL)))
            .andExpect(jsonPath("$.[*].stockCurrency").value(hasItem(DEFAULT_STOCK_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].stockAcquisitionDate").value(hasItem(DEFAULT_STOCK_ACQUISITION_DATE.toString())))
            .andExpect(jsonPath("$.[*].stockSharesNumber").value(hasItem(DEFAULT_STOCK_SHARES_NUMBER.doubleValue())))
            .andExpect(jsonPath("$.[*].stockAcquisitionPrice").value(hasItem(DEFAULT_STOCK_ACQUISITION_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockCurrentPrice").value(hasItem(DEFAULT_STOCK_CURRENT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockCurrentDate").value(hasItem(DEFAULT_STOCK_CURRENT_DATE.toString())))
            .andExpect(
                jsonPath("$.[*].stockAcquisitionCurrencyFactor").value(hasItem(DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR.doubleValue()))
            )
            .andExpect(jsonPath("$.[*].stockCurrentCurrencyFactor").value(hasItem(DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR.doubleValue())))
            .andExpect(jsonPath("$.[*].stockPriceAtAcquisitionDate").value(hasItem(DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockType").value(hasItem(DEFAULT_STOCK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastStockUpdate").value(hasItem(DEFAULT_LAST_STOCK_UPDATE.toString())))
            .andExpect(jsonPath("$.[*].lastCurrencyUpdate").value(hasItem(DEFAULT_LAST_CURRENCY_UPDATE.toString())));

        // Check, that the count call also returns 1
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockPortfolioItemShouldNotBeFound(String filter) throws Exception {
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockPortfolioItem() throws Exception {
        // Get the stockPortfolioItem
        restStockPortfolioItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockPortfolioItem() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        stockPortfolioItemSearchRepository.save(stockPortfolioItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());

        // Update the stockPortfolioItem
        StockPortfolioItem updatedStockPortfolioItem = stockPortfolioItemRepository.findById(stockPortfolioItem.getId()).get();
        // Disconnect from session so that the updates on updatedStockPortfolioItem are not directly saved in db
        em.detach(updatedStockPortfolioItem);
        updatedStockPortfolioItem
            .stockSymbol(UPDATED_STOCK_SYMBOL)
            .stockCurrency(UPDATED_STOCK_CURRENCY)
            .stockAcquisitionDate(UPDATED_STOCK_ACQUISITION_DATE)
            .stockSharesNumber(UPDATED_STOCK_SHARES_NUMBER)
            .stockAcquisitionPrice(UPDATED_STOCK_ACQUISITION_PRICE)
            .stockCurrentPrice(UPDATED_STOCK_CURRENT_PRICE)
            .stockCurrentDate(UPDATED_STOCK_CURRENT_DATE)
            .stockAcquisitionCurrencyFactor(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR)
            .stockCurrentCurrencyFactor(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR)
            .stockPriceAtAcquisitionDate(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE)
            .stockType(UPDATED_STOCK_TYPE)
            .lastStockUpdate(UPDATED_LAST_STOCK_UPDATE)
            .lastCurrencyUpdate(UPDATED_LAST_CURRENCY_UPDATE);
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(updatedStockPortfolioItem);

        restStockPortfolioItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockPortfolioItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        StockPortfolioItem testStockPortfolioItem = stockPortfolioItemList.get(stockPortfolioItemList.size() - 1);
        assertThat(testStockPortfolioItem.getStockSymbol()).isEqualTo(UPDATED_STOCK_SYMBOL);
        assertThat(testStockPortfolioItem.getStockCurrency()).isEqualTo(UPDATED_STOCK_CURRENCY);
        assertThat(testStockPortfolioItem.getStockAcquisitionDate()).isEqualTo(UPDATED_STOCK_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockSharesNumber()).isEqualTo(UPDATED_STOCK_SHARES_NUMBER);
        assertThat(testStockPortfolioItem.getStockAcquisitionPrice()).isEqualTo(UPDATED_STOCK_ACQUISITION_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentPrice()).isEqualTo(UPDATED_STOCK_CURRENT_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentDate()).isEqualTo(UPDATED_STOCK_CURRENT_DATE);
        assertThat(testStockPortfolioItem.getStockAcquisitionCurrencyFactor()).isEqualTo(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockCurrentCurrencyFactor()).isEqualTo(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockPriceAtAcquisitionDate()).isEqualTo(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockType()).isEqualTo(UPDATED_STOCK_TYPE);
        assertThat(testStockPortfolioItem.getLastStockUpdate()).isEqualTo(UPDATED_LAST_STOCK_UPDATE);
        assertThat(testStockPortfolioItem.getLastCurrencyUpdate()).isEqualTo(UPDATED_LAST_CURRENCY_UPDATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<StockPortfolioItem> stockPortfolioItemSearchList = IterableUtils.toList(stockPortfolioItemSearchRepository.findAll());
                StockPortfolioItem testStockPortfolioItemSearch = stockPortfolioItemSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testStockPortfolioItemSearch.getStockSymbol()).isEqualTo(UPDATED_STOCK_SYMBOL);
                assertThat(testStockPortfolioItemSearch.getStockCurrency()).isEqualTo(UPDATED_STOCK_CURRENCY);
                assertThat(testStockPortfolioItemSearch.getStockAcquisitionDate()).isEqualTo(UPDATED_STOCK_ACQUISITION_DATE);
                assertThat(testStockPortfolioItemSearch.getStockSharesNumber()).isEqualTo(UPDATED_STOCK_SHARES_NUMBER);
                assertThat(testStockPortfolioItemSearch.getStockAcquisitionPrice()).isEqualTo(UPDATED_STOCK_ACQUISITION_PRICE);
                assertThat(testStockPortfolioItemSearch.getStockCurrentPrice()).isEqualTo(UPDATED_STOCK_CURRENT_PRICE);
                assertThat(testStockPortfolioItemSearch.getStockCurrentDate()).isEqualTo(UPDATED_STOCK_CURRENT_DATE);
                assertThat(testStockPortfolioItemSearch.getStockAcquisitionCurrencyFactor())
                    .isEqualTo(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
                assertThat(testStockPortfolioItemSearch.getStockCurrentCurrencyFactor()).isEqualTo(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
                assertThat(testStockPortfolioItemSearch.getStockPriceAtAcquisitionDate())
                    .isEqualTo(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
                assertThat(testStockPortfolioItemSearch.getStockType()).isEqualTo(UPDATED_STOCK_TYPE);
                assertThat(testStockPortfolioItemSearch.getLastStockUpdate()).isEqualTo(UPDATED_LAST_STOCK_UPDATE);
                assertThat(testStockPortfolioItemSearch.getLastCurrencyUpdate()).isEqualTo(UPDATED_LAST_CURRENCY_UPDATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingStockPortfolioItem() throws Exception {
        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        stockPortfolioItem.setId(count.incrementAndGet());

        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockPortfolioItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockPortfolioItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockPortfolioItem() throws Exception {
        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        stockPortfolioItem.setId(count.incrementAndGet());

        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPortfolioItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockPortfolioItem() throws Exception {
        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        stockPortfolioItem.setId(count.incrementAndGet());

        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPortfolioItemMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStockPortfolioItemWithPatch() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();

        // Update the stockPortfolioItem using partial update
        StockPortfolioItem partialUpdatedStockPortfolioItem = new StockPortfolioItem();
        partialUpdatedStockPortfolioItem.setId(stockPortfolioItem.getId());

        partialUpdatedStockPortfolioItem
            .stockSymbol(UPDATED_STOCK_SYMBOL)
            .stockAcquisitionDate(UPDATED_STOCK_ACQUISITION_DATE)
            .stockAcquisitionPrice(UPDATED_STOCK_ACQUISITION_PRICE)
            .stockAcquisitionCurrencyFactor(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR)
            .stockCurrentCurrencyFactor(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR)
            .stockPriceAtAcquisitionDate(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE)
            .lastStockUpdate(UPDATED_LAST_STOCK_UPDATE)
            .lastCurrencyUpdate(UPDATED_LAST_CURRENCY_UPDATE);

        restStockPortfolioItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockPortfolioItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockPortfolioItem))
            )
            .andExpect(status().isOk());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        StockPortfolioItem testStockPortfolioItem = stockPortfolioItemList.get(stockPortfolioItemList.size() - 1);
        assertThat(testStockPortfolioItem.getStockSymbol()).isEqualTo(UPDATED_STOCK_SYMBOL);
        assertThat(testStockPortfolioItem.getStockCurrency()).isEqualTo(DEFAULT_STOCK_CURRENCY);
        assertThat(testStockPortfolioItem.getStockAcquisitionDate()).isEqualTo(UPDATED_STOCK_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockSharesNumber()).isEqualTo(DEFAULT_STOCK_SHARES_NUMBER);
        assertThat(testStockPortfolioItem.getStockAcquisitionPrice()).isEqualTo(UPDATED_STOCK_ACQUISITION_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentPrice()).isEqualTo(DEFAULT_STOCK_CURRENT_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentDate()).isEqualTo(DEFAULT_STOCK_CURRENT_DATE);
        assertThat(testStockPortfolioItem.getStockAcquisitionCurrencyFactor()).isEqualTo(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockCurrentCurrencyFactor()).isEqualTo(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockPriceAtAcquisitionDate()).isEqualTo(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockType()).isEqualTo(DEFAULT_STOCK_TYPE);
        assertThat(testStockPortfolioItem.getLastStockUpdate()).isEqualTo(UPDATED_LAST_STOCK_UPDATE);
        assertThat(testStockPortfolioItem.getLastCurrencyUpdate()).isEqualTo(UPDATED_LAST_CURRENCY_UPDATE);
    }

    @Test
    @Transactional
    void fullUpdateStockPortfolioItemWithPatch() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);

        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();

        // Update the stockPortfolioItem using partial update
        StockPortfolioItem partialUpdatedStockPortfolioItem = new StockPortfolioItem();
        partialUpdatedStockPortfolioItem.setId(stockPortfolioItem.getId());

        partialUpdatedStockPortfolioItem
            .stockSymbol(UPDATED_STOCK_SYMBOL)
            .stockCurrency(UPDATED_STOCK_CURRENCY)
            .stockAcquisitionDate(UPDATED_STOCK_ACQUISITION_DATE)
            .stockSharesNumber(UPDATED_STOCK_SHARES_NUMBER)
            .stockAcquisitionPrice(UPDATED_STOCK_ACQUISITION_PRICE)
            .stockCurrentPrice(UPDATED_STOCK_CURRENT_PRICE)
            .stockCurrentDate(UPDATED_STOCK_CURRENT_DATE)
            .stockAcquisitionCurrencyFactor(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR)
            .stockCurrentCurrencyFactor(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR)
            .stockPriceAtAcquisitionDate(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE)
            .stockType(UPDATED_STOCK_TYPE)
            .lastStockUpdate(UPDATED_LAST_STOCK_UPDATE)
            .lastCurrencyUpdate(UPDATED_LAST_CURRENCY_UPDATE);

        restStockPortfolioItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockPortfolioItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockPortfolioItem))
            )
            .andExpect(status().isOk());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        StockPortfolioItem testStockPortfolioItem = stockPortfolioItemList.get(stockPortfolioItemList.size() - 1);
        assertThat(testStockPortfolioItem.getStockSymbol()).isEqualTo(UPDATED_STOCK_SYMBOL);
        assertThat(testStockPortfolioItem.getStockCurrency()).isEqualTo(UPDATED_STOCK_CURRENCY);
        assertThat(testStockPortfolioItem.getStockAcquisitionDate()).isEqualTo(UPDATED_STOCK_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockSharesNumber()).isEqualTo(UPDATED_STOCK_SHARES_NUMBER);
        assertThat(testStockPortfolioItem.getStockAcquisitionPrice()).isEqualTo(UPDATED_STOCK_ACQUISITION_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentPrice()).isEqualTo(UPDATED_STOCK_CURRENT_PRICE);
        assertThat(testStockPortfolioItem.getStockCurrentDate()).isEqualTo(UPDATED_STOCK_CURRENT_DATE);
        assertThat(testStockPortfolioItem.getStockAcquisitionCurrencyFactor()).isEqualTo(UPDATED_STOCK_ACQUISITION_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockCurrentCurrencyFactor()).isEqualTo(UPDATED_STOCK_CURRENT_CURRENCY_FACTOR);
        assertThat(testStockPortfolioItem.getStockPriceAtAcquisitionDate()).isEqualTo(UPDATED_STOCK_PRICE_AT_ACQUISITION_DATE);
        assertThat(testStockPortfolioItem.getStockType()).isEqualTo(UPDATED_STOCK_TYPE);
        assertThat(testStockPortfolioItem.getLastStockUpdate()).isEqualTo(UPDATED_LAST_STOCK_UPDATE);
        assertThat(testStockPortfolioItem.getLastCurrencyUpdate()).isEqualTo(UPDATED_LAST_CURRENCY_UPDATE);
    }

    @Test
    @Transactional
    void patchNonExistingStockPortfolioItem() throws Exception {
        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        stockPortfolioItem.setId(count.incrementAndGet());

        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockPortfolioItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockPortfolioItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockPortfolioItem() throws Exception {
        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        stockPortfolioItem.setId(count.incrementAndGet());

        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPortfolioItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockPortfolioItem() throws Exception {
        int databaseSizeBeforeUpdate = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        stockPortfolioItem.setId(count.incrementAndGet());

        // Create the StockPortfolioItem
        StockPortfolioItemDTO stockPortfolioItemDTO = stockPortfolioItemMapper.toDto(stockPortfolioItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockPortfolioItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockPortfolioItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockPortfolioItem in the database
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStockPortfolioItem() throws Exception {
        // Initialize the database
        stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);
        stockPortfolioItemRepository.save(stockPortfolioItem);
        stockPortfolioItemSearchRepository.save(stockPortfolioItem);

        int databaseSizeBeforeDelete = stockPortfolioItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the stockPortfolioItem
        restStockPortfolioItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockPortfolioItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockPortfolioItem> stockPortfolioItemList = stockPortfolioItemRepository.findAll();
        assertThat(stockPortfolioItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockPortfolioItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStockPortfolioItem() throws Exception {
        // Initialize the database
        stockPortfolioItem = stockPortfolioItemRepository.saveAndFlush(stockPortfolioItem);
        stockPortfolioItemSearchRepository.save(stockPortfolioItem);

        // Search the stockPortfolioItem
        restStockPortfolioItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stockPortfolioItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockPortfolioItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockSymbol").value(hasItem(DEFAULT_STOCK_SYMBOL)))
            .andExpect(jsonPath("$.[*].stockCurrency").value(hasItem(DEFAULT_STOCK_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].stockAcquisitionDate").value(hasItem(DEFAULT_STOCK_ACQUISITION_DATE.toString())))
            .andExpect(jsonPath("$.[*].stockSharesNumber").value(hasItem(DEFAULT_STOCK_SHARES_NUMBER.doubleValue())))
            .andExpect(jsonPath("$.[*].stockAcquisitionPrice").value(hasItem(DEFAULT_STOCK_ACQUISITION_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockCurrentPrice").value(hasItem(DEFAULT_STOCK_CURRENT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockCurrentDate").value(hasItem(DEFAULT_STOCK_CURRENT_DATE.toString())))
            .andExpect(
                jsonPath("$.[*].stockAcquisitionCurrencyFactor").value(hasItem(DEFAULT_STOCK_ACQUISITION_CURRENCY_FACTOR.doubleValue()))
            )
            .andExpect(jsonPath("$.[*].stockCurrentCurrencyFactor").value(hasItem(DEFAULT_STOCK_CURRENT_CURRENCY_FACTOR.doubleValue())))
            .andExpect(jsonPath("$.[*].stockPriceAtAcquisitionDate").value(hasItem(DEFAULT_STOCK_PRICE_AT_ACQUISITION_DATE.doubleValue())))
            .andExpect(jsonPath("$.[*].stockType").value(hasItem(DEFAULT_STOCK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastStockUpdate").value(hasItem(DEFAULT_LAST_STOCK_UPDATE.toString())))
            .andExpect(jsonPath("$.[*].lastCurrencyUpdate").value(hasItem(DEFAULT_LAST_CURRENCY_UPDATE.toString())));
    }
}
