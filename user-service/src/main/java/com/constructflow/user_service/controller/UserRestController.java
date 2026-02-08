package com.constructflow.user_service.controller;

import com.constructflow.user_service.dto.AuthRequestDto;
import com.constructflow.user_service.dto.UserDto;
import com.constructflow.user_service.entity.CFUser;
import com.constructflow.user_service.enums.UserRole;
import com.constructflow.user_service.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController
{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable @NonNull Long userId)
    {
        CFUser user = userService.getById(userId);
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build());
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> create(@RequestBody @NonNull AuthRequestDto requestDto)
    {
        CFUser user = CFUser.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userRole(UserRole.USER)
                .build();

        user = userService.create(user);

        UserDto dto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable @NonNull Long userId,
            @RequestBody @NonNull AuthRequestDto requestDto)
    {
        CFUser user = CFUser.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userRole(UserRole.USER)
                .build();

        user = userService.update(userId, user);

        UserDto dto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();
        return ResponseEntity.ok(dto);
    }
}
