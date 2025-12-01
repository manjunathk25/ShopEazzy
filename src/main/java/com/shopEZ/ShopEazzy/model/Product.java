package com.shopEZ.ShopEazzy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank(message = "Product name must not be blank.")
    @Size(min = 3, message = "Product name must contain at least 3 characters.")
    private String productName;

    private String image;

    @NotBlank(message = "Product description must not be blank.")
    @Size(min = 6, message = "Product description must contain at least 6 characters.")
    private String description;

    @PositiveOrZero(message = "Product quantity must have valid value.")
    @NotNull(message = "Product quantity must have valid value.")
    private Integer quantity;

    @Positive(message = "Product price must have valid value.")
    private double price;

    @PositiveOrZero(message = "Product discount must have valid value.")
    private double discount;

    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
