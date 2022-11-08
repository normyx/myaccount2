package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BankAccountDTO;

/**
 * Mapper for the entity {@link BankAccount} and its DTO {@link BankAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface BankAccountMapper extends EntityMapper<BankAccountDTO, BankAccount> {
    @Mapping(target = "account", source = "account", qualifiedByName = "applicationUserNickName")
    BankAccountDTO toDto(BankAccount s);

    @Named("applicationUserNickName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nickName", source = "nickName")
    ApplicationUserDTO toDtoApplicationUserNickName(ApplicationUser applicationUser);
}
