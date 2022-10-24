package org.mgoulene.mya.service;

import java.util.Optional;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.User;
import org.mgoulene.mya.repository.MyaApplicationUserRepository;
import org.mgoulene.repository.ApplicationUserRepository;
import org.mgoulene.repository.search.ApplicationUserSearchRepository;
import org.mgoulene.service.ApplicationUserService;
import org.mgoulene.service.UserService;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.mapper.ApplicationUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ApplicationUser}.
 */
@Service
@Transactional
public class MyaApplicationUserService extends ApplicationUserService {

    private final Logger log = LoggerFactory.getLogger(MyaApplicationUserService.class);

    private final MyaApplicationUserRepository myaApplicationUserRepository;

    private final UserService userService;

    public MyaApplicationUserService(
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserMapper applicationUserMapper,
        ApplicationUserSearchRepository applicationUserSearchRepository,
        MyaApplicationUserRepository myaApplicationUserRepository,
        UserService userService
    ) {
        super(applicationUserRepository, applicationUserMapper, applicationUserSearchRepository);
        this.myaApplicationUserRepository = myaApplicationUserRepository;
        this.userService = userService;
    }

    /**
     * Get one applicationUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApplicationUserDTO> findOneWhereUser(User user) {
        log.debug("Request to get ApplicationUser : {}", user);
        return myaApplicationUserRepository.findOneByUser(user).map(applicationUserMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ApplicationUserDTO> findSignedInApplicationUser() {
        log.debug("Request to get Signed In ApplicationUser :");
        Optional<User> userOptional = userService.getUserWithAuthorities();
        if (userOptional.isPresent()) {
            return findOneWhereUser(userOptional.get());
        } else {
            return Optional.empty();
        }
    }
}
