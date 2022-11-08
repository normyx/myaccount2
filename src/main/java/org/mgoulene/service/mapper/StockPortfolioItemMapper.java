package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.service.dto.StockPortfolioItemDTO;

/**
 * Mapper for the entity {@link StockPortfolioItem} and its DTO {@link StockPortfolioItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockPortfolioItemMapper extends EntityMapper<StockPortfolioItemDTO, StockPortfolioItem> {}
