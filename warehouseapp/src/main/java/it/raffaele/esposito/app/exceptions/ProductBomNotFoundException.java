package it.raffaele.esposito.app.exceptions;

public class ProductBomNotFoundException extends RuntimeException {

    public ProductBomNotFoundException(String productIdentifier) {
        super(String.format("Bill of material not found for not product %s", productIdentifier));
    }
}
