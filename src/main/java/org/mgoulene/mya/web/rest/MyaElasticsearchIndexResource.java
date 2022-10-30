package org.mgoulene.mya.web.rest;

import com.codahale.metrics.annotation.Timed;
import java.net.URISyntaxException;
import java.util.List;
import org.mgoulene.mya.service.MyaElasticsearchIndexService;
import org.mgoulene.security.AuthoritiesConstants;
import org.mgoulene.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing Elasticsearch index.
 */
@RestController
@RequestMapping("/api")
public class MyaElasticsearchIndexResource {

    private final Logger log = LoggerFactory.getLogger(MyaElasticsearchIndexResource.class);

    private final MyaElasticsearchIndexService elasticsearchIndexService;

    public MyaElasticsearchIndexResource(MyaElasticsearchIndexService elasticsearchIndexService) {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    /**
     * POST  /elasticsearch/index -> Reindex all Elasticsearch documents
     */
    @PostMapping("/elasticsearch/index")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> reindexAll() throws URISyntaxException {
        log.info("REST request to reindex Elasticsearch by user : {}, all entities", SecurityUtils.getCurrentUserLogin());
        elasticsearchIndexService.reindexSelected(null, true);
        return ResponseEntity.accepted().headers(HeaderUtil.createAlert(applicationName, "elasticsearch.reindex.accepted", "")).build();
    }

    /**
     * POST  /elasticsearch/selected -> Reindex selected Elasticsearch documents
     */
    @PostMapping("/elasticsearch/selected")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> reindexSelected(@RequestBody List<String> selectedEntities) throws URISyntaxException {
        log.info("REST request to reindex Elasticsearch by user : {}, entities: {}", SecurityUtils.getCurrentUserLogin(), selectedEntities);
        elasticsearchIndexService.reindexSelected(selectedEntities, false);
        return ResponseEntity
            .accepted()
            .headers(HeaderUtil.createAlert(applicationName, "elasticsearch.reindex.acceptedSelected", ""))
            .build();
    }
}
