package com.constructflow.entity_service.client;

import com.constructflow.entity_service.dto.AuthRequestDto;
import com.constructflow.entity_service.dto.UserDto;
import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface CFUserClient
{
    @PostMapping(value = "/create")
    UserDto create(@RequestBody AuthRequestDto requestDto);

    @PutMapping("/update/{userId}")
    UserDto update(
            @PathVariable @NonNull Long userId,
            @RequestBody AuthRequestDto requestDto);

}
