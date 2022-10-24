package org.mgoulene.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BudgetItemMapperTest {

    private BudgetItemMapper budgetItemMapper;

    @BeforeEach
    public void setUp() {
        budgetItemMapper = new BudgetItemMapperImpl();
    }
}
