package org.mgoulene.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.repository.BudgetItemPeriodRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link BudgetItemPeriod} entity.
 */
public interface BudgetItemPeriodSearchRepository
    extends ElasticsearchRepository<BudgetItemPeriod, Long>, BudgetItemPeriodSearchRepositoryInternal {}

interface BudgetItemPeriodSearchRepositoryInternal {
    Stream<BudgetItemPeriod> search(String query);

    Stream<BudgetItemPeriod> search(Query query);

    void index(BudgetItemPeriod entity);
}

class BudgetItemPeriodSearchRepositoryInternalImpl implements BudgetItemPeriodSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final BudgetItemPeriodRepository repository;

    BudgetItemPeriodSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, BudgetItemPeriodRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<BudgetItemPeriod> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<BudgetItemPeriod> search(Query query) {
        return elasticsearchTemplate.search(query, BudgetItemPeriod.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(BudgetItemPeriod entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
