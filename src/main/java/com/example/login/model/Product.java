package com.example.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends IdBaseEntity{

    @Column(length = 100, unique = true)
    private String name;

    private Float cost;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "discount_percent")
    private Float discountPercent;

    private boolean enabled;


    @Column(length = 4096)
    private String description;

    @Column(length = 256)
    private String image;

    @Column(name="in_stock")
    private boolean inStock;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Float price;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @OneToMany(mappedBy = "product")
    private List<CartAndProduct> productAssoc;

    @Transient
    public float getDiscountPrice() {
        if(discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }


}
