package com.osm.auth.service;

import com.osm.auth.dto.LoginRequest;
import com.osm.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse.UserInfo getCurrentUser();
}
