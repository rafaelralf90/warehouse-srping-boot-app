package it.raffaele.esposito.app.model;

import lombok.Data;

@Data
public class ProductAvailability extends ProductBasicInfo {

    private boolean itemBomAvailable;
    private int quantityInStock;
}