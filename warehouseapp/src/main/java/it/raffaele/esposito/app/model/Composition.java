package it.raffaele.esposito.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Composition {

    @JsonProperty("art_id")
    @NotNull
    @Positive
    private Long itemId;
    @JsonProperty("amount_of")
    @Positive
    private int quantity;
}
