package org.mgoulene.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class BudgetItemPeriodTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BudgetItemPeriod.class);
        BudgetItemPeriod budgetItemPeriod1 = new BudgetItemPeriod();
        budgetItemPeriod1.setId(1L);
        BudgetItemPeriod budgetItemPeriod2 = new BudgetItemPeriod();
        budgetItemPeriod2.setId(budgetItemPeriod1.getId());
        assertThat(budgetItemPeriod1).isEqualTo(budgetItemPeriod2);
        budgetItemPeriod2.setId(2L);
        assertThat(budgetItemPeriod1).isNotEqualTo(budgetItemPeriod2);
        budgetItemPeriod1.setId(null);
        assertThat(budgetItemPeriod1).isNotEqualTo(budgetItemPeriod2);
    }
}
