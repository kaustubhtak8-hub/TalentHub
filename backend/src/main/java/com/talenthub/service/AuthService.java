package com.talenthub.service;

import com.talenthub.dto.AuthRequest;
import com.talenthub.dto.AuthResponse;
import com.talenthub.dto.RegisterRequest;
import com.talenthub.entity.User;

public interface AuthService {
    User registerUser(RegisterRequest registerRequest);
    AuthResponse authenticateUser(AuthRequest authRequest);
}
