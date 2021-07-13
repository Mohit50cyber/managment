package com.moglix.ems.repository;

import com.moglix.ems.entities.ApplicationUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<ApplicationUser, Long> {

    @Transactional
    Optional<ApplicationUser> findOneByEmail(String email);
}
