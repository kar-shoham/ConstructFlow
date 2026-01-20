package com.constructflow.user_service.service;

import com.constructflow.user_service.entity.CFUser;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService
    extends UserDetailsService
{
    CFUser getById(@NonNull Long id);

    CFUser create(@NonNull CFUser user);
}
