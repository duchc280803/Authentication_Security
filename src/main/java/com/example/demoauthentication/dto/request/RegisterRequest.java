package com.example.demoauthentication.dto.request;

import com.example.demoauthentication.enums.RoleEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    private String username;

    private String password;

    private String email;

    private RoleEnums role;
}
