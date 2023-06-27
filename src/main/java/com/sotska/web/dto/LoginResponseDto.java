package com.sotska.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String nickName;
    private String token;
}
