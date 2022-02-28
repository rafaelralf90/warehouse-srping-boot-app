package it.raffaele.esposito.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BulkProductImport {

    @JsonProperty("products")
    private List<ProductBom> products;
}