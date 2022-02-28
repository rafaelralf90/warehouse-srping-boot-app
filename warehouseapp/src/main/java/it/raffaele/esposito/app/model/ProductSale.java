package it.raffaele.esposito.app.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class ProductSale {
    @NotBlank
    String name;
    @Positive
    int quantity;
}
