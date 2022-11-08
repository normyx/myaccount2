package org.mgoulene.mya.web.rest;

import org.mgoulene.mya.service.MyaAlphaVantageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MyaStockResource {

    private final Logger log = LoggerFactory.getLogger(MyaStockResource.class);
    private final MyaAlphaVantageService myaAlphaVantageService;

    public MyaStockResource(MyaAlphaVantageService myaAlphaVantageService) {
        this.myaAlphaVantageService = myaAlphaVantageService;
    }

    @GetMapping(value = { "/mya-stock/updateAll" })
    public ResponseEntity<Void> updateStockPortfolioItem() {
        log.debug("REST request to update all StockPortfolioItem");
        myaAlphaVantageService.updateAllStockPortfolioItem();
        return ResponseEntity.ok().build();
    }
}
