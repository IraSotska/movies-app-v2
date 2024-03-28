package com.sotska.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Review implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Long movieId;

    @NotBlank
    private String text;

    @Override
    public Review clone() throws CloneNotSupportedException {
        return (Review) super.clone();
    }
}
