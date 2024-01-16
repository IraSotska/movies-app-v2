package com.sotska.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(id, country.id) && Objects.equals(name, country.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
