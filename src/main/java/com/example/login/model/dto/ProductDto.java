package com.example.login.model.dto;

import com.example.login.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDto implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;

    private Integer id;

    public ProductDto(int id){
        this.id = id;
    }

    @Size(min = 1, max = 50, message = "Name must between 1 and 50 characters")
    private String name;

    @Digits(integer = 10, fraction = 5, message = "Cost must be float")
    @NotNull(message = "Please enter cost")
    private Float cost;

    private Date createTime;

    private Date updateTime;

    @Digits(integer = 10, fraction = 5, message = "Discount percent must be float")
    @NotNull(message = "Please enter discount percent")
    private Float discountPercent;

    private boolean enabled;

    @Size(max = 4096, message = "Description must less than 4096 characters")
    private String description;

    private String image;

    private boolean inStock;

    @Digits(integer = 10, fraction = 0, message = "Quantity must be digits")
    @NotNull(message = "Please enter quantity")
    private Integer quantity;

    @Digits(integer = 10, fraction = 5, message = "Cost must be float")
    @NotNull(message = "Please enter price")
    private Float price;

    @Valid
    private Set<CategoryDto> category;

    @Valid
    private PublisherDto publisher;

    private Integer orderQuantity;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductDto other = (ProductDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Float getProductPrice(){
        if(discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }

    public Float getSubTotal(){
        return getProductPrice() * orderQuantity;
    }
}
