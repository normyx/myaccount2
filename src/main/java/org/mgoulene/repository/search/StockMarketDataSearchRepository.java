package org.mgoulene.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.repository.StockMarketDataRepository;
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
 * Spring Data Elasticsearch repository for the {@link StockMarketData} entity.
 */
public interface StockMarketDataSearchRepository
    extends ElasticsearchRepository<StockMarketData, Long>, StockMarketDataSearchRepositoryInternal {}

interface StockMarketDataSearchRepositoryInternal {
    Page<StockMarketData> search(String query, Pageable pageable);

    Page<StockMarketData> search(Query query);

    void index(StockMarketData entity);
}

class StockMarketDataSearchRepositoryInternalImpl implements StockMarketDataSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final StockMarketDataRepository repository;

    StockMarketDataSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, StockMarketDataRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<StockMarketData> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<StockMarketData> search(Query query) {
        SearchHits<StockMarketData> searchHits = elasticsearchTemplate.search(query, StockMarketData.class);
        List<StockMarketData> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(StockMarketData entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
