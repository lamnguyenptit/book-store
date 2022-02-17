package com.example.login.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Cart extends IdBaseEntity{

    @Column(name = "status_payment", length = 50)
    private String statusPayment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private String address;
    private String phone;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartAndProduct> cartAssoc;

    @Override
    public String toString() {
        return "name name: "+ this.id + this.user.getName();
    }
}
