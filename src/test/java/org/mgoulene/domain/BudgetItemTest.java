package org.mgoulene.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class BudgetItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BudgetItem.class);
        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setId(1L);
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(budgetItem1.getId());
        assertThat(budgetItem1).isEqualTo(budgetItem2);
        budgetItem2.setId(2L);
        assertThat(budgetItem1).isNotEqualTo(budgetItem2);
        budgetItem1.setId(null);
        assertThat(budgetItem1).isNotEqualTo(budgetItem2);
    }
}
