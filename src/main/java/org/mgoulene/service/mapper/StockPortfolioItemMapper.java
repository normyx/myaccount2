package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.dto.StockPortfolioItemDTO;

/**
 * Mapper for the entity {@link StockPortfolioItem} and its DTO {@link StockPortfolioItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockPortfolioItemMapper extends EntityMapper<StockPortfolioItemDTO, StockPortfolioItem> {
    @Mapping(target = "bankAccount", source = "bankAccount", qualifiedByName = "bankAccountAccountName")
    StockPortfolioItemDTO toDto(StockPortfolioItem s);

    @Named("bankAccountAccountName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    BankAccountDTO toDtoBankAccountAccountName(BankAccount bankAccount);
}
