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
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.domain.Category;
import org.mgoulene.repository.BudgetItemRepository;
import org.mgoulene.repository.search.BudgetItemSearchRepository;
import org.mgoulene.service.BudgetItemService;
import org.mgoulene.service.criteria.BudgetItemCriteria;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.mapper.BudgetItemMapper;
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
 * Integration tests for the {@link BudgetItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BudgetItemResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;
    private static final Integer SMALLER_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/budget-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/budget-items";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Mock
    private BudgetItemRepository budgetItemRepositoryMock;

    @Autowired
    private BudgetItemMapper budgetItemMapper;

    @Mock
    private BudgetItemService budgetItemServiceMock;

    @Autowired
    private BudgetItemSearchRepository budgetItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBudgetItemMockMvc;

    private BudgetItem budgetItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BudgetItem createEntity(EntityManager em) {
        BudgetItem budgetItem = new BudgetItem().name(DEFAULT_NAME).order(DEFAULT_ORDER);
        return budgetItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BudgetItem createUpdatedEntity(EntityManager em) {
        BudgetItem budgetItem = new BudgetItem().name(UPDATED_NAME).order(UPDATED_ORDER);
        return budgetItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        budgetItemSearchRepository.deleteAll();
        assertThat(budgetItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        budgetItem = createEntity(em);
    }

    @Test
    @Transactional
    void createBudgetItem() throws Exception {
        int databaseSizeBeforeCreate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);
        restBudgetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isCreated());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BudgetItem testBudgetItem = budgetItemList.get(budgetItemList.size() - 1);
        assertThat(testBudgetItem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBudgetItem.getOrder()).isEqualTo(DEFAULT_ORDER);
    }

    @Test
    @Transactional
    void createBudgetItemWithExistingId() throws Exception {
        // Create the BudgetItem with an existing ID
        budgetItem.setId(1L);
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        int databaseSizeBeforeCreate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBudgetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        // set the field null
        budgetItem.setName(null);

        // Create the BudgetItem, which fails.
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        restBudgetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        // set the field null
        budgetItem.setOrder(null);

        // Create the BudgetItem, which fails.
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        restBudgetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBudgetItems() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList
        restBudgetItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBudgetItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(budgetItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBudgetItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(budgetItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBudgetItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(budgetItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBudgetItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(budgetItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBudgetItem() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get the budgetItem
        restBudgetItemMockMvc
            .perform(get(ENTITY_API_URL_ID, budgetItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(budgetItem.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    void getBudgetItemsByIdFiltering() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        Long id = budgetItem.getId();

        defaultBudgetItemShouldBeFound("id.equals=" + id);
        defaultBudgetItemShouldNotBeFound("id.notEquals=" + id);

        defaultBudgetItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBudgetItemShouldNotBeFound("id.greaterThan=" + id);

        defaultBudgetItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBudgetItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name equals to DEFAULT_NAME
        defaultBudgetItemShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the budgetItemList where name equals to UPDATED_NAME
        defaultBudgetItemShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBudgetItemShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the budgetItemList where name equals to UPDATED_NAME
        defaultBudgetItemShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name is not null
        defaultBudgetItemShouldBeFound("name.specified=true");

        // Get all the budgetItemList where name is null
        defaultBudgetItemShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemsByNameContainsSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name contains DEFAULT_NAME
        defaultBudgetItemShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the budgetItemList where name contains UPDATED_NAME
        defaultBudgetItemShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name does not contain DEFAULT_NAME
        defaultBudgetItemShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the budgetItemList where name does not contain UPDATED_NAME
        defaultBudgetItemShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order equals to DEFAULT_ORDER
        defaultBudgetItemShouldBeFound("order.equals=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order equals to UPDATED_ORDER
        defaultBudgetItemShouldNotBeFound("order.equals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order in DEFAULT_ORDER or UPDATED_ORDER
        defaultBudgetItemShouldBeFound("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER);

        // Get all the budgetItemList where order equals to UPDATED_ORDER
        defaultBudgetItemShouldNotBeFound("order.in=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order is not null
        defaultBudgetItemShouldBeFound("order.specified=true");

        // Get all the budgetItemList where order is null
        defaultBudgetItemShouldNotBeFound("order.specified=false");
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order is greater than or equal to DEFAULT_ORDER
        defaultBudgetItemShouldBeFound("order.greaterThanOrEqual=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order is greater than or equal to UPDATED_ORDER
        defaultBudgetItemShouldNotBeFound("order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order is less than or equal to DEFAULT_ORDER
        defaultBudgetItemShouldBeFound("order.lessThanOrEqual=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order is less than or equal to SMALLER_ORDER
        defaultBudgetItemShouldNotBeFound("order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order is less than DEFAULT_ORDER
        defaultBudgetItemShouldNotBeFound("order.lessThan=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order is less than UPDATED_ORDER
        defaultBudgetItemShouldBeFound("order.lessThan=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order is greater than DEFAULT_ORDER
        defaultBudgetItemShouldNotBeFound("order.greaterThan=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order is greater than SMALLER_ORDER
        defaultBudgetItemShouldBeFound("order.greaterThan=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllBudgetItemsByBudgetItemPeriodsIsEqualToSomething() throws Exception {
        BudgetItemPeriod budgetItemPeriods;
        if (TestUtil.findAll(em, BudgetItemPeriod.class).isEmpty()) {
            budgetItemRepository.saveAndFlush(budgetItem);
            budgetItemPeriods = BudgetItemPeriodResourceIT.createEntity(em);
        } else {
            budgetItemPeriods = TestUtil.findAll(em, BudgetItemPeriod.class).get(0);
        }
        em.persist(budgetItemPeriods);
        em.flush();
        budgetItem.addBudgetItemPeriods(budgetItemPeriods);
        budgetItemRepository.saveAndFlush(budgetItem);
        Long budgetItemPeriodsId = budgetItemPeriods.getId();

        // Get all the budgetItemList where budgetItemPeriods equals to budgetItemPeriodsId
        defaultBudgetItemShouldBeFound("budgetItemPeriodsId.equals=" + budgetItemPeriodsId);

        // Get all the budgetItemList where budgetItemPeriods equals to (budgetItemPeriodsId + 1)
        defaultBudgetItemShouldNotBeFound("budgetItemPeriodsId.equals=" + (budgetItemPeriodsId + 1));
    }

    @Test
    @Transactional
    void getAllBudgetItemsByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            budgetItemRepository.saveAndFlush(budgetItem);
            category = CategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        budgetItem.setCategory(category);
        budgetItemRepository.saveAndFlush(budgetItem);
        Long categoryId = category.getId();

        // Get all the budgetItemList where category equals to categoryId
        defaultBudgetItemShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the budgetItemList where category equals to (categoryId + 1)
        defaultBudgetItemShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllBudgetItemsByAccountIsEqualToSomething() throws Exception {
        ApplicationUser account;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            budgetItemRepository.saveAndFlush(budgetItem);
            account = ApplicationUserResourceIT.createEntity(em);
        } else {
            account = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(account);
        em.flush();
        budgetItem.setAccount(account);
        budgetItemRepository.saveAndFlush(budgetItem);
        Long accountId = account.getId();

        // Get all the budgetItemList where account equals to accountId
        defaultBudgetItemShouldBeFound("accountId.equals=" + accountId);

        // Get all the budgetItemList where account equals to (accountId + 1)
        defaultBudgetItemShouldNotBeFound("accountId.equals=" + (accountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBudgetItemShouldBeFound(String filter) throws Exception {
        restBudgetItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));

        // Check, that the count call also returns 1
        restBudgetItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBudgetItemShouldNotBeFound(String filter) throws Exception {
        restBudgetItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBudgetItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBudgetItem() throws Exception {
        // Get the budgetItem
        restBudgetItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBudgetItem() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        budgetItemSearchRepository.save(budgetItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());

        // Update the budgetItem
        BudgetItem updatedBudgetItem = budgetItemRepository.findById(budgetItem.getId()).get();
        // Disconnect from session so that the updates on updatedBudgetItem are not directly saved in db
        em.detach(updatedBudgetItem);
        updatedBudgetItem.name(UPDATED_NAME).order(UPDATED_ORDER);
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(updatedBudgetItem);

        restBudgetItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, budgetItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        BudgetItem testBudgetItem = budgetItemList.get(budgetItemList.size() - 1);
        assertThat(testBudgetItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBudgetItem.getOrder()).isEqualTo(UPDATED_ORDER);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BudgetItem> budgetItemSearchList = IterableUtils.toList(budgetItemSearchRepository.findAll());
                BudgetItem testBudgetItemSearch = budgetItemSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBudgetItemSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testBudgetItemSearch.getOrder()).isEqualTo(UPDATED_ORDER);
            });
    }

    @Test
    @Transactional
    void putNonExistingBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        budgetItem.setId(count.incrementAndGet());

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, budgetItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        budgetItem.setId(count.incrementAndGet());

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        budgetItem.setId(count.incrementAndGet());

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBudgetItemWithPatch() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();

        // Update the budgetItem using partial update
        BudgetItem partialUpdatedBudgetItem = new BudgetItem();
        partialUpdatedBudgetItem.setId(budgetItem.getId());

        partialUpdatedBudgetItem.name(UPDATED_NAME);

        restBudgetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBudgetItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBudgetItem))
            )
            .andExpect(status().isOk());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        BudgetItem testBudgetItem = budgetItemList.get(budgetItemList.size() - 1);
        assertThat(testBudgetItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBudgetItem.getOrder()).isEqualTo(DEFAULT_ORDER);
    }

    @Test
    @Transactional
    void fullUpdateBudgetItemWithPatch() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();

        // Update the budgetItem using partial update
        BudgetItem partialUpdatedBudgetItem = new BudgetItem();
        partialUpdatedBudgetItem.setId(budgetItem.getId());

        partialUpdatedBudgetItem.name(UPDATED_NAME).order(UPDATED_ORDER);

        restBudgetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBudgetItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBudgetItem))
            )
            .andExpect(status().isOk());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        BudgetItem testBudgetItem = budgetItemList.get(budgetItemList.size() - 1);
        assertThat(testBudgetItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBudgetItem.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    void patchNonExistingBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        budgetItem.setId(count.incrementAndGet());

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, budgetItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        budgetItem.setId(count.incrementAndGet());

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        budgetItem.setId(count.incrementAndGet());

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(budgetItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBudgetItem() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);
        budgetItemRepository.save(budgetItem);
        budgetItemSearchRepository.save(budgetItem);

        int databaseSizeBeforeDelete = budgetItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the budgetItem
        restBudgetItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, budgetItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(budgetItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBudgetItem() throws Exception {
        // Initialize the database
        budgetItem = budgetItemRepository.saveAndFlush(budgetItem);
        budgetItemSearchRepository.save(budgetItem);

        // Search the budgetItem
        restBudgetItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + budgetItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }
}
