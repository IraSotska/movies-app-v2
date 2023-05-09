package com.sotska.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String nickName;

    @NotBlank
    private String encryptedPassword;
}
