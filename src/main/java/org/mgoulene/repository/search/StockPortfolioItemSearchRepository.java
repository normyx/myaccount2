package org.mgoulene.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link StockPortfolioItem} entity.
 */
public interface StockPortfolioItemSearchRepository
    extends ElasticsearchRepository<StockPortfolioItem, Long>, StockPortfolioItemSearchRepositoryInternal {}

interface StockPortfolioItemSearchRepositoryInternal {
    Page<StockPortfolioItem> search(String query, Pageable pageable);

    Page<StockPortfolioItem> search(Query query);

    void index(StockPortfolioItem entity);
}

class StockPortfolioItemSearchRepositoryInternalImpl implements StockPortfolioItemSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final StockPortfolioItemRepository repository;

    StockPortfolioItemSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        StockPortfolioItemRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<StockPortfolioItem> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<StockPortfolioItem> search(Query query) {
        SearchHits<StockPortfolioItem> searchHits = elasticsearchTemplate.search(query, StockPortfolioItem.class);
        List<StockPortfolioItem> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(StockPortfolioItem entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
