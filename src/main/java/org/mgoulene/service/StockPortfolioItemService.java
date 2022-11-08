package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.mgoulene.repository.search.StockPortfolioItemSearchRepository;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.mgoulene.service.mapper.StockPortfolioItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockPortfolioItem}.
 */
@Service
@Transactional
public class StockPortfolioItemService {

    private final Logger log = LoggerFactory.getLogger(StockPortfolioItemService.class);

    protected final StockPortfolioItemRepository stockPortfolioItemRepository;

    protected final StockPortfolioItemMapper stockPortfolioItemMapper;

    private final StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository;

    public StockPortfolioItemService(
        StockPortfolioItemRepository stockPortfolioItemRepository,
        StockPortfolioItemMapper stockPortfolioItemMapper,
        StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository
    ) {
        this.stockPortfolioItemRepository = stockPortfolioItemRepository;
        this.stockPortfolioItemMapper = stockPortfolioItemMapper;
        this.stockPortfolioItemSearchRepository = stockPortfolioItemSearchRepository;
    }

    /**
     * Save a stockPortfolioItem.
     *
     * @param stockPortfolioItemDTO the entity to save.
     * @return the persisted entity.
     */
    public StockPortfolioItemDTO save(StockPortfolioItemDTO stockPortfolioItemDTO) {
        log.debug("Request to save StockPortfolioItem : {}", stockPortfolioItemDTO);
        StockPortfolioItem stockPortfolioItem = stockPortfolioItemMapper.toEntity(stockPortfolioItemDTO);
        stockPortfolioItem = stockPortfolioItemRepository.save(stockPortfolioItem);
        StockPortfolioItemDTO result = stockPortfolioItemMapper.toDto(stockPortfolioItem);
        stockPortfolioItemSearchRepository.index(stockPortfolioItem);
        return result;
    }

    /**
     * Update a stockPortfolioItem.
     *
     * @param stockPortfolioItemDTO the entity to save.
     * @return the persisted entity.
     */
    public StockPortfolioItemDTO update(StockPortfolioItemDTO stockPortfolioItemDTO) {
        log.debug("Request to update StockPortfolioItem : {}", stockPortfolioItemDTO);
        StockPortfolioItem stockPortfolioItem = stockPortfolioItemMapper.toEntity(stockPortfolioItemDTO);
        stockPortfolioItem = stockPortfolioItemRepository.save(stockPortfolioItem);
        StockPortfolioItemDTO result = stockPortfolioItemMapper.toDto(stockPortfolioItem);
        stockPortfolioItemSearchRepository.index(stockPortfolioItem);
        return result;
    }

    /**
     * Partially update a stockPortfolioItem.
     *
     * @param stockPortfolioItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockPortfolioItemDTO> partialUpdate(StockPortfolioItemDTO stockPortfolioItemDTO) {
        log.debug("Request to partially update StockPortfolioItem : {}", stockPortfolioItemDTO);

        return stockPortfolioItemRepository
            .findById(stockPortfolioItemDTO.getId())
            .map(existingStockPortfolioItem -> {
                stockPortfolioItemMapper.partialUpdate(existingStockPortfolioItem, stockPortfolioItemDTO);

                return existingStockPortfolioItem;
            })
            .map(stockPortfolioItemRepository::save)
            .map(savedStockPortfolioItem -> {
                stockPortfolioItemSearchRepository.save(savedStockPortfolioItem);

                return savedStockPortfolioItem;
            })
            .map(stockPortfolioItemMapper::toDto);
    }

    /**
     * Get all the stockPortfolioItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockPortfolioItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockPortfolioItems");
        return stockPortfolioItemRepository.findAll(pageable).map(stockPortfolioItemMapper::toDto);
    }

    /**
     * Get all the stockPortfolioItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockPortfolioItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockPortfolioItemRepository.findAllWithEagerRelationships(pageable).map(stockPortfolioItemMapper::toDto);
    }

    /**
     * Get one stockPortfolioItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockPortfolioItemDTO> findOne(Long id) {
        log.debug("Request to get StockPortfolioItem : {}", id);
        return stockPortfolioItemRepository.findOneWithEagerRelationships(id).map(stockPortfolioItemMapper::toDto);
    }

    /**
     * Delete the stockPortfolioItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockPortfolioItem : {}", id);
        stockPortfolioItemRepository.deleteById(id);
        stockPortfolioItemSearchRepository.deleteById(id);
    }

    /**
     * Search for the stockPortfolioItem corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockPortfolioItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of StockPortfolioItems for query {}", query);
        return stockPortfolioItemSearchRepository.search(query, pageable).map(stockPortfolioItemMapper::toDto);
    }
}
