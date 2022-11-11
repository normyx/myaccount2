package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.repository.StockMarketDataRepository;
import org.mgoulene.repository.search.StockMarketDataSearchRepository;
import org.mgoulene.service.dto.StockMarketDataDTO;
import org.mgoulene.service.mapper.StockMarketDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockMarketData}.
 */
@Service
@Transactional
public class StockMarketDataService {

    private final Logger log = LoggerFactory.getLogger(StockMarketDataService.class);

    private final StockMarketDataRepository stockMarketDataRepository;

    private final StockMarketDataMapper stockMarketDataMapper;

    private final StockMarketDataSearchRepository stockMarketDataSearchRepository;

    public StockMarketDataService(
        StockMarketDataRepository stockMarketDataRepository,
        StockMarketDataMapper stockMarketDataMapper,
        StockMarketDataSearchRepository stockMarketDataSearchRepository
    ) {
        this.stockMarketDataRepository = stockMarketDataRepository;
        this.stockMarketDataMapper = stockMarketDataMapper;
        this.stockMarketDataSearchRepository = stockMarketDataSearchRepository;
    }

    /**
     * Save a stockMarketData.
     *
     * @param stockMarketDataDTO the entity to save.
     * @return the persisted entity.
     */
    public StockMarketDataDTO save(StockMarketDataDTO stockMarketDataDTO) {
        log.debug("Request to save StockMarketData : {}", stockMarketDataDTO);
        StockMarketData stockMarketData = stockMarketDataMapper.toEntity(stockMarketDataDTO);
        stockMarketData = stockMarketDataRepository.save(stockMarketData);
        StockMarketDataDTO result = stockMarketDataMapper.toDto(stockMarketData);
        stockMarketDataSearchRepository.index(stockMarketData);
        return result;
    }

    /**
     * Update a stockMarketData.
     *
     * @param stockMarketDataDTO the entity to save.
     * @return the persisted entity.
     */
    public StockMarketDataDTO update(StockMarketDataDTO stockMarketDataDTO) {
        log.debug("Request to update StockMarketData : {}", stockMarketDataDTO);
        StockMarketData stockMarketData = stockMarketDataMapper.toEntity(stockMarketDataDTO);
        stockMarketData = stockMarketDataRepository.save(stockMarketData);
        StockMarketDataDTO result = stockMarketDataMapper.toDto(stockMarketData);
        stockMarketDataSearchRepository.index(stockMarketData);
        return result;
    }

    /**
     * Partially update a stockMarketData.
     *
     * @param stockMarketDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockMarketDataDTO> partialUpdate(StockMarketDataDTO stockMarketDataDTO) {
        log.debug("Request to partially update StockMarketData : {}", stockMarketDataDTO);

        return stockMarketDataRepository
            .findById(stockMarketDataDTO.getId())
            .map(existingStockMarketData -> {
                stockMarketDataMapper.partialUpdate(existingStockMarketData, stockMarketDataDTO);

                return existingStockMarketData;
            })
            .map(stockMarketDataRepository::save)
            .map(savedStockMarketData -> {
                stockMarketDataSearchRepository.save(savedStockMarketData);

                return savedStockMarketData;
            })
            .map(stockMarketDataMapper::toDto);
    }

    /**
     * Get all the stockMarketData.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockMarketDataDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockMarketData");
        return stockMarketDataRepository.findAll(pageable).map(stockMarketDataMapper::toDto);
    }

    /**
     * Get one stockMarketData by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockMarketDataDTO> findOne(Long id) {
        log.debug("Request to get StockMarketData : {}", id);
        return stockMarketDataRepository.findById(id).map(stockMarketDataMapper::toDto);
    }

    /**
     * Delete the stockMarketData by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockMarketData : {}", id);
        stockMarketDataRepository.deleteById(id);
        stockMarketDataSearchRepository.deleteById(id);
    }

    /**
     * Search for the stockMarketData corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockMarketDataDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of StockMarketData for query {}", query);
        return stockMarketDataSearchRepository.search(query, pageable).map(stockMarketDataMapper::toDto);
    }
}
