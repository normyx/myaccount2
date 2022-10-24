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
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mgoulene.IntegrationTest;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.domain.Operation;
import org.mgoulene.repository.BudgetItemPeriodRepository;
import org.mgoulene.repository.search.BudgetItemPeriodSearchRepository;
import org.mgoulene.service.criteria.BudgetItemPeriodCriteria;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.mapper.BudgetItemPeriodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BudgetItemPeriodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BudgetItemPeriodResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_MONTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MONTH = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_MONTH = LocalDate.ofEpochDay(-1L);

    private static final Float DEFAULT_AMOUNT = 1F;
    private static final Float UPDATED_AMOUNT = 2F;
    private static final Float SMALLER_AMOUNT = 1F - 1F;

    private static final Boolean DEFAULT_IS_SMOOTHED = false;
    private static final Boolean UPDATED_IS_SMOOTHED = true;

    private static final Boolean DEFAULT_IS_RECURRENT = false;
    private static final Boolean UPDATED_IS_RECURRENT = true;

    private static final String ENTITY_API_URL = "/api/budget-item-periods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/budget-item-periods";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BudgetItemPeriodRepository budgetItemPeriodRepository;

    @Autowired
    private BudgetItemPeriodMapper budgetItemPeriodMapper;

    @Autowired
    private BudgetItemPeriodSearchRepository budgetItemPeriodSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBudgetItemPeriodMockMvc;

    private BudgetItemPeriod budgetItemPeriod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BudgetItemPeriod createEntity(EntityManager em) {
        BudgetItemPeriod budgetItemPeriod = new BudgetItemPeriod()
            .date(DEFAULT_DATE)
            .month(DEFAULT_MONTH)
            .amount(DEFAULT_AMOUNT)
            .isSmoothed(DEFAULT_IS_SMOOTHED)
            .isRecurrent(DEFAULT_IS_RECURRENT);
        return budgetItemPeriod;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BudgetItemPeriod createUpdatedEntity(EntityManager em) {
        BudgetItemPeriod budgetItemPeriod = new BudgetItemPeriod()
            .date(UPDATED_DATE)
            .month(UPDATED_MONTH)
            .amount(UPDATED_AMOUNT)
            .isSmoothed(UPDATED_IS_SMOOTHED)
            .isRecurrent(UPDATED_IS_RECURRENT);
        return budgetItemPeriod;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        budgetItemPeriodSearchRepository.deleteAll();
        assertThat(budgetItemPeriodSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        budgetItemPeriod = createEntity(em);
    }

    @Test
    @Transactional
    void createBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeCreate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);
        restBudgetItemPeriodMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BudgetItemPeriod testBudgetItemPeriod = budgetItemPeriodList.get(budgetItemPeriodList.size() - 1);
        assertThat(testBudgetItemPeriod.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testBudgetItemPeriod.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testBudgetItemPeriod.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testBudgetItemPeriod.getIsSmoothed()).isEqualTo(DEFAULT_IS_SMOOTHED);
        assertThat(testBudgetItemPeriod.getIsRecurrent()).isEqualTo(DEFAULT_IS_RECURRENT);
    }

    @Test
    @Transactional
    void createBudgetItemPeriodWithExistingId() throws Exception {
        // Create the BudgetItemPeriod with an existing ID
        budgetItemPeriod.setId(1L);
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        int databaseSizeBeforeCreate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBudgetItemPeriodMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMonthIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        // set the field null
        budgetItemPeriod.setMonth(null);

        // Create the BudgetItemPeriod, which fails.
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        restBudgetItemPeriodMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        // set the field null
        budgetItemPeriod.setAmount(null);

        // Create the BudgetItemPeriod, which fails.
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        restBudgetItemPeriodMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriods() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItemPeriod.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].isSmoothed").value(hasItem(DEFAULT_IS_SMOOTHED.booleanValue())))
            .andExpect(jsonPath("$.[*].isRecurrent").value(hasItem(DEFAULT_IS_RECURRENT.booleanValue())));
    }

    @Test
    @Transactional
    void getBudgetItemPeriod() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get the budgetItemPeriod
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_API_URL_ID, budgetItemPeriod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(budgetItemPeriod.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.isSmoothed").value(DEFAULT_IS_SMOOTHED.booleanValue()))
            .andExpect(jsonPath("$.isRecurrent").value(DEFAULT_IS_RECURRENT.booleanValue()));
    }

    @Test
    @Transactional
    void getBudgetItemPeriodsByIdFiltering() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        Long id = budgetItemPeriod.getId();

        defaultBudgetItemPeriodShouldBeFound("id.equals=" + id);
        defaultBudgetItemPeriodShouldNotBeFound("id.notEquals=" + id);

        defaultBudgetItemPeriodShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBudgetItemPeriodShouldNotBeFound("id.greaterThan=" + id);

        defaultBudgetItemPeriodShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBudgetItemPeriodShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date equals to DEFAULT_DATE
        defaultBudgetItemPeriodShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the budgetItemPeriodList where date equals to UPDATED_DATE
        defaultBudgetItemPeriodShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date in DEFAULT_DATE or UPDATED_DATE
        defaultBudgetItemPeriodShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the budgetItemPeriodList where date equals to UPDATED_DATE
        defaultBudgetItemPeriodShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date is not null
        defaultBudgetItemPeriodShouldBeFound("date.specified=true");

        // Get all the budgetItemPeriodList where date is null
        defaultBudgetItemPeriodShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date is greater than or equal to DEFAULT_DATE
        defaultBudgetItemPeriodShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the budgetItemPeriodList where date is greater than or equal to UPDATED_DATE
        defaultBudgetItemPeriodShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date is less than or equal to DEFAULT_DATE
        defaultBudgetItemPeriodShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the budgetItemPeriodList where date is less than or equal to SMALLER_DATE
        defaultBudgetItemPeriodShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date is less than DEFAULT_DATE
        defaultBudgetItemPeriodShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the budgetItemPeriodList where date is less than UPDATED_DATE
        defaultBudgetItemPeriodShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where date is greater than DEFAULT_DATE
        defaultBudgetItemPeriodShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the budgetItemPeriodList where date is greater than SMALLER_DATE
        defaultBudgetItemPeriodShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month equals to DEFAULT_MONTH
        defaultBudgetItemPeriodShouldBeFound("month.equals=" + DEFAULT_MONTH);

        // Get all the budgetItemPeriodList where month equals to UPDATED_MONTH
        defaultBudgetItemPeriodShouldNotBeFound("month.equals=" + UPDATED_MONTH);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month in DEFAULT_MONTH or UPDATED_MONTH
        defaultBudgetItemPeriodShouldBeFound("month.in=" + DEFAULT_MONTH + "," + UPDATED_MONTH);

        // Get all the budgetItemPeriodList where month equals to UPDATED_MONTH
        defaultBudgetItemPeriodShouldNotBeFound("month.in=" + UPDATED_MONTH);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month is not null
        defaultBudgetItemPeriodShouldBeFound("month.specified=true");

        // Get all the budgetItemPeriodList where month is null
        defaultBudgetItemPeriodShouldNotBeFound("month.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month is greater than or equal to DEFAULT_MONTH
        defaultBudgetItemPeriodShouldBeFound("month.greaterThanOrEqual=" + DEFAULT_MONTH);

        // Get all the budgetItemPeriodList where month is greater than or equal to UPDATED_MONTH
        defaultBudgetItemPeriodShouldNotBeFound("month.greaterThanOrEqual=" + UPDATED_MONTH);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month is less than or equal to DEFAULT_MONTH
        defaultBudgetItemPeriodShouldBeFound("month.lessThanOrEqual=" + DEFAULT_MONTH);

        // Get all the budgetItemPeriodList where month is less than or equal to SMALLER_MONTH
        defaultBudgetItemPeriodShouldNotBeFound("month.lessThanOrEqual=" + SMALLER_MONTH);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsLessThanSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month is less than DEFAULT_MONTH
        defaultBudgetItemPeriodShouldNotBeFound("month.lessThan=" + DEFAULT_MONTH);

        // Get all the budgetItemPeriodList where month is less than UPDATED_MONTH
        defaultBudgetItemPeriodShouldBeFound("month.lessThan=" + UPDATED_MONTH);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByMonthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where month is greater than DEFAULT_MONTH
        defaultBudgetItemPeriodShouldNotBeFound("month.greaterThan=" + DEFAULT_MONTH);

        // Get all the budgetItemPeriodList where month is greater than SMALLER_MONTH
        defaultBudgetItemPeriodShouldBeFound("month.greaterThan=" + SMALLER_MONTH);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount equals to DEFAULT_AMOUNT
        defaultBudgetItemPeriodShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the budgetItemPeriodList where amount equals to UPDATED_AMOUNT
        defaultBudgetItemPeriodShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultBudgetItemPeriodShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the budgetItemPeriodList where amount equals to UPDATED_AMOUNT
        defaultBudgetItemPeriodShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount is not null
        defaultBudgetItemPeriodShouldBeFound("amount.specified=true");

        // Get all the budgetItemPeriodList where amount is null
        defaultBudgetItemPeriodShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultBudgetItemPeriodShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the budgetItemPeriodList where amount is greater than or equal to UPDATED_AMOUNT
        defaultBudgetItemPeriodShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount is less than or equal to DEFAULT_AMOUNT
        defaultBudgetItemPeriodShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the budgetItemPeriodList where amount is less than or equal to SMALLER_AMOUNT
        defaultBudgetItemPeriodShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount is less than DEFAULT_AMOUNT
        defaultBudgetItemPeriodShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the budgetItemPeriodList where amount is less than UPDATED_AMOUNT
        defaultBudgetItemPeriodShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where amount is greater than DEFAULT_AMOUNT
        defaultBudgetItemPeriodShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the budgetItemPeriodList where amount is greater than SMALLER_AMOUNT
        defaultBudgetItemPeriodShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByIsSmoothedIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where isSmoothed equals to DEFAULT_IS_SMOOTHED
        defaultBudgetItemPeriodShouldBeFound("isSmoothed.equals=" + DEFAULT_IS_SMOOTHED);

        // Get all the budgetItemPeriodList where isSmoothed equals to UPDATED_IS_SMOOTHED
        defaultBudgetItemPeriodShouldNotBeFound("isSmoothed.equals=" + UPDATED_IS_SMOOTHED);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByIsSmoothedIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where isSmoothed in DEFAULT_IS_SMOOTHED or UPDATED_IS_SMOOTHED
        defaultBudgetItemPeriodShouldBeFound("isSmoothed.in=" + DEFAULT_IS_SMOOTHED + "," + UPDATED_IS_SMOOTHED);

        // Get all the budgetItemPeriodList where isSmoothed equals to UPDATED_IS_SMOOTHED
        defaultBudgetItemPeriodShouldNotBeFound("isSmoothed.in=" + UPDATED_IS_SMOOTHED);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByIsSmoothedIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where isSmoothed is not null
        defaultBudgetItemPeriodShouldBeFound("isSmoothed.specified=true");

        // Get all the budgetItemPeriodList where isSmoothed is null
        defaultBudgetItemPeriodShouldNotBeFound("isSmoothed.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByIsRecurrentIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where isRecurrent equals to DEFAULT_IS_RECURRENT
        defaultBudgetItemPeriodShouldBeFound("isRecurrent.equals=" + DEFAULT_IS_RECURRENT);

        // Get all the budgetItemPeriodList where isRecurrent equals to UPDATED_IS_RECURRENT
        defaultBudgetItemPeriodShouldNotBeFound("isRecurrent.equals=" + UPDATED_IS_RECURRENT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByIsRecurrentIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where isRecurrent in DEFAULT_IS_RECURRENT or UPDATED_IS_RECURRENT
        defaultBudgetItemPeriodShouldBeFound("isRecurrent.in=" + DEFAULT_IS_RECURRENT + "," + UPDATED_IS_RECURRENT);

        // Get all the budgetItemPeriodList where isRecurrent equals to UPDATED_IS_RECURRENT
        defaultBudgetItemPeriodShouldNotBeFound("isRecurrent.in=" + UPDATED_IS_RECURRENT);
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByIsRecurrentIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        // Get all the budgetItemPeriodList where isRecurrent is not null
        defaultBudgetItemPeriodShouldBeFound("isRecurrent.specified=true");

        // Get all the budgetItemPeriodList where isRecurrent is null
        defaultBudgetItemPeriodShouldNotBeFound("isRecurrent.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByOperationIsEqualToSomething() throws Exception {
        Operation operation;
        if (TestUtil.findAll(em, Operation.class).isEmpty()) {
            budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);
            operation = OperationResourceIT.createEntity(em);
        } else {
            operation = TestUtil.findAll(em, Operation.class).get(0);
        }
        em.persist(operation);
        em.flush();
        budgetItemPeriod.setOperation(operation);
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);
        Long operationId = operation.getId();

        // Get all the budgetItemPeriodList where operation equals to operationId
        defaultBudgetItemPeriodShouldBeFound("operationId.equals=" + operationId);

        // Get all the budgetItemPeriodList where operation equals to (operationId + 1)
        defaultBudgetItemPeriodShouldNotBeFound("operationId.equals=" + (operationId + 1));
    }

    @Test
    @Transactional
    void getAllBudgetItemPeriodsByBudgetItemIsEqualToSomething() throws Exception {
        BudgetItem budgetItem;
        if (TestUtil.findAll(em, BudgetItem.class).isEmpty()) {
            budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);
            budgetItem = BudgetItemResourceIT.createEntity(em);
        } else {
            budgetItem = TestUtil.findAll(em, BudgetItem.class).get(0);
        }
        em.persist(budgetItem);
        em.flush();
        budgetItemPeriod.setBudgetItem(budgetItem);
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);
        Long budgetItemId = budgetItem.getId();

        // Get all the budgetItemPeriodList where budgetItem equals to budgetItemId
        defaultBudgetItemPeriodShouldBeFound("budgetItemId.equals=" + budgetItemId);

        // Get all the budgetItemPeriodList where budgetItem equals to (budgetItemId + 1)
        defaultBudgetItemPeriodShouldNotBeFound("budgetItemId.equals=" + (budgetItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBudgetItemPeriodShouldBeFound(String filter) throws Exception {
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItemPeriod.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].isSmoothed").value(hasItem(DEFAULT_IS_SMOOTHED.booleanValue())))
            .andExpect(jsonPath("$.[*].isRecurrent").value(hasItem(DEFAULT_IS_RECURRENT.booleanValue())));

        // Check, that the count call also returns 1
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBudgetItemPeriodShouldNotBeFound(String filter) throws Exception {
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBudgetItemPeriod() throws Exception {
        // Get the budgetItemPeriod
        restBudgetItemPeriodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBudgetItemPeriod() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        budgetItemPeriodSearchRepository.save(budgetItemPeriod);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());

        // Update the budgetItemPeriod
        BudgetItemPeriod updatedBudgetItemPeriod = budgetItemPeriodRepository.findById(budgetItemPeriod.getId()).get();
        // Disconnect from session so that the updates on updatedBudgetItemPeriod are not directly saved in db
        em.detach(updatedBudgetItemPeriod);
        updatedBudgetItemPeriod
            .date(UPDATED_DATE)
            .month(UPDATED_MONTH)
            .amount(UPDATED_AMOUNT)
            .isSmoothed(UPDATED_IS_SMOOTHED)
            .isRecurrent(UPDATED_IS_RECURRENT);
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(updatedBudgetItemPeriod);

        restBudgetItemPeriodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, budgetItemPeriodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isOk());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        BudgetItemPeriod testBudgetItemPeriod = budgetItemPeriodList.get(budgetItemPeriodList.size() - 1);
        assertThat(testBudgetItemPeriod.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBudgetItemPeriod.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testBudgetItemPeriod.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBudgetItemPeriod.getIsSmoothed()).isEqualTo(UPDATED_IS_SMOOTHED);
        assertThat(testBudgetItemPeriod.getIsRecurrent()).isEqualTo(UPDATED_IS_RECURRENT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BudgetItemPeriod> budgetItemPeriodSearchList = IterableUtils.toList(budgetItemPeriodSearchRepository.findAll());
                BudgetItemPeriod testBudgetItemPeriodSearch = budgetItemPeriodSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBudgetItemPeriodSearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testBudgetItemPeriodSearch.getMonth()).isEqualTo(UPDATED_MONTH);
                assertThat(testBudgetItemPeriodSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
                assertThat(testBudgetItemPeriodSearch.getIsSmoothed()).isEqualTo(UPDATED_IS_SMOOTHED);
                assertThat(testBudgetItemPeriodSearch.getIsRecurrent()).isEqualTo(UPDATED_IS_RECURRENT);
            });
    }

    @Test
    @Transactional
    void putNonExistingBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        budgetItemPeriod.setId(count.incrementAndGet());

        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBudgetItemPeriodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, budgetItemPeriodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        budgetItemPeriod.setId(count.incrementAndGet());

        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemPeriodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        budgetItemPeriod.setId(count.incrementAndGet());

        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemPeriodMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBudgetItemPeriodWithPatch() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();

        // Update the budgetItemPeriod using partial update
        BudgetItemPeriod partialUpdatedBudgetItemPeriod = new BudgetItemPeriod();
        partialUpdatedBudgetItemPeriod.setId(budgetItemPeriod.getId());

        partialUpdatedBudgetItemPeriod
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .isSmoothed(UPDATED_IS_SMOOTHED)
            .isRecurrent(UPDATED_IS_RECURRENT);

        restBudgetItemPeriodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBudgetItemPeriod.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBudgetItemPeriod))
            )
            .andExpect(status().isOk());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        BudgetItemPeriod testBudgetItemPeriod = budgetItemPeriodList.get(budgetItemPeriodList.size() - 1);
        assertThat(testBudgetItemPeriod.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBudgetItemPeriod.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testBudgetItemPeriod.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBudgetItemPeriod.getIsSmoothed()).isEqualTo(UPDATED_IS_SMOOTHED);
        assertThat(testBudgetItemPeriod.getIsRecurrent()).isEqualTo(UPDATED_IS_RECURRENT);
    }

    @Test
    @Transactional
    void fullUpdateBudgetItemPeriodWithPatch() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);

        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();

        // Update the budgetItemPeriod using partial update
        BudgetItemPeriod partialUpdatedBudgetItemPeriod = new BudgetItemPeriod();
        partialUpdatedBudgetItemPeriod.setId(budgetItemPeriod.getId());

        partialUpdatedBudgetItemPeriod
            .date(UPDATED_DATE)
            .month(UPDATED_MONTH)
            .amount(UPDATED_AMOUNT)
            .isSmoothed(UPDATED_IS_SMOOTHED)
            .isRecurrent(UPDATED_IS_RECURRENT);

        restBudgetItemPeriodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBudgetItemPeriod.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBudgetItemPeriod))
            )
            .andExpect(status().isOk());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        BudgetItemPeriod testBudgetItemPeriod = budgetItemPeriodList.get(budgetItemPeriodList.size() - 1);
        assertThat(testBudgetItemPeriod.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBudgetItemPeriod.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testBudgetItemPeriod.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBudgetItemPeriod.getIsSmoothed()).isEqualTo(UPDATED_IS_SMOOTHED);
        assertThat(testBudgetItemPeriod.getIsRecurrent()).isEqualTo(UPDATED_IS_RECURRENT);
    }

    @Test
    @Transactional
    void patchNonExistingBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        budgetItemPeriod.setId(count.incrementAndGet());

        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBudgetItemPeriodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, budgetItemPeriodDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        budgetItemPeriod.setId(count.incrementAndGet());

        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemPeriodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBudgetItemPeriod() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        budgetItemPeriod.setId(count.incrementAndGet());

        // Create the BudgetItemPeriod
        BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodMapper.toDto(budgetItemPeriod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemPeriodMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemPeriodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BudgetItemPeriod in the database
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBudgetItemPeriod() throws Exception {
        // Initialize the database
        budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);
        budgetItemPeriodRepository.save(budgetItemPeriod);
        budgetItemPeriodSearchRepository.save(budgetItemPeriod);

        int databaseSizeBeforeDelete = budgetItemPeriodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the budgetItemPeriod
        restBudgetItemPeriodMockMvc
            .perform(delete(ENTITY_API_URL_ID, budgetItemPeriod.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BudgetItemPeriod> budgetItemPeriodList = budgetItemPeriodRepository.findAll();
        assertThat(budgetItemPeriodList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemPeriodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBudgetItemPeriod() throws Exception {
        // Initialize the database
        budgetItemPeriod = budgetItemPeriodRepository.saveAndFlush(budgetItemPeriod);
        budgetItemPeriodSearchRepository.save(budgetItemPeriod);

        // Search the budgetItemPeriod
        restBudgetItemPeriodMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + budgetItemPeriod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItemPeriod.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].isSmoothed").value(hasItem(DEFAULT_IS_SMOOTHED.booleanValue())))
            .andExpect(jsonPath("$.[*].isRecurrent").value(hasItem(DEFAULT_IS_RECURRENT.booleanValue())));
    }
}
