package org.mgoulene.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class RealEstateItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RealEstateItem.class);
        RealEstateItem realEstateItem1 = new RealEstateItem();
        realEstateItem1.setId(1L);
        RealEstateItem realEstateItem2 = new RealEstateItem();
        realEstateItem2.setId(realEstateItem1.getId());
        assertThat(realEstateItem1).isEqualTo(realEstateItem2);
        realEstateItem2.setId(2L);
        assertThat(realEstateItem1).isNotEqualTo(realEstateItem2);
        realEstateItem1.setId(null);
        assertThat(realEstateItem1).isNotEqualTo(realEstateItem2);
    }
}
