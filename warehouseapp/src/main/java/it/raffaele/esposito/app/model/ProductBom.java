package it.raffaele.esposito.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductBom {

    @Size(min=2)
    private String name;
    @Digits(integer = 6, fraction = 2)
    private BigDecimal price;
    @JsonProperty("contain_articles")
    @NotEmpty
    private List<@Valid Composition> compositions;
}
