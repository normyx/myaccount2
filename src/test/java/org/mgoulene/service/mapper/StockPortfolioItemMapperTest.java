package org.mgoulene.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockPortfolioItemMapperTest {

    private StockPortfolioItemMapper stockPortfolioItemMapper;

    @BeforeEach
    public void setUp() {
        stockPortfolioItemMapper = new StockPortfolioItemMapperImpl();
    }
}
