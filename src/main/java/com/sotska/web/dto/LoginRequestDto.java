package com.sotska.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    private String email;
    private String password;
}
