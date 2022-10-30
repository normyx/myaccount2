package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.domain.Operation;
import org.mgoulene.domain.SubCategory;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.dto.SubCategoryDTO;

/**
 * Mapper for the entity {@link Operation} and its DTO {@link OperationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OperationMapper extends EntityMapper<OperationDTO, Operation> {
    @Mapping(target = "subCategory", source = "subCategory", qualifiedByName = "subCategorySubCategoryName")
    @Mapping(target = "account", source = "account", qualifiedByName = "applicationUserNickName")
    @Mapping(target = "bankAccount", source = "bankAccount", qualifiedByName = "bankAccountId")
    @Mapping(target = "budgetItemPeriod", source = "budgetItemPeriod", qualifiedByName = "budgetItemPeriodId")
    OperationDTO toDto(Operation s);

    @Named("subCategorySubCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "subCategoryName", source = "subCategoryName")
    @Mapping(target = "category", source = "category")
    SubCategoryDTO toDtoSubCategorySubCategoryName(SubCategory subCategory);

    @Named("applicationUserNickName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nickName", source = "nickName")
    ApplicationUserDTO toDtoApplicationUserNickName(ApplicationUser applicationUser);

    @Named("bankAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    BankAccountDTO toDtoBankAccountId(BankAccount bankAccount);

    @Named("budgetItemPeriodId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BudgetItemPeriodDTO toDtoBudgetItemPeriodId(BudgetItemPeriod budgetItemPeriod);
}
