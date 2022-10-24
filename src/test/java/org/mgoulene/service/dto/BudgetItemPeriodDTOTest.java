package org.mgoulene.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class BudgetItemPeriodDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BudgetItemPeriodDTO.class);
        BudgetItemPeriodDTO budgetItemPeriodDTO1 = new BudgetItemPeriodDTO();
        budgetItemPeriodDTO1.setId(1L);
        BudgetItemPeriodDTO budgetItemPeriodDTO2 = new BudgetItemPeriodDTO();
        assertThat(budgetItemPeriodDTO1).isNotEqualTo(budgetItemPeriodDTO2);
        budgetItemPeriodDTO2.setId(budgetItemPeriodDTO1.getId());
        assertThat(budgetItemPeriodDTO1).isEqualTo(budgetItemPeriodDTO2);
        budgetItemPeriodDTO2.setId(2L);
        assertThat(budgetItemPeriodDTO1).isNotEqualTo(budgetItemPeriodDTO2);
        budgetItemPeriodDTO1.setId(null);
        assertThat(budgetItemPeriodDTO1).isNotEqualTo(budgetItemPeriodDTO2);
    }
}
