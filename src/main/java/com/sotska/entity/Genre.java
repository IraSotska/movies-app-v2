package com.sotska.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    private Long id;
    private String name;

    public Genre copy() {
        var copy = new Genre();
        copy.name = name;
        copy.id = id;
        return copy;
    }
}
