package org.mgoulene.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class StockPortfolioItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockPortfolioItemDTO.class);
        StockPortfolioItemDTO stockPortfolioItemDTO1 = new StockPortfolioItemDTO();
        stockPortfolioItemDTO1.setId(1L);
        StockPortfolioItemDTO stockPortfolioItemDTO2 = new StockPortfolioItemDTO();
        assertThat(stockPortfolioItemDTO1).isNotEqualTo(stockPortfolioItemDTO2);
        stockPortfolioItemDTO2.setId(stockPortfolioItemDTO1.getId());
        assertThat(stockPortfolioItemDTO1).isEqualTo(stockPortfolioItemDTO2);
        stockPortfolioItemDTO2.setId(2L);
        assertThat(stockPortfolioItemDTO1).isNotEqualTo(stockPortfolioItemDTO2);
        stockPortfolioItemDTO1.setId(null);
        assertThat(stockPortfolioItemDTO1).isNotEqualTo(stockPortfolioItemDTO2);
    }
}
