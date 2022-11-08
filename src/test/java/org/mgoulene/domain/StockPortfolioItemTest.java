package org.mgoulene.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class StockPortfolioItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockPortfolioItem.class);
        StockPortfolioItem stockPortfolioItem1 = new StockPortfolioItem();
        stockPortfolioItem1.setId(1L);
        StockPortfolioItem stockPortfolioItem2 = new StockPortfolioItem();
        stockPortfolioItem2.setId(stockPortfolioItem1.getId());
        assertThat(stockPortfolioItem1).isEqualTo(stockPortfolioItem2);
        stockPortfolioItem2.setId(2L);
        assertThat(stockPortfolioItem1).isNotEqualTo(stockPortfolioItem2);
        stockPortfolioItem1.setId(null);
        assertThat(stockPortfolioItem1).isNotEqualTo(stockPortfolioItem2);
    }
}
