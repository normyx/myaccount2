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
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.domain.Operation;
import org.mgoulene.domain.SubCategory;
import org.mgoulene.repository.OperationRepository;
import org.mgoulene.repository.search.OperationSearchRepository;
import org.mgoulene.service.OperationService;
import org.mgoulene.service.criteria.OperationCriteria;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.mapper.OperationMapper;
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
 * Integration tests for the {@link OperationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OperationResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final Float DEFAULT_AMOUNT = 1F;
    private static final Float UPDATED_AMOUNT = 2F;
    private static final Float SMALLER_AMOUNT = 1F - 1F;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_CHECK_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CHECK_NUMBER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_UP_TO_DATE = false;
    private static final Boolean UPDATED_IS_UP_TO_DATE = true;

    private static final Boolean DEFAULT_DELETING_HARD_LOCK = false;
    private static final Boolean UPDATED_DELETING_HARD_LOCK = true;

    private static final String ENTITY_API_URL = "/api/operations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/operations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OperationRepository operationRepository;

    @Mock
    private OperationRepository operationRepositoryMock;

    @Autowired
    private OperationMapper operationMapper;

    @Mock
    private OperationService operationServiceMock;

    @Autowired
    private OperationSearchRepository operationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOperationMockMvc;

    private Operation operation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Operation createEntity(EntityManager em) {
        Operation operation = new Operation()
            .label(DEFAULT_LABEL)
            .date(DEFAULT_DATE)
            .amount(DEFAULT_AMOUNT)
            .note(DEFAULT_NOTE)
            .checkNumber(DEFAULT_CHECK_NUMBER)
            .isUpToDate(DEFAULT_IS_UP_TO_DATE)
            .deletingHardLock(DEFAULT_DELETING_HARD_LOCK);
        // Add required entity
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            bankAccount = BankAccountResourceIT.createEntity(em);
            em.persist(bankAccount);
            em.flush();
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        operation.setBankAccount(bankAccount);
        return operation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Operation createUpdatedEntity(EntityManager em) {
        Operation operation = new Operation()
            .label(UPDATED_LABEL)
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .note(UPDATED_NOTE)
            .checkNumber(UPDATED_CHECK_NUMBER)
            .isUpToDate(UPDATED_IS_UP_TO_DATE)
            .deletingHardLock(UPDATED_DELETING_HARD_LOCK);
        // Add required entity
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            bankAccount = BankAccountResourceIT.createUpdatedEntity(em);
            em.persist(bankAccount);
            em.flush();
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        operation.setBankAccount(bankAccount);
        return operation;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        operationSearchRepository.deleteAll();
        assertThat(operationSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        operation = createEntity(em);
    }

    @Test
    @Transactional
    void createOperation() throws Exception {
        int databaseSizeBeforeCreate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);
        restOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isCreated());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Operation testOperation = operationList.get(operationList.size() - 1);
        assertThat(testOperation.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testOperation.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOperation.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testOperation.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testOperation.getCheckNumber()).isEqualTo(DEFAULT_CHECK_NUMBER);
        assertThat(testOperation.getIsUpToDate()).isEqualTo(DEFAULT_IS_UP_TO_DATE);
        assertThat(testOperation.getDeletingHardLock()).isEqualTo(DEFAULT_DELETING_HARD_LOCK);
    }

    @Test
    @Transactional
    void createOperationWithExistingId() throws Exception {
        // Create the Operation with an existing ID
        operation.setId(1L);
        OperationDTO operationDTO = operationMapper.toDto(operation);

        int databaseSizeBeforeCreate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        // set the field null
        operation.setLabel(null);

        // Create the Operation, which fails.
        OperationDTO operationDTO = operationMapper.toDto(operation);

        restOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isBadRequest());

        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        // set the field null
        operation.setDate(null);

        // Create the Operation, which fails.
        OperationDTO operationDTO = operationMapper.toDto(operation);

        restOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isBadRequest());

        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        // set the field null
        operation.setAmount(null);

        // Create the Operation, which fails.
        OperationDTO operationDTO = operationMapper.toDto(operation);

        restOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isBadRequest());

        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsUpToDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        // set the field null
        operation.setIsUpToDate(null);

        // Create the Operation, which fails.
        OperationDTO operationDTO = operationMapper.toDto(operation);

        restOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isBadRequest());

        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOperations() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList
        restOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operation.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].checkNumber").value(hasItem(DEFAULT_CHECK_NUMBER)))
            .andExpect(jsonPath("$.[*].isUpToDate").value(hasItem(DEFAULT_IS_UP_TO_DATE.booleanValue())))
            .andExpect(jsonPath("$.[*].deletingHardLock").value(hasItem(DEFAULT_DELETING_HARD_LOCK.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOperationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(operationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOperationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(operationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOperationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(operationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOperationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(operationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get the operation
        restOperationMockMvc
            .perform(get(ENTITY_API_URL_ID, operation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(operation.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.checkNumber").value(DEFAULT_CHECK_NUMBER))
            .andExpect(jsonPath("$.isUpToDate").value(DEFAULT_IS_UP_TO_DATE.booleanValue()))
            .andExpect(jsonPath("$.deletingHardLock").value(DEFAULT_DELETING_HARD_LOCK.booleanValue()));
    }

    @Test
    @Transactional
    void getOperationsByIdFiltering() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        Long id = operation.getId();

        defaultOperationShouldBeFound("id.equals=" + id);
        defaultOperationShouldNotBeFound("id.notEquals=" + id);

        defaultOperationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOperationShouldNotBeFound("id.greaterThan=" + id);

        defaultOperationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOperationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOperationsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where label equals to DEFAULT_LABEL
        defaultOperationShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the operationList where label equals to UPDATED_LABEL
        defaultOperationShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllOperationsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultOperationShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the operationList where label equals to UPDATED_LABEL
        defaultOperationShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllOperationsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where label is not null
        defaultOperationShouldBeFound("label.specified=true");

        // Get all the operationList where label is null
        defaultOperationShouldNotBeFound("label.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsByLabelContainsSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where label contains DEFAULT_LABEL
        defaultOperationShouldBeFound("label.contains=" + DEFAULT_LABEL);

        // Get all the operationList where label contains UPDATED_LABEL
        defaultOperationShouldNotBeFound("label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllOperationsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where label does not contain DEFAULT_LABEL
        defaultOperationShouldNotBeFound("label.doesNotContain=" + DEFAULT_LABEL);

        // Get all the operationList where label does not contain UPDATED_LABEL
        defaultOperationShouldBeFound("label.doesNotContain=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date equals to DEFAULT_DATE
        defaultOperationShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the operationList where date equals to UPDATED_DATE
        defaultOperationShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date in DEFAULT_DATE or UPDATED_DATE
        defaultOperationShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the operationList where date equals to UPDATED_DATE
        defaultOperationShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date is not null
        defaultOperationShouldBeFound("date.specified=true");

        // Get all the operationList where date is null
        defaultOperationShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date is greater than or equal to DEFAULT_DATE
        defaultOperationShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the operationList where date is greater than or equal to UPDATED_DATE
        defaultOperationShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date is less than or equal to DEFAULT_DATE
        defaultOperationShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the operationList where date is less than or equal to SMALLER_DATE
        defaultOperationShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date is less than DEFAULT_DATE
        defaultOperationShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the operationList where date is less than UPDATED_DATE
        defaultOperationShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where date is greater than DEFAULT_DATE
        defaultOperationShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the operationList where date is greater than SMALLER_DATE
        defaultOperationShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount equals to DEFAULT_AMOUNT
        defaultOperationShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the operationList where amount equals to UPDATED_AMOUNT
        defaultOperationShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultOperationShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the operationList where amount equals to UPDATED_AMOUNT
        defaultOperationShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount is not null
        defaultOperationShouldBeFound("amount.specified=true");

        // Get all the operationList where amount is null
        defaultOperationShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultOperationShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the operationList where amount is greater than or equal to UPDATED_AMOUNT
        defaultOperationShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount is less than or equal to DEFAULT_AMOUNT
        defaultOperationShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the operationList where amount is less than or equal to SMALLER_AMOUNT
        defaultOperationShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount is less than DEFAULT_AMOUNT
        defaultOperationShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the operationList where amount is less than UPDATED_AMOUNT
        defaultOperationShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOperationsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where amount is greater than DEFAULT_AMOUNT
        defaultOperationShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the operationList where amount is greater than SMALLER_AMOUNT
        defaultOperationShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOperationsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where note equals to DEFAULT_NOTE
        defaultOperationShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the operationList where note equals to UPDATED_NOTE
        defaultOperationShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOperationsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultOperationShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the operationList where note equals to UPDATED_NOTE
        defaultOperationShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOperationsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where note is not null
        defaultOperationShouldBeFound("note.specified=true");

        // Get all the operationList where note is null
        defaultOperationShouldNotBeFound("note.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsByNoteContainsSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where note contains DEFAULT_NOTE
        defaultOperationShouldBeFound("note.contains=" + DEFAULT_NOTE);

        // Get all the operationList where note contains UPDATED_NOTE
        defaultOperationShouldNotBeFound("note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOperationsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where note does not contain DEFAULT_NOTE
        defaultOperationShouldNotBeFound("note.doesNotContain=" + DEFAULT_NOTE);

        // Get all the operationList where note does not contain UPDATED_NOTE
        defaultOperationShouldBeFound("note.doesNotContain=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOperationsByCheckNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where checkNumber equals to DEFAULT_CHECK_NUMBER
        defaultOperationShouldBeFound("checkNumber.equals=" + DEFAULT_CHECK_NUMBER);

        // Get all the operationList where checkNumber equals to UPDATED_CHECK_NUMBER
        defaultOperationShouldNotBeFound("checkNumber.equals=" + UPDATED_CHECK_NUMBER);
    }

    @Test
    @Transactional
    void getAllOperationsByCheckNumberIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where checkNumber in DEFAULT_CHECK_NUMBER or UPDATED_CHECK_NUMBER
        defaultOperationShouldBeFound("checkNumber.in=" + DEFAULT_CHECK_NUMBER + "," + UPDATED_CHECK_NUMBER);

        // Get all the operationList where checkNumber equals to UPDATED_CHECK_NUMBER
        defaultOperationShouldNotBeFound("checkNumber.in=" + UPDATED_CHECK_NUMBER);
    }

    @Test
    @Transactional
    void getAllOperationsByCheckNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where checkNumber is not null
        defaultOperationShouldBeFound("checkNumber.specified=true");

        // Get all the operationList where checkNumber is null
        defaultOperationShouldNotBeFound("checkNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsByCheckNumberContainsSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where checkNumber contains DEFAULT_CHECK_NUMBER
        defaultOperationShouldBeFound("checkNumber.contains=" + DEFAULT_CHECK_NUMBER);

        // Get all the operationList where checkNumber contains UPDATED_CHECK_NUMBER
        defaultOperationShouldNotBeFound("checkNumber.contains=" + UPDATED_CHECK_NUMBER);
    }

    @Test
    @Transactional
    void getAllOperationsByCheckNumberNotContainsSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where checkNumber does not contain DEFAULT_CHECK_NUMBER
        defaultOperationShouldNotBeFound("checkNumber.doesNotContain=" + DEFAULT_CHECK_NUMBER);

        // Get all the operationList where checkNumber does not contain UPDATED_CHECK_NUMBER
        defaultOperationShouldBeFound("checkNumber.doesNotContain=" + UPDATED_CHECK_NUMBER);
    }

    @Test
    @Transactional
    void getAllOperationsByIsUpToDateIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where isUpToDate equals to DEFAULT_IS_UP_TO_DATE
        defaultOperationShouldBeFound("isUpToDate.equals=" + DEFAULT_IS_UP_TO_DATE);

        // Get all the operationList where isUpToDate equals to UPDATED_IS_UP_TO_DATE
        defaultOperationShouldNotBeFound("isUpToDate.equals=" + UPDATED_IS_UP_TO_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByIsUpToDateIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where isUpToDate in DEFAULT_IS_UP_TO_DATE or UPDATED_IS_UP_TO_DATE
        defaultOperationShouldBeFound("isUpToDate.in=" + DEFAULT_IS_UP_TO_DATE + "," + UPDATED_IS_UP_TO_DATE);

        // Get all the operationList where isUpToDate equals to UPDATED_IS_UP_TO_DATE
        defaultOperationShouldNotBeFound("isUpToDate.in=" + UPDATED_IS_UP_TO_DATE);
    }

    @Test
    @Transactional
    void getAllOperationsByIsUpToDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where isUpToDate is not null
        defaultOperationShouldBeFound("isUpToDate.specified=true");

        // Get all the operationList where isUpToDate is null
        defaultOperationShouldNotBeFound("isUpToDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsByDeletingHardLockIsEqualToSomething() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where deletingHardLock equals to DEFAULT_DELETING_HARD_LOCK
        defaultOperationShouldBeFound("deletingHardLock.equals=" + DEFAULT_DELETING_HARD_LOCK);

        // Get all the operationList where deletingHardLock equals to UPDATED_DELETING_HARD_LOCK
        defaultOperationShouldNotBeFound("deletingHardLock.equals=" + UPDATED_DELETING_HARD_LOCK);
    }

    @Test
    @Transactional
    void getAllOperationsByDeletingHardLockIsInShouldWork() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where deletingHardLock in DEFAULT_DELETING_HARD_LOCK or UPDATED_DELETING_HARD_LOCK
        defaultOperationShouldBeFound("deletingHardLock.in=" + DEFAULT_DELETING_HARD_LOCK + "," + UPDATED_DELETING_HARD_LOCK);

        // Get all the operationList where deletingHardLock equals to UPDATED_DELETING_HARD_LOCK
        defaultOperationShouldNotBeFound("deletingHardLock.in=" + UPDATED_DELETING_HARD_LOCK);
    }

    @Test
    @Transactional
    void getAllOperationsByDeletingHardLockIsNullOrNotNull() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operationList where deletingHardLock is not null
        defaultOperationShouldBeFound("deletingHardLock.specified=true");

        // Get all the operationList where deletingHardLock is null
        defaultOperationShouldNotBeFound("deletingHardLock.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationsBySubCategoryIsEqualToSomething() throws Exception {
        SubCategory subCategory;
        if (TestUtil.findAll(em, SubCategory.class).isEmpty()) {
            operationRepository.saveAndFlush(operation);
            subCategory = SubCategoryResourceIT.createEntity(em);
        } else {
            subCategory = TestUtil.findAll(em, SubCategory.class).get(0);
        }
        em.persist(subCategory);
        em.flush();
        operation.setSubCategory(subCategory);
        operationRepository.saveAndFlush(operation);
        Long subCategoryId = subCategory.getId();

        // Get all the operationList where subCategory equals to subCategoryId
        defaultOperationShouldBeFound("subCategoryId.equals=" + subCategoryId);

        // Get all the operationList where subCategory equals to (subCategoryId + 1)
        defaultOperationShouldNotBeFound("subCategoryId.equals=" + (subCategoryId + 1));
    }

    @Test
    @Transactional
    void getAllOperationsByAccountIsEqualToSomething() throws Exception {
        ApplicationUser account;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            operationRepository.saveAndFlush(operation);
            account = ApplicationUserResourceIT.createEntity(em);
        } else {
            account = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(account);
        em.flush();
        operation.setAccount(account);
        operationRepository.saveAndFlush(operation);
        Long accountId = account.getId();

        // Get all the operationList where account equals to accountId
        defaultOperationShouldBeFound("accountId.equals=" + accountId);

        // Get all the operationList where account equals to (accountId + 1)
        defaultOperationShouldNotBeFound("accountId.equals=" + (accountId + 1));
    }

    @Test
    @Transactional
    void getAllOperationsByBankAccountIsEqualToSomething() throws Exception {
        BankAccount bankAccount;
        if (TestUtil.findAll(em, BankAccount.class).isEmpty()) {
            operationRepository.saveAndFlush(operation);
            bankAccount = BankAccountResourceIT.createEntity(em);
        } else {
            bankAccount = TestUtil.findAll(em, BankAccount.class).get(0);
        }
        em.persist(bankAccount);
        em.flush();
        operation.setBankAccount(bankAccount);
        operationRepository.saveAndFlush(operation);
        Long bankAccountId = bankAccount.getId();

        // Get all the operationList where bankAccount equals to bankAccountId
        defaultOperationShouldBeFound("bankAccountId.equals=" + bankAccountId);

        // Get all the operationList where bankAccount equals to (bankAccountId + 1)
        defaultOperationShouldNotBeFound("bankAccountId.equals=" + (bankAccountId + 1));
    }

    @Test
    @Transactional
    void getAllOperationsByBudgetItemPeriodIsEqualToSomething() throws Exception {
        BudgetItemPeriod budgetItemPeriod;
        if (TestUtil.findAll(em, BudgetItemPeriod.class).isEmpty()) {
            operationRepository.saveAndFlush(operation);
            budgetItemPeriod = BudgetItemPeriodResourceIT.createEntity(em);
        } else {
            budgetItemPeriod = TestUtil.findAll(em, BudgetItemPeriod.class).get(0);
        }
        em.persist(budgetItemPeriod);
        em.flush();
        operation.setBudgetItemPeriod(budgetItemPeriod);
        budgetItemPeriod.setOperation(operation);
        operationRepository.saveAndFlush(operation);
        Long budgetItemPeriodId = budgetItemPeriod.getId();

        // Get all the operationList where budgetItemPeriod equals to budgetItemPeriodId
        defaultOperationShouldBeFound("budgetItemPeriodId.equals=" + budgetItemPeriodId);

        // Get all the operationList where budgetItemPeriod equals to (budgetItemPeriodId + 1)
        defaultOperationShouldNotBeFound("budgetItemPeriodId.equals=" + (budgetItemPeriodId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOperationShouldBeFound(String filter) throws Exception {
        restOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operation.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].checkNumber").value(hasItem(DEFAULT_CHECK_NUMBER)))
            .andExpect(jsonPath("$.[*].isUpToDate").value(hasItem(DEFAULT_IS_UP_TO_DATE.booleanValue())))
            .andExpect(jsonPath("$.[*].deletingHardLock").value(hasItem(DEFAULT_DELETING_HARD_LOCK.booleanValue())));

        // Check, that the count call also returns 1
        restOperationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOperationShouldNotBeFound(String filter) throws Exception {
        restOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOperationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOperation() throws Exception {
        // Get the operation
        restOperationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        operationSearchRepository.save(operation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());

        // Update the operation
        Operation updatedOperation = operationRepository.findById(operation.getId()).get();
        // Disconnect from session so that the updates on updatedOperation are not directly saved in db
        em.detach(updatedOperation);
        updatedOperation
            .label(UPDATED_LABEL)
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .note(UPDATED_NOTE)
            .checkNumber(UPDATED_CHECK_NUMBER)
            .isUpToDate(UPDATED_IS_UP_TO_DATE)
            .deletingHardLock(UPDATED_DELETING_HARD_LOCK);
        OperationDTO operationDTO = operationMapper.toDto(updatedOperation);

        restOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        Operation testOperation = operationList.get(operationList.size() - 1);
        assertThat(testOperation.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testOperation.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOperation.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOperation.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testOperation.getCheckNumber()).isEqualTo(UPDATED_CHECK_NUMBER);
        assertThat(testOperation.getIsUpToDate()).isEqualTo(UPDATED_IS_UP_TO_DATE);
        assertThat(testOperation.getDeletingHardLock()).isEqualTo(UPDATED_DELETING_HARD_LOCK);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Operation> operationSearchList = IterableUtils.toList(operationSearchRepository.findAll());
                Operation testOperationSearch = operationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOperationSearch.getLabel()).isEqualTo(UPDATED_LABEL);
                assertThat(testOperationSearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testOperationSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
                assertThat(testOperationSearch.getNote()).isEqualTo(UPDATED_NOTE);
                assertThat(testOperationSearch.getCheckNumber()).isEqualTo(UPDATED_CHECK_NUMBER);
                assertThat(testOperationSearch.getIsUpToDate()).isEqualTo(UPDATED_IS_UP_TO_DATE);
                assertThat(testOperationSearch.getDeletingHardLock()).isEqualTo(UPDATED_DELETING_HARD_LOCK);
            });
    }

    @Test
    @Transactional
    void putNonExistingOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        operation.setId(count.incrementAndGet());

        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        operation.setId(count.incrementAndGet());

        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        operation.setId(count.incrementAndGet());

        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(operationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOperationWithPatch() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        int databaseSizeBeforeUpdate = operationRepository.findAll().size();

        // Update the operation using partial update
        Operation partialUpdatedOperation = new Operation();
        partialUpdatedOperation.setId(operation.getId());

        partialUpdatedOperation.note(UPDATED_NOTE).isUpToDate(UPDATED_IS_UP_TO_DATE);

        restOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOperation))
            )
            .andExpect(status().isOk());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        Operation testOperation = operationList.get(operationList.size() - 1);
        assertThat(testOperation.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testOperation.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOperation.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testOperation.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testOperation.getCheckNumber()).isEqualTo(DEFAULT_CHECK_NUMBER);
        assertThat(testOperation.getIsUpToDate()).isEqualTo(UPDATED_IS_UP_TO_DATE);
        assertThat(testOperation.getDeletingHardLock()).isEqualTo(DEFAULT_DELETING_HARD_LOCK);
    }

    @Test
    @Transactional
    void fullUpdateOperationWithPatch() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        int databaseSizeBeforeUpdate = operationRepository.findAll().size();

        // Update the operation using partial update
        Operation partialUpdatedOperation = new Operation();
        partialUpdatedOperation.setId(operation.getId());

        partialUpdatedOperation
            .label(UPDATED_LABEL)
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .note(UPDATED_NOTE)
            .checkNumber(UPDATED_CHECK_NUMBER)
            .isUpToDate(UPDATED_IS_UP_TO_DATE)
            .deletingHardLock(UPDATED_DELETING_HARD_LOCK);

        restOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOperation))
            )
            .andExpect(status().isOk());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        Operation testOperation = operationList.get(operationList.size() - 1);
        assertThat(testOperation.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testOperation.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOperation.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOperation.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testOperation.getCheckNumber()).isEqualTo(UPDATED_CHECK_NUMBER);
        assertThat(testOperation.getIsUpToDate()).isEqualTo(UPDATED_IS_UP_TO_DATE);
        assertThat(testOperation.getDeletingHardLock()).isEqualTo(UPDATED_DELETING_HARD_LOCK);
    }

    @Test
    @Transactional
    void patchNonExistingOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        operation.setId(count.incrementAndGet());

        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, operationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(operationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        operation.setId(count.incrementAndGet());

        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(operationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        operation.setId(count.incrementAndGet());

        // Create the Operation
        OperationDTO operationDTO = operationMapper.toDto(operation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(operationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        operationRepository.save(operation);
        operationSearchRepository.save(operation);

        int databaseSizeBeforeDelete = operationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the operation
        restOperationMockMvc
            .perform(delete(ENTITY_API_URL_ID, operation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOperation() throws Exception {
        // Initialize the database
        operation = operationRepository.saveAndFlush(operation);
        operationSearchRepository.save(operation);

        // Search the operation
        restOperationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + operation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operation.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].checkNumber").value(hasItem(DEFAULT_CHECK_NUMBER)))
            .andExpect(jsonPath("$.[*].isUpToDate").value(hasItem(DEFAULT_IS_UP_TO_DATE.booleanValue())))
            .andExpect(jsonPath("$.[*].deletingHardLock").value(hasItem(DEFAULT_DELETING_HARD_LOCK.booleanValue())));
    }
}
