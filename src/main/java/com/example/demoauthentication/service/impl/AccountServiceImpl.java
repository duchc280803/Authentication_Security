package com.example.demoauthentication.service.impl;

import com.example.demoauthentication.dto.reponse.MessageResponse;
import com.example.demoauthentication.dto.reponse.TokenResponse;
import com.example.demoauthentication.dto.request.LoginRequest;
import com.example.demoauthentication.dto.request.RegisterRequest;
import com.example.demoauthentication.entity.Account;
import com.example.demoauthentication.entity.Role;
import com.example.demoauthentication.model.UserCustomDetail;
import com.example.demoauthentication.repository.AccountRepository;
import com.example.demoauthentication.repository.RoleRepository;
import com.example.demoauthentication.service.AccountService;
import com.example.demoauthentication.utill.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Optional<Account> findByAccount = accountRepository.findByUsername(loginRequest.getUsername());
        String jwtToken = jwtService.generateToken(new UserCustomDetail(findByAccount.get()));
        return TokenResponse
                .builder()
                .token(jwtToken)
                .role(findByAccount.get().getRole().getRoleEnums().name())
                .build();
    }

    @Override
    public MessageResponse register(RegisterRequest registerRequest) {
        Optional<Role> findByRole = roleRepository.findByRoleEnums(registerRequest.getRole());
        Account account = Account
                .builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(findByRole.get())
                .build();
        accountRepository.save(account);
        return MessageResponse.builder().message("Đăng ký thành công").build();
    }
}
