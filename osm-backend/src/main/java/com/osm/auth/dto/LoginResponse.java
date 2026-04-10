package com.osm.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private UserInfo user;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private List<String> roles;
    }
}
