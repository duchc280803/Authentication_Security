package com.example.demoauthentication.service;

import com.example.demoauthentication.dto.reponse.MessageResponse;
import com.example.demoauthentication.dto.reponse.TokenResponse;
import com.example.demoauthentication.dto.request.LoginRequest;
import com.example.demoauthentication.dto.request.RegisterRequest;

public interface AccountService {

    TokenResponse login(LoginRequest loginRequest);

    MessageResponse register(RegisterRequest registerRequest);
}
