package it.raffaele.esposito.app.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductBasicInfo {

    private Long id;
    private String name;
    private BigDecimal price;
}
