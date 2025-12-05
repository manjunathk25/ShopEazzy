package com.shopEZ.ShopEazzy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank(message = "Street name must not be blank.")
    @Size(min = 5, message = "Street name must contain at least 5 characters.")
    private String street;

    @NotBlank(message = "Building name must not be blank.")
    @Size(min = 5, message = "Building name must contain at least 5 characters.")
    private String buildingName;

    @NotBlank(message = "City name must not be blank.")
    @Size(min = 4, message = "City name must contain at least 4 characters.")
    private String city;

    @NotBlank(message = "State name must not be blank.")
    @Size(min = 2, message = "State name must contain at least 2 characters.")
    private String state;

    @NotBlank(message = "Country name must not be blank.")
    @Size(min = 2, message = "Country name must contain at least 2 characters.")
    private String country;

    @NotBlank(message = "Zipcode must not be blank.")
    @Size(min = 6, message = "ZipCode must contain at least 6 characters.")
    private String zipcode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String city, String state, String country, String zipcode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipcode = zipcode;
    }
}
