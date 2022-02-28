package it.raffaele.esposito.app.controller;

import it.raffaele.esposito.app.exceptions.ItemNotFoundException;
import it.raffaele.esposito.app.exceptions.MissingStockForSaleException;
import it.raffaele.esposito.app.exceptions.ProductBomNotFoundException;
import it.raffaele.esposito.app.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final int ITEM_NOT_FOUND_ERROR_CODE = 1;
    public static final int PRODUCT_NOT_FOUND_EXCEPTION = 2;
    public static final int MISSING_STOCK_FOR_SALE_ERROR_CODE = 3;
    public static final int PRODUCT_BOM_NOT_FOUND_ERROR_CODE = 4;
    public static final int MALFORMED_REQUEST = 5;
    public static final int GENERIC_ERROR_CODE = -1;

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<List<APIError>> handleItemNotFoundException(ItemNotFoundException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonList(new APIError(ITEM_NOT_FOUND_ERROR_CODE, "Item not found.")), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingStockForSaleException.class)
    public ResponseEntity<List<APIError>> handleMissingStockForSaleException(MissingStockForSaleException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonList(new APIError(MISSING_STOCK_FOR_SALE_ERROR_CODE, "Missing stock for sale.")), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<List<APIError>> handleProductNotFoundException(ProductNotFoundException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonList(new APIError(PRODUCT_NOT_FOUND_EXCEPTION, "Product not found.")), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductBomNotFoundException.class)
    public ResponseEntity<List<APIError>> handleProductBomNotFoundException(ProductBomNotFoundException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonList(new APIError(PRODUCT_BOM_NOT_FOUND_ERROR_CODE, "Product bom not found.")), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<APIError>> handleValidation(ConstraintViolationException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonList(new APIError(MALFORMED_REQUEST, "Malformed request.")), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<List<APIError>> handleGeneric(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonList(new APIError(GENERIC_ERROR_CODE, "Server error.")), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}