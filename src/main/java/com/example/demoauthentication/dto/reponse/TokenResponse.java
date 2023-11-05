package com.example.demoauthentication.dto.reponse;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenResponse {

    private String token;

    private String role;
}
