package it.raffaele.esposito.app.exceptions;

public class MissingStockForSaleException extends RuntimeException {

    public MissingStockForSaleException(String productIdentifier){
        super(String.format("Stocks not enough for product %s", productIdentifier));
    }
}
