package org.mgoulene.mya.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.mgoulene.repository.search.StockPortfolioItemSearchRepository;
import org.mgoulene.service.StockPortfolioItemService;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.mgoulene.service.mapper.StockPortfolioItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockPortfolioItem}.
 */
@Service
@Transactional
public class MyaStockPortfolioItemService extends StockPortfolioItemService {

    private final Logger log = LoggerFactory.getLogger(MyaStockPortfolioItemService.class);

    public MyaStockPortfolioItemService(
        StockPortfolioItemRepository stockPortfolioItemRepository,
        StockPortfolioItemMapper stockPortfolioItemMapper,
        StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository
    ) {
        super(stockPortfolioItemRepository, stockPortfolioItemMapper, stockPortfolioItemSearchRepository);
    }

    /**
     * Get all the stockPortfolioItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockPortfolioItemDTO> findAll() {
        log.debug("Request to get all StockPortfolioItems");
        return stockPortfolioItemRepository
            .findAll()
            .stream()
            .map(stockPortfolioItemMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
