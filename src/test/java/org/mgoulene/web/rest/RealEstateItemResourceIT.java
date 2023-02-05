package org.mgoulene.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
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
import org.mgoulene.domain.RealEstateItem;
import org.mgoulene.repository.RealEstateItemRepository;
import org.mgoulene.repository.search.RealEstateItemSearchRepository;
import org.mgoulene.service.RealEstateItemService;
import org.mgoulene.service.criteria.RealEstateItemCriteria;
import org.mgoulene.service.dto.RealEstateItemDTO;
import org.mgoulene.service.mapper.RealEstateItemMapper;
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
 * Integration tests for the {@link RealEstateItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RealEstateItemResourceIT {

    private static final Float DEFAULT_LOAN_VALUE = 0F;
    private static final Float UPDATED_LOAN_VALUE = 1F;
    private static final Float SMALLER_LOAN_VALUE = 0F - 1F;

    private static final Float DEFAULT_TOTAL_VALUE = 0F;
    private static final Float UPDATED_TOTAL_VALUE = 1F;
    private static final Float SMALLER_TOTAL_VALUE = 0F - 1F;

    private static final Float DEFAULT_PERCENT_OWNED = 0F;
    private static final Float UPDATED_PERCENT_OWNED = 1F;
    private static final Float SMALLER_PERCENT_OWNED = 0F - 1F;

    private static final LocalDate DEFAULT_ITEM_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ITEM_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ITEM_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/real-estate-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/real-estate-items";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RealEstateItemRepository realEstateItemRepository;

    @Mock
    private RealEstateItemRepository realEstateItemRepositoryMock;

    @Autowired
    private RealEstateItemMapper realEstateItemMapper;

    @Mock
    private RealEstateItemService realEstateItemServiceMock;

    @Autowired
    private RealEstateItemSearchRepository realEstateItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRealEstateItemMockMvc;

    private RealEstateItem realEstateItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RealEstateItem createEntity(EntityManager em) {
        RealEstateItem realEstateItem = new RealEstateItem()
            .loanValue(DEFAULT_LOAN_VALUE)
            .totalValue(DEFAULT_TOTAL_VALUE)
            .percentOwned(DEFAULT_PERCENT_OWNED)
            .itemDate(DEFAULT_ITEM_DATE);
        // Add required entity
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            bankAccount = BankAccountResourceIT.createEntity(em);
            em.persist(bankAccount);
            em.flush();
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        realEstateItem.setBankAccount(bankAccount);
        return realEstateItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RealEstateItem createUpdatedEntity(EntityManager em) {
        RealEstateItem realEstateItem = new RealEstateItem()
            .loanValue(UPDATED_LOAN_VALUE)
            .totalValue(UPDATED_TOTAL_VALUE)
            .percentOwned(UPDATED_PERCENT_OWNED)
            .itemDate(UPDATED_ITEM_DATE);
        // Add required entity
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            bankAccount = BankAccountResourceIT.createUpdatedEntity(em);
            em.persist(bankAccount);
            em.flush();
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        realEstateItem.setBankAccount(bankAccount);
        return realEstateItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        realEstateItemSearchRepository.deleteAll();
        assertThat(realEstateItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        realEstateItem = createEntity(em);
    }

    @Test
    @Transactional
    void createRealEstateItem() throws Exception {
        int databaseSizeBeforeCreate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);
        restRealEstateItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        RealEstateItem testRealEstateItem = realEstateItemList.get(realEstateItemList.size() - 1);
        assertThat(testRealEstateItem.getLoanValue()).isEqualTo(DEFAULT_LOAN_VALUE);
        assertThat(testRealEstateItem.getTotalValue()).isEqualTo(DEFAULT_TOTAL_VALUE);
        assertThat(testRealEstateItem.getPercentOwned()).isEqualTo(DEFAULT_PERCENT_OWNED);
        assertThat(testRealEstateItem.getItemDate()).isEqualTo(DEFAULT_ITEM_DATE);
    }

    @Test
    @Transactional
    void createRealEstateItemWithExistingId() throws Exception {
        // Create the RealEstateItem with an existing ID
        realEstateItem.setId(1L);
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        int databaseSizeBeforeCreate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRealEstateItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLoanValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        // set the field null
        realEstateItem.setLoanValue(null);

        // Create the RealEstateItem, which fails.
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        restRealEstateItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTotalValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        // set the field null
        realEstateItem.setTotalValue(null);

        // Create the RealEstateItem, which fails.
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        restRealEstateItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPercentOwnedIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        // set the field null
        realEstateItem.setPercentOwned(null);

        // Create the RealEstateItem, which fails.
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        restRealEstateItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        // set the field null
        realEstateItem.setItemDate(null);

        // Create the RealEstateItem, which fails.
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        restRealEstateItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRealEstateItems() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList
        restRealEstateItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(realEstateItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].loanValue").value(hasItem(DEFAULT_LOAN_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalValue").value(hasItem(DEFAULT_TOTAL_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].percentOwned").value(hasItem(DEFAULT_PERCENT_OWNED.doubleValue())))
            .andExpect(jsonPath("$.[*].itemDate").value(hasItem(DEFAULT_ITEM_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRealEstateItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(realEstateItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRealEstateItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(realEstateItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRealEstateItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(realEstateItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRealEstateItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(realEstateItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRealEstateItem() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get the realEstateItem
        restRealEstateItemMockMvc
            .perform(get(ENTITY_API_URL_ID, realEstateItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(realEstateItem.getId().intValue()))
            .andExpect(jsonPath("$.loanValue").value(DEFAULT_LOAN_VALUE.doubleValue()))
            .andExpect(jsonPath("$.totalValue").value(DEFAULT_TOTAL_VALUE.doubleValue()))
            .andExpect(jsonPath("$.percentOwned").value(DEFAULT_PERCENT_OWNED.doubleValue()))
            .andExpect(jsonPath("$.itemDate").value(DEFAULT_ITEM_DATE.toString()));
    }

    @Test
    @Transactional
    void getRealEstateItemsByIdFiltering() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        Long id = realEstateItem.getId();

        defaultRealEstateItemShouldBeFound("id.equals=" + id);
        defaultRealEstateItemShouldNotBeFound("id.notEquals=" + id);

        defaultRealEstateItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRealEstateItemShouldNotBeFound("id.greaterThan=" + id);

        defaultRealEstateItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRealEstateItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue equals to DEFAULT_LOAN_VALUE
        defaultRealEstateItemShouldBeFound("loanValue.equals=" + DEFAULT_LOAN_VALUE);

        // Get all the realEstateItemList where loanValue equals to UPDATED_LOAN_VALUE
        defaultRealEstateItemShouldNotBeFound("loanValue.equals=" + UPDATED_LOAN_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsInShouldWork() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue in DEFAULT_LOAN_VALUE or UPDATED_LOAN_VALUE
        defaultRealEstateItemShouldBeFound("loanValue.in=" + DEFAULT_LOAN_VALUE + "," + UPDATED_LOAN_VALUE);

        // Get all the realEstateItemList where loanValue equals to UPDATED_LOAN_VALUE
        defaultRealEstateItemShouldNotBeFound("loanValue.in=" + UPDATED_LOAN_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue is not null
        defaultRealEstateItemShouldBeFound("loanValue.specified=true");

        // Get all the realEstateItemList where loanValue is null
        defaultRealEstateItemShouldNotBeFound("loanValue.specified=false");
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue is greater than or equal to DEFAULT_LOAN_VALUE
        defaultRealEstateItemShouldBeFound("loanValue.greaterThanOrEqual=" + DEFAULT_LOAN_VALUE);

        // Get all the realEstateItemList where loanValue is greater than or equal to UPDATED_LOAN_VALUE
        defaultRealEstateItemShouldNotBeFound("loanValue.greaterThanOrEqual=" + UPDATED_LOAN_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue is less than or equal to DEFAULT_LOAN_VALUE
        defaultRealEstateItemShouldBeFound("loanValue.lessThanOrEqual=" + DEFAULT_LOAN_VALUE);

        // Get all the realEstateItemList where loanValue is less than or equal to SMALLER_LOAN_VALUE
        defaultRealEstateItemShouldNotBeFound("loanValue.lessThanOrEqual=" + SMALLER_LOAN_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsLessThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue is less than DEFAULT_LOAN_VALUE
        defaultRealEstateItemShouldNotBeFound("loanValue.lessThan=" + DEFAULT_LOAN_VALUE);

        // Get all the realEstateItemList where loanValue is less than UPDATED_LOAN_VALUE
        defaultRealEstateItemShouldBeFound("loanValue.lessThan=" + UPDATED_LOAN_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByLoanValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where loanValue is greater than DEFAULT_LOAN_VALUE
        defaultRealEstateItemShouldNotBeFound("loanValue.greaterThan=" + DEFAULT_LOAN_VALUE);

        // Get all the realEstateItemList where loanValue is greater than SMALLER_LOAN_VALUE
        defaultRealEstateItemShouldBeFound("loanValue.greaterThan=" + SMALLER_LOAN_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue equals to DEFAULT_TOTAL_VALUE
        defaultRealEstateItemShouldBeFound("totalValue.equals=" + DEFAULT_TOTAL_VALUE);

        // Get all the realEstateItemList where totalValue equals to UPDATED_TOTAL_VALUE
        defaultRealEstateItemShouldNotBeFound("totalValue.equals=" + UPDATED_TOTAL_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsInShouldWork() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue in DEFAULT_TOTAL_VALUE or UPDATED_TOTAL_VALUE
        defaultRealEstateItemShouldBeFound("totalValue.in=" + DEFAULT_TOTAL_VALUE + "," + UPDATED_TOTAL_VALUE);

        // Get all the realEstateItemList where totalValue equals to UPDATED_TOTAL_VALUE
        defaultRealEstateItemShouldNotBeFound("totalValue.in=" + UPDATED_TOTAL_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue is not null
        defaultRealEstateItemShouldBeFound("totalValue.specified=true");

        // Get all the realEstateItemList where totalValue is null
        defaultRealEstateItemShouldNotBeFound("totalValue.specified=false");
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue is greater than or equal to DEFAULT_TOTAL_VALUE
        defaultRealEstateItemShouldBeFound("totalValue.greaterThanOrEqual=" + DEFAULT_TOTAL_VALUE);

        // Get all the realEstateItemList where totalValue is greater than or equal to UPDATED_TOTAL_VALUE
        defaultRealEstateItemShouldNotBeFound("totalValue.greaterThanOrEqual=" + UPDATED_TOTAL_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue is less than or equal to DEFAULT_TOTAL_VALUE
        defaultRealEstateItemShouldBeFound("totalValue.lessThanOrEqual=" + DEFAULT_TOTAL_VALUE);

        // Get all the realEstateItemList where totalValue is less than or equal to SMALLER_TOTAL_VALUE
        defaultRealEstateItemShouldNotBeFound("totalValue.lessThanOrEqual=" + SMALLER_TOTAL_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsLessThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue is less than DEFAULT_TOTAL_VALUE
        defaultRealEstateItemShouldNotBeFound("totalValue.lessThan=" + DEFAULT_TOTAL_VALUE);

        // Get all the realEstateItemList where totalValue is less than UPDATED_TOTAL_VALUE
        defaultRealEstateItemShouldBeFound("totalValue.lessThan=" + UPDATED_TOTAL_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByTotalValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where totalValue is greater than DEFAULT_TOTAL_VALUE
        defaultRealEstateItemShouldNotBeFound("totalValue.greaterThan=" + DEFAULT_TOTAL_VALUE);

        // Get all the realEstateItemList where totalValue is greater than SMALLER_TOTAL_VALUE
        defaultRealEstateItemShouldBeFound("totalValue.greaterThan=" + SMALLER_TOTAL_VALUE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned equals to DEFAULT_PERCENT_OWNED
        defaultRealEstateItemShouldBeFound("percentOwned.equals=" + DEFAULT_PERCENT_OWNED);

        // Get all the realEstateItemList where percentOwned equals to UPDATED_PERCENT_OWNED
        defaultRealEstateItemShouldNotBeFound("percentOwned.equals=" + UPDATED_PERCENT_OWNED);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsInShouldWork() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned in DEFAULT_PERCENT_OWNED or UPDATED_PERCENT_OWNED
        defaultRealEstateItemShouldBeFound("percentOwned.in=" + DEFAULT_PERCENT_OWNED + "," + UPDATED_PERCENT_OWNED);

        // Get all the realEstateItemList where percentOwned equals to UPDATED_PERCENT_OWNED
        defaultRealEstateItemShouldNotBeFound("percentOwned.in=" + UPDATED_PERCENT_OWNED);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsNullOrNotNull() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned is not null
        defaultRealEstateItemShouldBeFound("percentOwned.specified=true");

        // Get all the realEstateItemList where percentOwned is null
        defaultRealEstateItemShouldNotBeFound("percentOwned.specified=false");
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned is greater than or equal to DEFAULT_PERCENT_OWNED
        defaultRealEstateItemShouldBeFound("percentOwned.greaterThanOrEqual=" + DEFAULT_PERCENT_OWNED);

        // Get all the realEstateItemList where percentOwned is greater than or equal to (DEFAULT_PERCENT_OWNED + 1)
        defaultRealEstateItemShouldNotBeFound("percentOwned.greaterThanOrEqual=" + (DEFAULT_PERCENT_OWNED + 1));
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned is less than or equal to DEFAULT_PERCENT_OWNED
        defaultRealEstateItemShouldBeFound("percentOwned.lessThanOrEqual=" + DEFAULT_PERCENT_OWNED);

        // Get all the realEstateItemList where percentOwned is less than or equal to SMALLER_PERCENT_OWNED
        defaultRealEstateItemShouldNotBeFound("percentOwned.lessThanOrEqual=" + SMALLER_PERCENT_OWNED);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsLessThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned is less than DEFAULT_PERCENT_OWNED
        defaultRealEstateItemShouldNotBeFound("percentOwned.lessThan=" + DEFAULT_PERCENT_OWNED);

        // Get all the realEstateItemList where percentOwned is less than (DEFAULT_PERCENT_OWNED + 1)
        defaultRealEstateItemShouldBeFound("percentOwned.lessThan=" + (DEFAULT_PERCENT_OWNED + 1));
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByPercentOwnedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where percentOwned is greater than DEFAULT_PERCENT_OWNED
        defaultRealEstateItemShouldNotBeFound("percentOwned.greaterThan=" + DEFAULT_PERCENT_OWNED);

        // Get all the realEstateItemList where percentOwned is greater than SMALLER_PERCENT_OWNED
        defaultRealEstateItemShouldBeFound("percentOwned.greaterThan=" + SMALLER_PERCENT_OWNED);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate equals to DEFAULT_ITEM_DATE
        defaultRealEstateItemShouldBeFound("itemDate.equals=" + DEFAULT_ITEM_DATE);

        // Get all the realEstateItemList where itemDate equals to UPDATED_ITEM_DATE
        defaultRealEstateItemShouldNotBeFound("itemDate.equals=" + UPDATED_ITEM_DATE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsInShouldWork() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate in DEFAULT_ITEM_DATE or UPDATED_ITEM_DATE
        defaultRealEstateItemShouldBeFound("itemDate.in=" + DEFAULT_ITEM_DATE + "," + UPDATED_ITEM_DATE);

        // Get all the realEstateItemList where itemDate equals to UPDATED_ITEM_DATE
        defaultRealEstateItemShouldNotBeFound("itemDate.in=" + UPDATED_ITEM_DATE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate is not null
        defaultRealEstateItemShouldBeFound("itemDate.specified=true");

        // Get all the realEstateItemList where itemDate is null
        defaultRealEstateItemShouldNotBeFound("itemDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate is greater than or equal to DEFAULT_ITEM_DATE
        defaultRealEstateItemShouldBeFound("itemDate.greaterThanOrEqual=" + DEFAULT_ITEM_DATE);

        // Get all the realEstateItemList where itemDate is greater than or equal to UPDATED_ITEM_DATE
        defaultRealEstateItemShouldNotBeFound("itemDate.greaterThanOrEqual=" + UPDATED_ITEM_DATE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate is less than or equal to DEFAULT_ITEM_DATE
        defaultRealEstateItemShouldBeFound("itemDate.lessThanOrEqual=" + DEFAULT_ITEM_DATE);

        // Get all the realEstateItemList where itemDate is less than or equal to SMALLER_ITEM_DATE
        defaultRealEstateItemShouldNotBeFound("itemDate.lessThanOrEqual=" + SMALLER_ITEM_DATE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsLessThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate is less than DEFAULT_ITEM_DATE
        defaultRealEstateItemShouldNotBeFound("itemDate.lessThan=" + DEFAULT_ITEM_DATE);

        // Get all the realEstateItemList where itemDate is less than UPDATED_ITEM_DATE
        defaultRealEstateItemShouldBeFound("itemDate.lessThan=" + UPDATED_ITEM_DATE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByItemDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        // Get all the realEstateItemList where itemDate is greater than DEFAULT_ITEM_DATE
        defaultRealEstateItemShouldNotBeFound("itemDate.greaterThan=" + DEFAULT_ITEM_DATE);

        // Get all the realEstateItemList where itemDate is greater than SMALLER_ITEM_DATE
        defaultRealEstateItemShouldBeFound("itemDate.greaterThan=" + SMALLER_ITEM_DATE);
    }

    @Test
    @Transactional
    void getAllRealEstateItemsByBankAccountIsEqualToSomething() throws Exception {
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            realEstateItemRepository.saveAndFlush(realEstateItem);
            bankAccount = BankAccountResourceIT.createEntity(em);
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        em.persist(bankAccount);
        em.flush();
        realEstateItem.setBankAccount(bankAccount);
        realEstateItemRepository.saveAndFlush(realEstateItem);
        Long bankAccountId = bankAccount.getId();

        // Get all the realEstateItemList where bankAccount equals to bankAccountId
        defaultRealEstateItemShouldBeFound("bankAccountId.equals=" + bankAccountId);

        // Get all the realEstateItemList where bankAccount equals to (bankAccountId + 1)
        defaultRealEstateItemShouldNotBeFound("bankAccountId.equals=" + (bankAccountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRealEstateItemShouldBeFound(String filter) throws Exception {
        restRealEstateItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(realEstateItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].loanValue").value(hasItem(DEFAULT_LOAN_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalValue").value(hasItem(DEFAULT_TOTAL_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].percentOwned").value(hasItem(DEFAULT_PERCENT_OWNED.doubleValue())))
            .andExpect(jsonPath("$.[*].itemDate").value(hasItem(DEFAULT_ITEM_DATE.toString())));

        // Check, that the count call also returns 1
        restRealEstateItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRealEstateItemShouldNotBeFound(String filter) throws Exception {
        restRealEstateItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRealEstateItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRealEstateItem() throws Exception {
        // Get the realEstateItem
        restRealEstateItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRealEstateItem() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        realEstateItemSearchRepository.save(realEstateItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());

        // Update the realEstateItem
        RealEstateItem updatedRealEstateItem = realEstateItemRepository.findById(realEstateItem.getId()).get();
        // Disconnect from session so that the updates on updatedRealEstateItem are not directly saved in db
        em.detach(updatedRealEstateItem);
        updatedRealEstateItem
            .loanValue(UPDATED_LOAN_VALUE)
            .totalValue(UPDATED_TOTAL_VALUE)
            .percentOwned(UPDATED_PERCENT_OWNED)
            .itemDate(UPDATED_ITEM_DATE);
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(updatedRealEstateItem);

        restRealEstateItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, realEstateItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        RealEstateItem testRealEstateItem = realEstateItemList.get(realEstateItemList.size() - 1);
        assertThat(testRealEstateItem.getLoanValue()).isEqualTo(UPDATED_LOAN_VALUE);
        assertThat(testRealEstateItem.getTotalValue()).isEqualTo(UPDATED_TOTAL_VALUE);
        assertThat(testRealEstateItem.getPercentOwned()).isEqualTo(UPDATED_PERCENT_OWNED);
        assertThat(testRealEstateItem.getItemDate()).isEqualTo(UPDATED_ITEM_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RealEstateItem> realEstateItemSearchList = IterableUtils.toList(realEstateItemSearchRepository.findAll());
                RealEstateItem testRealEstateItemSearch = realEstateItemSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRealEstateItemSearch.getLoanValue()).isEqualTo(UPDATED_LOAN_VALUE);
                assertThat(testRealEstateItemSearch.getTotalValue()).isEqualTo(UPDATED_TOTAL_VALUE);
                assertThat(testRealEstateItemSearch.getPercentOwned()).isEqualTo(UPDATED_PERCENT_OWNED);
                assertThat(testRealEstateItemSearch.getItemDate()).isEqualTo(UPDATED_ITEM_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingRealEstateItem() throws Exception {
        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        realEstateItem.setId(count.incrementAndGet());

        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRealEstateItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, realEstateItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRealEstateItem() throws Exception {
        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        realEstateItem.setId(count.incrementAndGet());

        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealEstateItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRealEstateItem() throws Exception {
        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        realEstateItem.setId(count.incrementAndGet());

        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealEstateItemMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRealEstateItemWithPatch() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();

        // Update the realEstateItem using partial update
        RealEstateItem partialUpdatedRealEstateItem = new RealEstateItem();
        partialUpdatedRealEstateItem.setId(realEstateItem.getId());

        partialUpdatedRealEstateItem.loanValue(UPDATED_LOAN_VALUE).percentOwned(UPDATED_PERCENT_OWNED);

        restRealEstateItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRealEstateItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRealEstateItem))
            )
            .andExpect(status().isOk());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        RealEstateItem testRealEstateItem = realEstateItemList.get(realEstateItemList.size() - 1);
        assertThat(testRealEstateItem.getLoanValue()).isEqualTo(UPDATED_LOAN_VALUE);
        assertThat(testRealEstateItem.getTotalValue()).isEqualTo(DEFAULT_TOTAL_VALUE);
        assertThat(testRealEstateItem.getPercentOwned()).isEqualTo(UPDATED_PERCENT_OWNED);
        assertThat(testRealEstateItem.getItemDate()).isEqualTo(DEFAULT_ITEM_DATE);
    }

    @Test
    @Transactional
    void fullUpdateRealEstateItemWithPatch() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);

        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();

        // Update the realEstateItem using partial update
        RealEstateItem partialUpdatedRealEstateItem = new RealEstateItem();
        partialUpdatedRealEstateItem.setId(realEstateItem.getId());

        partialUpdatedRealEstateItem
            .loanValue(UPDATED_LOAN_VALUE)
            .totalValue(UPDATED_TOTAL_VALUE)
            .percentOwned(UPDATED_PERCENT_OWNED)
            .itemDate(UPDATED_ITEM_DATE);

        restRealEstateItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRealEstateItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRealEstateItem))
            )
            .andExpect(status().isOk());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        RealEstateItem testRealEstateItem = realEstateItemList.get(realEstateItemList.size() - 1);
        assertThat(testRealEstateItem.getLoanValue()).isEqualTo(UPDATED_LOAN_VALUE);
        assertThat(testRealEstateItem.getTotalValue()).isEqualTo(UPDATED_TOTAL_VALUE);
        assertThat(testRealEstateItem.getPercentOwned()).isEqualTo(UPDATED_PERCENT_OWNED);
        assertThat(testRealEstateItem.getItemDate()).isEqualTo(UPDATED_ITEM_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingRealEstateItem() throws Exception {
        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        realEstateItem.setId(count.incrementAndGet());

        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRealEstateItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, realEstateItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRealEstateItem() throws Exception {
        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        realEstateItem.setId(count.incrementAndGet());

        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealEstateItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRealEstateItem() throws Exception {
        int databaseSizeBeforeUpdate = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        realEstateItem.setId(count.incrementAndGet());

        // Create the RealEstateItem
        RealEstateItemDTO realEstateItemDTO = realEstateItemMapper.toDto(realEstateItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRealEstateItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(realEstateItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RealEstateItem in the database
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRealEstateItem() throws Exception {
        // Initialize the database
        realEstateItemRepository.saveAndFlush(realEstateItem);
        realEstateItemRepository.save(realEstateItem);
        realEstateItemSearchRepository.save(realEstateItem);

        int databaseSizeBeforeDelete = realEstateItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the realEstateItem
        restRealEstateItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, realEstateItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RealEstateItem> realEstateItemList = realEstateItemRepository.findAll();
        assertThat(realEstateItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(realEstateItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRealEstateItem() throws Exception {
        // Initialize the database
        realEstateItem = realEstateItemRepository.saveAndFlush(realEstateItem);
        realEstateItemSearchRepository.save(realEstateItem);

        // Search the realEstateItem
        restRealEstateItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + realEstateItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(realEstateItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].loanValue").value(hasItem(DEFAULT_LOAN_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalValue").value(hasItem(DEFAULT_TOTAL_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].percentOwned").value(hasItem(DEFAULT_PERCENT_OWNED.doubleValue())))
            .andExpect(jsonPath("$.[*].itemDate").value(hasItem(DEFAULT_ITEM_DATE.toString())));
    }
}
