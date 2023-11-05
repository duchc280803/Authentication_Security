package com.example.demoauthentication.controller;

import com.example.demoauthentication.dto.reponse.MessageResponse;
import com.example.demoauthentication.dto.reponse.TokenResponse;
import com.example.demoauthentication.dto.request.LoginRequest;
import com.example.demoauthentication.dto.request.RegisterRequest;
import com.example.demoauthentication.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication/")
public class UserController {

    @Autowired
    private AccountServiceImpl accountService;

    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(accountService.login(loginRequest), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(accountService.register(registerRequest), HttpStatus.CREATED);
    }
}
