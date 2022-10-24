package org.mgoulene.mya.repository;

import java.util.Optional;
import org.mgoulene.domain.ApplicationUser;
import org.mgoulene.domain.User;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyaApplicationUserRepository extends JpaRepository<ApplicationUser, Long>, JpaSpecificationExecutor<ApplicationUser> {
    @Query("select unique applicationUser from ApplicationUser applicationUser where applicationUser.user =: user")
    Optional<ApplicationUser> findOneByUser(@Param("user") User user);
}
