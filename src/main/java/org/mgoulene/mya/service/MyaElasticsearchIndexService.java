package org.mgoulene.mya.service;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import javax.persistence.ManyToMany;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.mgoulene.domain.*;
import org.mgoulene.repository.*;
import org.mgoulene.repository.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MyaElasticsearchIndexService implements ApplicationListener<ContextRefreshedEvent> {

    private static final Lock reindexLock = new ReentrantLock();

    private final Logger log = LoggerFactory.getLogger(MyaElasticsearchIndexService.class);

    private final ApplicationUserRepository applicationUserRepository;

    private final ApplicationUserSearchRepository applicationUserSearchRepository;

    private final BankAccountRepository bankAccountRepository;

    private final BankAccountSearchRepository bankAccountSearchRepository;

    private final BudgetItemRepository budgetItemRepository;

    private final BudgetItemSearchRepository budgetItemSearchRepository;

    private final BudgetItemPeriodRepository budgetItemPeriodRepository;

    private final BudgetItemPeriodSearchRepository budgetItemPeriodSearchRepository;

    private final CategoryRepository categoryRepository;

    private final CategorySearchRepository categorySearchRepository;

    private final OperationRepository operationRepository;

    private final OperationSearchRepository operationSearchRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final SubCategorySearchRepository subCategorySearchRepository;

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final ElasticsearchOperations elasticsearchTemplate;

    @Value("#{systemEnvironment['REINDEX_ELASTICSEARCH'] ?: ''}")
    private String reindex_elasticsearch;

    public MyaElasticsearchIndexService(
        UserRepository userRepository,
        UserSearchRepository userSearchRepository,
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserSearchRepository applicationUserSearchRepository,
        BankAccountRepository bankAccountRepository,
        BankAccountSearchRepository bankAccountSearchRepository,
        BudgetItemRepository budgetItemRepository,
        BudgetItemSearchRepository budgetItemSearchRepository,
        BudgetItemPeriodRepository budgetItemPeriodRepository,
        BudgetItemPeriodSearchRepository budgetItemPeriodSearchRepository,
        CategoryRepository categoryRepository,
        CategorySearchRepository categorySearchRepository,
        OperationRepository operationRepository,
        OperationSearchRepository operationSearchRepository,
        SubCategoryRepository subCategoryRepository,
        SubCategorySearchRepository subCategorySearchRepository,
        ElasticsearchOperations elasticsearchTemplate
    ) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserSearchRepository = applicationUserSearchRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountSearchRepository = bankAccountSearchRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemSearchRepository = budgetItemSearchRepository;
        this.budgetItemPeriodRepository = budgetItemPeriodRepository;
        this.budgetItemPeriodSearchRepository = budgetItemPeriodSearchRepository;
        this.categoryRepository = categoryRepository;
        this.categorySearchRepository = categorySearchRepository;
        this.operationRepository = operationRepository;
        this.operationSearchRepository = operationSearchRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.subCategorySearchRepository = subCategorySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (this.reindex_elasticsearch.isEmpty()) {
            log.info("You may reindex all ElasticSearch entities on start-up with the REINDEX_ELASTICSEARCH environment variable set");
        } else {
            log.info("Reindexing all ElasticSearch entities");
            this.reindexSelected(null, true);
        }
    }

    @Async
    @Timed
    public void reindexSelected(List<String> classesForReindex, boolean all) {
        if (reindexLock.tryLock()) {
            try {
                if (all || classesForReindex.contains("ApplicationUser")) {
                    reindexForClass(ApplicationUser.class, applicationUserRepository, applicationUserSearchRepository);
                }
                if (all || classesForReindex.contains("BankAccount")) {
                    reindexForClass(BankAccount.class, bankAccountRepository, bankAccountSearchRepository);
                }
                if (all || classesForReindex.contains("BudgetItem")) {
                    reindexForClass(BudgetItem.class, budgetItemRepository, budgetItemSearchRepository);
                }
                if (all || classesForReindex.contains("BudgetItemPeriod")) {
                    reindexForClass(BudgetItemPeriod.class, budgetItemPeriodRepository, budgetItemPeriodSearchRepository);
                }
                if (all || classesForReindex.contains("Category")) {
                    reindexForClass(Category.class, categoryRepository, categorySearchRepository);
                }
                if (all || classesForReindex.contains("Operation")) {
                    reindexForClass(Operation.class, operationRepository, operationSearchRepository);
                }
                if (all || classesForReindex.contains("SubCategory")) {
                    reindexForClass(SubCategory.class, subCategoryRepository, subCategorySearchRepository);
                }
                if (all || classesForReindex.contains("User")) {
                    reindexForClass(User.class, userRepository, userSearchRepository);
                }

                log.info("Elasticsearch: Successfully performed reindexing");
            } finally {
                reindexLock.unlock();
            }
        } else {
            log.info("Elasticsearch: concurrent reindexing attempt");
        }
    }

    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(
        Class<T> entityClass,
        JpaRepository<T, ID> jpaRepository,
        ElasticsearchRepository<T, ID> elasticsearchRepository
    ) {
        elasticsearchRepository.deleteAll();
        String className = entityClass.getSimpleName();
        /*elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (ResourceAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);*/
        if (jpaRepository.count() > 0) {
            // if a JHipster entity field is the owner side of a many-to-many relationship, it should be loaded manually
            List<Method> relationshipGetters = Arrays
                .stream(entityClass.getDeclaredFields())
                .filter(field -> field.getType().equals(Set.class))
                .filter(field -> field.getAnnotation(ManyToMany.class) != null)
                .filter(field -> field.getAnnotation(ManyToMany.class).mappedBy().isEmpty())
                .filter(field -> field.getAnnotation(JsonIgnore.class) == null)
                .map(field -> {
                    try {
                        return new PropertyDescriptor(field.getName(), entityClass).getReadMethod();
                    } catch (IntrospectionException e) {
                        log.error(
                            "Error retrieving getter for class {}, field {}. Field will NOT be indexed",
                            className,
                            field.getName(),
                            e
                        );
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            int size = 100;
            for (int i = 0; i <= jpaRepository.count() / size; i++) {
                Pageable page = PageRequest.of(i, size);
                log.info("Indexing {} page {} of {}, size {}", className, i, jpaRepository.count() / size, size);
                Page<T> results = jpaRepository.findAll(page);
                results.map(result -> {
                    // if there are any relationships to load, do it now
                    relationshipGetters.forEach(method -> {
                        try {
                            // eagerly load the relationship set
                            ((Set) method.invoke(result)).size();
                        } catch (Exception ex) {
                            log.error(ex.getMessage());
                        }
                    });
                    return result;
                });
                elasticsearchRepository.saveAll(results.getContent());
            }
        }
        log.info("Elasticsearch: Indexed all rows for {}", className);
    }
}
