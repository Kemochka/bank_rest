package com.example.bankcards.security.dtos.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String login;
    private List<String> roles;

    public JwtResponseDTO(String accessToken, Long id, String username, String login, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.login = login;
        this.roles = roles;
    }
}
