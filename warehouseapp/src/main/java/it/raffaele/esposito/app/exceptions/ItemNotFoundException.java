package it.raffaele.esposito.app.exceptions;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(long itemIdentifier) {
        super(String.format("Item %d not found.", itemIdentifier));
    }
}
