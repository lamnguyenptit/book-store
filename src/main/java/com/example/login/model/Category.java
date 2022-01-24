package com.example.login.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category extends IdBaseEntity{

    @Column(name="name", length = 128, nullable = false, unique = true)
    private String name;

    @Column(name="image", length = 256, nullable = false)
    private String image;

    private boolean enabled;

//    @Transient
    private String getImagePath(){
        if(this.image == null)
            return "/images/image-thumbnail.png";
        return "/category-images/"+this.id+"/"+this.image;
    }

    @ManyToMany(mappedBy = "categories")
    private List<Product> productSet;


}
