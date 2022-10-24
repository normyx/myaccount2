package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.Category;
import org.mgoulene.domain.SubCategory;
import org.mgoulene.service.dto.CategoryDTO;
import org.mgoulene.service.dto.SubCategoryDTO;

/**
 * Mapper for the entity {@link SubCategory} and its DTO {@link SubCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubCategoryMapper extends EntityMapper<SubCategoryDTO, SubCategory> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryCategoryName")
    SubCategoryDTO toDto(SubCategory s);

    @Named("categoryCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryName", source = "categoryName")
    CategoryDTO toDtoCategoryCategoryName(Category category);
}
