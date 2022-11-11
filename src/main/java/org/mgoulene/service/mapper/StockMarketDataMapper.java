package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.service.dto.StockMarketDataDTO;

/**
 * Mapper for the entity {@link StockMarketData} and its DTO {@link StockMarketDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMarketDataMapper extends EntityMapper<StockMarketDataDTO, StockMarketData> {}
