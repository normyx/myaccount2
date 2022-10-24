package org.mgoulene.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubCategoryMapperTest {

    private SubCategoryMapper subCategoryMapper;

    @BeforeEach
    public void setUp() {
        subCategoryMapper = new SubCategoryMapperImpl();
    }
}
