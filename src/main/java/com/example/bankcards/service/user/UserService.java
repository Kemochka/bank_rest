package com.example.bankcards.service.user;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findById(Long id);

    List<UserDto> findAll();

    void deleteById(Long id);

    boolean update(User user);
}
