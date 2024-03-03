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
public class Genre implements Cloneable {

    @Id
    private Long id;
    private String name;

    @Override
    public Genre clone() throws CloneNotSupportedException {
        return (Genre) super.clone();
    }
}
