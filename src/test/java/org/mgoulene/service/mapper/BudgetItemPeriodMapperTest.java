package org.mgoulene.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BudgetItemPeriodMapperTest {

    private BudgetItemPeriodMapper budgetItemPeriodMapper;

    @BeforeEach
    public void setUp() {
        budgetItemPeriodMapper = new BudgetItemPeriodMapperImpl();
    }
}
