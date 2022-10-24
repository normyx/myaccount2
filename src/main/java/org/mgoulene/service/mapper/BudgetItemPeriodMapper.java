package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.domain.Operation;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.dto.OperationDTO;

/**
 * Mapper for the entity {@link BudgetItemPeriod} and its DTO {@link BudgetItemPeriodDTO}.
 */
@Mapper(componentModel = "spring")
public interface BudgetItemPeriodMapper extends EntityMapper<BudgetItemPeriodDTO, BudgetItemPeriod> {
    @Mapping(target = "operation", source = "operation", qualifiedByName = "operationId")
    @Mapping(target = "budgetItem", source = "budgetItem", qualifiedByName = "budgetItemId")
    BudgetItemPeriodDTO toDto(BudgetItemPeriod s);

    @Named("operationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OperationDTO toDtoOperationId(Operation operation);

    @Named("budgetItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BudgetItemDTO toDtoBudgetItemId(BudgetItem budgetItem);
}
