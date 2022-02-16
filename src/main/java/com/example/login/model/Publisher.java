package com.example.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publisher extends IdBaseEntity implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;

    @Column(name="logo", nullable = false, length = 256)
    private String logo;

    @Column(name="name", nullable = false, length = 50)
    private String name;

    @ManyToMany
    @JoinTable(
            name="publishers_categories",
            joinColumns = @JoinColumn(name = "publisher_id"),
            inverseJoinColumns = @JoinColumn(name="category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
