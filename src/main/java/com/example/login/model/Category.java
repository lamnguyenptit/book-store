package com.example.login.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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


}
