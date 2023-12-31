package org.mgoulene.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mgoulene.web.rest.TestUtil;

class SubCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubCategory.class);
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setId(1L);
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setId(subCategory1.getId());
        assertThat(subCategory1).isEqualTo(subCategory2);
        subCategory2.setId(2L);
        assertThat(subCategory1).isNotEqualTo(subCategory2);
        subCategory1.setId(null);
        assertThat(subCategory1).isNotEqualTo(subCategory2);
    }
}
