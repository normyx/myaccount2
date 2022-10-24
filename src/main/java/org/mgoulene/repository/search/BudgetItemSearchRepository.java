package org.mgoulene.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.repository.BudgetItemRepository;
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
 * Spring Data Elasticsearch repository for the {@link BudgetItem} entity.
 */
public interface BudgetItemSearchRepository extends ElasticsearchRepository<BudgetItem, Long>, BudgetItemSearchRepositoryInternal {}

interface BudgetItemSearchRepositoryInternal {
    Stream<BudgetItem> search(String query);

    Stream<BudgetItem> search(Query query);

    void index(BudgetItem entity);
}

class BudgetItemSearchRepositoryInternalImpl implements BudgetItemSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final BudgetItemRepository repository;

    BudgetItemSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, BudgetItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<BudgetItem> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<BudgetItem> search(Query query) {
        return elasticsearchTemplate.search(query, BudgetItem.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(BudgetItem entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
