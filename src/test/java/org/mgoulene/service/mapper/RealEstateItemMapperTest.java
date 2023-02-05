package org.mgoulene.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RealEstateItemMapperTest {

    private RealEstateItemMapper realEstateItemMapper;

    @BeforeEach
    public void setUp() {
        realEstateItemMapper = new RealEstateItemMapperImpl();
    }
}
