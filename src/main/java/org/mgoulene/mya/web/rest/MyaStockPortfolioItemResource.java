package org.mgoulene.mya.web.rest;

import java.util.Optional;
import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.mgoulene.mya.service.dto.MyaDateDataStockPoints;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    /**
     * {@code GET  /stock-portfolio-items} : get all the stockPortfolioItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of stockPortfolioItems in body.
     */
    /*
     * @GetMapping(
     * "/mya-stock-portfolio-items/evolution-data-points-for-symbol/{symbol}")
     * public ResponseEntity<MyaMarketDataAggregator.MarketShareDataPoints>
     * getSymbolStockPortfolioItemEvolutionDataPoints(
     *
     * @PathVariable String symbol) {
     * log.debug("REST request to getAllStockPortfolioItemEvolutionDataPoints: {}",
     * symbol);
     * Optional<ApplicationUserDTO> applicationUserOptional =
     * applicationUserService.findSignedInApplicationUser();
     * if (applicationUserOptional.isPresent()) {
     * MyaMarketDataAggregator.MarketShareDataPoints dataPoints =
     * myaStockMarketDataService
     * .findMarketDataPointsForSymbol(symbol,
     * applicationUserOptional.get().getId());
     *
     * return ResponseEntity.ok().body(dataPoints);
     * }
     * return null;
     * }
     */

    /*
     * @GetMapping("/mya-stock-portfolio-items/evolution-data-points2")
     * public ResponseEntity<MyaDateDataStockPoints> getDummy() {
     * log.debug("REST request to getAllStockPortfolioItemEvolutionDataPoints: {}");
     * Optional<ApplicationUserDTO> applicationUserOptional =
     * applicationUserService.findSignedInApplicationUser();
     * if (applicationUserOptional.isPresent()) {
     * MyaDateDataStockPoints dataPoints = myaStockMarketDataService
     * .findDateDataPoints(applicationUserOptional.get().getId());
     *
     * return ResponseEntity.ok().body(dataPoints);
     * }
     * return null;
     * }
     */

}
