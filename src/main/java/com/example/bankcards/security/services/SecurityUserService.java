package com.example.bankcards.security.services;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.user.UserRepository;
import com.example.bankcards.security.dtos.request.SignupRequestDTO;
import com.example.bankcards.security.dtos.response.RegisterDTO;
import com.example.bankcards.security.models.ERole;
import com.example.bankcards.security.models.Role;
import com.example.bankcards.security.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class SecurityUserService {
    private PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RegisterDTO signUp(SignupRequestDTO signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))
                || Boolean.TRUE.equals(userRepository.existsByLogin(signUpRequest.getLogin()))) {
            return new RegisterDTO(HttpStatus.BAD_REQUEST, "Error: Username or Email is already taken!");
        }

        /* Create new person's account */
        User user = new User(signUpRequest.getUsername(), signUpRequest.getLogin(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        Supplier<RuntimeException> supplier = () -> new RuntimeException("Error: Role is not found.");

        if (strRoles == null) {
            roles.add(roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(supplier));
        } else {
            strRoles.forEach(role -> {
                if (role.equals("ADMIN")) {
                    roles.add(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(supplier));
                } else {
                    roles.add(roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(supplier));
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return new RegisterDTO(HttpStatus.OK, "Person registered successfully!");
    }
}
