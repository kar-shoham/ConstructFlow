package com.constructflow.user_service.repository;

import com.constructflow.user_service.entity.CFUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CFUserRepository
        extends JpaRepository<CFUser, Long>
{
    Optional<CFUser> findCFUserByUsername(String username);

    Optional<CFUser> findCFUserByEmail(String email);
}
