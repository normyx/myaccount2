package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.domain.RealEstateItem;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.dto.RealEstateItemDTO;

/**
 * Mapper for the entity {@link RealEstateItem} and its DTO {@link RealEstateItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface RealEstateItemMapper extends EntityMapper<RealEstateItemDTO, RealEstateItem> {
    @Mapping(target = "bankAccount", source = "bankAccount", qualifiedByName = "bankAccountAccountName")
    RealEstateItemDTO toDto(RealEstateItem s);

    @Named("bankAccountAccountName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    BankAccountDTO toDtoBankAccountAccountName(BankAccount bankAccount);
}
