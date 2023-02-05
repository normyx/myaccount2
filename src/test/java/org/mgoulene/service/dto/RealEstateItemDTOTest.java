package org.mgoulene.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class RealEstateItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RealEstateItemDTO.class);
        RealEstateItemDTO realEstateItemDTO1 = new RealEstateItemDTO();
        realEstateItemDTO1.setId(1L);
        RealEstateItemDTO realEstateItemDTO2 = new RealEstateItemDTO();
        assertThat(realEstateItemDTO1).isNotEqualTo(realEstateItemDTO2);
        realEstateItemDTO2.setId(realEstateItemDTO1.getId());
        assertThat(realEstateItemDTO1).isEqualTo(realEstateItemDTO2);
        realEstateItemDTO2.setId(2L);
        assertThat(realEstateItemDTO1).isNotEqualTo(realEstateItemDTO2);
        realEstateItemDTO1.setId(null);
        assertThat(realEstateItemDTO1).isNotEqualTo(realEstateItemDTO2);
    }
}
