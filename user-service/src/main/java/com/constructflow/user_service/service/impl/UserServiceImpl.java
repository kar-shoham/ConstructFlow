package com.constructflow.user_service.service.impl;

import com.constructflow.user_service.dto.JwtInfo;
import com.constructflow.user_service.entity.CFUser;
import com.constructflow.user_service.exception.InsufficientPermissionException;
import com.constructflow.user_service.exception.MandatoryFieldsMissingException;
import com.constructflow.user_service.exception.UserAlreadyExistsException;
import com.constructflow.user_service.exception.UserNotFoundException;
import com.constructflow.user_service.repository.CFUserRepository;
import com.constructflow.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (Objects.isNull(user.getUsername()) || Objects.isNull(user.getEmail()) || Objects.isNull(user.getPassword())) {
            throw new MandatoryFieldsMissingException("Some of the mandatory fields are missing!");
        }
        if (repository.findCFUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username " + user.getUsername() + " already taken!");
        }
        if (repository.findCFUserByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email " + user.getEmail() + " already in use!");
        }

        user.setCreatedBy(getLoggedInUserId());
        user.setModifiedBy(getLoggedInUserId());

        return repository.save(user);
    }

    @Override
    public CFUser update(
            @NonNull Long userId,
            @NonNull CFUser user)
    {
        if (Objects.isNull(user.getUsername()) || Objects.isNull(user.getEmail()) || Objects.isNull(user.getPassword())) {
            throw new MandatoryFieldsMissingException("Some of the mandatory fields are missing!");
        }
        CFUser dbUser = repository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id: " + userId + " not found!"));

        if (Objects.nonNull(user.getEmail()) && !user.getEmail().equals(dbUser.getEmail())) {
            if (repository.findCFUserByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("Email " + user.getEmail() + " already in use!");
            }

            dbUser.setEmail(user.getEmail());
            dbUser.setModifiedBy(getLoggedInUserId());

            dbUser = repository.save(dbUser);
        }
        return dbUser;
    }

    protected void onlyAdmin()
    {
        if (!isLoggedUserAdmin()) {
            throw new InsufficientPermissionException();
        }
    }

    protected boolean isLoggedUserAdmin()
    {
        JwtInfo info = getAuthInfo();
        return info.getUserRole().equals("ADMIN");
    }

    protected Long getLoggedInUserId()
    {
        JwtInfo info = getAuthInfo();
        return info.getUserId();
    }

    private JwtInfo getAuthInfo()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (JwtInfo) authentication.getPrincipal();
    }
}
