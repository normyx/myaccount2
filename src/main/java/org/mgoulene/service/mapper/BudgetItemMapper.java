package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.domain.Category;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.dto.CategoryDTO;

/**
 * Mapper for the entity {@link BudgetItem} and its DTO {@link BudgetItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface BudgetItemMapper extends EntityMapper<BudgetItemDTO, BudgetItem> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryCategoryName")
    @Mapping(target = "account", source = "account", qualifiedByName = "applicationUserNickName")
    BudgetItemDTO toDto(BudgetItem s);

    @Named("categoryCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryName", source = "categoryName")
    CategoryDTO toDtoCategoryCategoryName(Category category);

    @Named("applicationUserNickName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nickName", source = "nickName")
    ApplicationUserDTO toDtoApplicationUserNickName(ApplicationUser applicationUser);
}
