package org.mgoulene.mya.service;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mgoulene.mya.service.alphavantage.MyaAlphaVantageActivityStackService;
import org.mgoulene.mya.service.alphavantage.MyaAlphaVantageActivityStackService.ActivityType;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyaAlphaVantageService {

    private final Logger log = LoggerFactory.getLogger(MyaAlphaVantageService.class);

    private final MyaStockPortfolioItemService stockPortfolioItemService;

    private final MyaAlphaVantageActivityStackService myaAlphaVantageActivityStackService;

    public MyaAlphaVantageService(
        MyaStockPortfolioItemService stockPortfolioItemService,
        MyaAlphaVantageActivityStackService myaAlphaVantageActivityStackService
    ) {
        this.stockPortfolioItemService = stockPortfolioItemService;
        this.myaAlphaVantageActivityStackService = myaAlphaVantageActivityStackService;
    }

    public void updateStockPortfolioItem(Long stockPortfolioItemId) {
        log.debug("Service to Update StockPortfolioItem : {}", stockPortfolioItemId);
        Optional<StockPortfolioItemDTO> item = stockPortfolioItemService.findOne(stockPortfolioItemId);
        if (item.isPresent()) {
            myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_STOCK_CURRENT, item.get());
            myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_STOCK_CURRENT_CURRENCY, item.get());
        }
    }

    public void updateAllStockPortfolioItem() {
        List<StockPortfolioItemDTO> stockPortfolioItemDTOs = stockPortfolioItemService.findAll();
        for (StockPortfolioItemDTO spi : stockPortfolioItemDTOs) {
            this.updateStockPortfolioItem(spi.getId());
        }
    }
}
