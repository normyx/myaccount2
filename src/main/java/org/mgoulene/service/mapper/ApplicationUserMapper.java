package org.mgoulene.service.mapper;

import org.mapstruct.*;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.User;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.UserDTO;

/**
 * Mapper for the entity {@link ApplicationUser} and its DTO {@link ApplicationUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApplicationUserMapper extends EntityMapper<ApplicationUserDTO, ApplicationUser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    ApplicationUserDTO toDto(ApplicationUser s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
