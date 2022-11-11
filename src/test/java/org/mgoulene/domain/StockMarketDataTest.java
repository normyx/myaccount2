package org.mgoulene.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class StockMarketDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockMarketData.class);
        StockMarketData stockMarketData1 = new StockMarketData();
        stockMarketData1.setId(1L);
        StockMarketData stockMarketData2 = new StockMarketData();
        stockMarketData2.setId(stockMarketData1.getId());
        assertThat(stockMarketData1).isEqualTo(stockMarketData2);
        stockMarketData2.setId(2L);
        assertThat(stockMarketData1).isNotEqualTo(stockMarketData2);
        stockMarketData1.setId(null);
        assertThat(stockMarketData1).isNotEqualTo(stockMarketData2);
    }
}
