package it.raffaele.esposito.app.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productIdentifier) {
        super(String.format("Product not found %s", productIdentifier));
    }
}
