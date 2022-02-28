package it.raffaele.esposito.app.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class ItemModel {

    @NotNull
    @Positive
    @JsonProperty("art_id")
    private Long id;
    @Size(min=2)
    private String name;
    @Positive
    private int stock;
}