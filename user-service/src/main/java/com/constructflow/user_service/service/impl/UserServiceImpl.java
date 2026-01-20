package com.constructflow.user_service.service.impl;

import com.constructflow.user_service.entity.CFUser;
import com.constructflow.user_service.repository.CFUserRepository;
import com.constructflow.user_service.service.UserService;
import com.constructflow.user_service.exception.MandatoryFieldsMissingException;
import com.constructflow.user_service.exception.UserAlreadyExistsException;
import com.constructflow.user_service.exception.UserNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl
        implements UserService
{
    @Autowired
    private CFUserRepository repository;

    @Override
    public CFUser loadUserByUsername(String username)
            throws UsernameNotFoundException
    {
        return repository.findCFUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    @Override
    public CFUser getById(@NonNull Long id)
    {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + id + " not found!"));
    }

    @Override
    public CFUser create(@NonNull CFUser user)
    {
        if(Objects.isNull(user.getUsername()) || Objects.isNull(user.getEmail()) || Objects.isNull(user.getPassword())) {
            throw new MandatoryFieldsMissingException("Some of the mandatory fields are missing!");
        }
        if (repository.findCFUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username " + user.getUsername() + " already taken!");
        }
        if (repository.findCFUserByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email " + user.getEmail() + " already in use!");
        }

        return repository.save(user);
    }
}
