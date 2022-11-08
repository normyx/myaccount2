package org.mgoulene.mya.service.alphavantage;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.enumeration.StockType;
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.mgoulene.mya.service.alphavantage.MyaAlphaVantageActivityStackService.ActivityType;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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
        Optional<StockPortfolioItemDTO> stockOpt = stockPortfolioItemService.findOne(stockPortfolioItemId);
        if (stockOpt.isPresent()) {
            StockPortfolioItemDTO stockPortfolioItemDTO = stockOpt.get();
            if (stockPortfolioItemDTO.getStockType() == StockType.STOCK) {
                myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_STOCK_PRICES, stockPortfolioItemDTO);
                myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_STOCK_CURRENCIES, stockPortfolioItemDTO);
            } else if (stockPortfolioItemDTO.getStockType() == StockType.CRYPTO) {
                myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_CRYPTO, stockPortfolioItemDTO);
            }
        }
    }

    @Scheduled(fixedRateString = "${alphavantage.update-scheduled-fixe-rate}")
    public void updateAllStockPortfolioItem() {
        List<StockPortfolioItemDTO> stockPortfolioItemDTOs = stockPortfolioItemService.findAll();
        for (StockPortfolioItemDTO spi : stockPortfolioItemDTOs) {
            this.updateStockPortfolioItem(spi.getId());
        }
    }
}
