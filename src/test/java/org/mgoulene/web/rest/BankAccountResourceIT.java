package org.mgoulene.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mgoulene.IntegrationTest;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.domain.enumeration.BankAccountType;
import org.mgoulene.repository.BankAccountRepository;
import org.mgoulene.repository.search.BankAccountSearchRepository;
import org.mgoulene.service.BankAccountService;
import org.mgoulene.service.criteria.BankAccountCriteria;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.mapper.BankAccountMapper;
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
 * Integration tests for the {@link BankAccountResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BankAccountResourceIT {

    private static final String DEFAULT_ACCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_BANK = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_BANK = "BBBBBBBBBB";

    private static final Float DEFAULT_INITIAL_AMOUNT = 1F;
    private static final Float UPDATED_INITIAL_AMOUNT = 2F;
    private static final Float SMALLER_INITIAL_AMOUNT = 1F - 1F;

    private static final Boolean DEFAULT_ARCHIVED = false;
    private static final Boolean UPDATED_ARCHIVED = true;

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final BankAccountType DEFAULT_ACCOUNT_TYPE = BankAccountType.CURRENTACCOUNT;
    private static final BankAccountType UPDATED_ACCOUNT_TYPE = BankAccountType.SAVINGSACCOUNT;

    private static final String ENTITY_API_URL = "/api/bank-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/bank-accounts";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankAccountRepository bankAccountRepositoryMock;

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Mock
    private BankAccountService bankAccountServiceMock;

    @Autowired
    private BankAccountSearchRepository bankAccountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBankAccountMockMvc;

    private BankAccount bankAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankAccount createEntity(EntityManager em) {
        BankAccount bankAccount = new BankAccount()
            .accountName(DEFAULT_ACCOUNT_NAME)
            .accountBank(DEFAULT_ACCOUNT_BANK)
            .initialAmount(DEFAULT_INITIAL_AMOUNT)
            .archived(DEFAULT_ARCHIVED)
            .shortName(DEFAULT_SHORT_NAME)
            .accountType(DEFAULT_ACCOUNT_TYPE);
        // Add required entity
        ApplicationUser applicationUser;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            applicationUser = ApplicationUserResourceIT.createEntity(em);
            em.persist(applicationUser);
            em.flush();
        } else {
            applicationUser = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        bankAccount.setAccount(applicationUser);
        return bankAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankAccount createUpdatedEntity(EntityManager em) {
        BankAccount bankAccount = new BankAccount()
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBank(UPDATED_ACCOUNT_BANK)
            .initialAmount(UPDATED_INITIAL_AMOUNT)
            .archived(UPDATED_ARCHIVED)
            .shortName(UPDATED_SHORT_NAME)
            .accountType(UPDATED_ACCOUNT_TYPE);
        // Add required entity
        ApplicationUser applicationUser;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            applicationUser = ApplicationUserResourceIT.createUpdatedEntity(em);
            em.persist(applicationUser);
            em.flush();
        } else {
            applicationUser = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        bankAccount.setAccount(applicationUser);
        return bankAccount;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        bankAccountSearchRepository.deleteAll();
        assertThat(bankAccountSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        bankAccount = createEntity(em);
    }

    @Test
    @Transactional
    void createBankAccount() throws Exception {
        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);
        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BankAccount testBankAccount = bankAccountList.get(bankAccountList.size() - 1);
        assertThat(testBankAccount.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testBankAccount.getAccountBank()).isEqualTo(DEFAULT_ACCOUNT_BANK);
        assertThat(testBankAccount.getInitialAmount()).isEqualTo(DEFAULT_INITIAL_AMOUNT);
        assertThat(testBankAccount.getArchived()).isEqualTo(DEFAULT_ARCHIVED);
        assertThat(testBankAccount.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testBankAccount.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void createBankAccountWithExistingId() throws Exception {
        // Create the BankAccount with an existing ID
        bankAccount.setId(1L);
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAccountNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        // set the field null
        bankAccount.setAccountName(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAccountBankIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        // set the field null
        bankAccount.setAccountBank(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkInitialAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        // set the field null
        bankAccount.setInitialAmount(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkArchivedIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        // set the field null
        bankAccount.setArchived(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAccountTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        // set the field null
        bankAccount.setAccountType(null);

        // Create the BankAccount, which fails.
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        restBankAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBankAccounts() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList
        restBankAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountBank").value(hasItem(DEFAULT_ACCOUNT_BANK)))
            .andExpect(jsonPath("$.[*].initialAmount").value(hasItem(DEFAULT_INITIAL_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].archived").value(hasItem(DEFAULT_ARCHIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBankAccountsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bankAccountServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBankAccountMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bankAccountServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBankAccountsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bankAccountServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBankAccountMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bankAccountRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get the bankAccount
        restBankAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, bankAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bankAccount.getId().intValue()))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.accountBank").value(DEFAULT_ACCOUNT_BANK))
            .andExpect(jsonPath("$.initialAmount").value(DEFAULT_INITIAL_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.archived").value(DEFAULT_ARCHIVED.booleanValue()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME))
            .andExpect(jsonPath("$.accountType").value(DEFAULT_ACCOUNT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getBankAccountsByIdFiltering() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        Long id = bankAccount.getId();

        defaultBankAccountShouldBeFound("id.equals=" + id);
        defaultBankAccountShouldNotBeFound("id.notEquals=" + id);

        defaultBankAccountShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBankAccountShouldNotBeFound("id.greaterThan=" + id);

        defaultBankAccountShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBankAccountShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountNameIsEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountName equals to DEFAULT_ACCOUNT_NAME
        defaultBankAccountShouldBeFound("accountName.equals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the bankAccountList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultBankAccountShouldNotBeFound("accountName.equals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountNameIsInShouldWork() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountName in DEFAULT_ACCOUNT_NAME or UPDATED_ACCOUNT_NAME
        defaultBankAccountShouldBeFound("accountName.in=" + DEFAULT_ACCOUNT_NAME + "," + UPDATED_ACCOUNT_NAME);

        // Get all the bankAccountList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultBankAccountShouldNotBeFound("accountName.in=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountName is not null
        defaultBankAccountShouldBeFound("accountName.specified=true");

        // Get all the bankAccountList where accountName is null
        defaultBankAccountShouldNotBeFound("accountName.specified=false");
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountNameContainsSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountName contains DEFAULT_ACCOUNT_NAME
        defaultBankAccountShouldBeFound("accountName.contains=" + DEFAULT_ACCOUNT_NAME);

        // Get all the bankAccountList where accountName contains UPDATED_ACCOUNT_NAME
        defaultBankAccountShouldNotBeFound("accountName.contains=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountNameNotContainsSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountName does not contain DEFAULT_ACCOUNT_NAME
        defaultBankAccountShouldNotBeFound("accountName.doesNotContain=" + DEFAULT_ACCOUNT_NAME);

        // Get all the bankAccountList where accountName does not contain UPDATED_ACCOUNT_NAME
        defaultBankAccountShouldBeFound("accountName.doesNotContain=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountBankIsEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountBank equals to DEFAULT_ACCOUNT_BANK
        defaultBankAccountShouldBeFound("accountBank.equals=" + DEFAULT_ACCOUNT_BANK);

        // Get all the bankAccountList where accountBank equals to UPDATED_ACCOUNT_BANK
        defaultBankAccountShouldNotBeFound("accountBank.equals=" + UPDATED_ACCOUNT_BANK);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountBankIsInShouldWork() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountBank in DEFAULT_ACCOUNT_BANK or UPDATED_ACCOUNT_BANK
        defaultBankAccountShouldBeFound("accountBank.in=" + DEFAULT_ACCOUNT_BANK + "," + UPDATED_ACCOUNT_BANK);

        // Get all the bankAccountList where accountBank equals to UPDATED_ACCOUNT_BANK
        defaultBankAccountShouldNotBeFound("accountBank.in=" + UPDATED_ACCOUNT_BANK);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountBankIsNullOrNotNull() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountBank is not null
        defaultBankAccountShouldBeFound("accountBank.specified=true");

        // Get all the bankAccountList where accountBank is null
        defaultBankAccountShouldNotBeFound("accountBank.specified=false");
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountBankContainsSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountBank contains DEFAULT_ACCOUNT_BANK
        defaultBankAccountShouldBeFound("accountBank.contains=" + DEFAULT_ACCOUNT_BANK);

        // Get all the bankAccountList where accountBank contains UPDATED_ACCOUNT_BANK
        defaultBankAccountShouldNotBeFound("accountBank.contains=" + UPDATED_ACCOUNT_BANK);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountBankNotContainsSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountBank does not contain DEFAULT_ACCOUNT_BANK
        defaultBankAccountShouldNotBeFound("accountBank.doesNotContain=" + DEFAULT_ACCOUNT_BANK);

        // Get all the bankAccountList where accountBank does not contain UPDATED_ACCOUNT_BANK
        defaultBankAccountShouldBeFound("accountBank.doesNotContain=" + UPDATED_ACCOUNT_BANK);
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount equals to DEFAULT_INITIAL_AMOUNT
        defaultBankAccountShouldBeFound("initialAmount.equals=" + DEFAULT_INITIAL_AMOUNT);

        // Get all the bankAccountList where initialAmount equals to UPDATED_INITIAL_AMOUNT
        defaultBankAccountShouldNotBeFound("initialAmount.equals=" + UPDATED_INITIAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsInShouldWork() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount in DEFAULT_INITIAL_AMOUNT or UPDATED_INITIAL_AMOUNT
        defaultBankAccountShouldBeFound("initialAmount.in=" + DEFAULT_INITIAL_AMOUNT + "," + UPDATED_INITIAL_AMOUNT);

        // Get all the bankAccountList where initialAmount equals to UPDATED_INITIAL_AMOUNT
        defaultBankAccountShouldNotBeFound("initialAmount.in=" + UPDATED_INITIAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount is not null
        defaultBankAccountShouldBeFound("initialAmount.specified=true");

        // Get all the bankAccountList where initialAmount is null
        defaultBankAccountShouldNotBeFound("initialAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount is greater than or equal to DEFAULT_INITIAL_AMOUNT
        defaultBankAccountShouldBeFound("initialAmount.greaterThanOrEqual=" + DEFAULT_INITIAL_AMOUNT);

        // Get all the bankAccountList where initialAmount is greater than or equal to UPDATED_INITIAL_AMOUNT
        defaultBankAccountShouldNotBeFound("initialAmount.greaterThanOrEqual=" + UPDATED_INITIAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount is less than or equal to DEFAULT_INITIAL_AMOUNT
        defaultBankAccountShouldBeFound("initialAmount.lessThanOrEqual=" + DEFAULT_INITIAL_AMOUNT);

        // Get all the bankAccountList where initialAmount is less than or equal to SMALLER_INITIAL_AMOUNT
        defaultBankAccountShouldNotBeFound("initialAmount.lessThanOrEqual=" + SMALLER_INITIAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount is less than DEFAULT_INITIAL_AMOUNT
        defaultBankAccountShouldNotBeFound("initialAmount.lessThan=" + DEFAULT_INITIAL_AMOUNT);

        // Get all the bankAccountList where initialAmount is less than UPDATED_INITIAL_AMOUNT
        defaultBankAccountShouldBeFound("initialAmount.lessThan=" + UPDATED_INITIAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBankAccountsByInitialAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where initialAmount is greater than DEFAULT_INITIAL_AMOUNT
        defaultBankAccountShouldNotBeFound("initialAmount.greaterThan=" + DEFAULT_INITIAL_AMOUNT);

        // Get all the bankAccountList where initialAmount is greater than SMALLER_INITIAL_AMOUNT
        defaultBankAccountShouldBeFound("initialAmount.greaterThan=" + SMALLER_INITIAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBankAccountsByArchivedIsEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where archived equals to DEFAULT_ARCHIVED
        defaultBankAccountShouldBeFound("archived.equals=" + DEFAULT_ARCHIVED);

        // Get all the bankAccountList where archived equals to UPDATED_ARCHIVED
        defaultBankAccountShouldNotBeFound("archived.equals=" + UPDATED_ARCHIVED);
    }

    @Test
    @Transactional
    void getAllBankAccountsByArchivedIsInShouldWork() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where archived in DEFAULT_ARCHIVED or UPDATED_ARCHIVED
        defaultBankAccountShouldBeFound("archived.in=" + DEFAULT_ARCHIVED + "," + UPDATED_ARCHIVED);

        // Get all the bankAccountList where archived equals to UPDATED_ARCHIVED
        defaultBankAccountShouldNotBeFound("archived.in=" + UPDATED_ARCHIVED);
    }

    @Test
    @Transactional
    void getAllBankAccountsByArchivedIsNullOrNotNull() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where archived is not null
        defaultBankAccountShouldBeFound("archived.specified=true");

        // Get all the bankAccountList where archived is null
        defaultBankAccountShouldNotBeFound("archived.specified=false");
    }

    @Test
    @Transactional
    void getAllBankAccountsByShortNameIsEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where shortName equals to DEFAULT_SHORT_NAME
        defaultBankAccountShouldBeFound("shortName.equals=" + DEFAULT_SHORT_NAME);

        // Get all the bankAccountList where shortName equals to UPDATED_SHORT_NAME
        defaultBankAccountShouldNotBeFound("shortName.equals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByShortNameIsInShouldWork() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where shortName in DEFAULT_SHORT_NAME or UPDATED_SHORT_NAME
        defaultBankAccountShouldBeFound("shortName.in=" + DEFAULT_SHORT_NAME + "," + UPDATED_SHORT_NAME);

        // Get all the bankAccountList where shortName equals to UPDATED_SHORT_NAME
        defaultBankAccountShouldNotBeFound("shortName.in=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByShortNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where shortName is not null
        defaultBankAccountShouldBeFound("shortName.specified=true");

        // Get all the bankAccountList where shortName is null
        defaultBankAccountShouldNotBeFound("shortName.specified=false");
    }

    @Test
    @Transactional
    void getAllBankAccountsByShortNameContainsSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where shortName contains DEFAULT_SHORT_NAME
        defaultBankAccountShouldBeFound("shortName.contains=" + DEFAULT_SHORT_NAME);

        // Get all the bankAccountList where shortName contains UPDATED_SHORT_NAME
        defaultBankAccountShouldNotBeFound("shortName.contains=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByShortNameNotContainsSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where shortName does not contain DEFAULT_SHORT_NAME
        defaultBankAccountShouldNotBeFound("shortName.doesNotContain=" + DEFAULT_SHORT_NAME);

        // Get all the bankAccountList where shortName does not contain UPDATED_SHORT_NAME
        defaultBankAccountShouldBeFound("shortName.doesNotContain=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountType equals to DEFAULT_ACCOUNT_TYPE
        defaultBankAccountShouldBeFound("accountType.equals=" + DEFAULT_ACCOUNT_TYPE);

        // Get all the bankAccountList where accountType equals to UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldNotBeFound("accountType.equals=" + UPDATED_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountTypeIsInShouldWork() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountType in DEFAULT_ACCOUNT_TYPE or UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldBeFound("accountType.in=" + DEFAULT_ACCOUNT_TYPE + "," + UPDATED_ACCOUNT_TYPE);

        // Get all the bankAccountList where accountType equals to UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldNotBeFound("accountType.in=" + UPDATED_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        // Get all the bankAccountList where accountType is not null
        defaultBankAccountShouldBeFound("accountType.specified=true");

        // Get all the bankAccountList where accountType is null
        defaultBankAccountShouldNotBeFound("accountType.specified=false");
    }

    @Test
    @Transactional
    void getAllBankAccountsByAccountIsEqualToSomething() throws Exception {
        ApplicationUser account;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            bankAccountRepository.saveAndFlush(bankAccount);
            account = ApplicationUserResourceIT.createEntity(em);
        } else {
            account = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(account);
        em.flush();
        bankAccount.setAccount(account);
        bankAccountRepository.saveAndFlush(bankAccount);
        Long accountId = account.getId();

        // Get all the bankAccountList where account equals to accountId
        defaultBankAccountShouldBeFound("accountId.equals=" + accountId);

        // Get all the bankAccountList where account equals to (accountId + 1)
        defaultBankAccountShouldNotBeFound("accountId.equals=" + (accountId + 1));
    }

    @Test
    @Transactional
    void getAllBankAccountsByStockPortfolioItemIsEqualToSomething() throws Exception {
        StockPortfolioItem stockPortfolioItem;
        if (TestUtil.findAll(em, StockPortfolioItem.class).isEmpty()) {
            bankAccountRepository.saveAndFlush(bankAccount);
            stockPortfolioItem = StockPortfolioItemResourceIT.createEntity(em);
        } else {
            stockPortfolioItem = TestUtil.findAll(em, StockPortfolioItem.class).get(0);
        }
        em.persist(stockPortfolioItem);
        em.flush();
        bankAccount.addStockPortfolioItem(stockPortfolioItem);
        bankAccountRepository.saveAndFlush(bankAccount);
        Long stockPortfolioItemId = stockPortfolioItem.getId();

        // Get all the bankAccountList where stockPortfolioItem equals to stockPortfolioItemId
        defaultBankAccountShouldBeFound("stockPortfolioItemId.equals=" + stockPortfolioItemId);

        // Get all the bankAccountList where stockPortfolioItem equals to (stockPortfolioItemId + 1)
        defaultBankAccountShouldNotBeFound("stockPortfolioItemId.equals=" + (stockPortfolioItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBankAccountShouldBeFound(String filter) throws Exception {
        restBankAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountBank").value(hasItem(DEFAULT_ACCOUNT_BANK)))
            .andExpect(jsonPath("$.[*].initialAmount").value(hasItem(DEFAULT_INITIAL_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].archived").value(hasItem(DEFAULT_ARCHIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())));

        // Check, that the count call also returns 1
        restBankAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBankAccountShouldNotBeFound(String filter) throws Exception {
        restBankAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBankAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBankAccount() throws Exception {
        // Get the bankAccount
        restBankAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        bankAccountSearchRepository.save(bankAccount);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());

        // Update the bankAccount
        BankAccount updatedBankAccount = bankAccountRepository.findById(bankAccount.getId()).get();
        // Disconnect from session so that the updates on updatedBankAccount are not directly saved in db
        em.detach(updatedBankAccount);
        updatedBankAccount
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBank(UPDATED_ACCOUNT_BANK)
            .initialAmount(UPDATED_INITIAL_AMOUNT)
            .archived(UPDATED_ARCHIVED)
            .shortName(UPDATED_SHORT_NAME)
            .accountType(UPDATED_ACCOUNT_TYPE);
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(updatedBankAccount);

        restBankAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bankAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        BankAccount testBankAccount = bankAccountList.get(bankAccountList.size() - 1);
        assertThat(testBankAccount.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testBankAccount.getAccountBank()).isEqualTo(UPDATED_ACCOUNT_BANK);
        assertThat(testBankAccount.getInitialAmount()).isEqualTo(UPDATED_INITIAL_AMOUNT);
        assertThat(testBankAccount.getArchived()).isEqualTo(UPDATED_ARCHIVED);
        assertThat(testBankAccount.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testBankAccount.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BankAccount> bankAccountSearchList = IterableUtils.toList(bankAccountSearchRepository.findAll());
                BankAccount testBankAccountSearch = bankAccountSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBankAccountSearch.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
                assertThat(testBankAccountSearch.getAccountBank()).isEqualTo(UPDATED_ACCOUNT_BANK);
                assertThat(testBankAccountSearch.getInitialAmount()).isEqualTo(UPDATED_INITIAL_AMOUNT);
                assertThat(testBankAccountSearch.getArchived()).isEqualTo(UPDATED_ARCHIVED);
                assertThat(testBankAccountSearch.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
                assertThat(testBankAccountSearch.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
            });
    }

    @Test
    @Transactional
    void putNonExistingBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        bankAccount.setId(count.incrementAndGet());

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bankAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        bankAccount.setId(count.incrementAndGet());

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        bankAccount.setId(count.incrementAndGet());

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankAccountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBankAccountWithPatch() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();

        // Update the bankAccount using partial update
        BankAccount partialUpdatedBankAccount = new BankAccount();
        partialUpdatedBankAccount.setId(bankAccount.getId());

        partialUpdatedBankAccount
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBank(UPDATED_ACCOUNT_BANK)
            .archived(UPDATED_ARCHIVED)
            .accountType(UPDATED_ACCOUNT_TYPE);

        restBankAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankAccount))
            )
            .andExpect(status().isOk());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        BankAccount testBankAccount = bankAccountList.get(bankAccountList.size() - 1);
        assertThat(testBankAccount.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testBankAccount.getAccountBank()).isEqualTo(UPDATED_ACCOUNT_BANK);
        assertThat(testBankAccount.getInitialAmount()).isEqualTo(DEFAULT_INITIAL_AMOUNT);
        assertThat(testBankAccount.getArchived()).isEqualTo(UPDATED_ARCHIVED);
        assertThat(testBankAccount.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testBankAccount.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateBankAccountWithPatch() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);

        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();

        // Update the bankAccount using partial update
        BankAccount partialUpdatedBankAccount = new BankAccount();
        partialUpdatedBankAccount.setId(bankAccount.getId());

        partialUpdatedBankAccount
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBank(UPDATED_ACCOUNT_BANK)
            .initialAmount(UPDATED_INITIAL_AMOUNT)
            .archived(UPDATED_ARCHIVED)
            .shortName(UPDATED_SHORT_NAME)
            .accountType(UPDATED_ACCOUNT_TYPE);

        restBankAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankAccount))
            )
            .andExpect(status().isOk());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        BankAccount testBankAccount = bankAccountList.get(bankAccountList.size() - 1);
        assertThat(testBankAccount.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testBankAccount.getAccountBank()).isEqualTo(UPDATED_ACCOUNT_BANK);
        assertThat(testBankAccount.getInitialAmount()).isEqualTo(UPDATED_INITIAL_AMOUNT);
        assertThat(testBankAccount.getArchived()).isEqualTo(UPDATED_ARCHIVED);
        assertThat(testBankAccount.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testBankAccount.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        bankAccount.setId(count.incrementAndGet());

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bankAccountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        bankAccount.setId(count.incrementAndGet());

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        bankAccount.setId(count.incrementAndGet());

        // Create the BankAccount
        BankAccountDTO bankAccountDTO = bankAccountMapper.toDto(bankAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankAccountMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bankAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        bankAccountRepository.save(bankAccount);
        bankAccountSearchRepository.save(bankAccount);

        int databaseSizeBeforeDelete = bankAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bankAccount
        restBankAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, bankAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bankAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBankAccount() throws Exception {
        // Initialize the database
        bankAccount = bankAccountRepository.saveAndFlush(bankAccount);
        bankAccountSearchRepository.save(bankAccount);

        // Search the bankAccount
        restBankAccountMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + bankAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountBank").value(hasItem(DEFAULT_ACCOUNT_BANK)))
            .andExpect(jsonPath("$.[*].initialAmount").value(hasItem(DEFAULT_INITIAL_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].archived").value(hasItem(DEFAULT_ARCHIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())));
    }
}
