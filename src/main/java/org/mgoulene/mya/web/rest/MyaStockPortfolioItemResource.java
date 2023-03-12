package org.mgoulene.mya.web.rest;

import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link org.mgoulene.domain.StockPortfolioItem}.
 */
@RestController
@RequestMapping("/api")
public class MyaStockPortfolioItemResource {

    private final Logger log = LoggerFactory.getLogger(MyaStockPortfolioItemResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MyaStockPortfolioItemService myaStockPortfolioItemService;

    private final MyaApplicationUserService applicationUserService;

    public MyaStockPortfolioItemResource(
        MyaStockPortfolioItemService myaStockPortfolioItemService,
        MyaApplicationUserService applicationUserService
    ) {
        this.myaStockPortfolioItemService = myaStockPortfolioItemService;
        this.applicationUserService = applicationUserService;
    }
}
