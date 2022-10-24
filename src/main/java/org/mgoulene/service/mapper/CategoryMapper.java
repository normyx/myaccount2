package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.Category;
import org.mgoulene.service.dto.CategoryDTO;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {}
