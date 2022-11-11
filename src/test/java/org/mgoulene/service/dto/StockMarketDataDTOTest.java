package org.mgoulene.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class StockMarketDataDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockMarketDataDTO.class);
        StockMarketDataDTO stockMarketDataDTO1 = new StockMarketDataDTO();
        stockMarketDataDTO1.setId(1L);
        StockMarketDataDTO stockMarketDataDTO2 = new StockMarketDataDTO();
        assertThat(stockMarketDataDTO1).isNotEqualTo(stockMarketDataDTO2);
        stockMarketDataDTO2.setId(stockMarketDataDTO1.getId());
        assertThat(stockMarketDataDTO1).isEqualTo(stockMarketDataDTO2);
        stockMarketDataDTO2.setId(2L);
        assertThat(stockMarketDataDTO1).isNotEqualTo(stockMarketDataDTO2);
        stockMarketDataDTO1.setId(null);
        assertThat(stockMarketDataDTO1).isNotEqualTo(stockMarketDataDTO2);
    }
}
