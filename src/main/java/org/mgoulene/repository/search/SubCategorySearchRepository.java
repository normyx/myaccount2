package org.mgoulene.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.stream.Stream;
import org.mgoulene.domain.SubCategory;
import org.mgoulene.repository.SubCategoryRepository;
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
 * Spring Data Elasticsearch repository for the {@link SubCategory} entity.
 */
public interface SubCategorySearchRepository extends ElasticsearchRepository<SubCategory, Long>, SubCategorySearchRepositoryInternal {}

interface SubCategorySearchRepositoryInternal {
    Stream<SubCategory> search(String query);

    Stream<SubCategory> search(Query query);

    void index(SubCategory entity);
}

class SubCategorySearchRepositoryInternalImpl implements SubCategorySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final SubCategoryRepository repository;

    SubCategorySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, SubCategoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<SubCategory> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<SubCategory> search(Query query) {
        return elasticsearchTemplate.search(query, SubCategory.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(SubCategory entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
