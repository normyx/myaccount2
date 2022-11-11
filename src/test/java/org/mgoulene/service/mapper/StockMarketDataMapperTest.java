package org.mgoulene.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockMarketDataMapperTest {

    private StockMarketDataMapper stockMarketDataMapper;

    @BeforeEach
    public void setUp() {
        stockMarketDataMapper = new StockMarketDataMapperImpl();
    }
}
