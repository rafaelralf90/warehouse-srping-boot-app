package it.raffaele.esposito.app.controller;

import it.raffaele.esposito.app.model.ProductAvailability;
import it.raffaele.esposito.app.model.ProductBasicInfo;
import it.raffaele.esposito.app.model.ProductSale;
import it.raffaele.esposito.app.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping(value = "/api/products/")
public class ProductResourceController {

    private WarehouseService warehouseService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductAvailability> getProducts(@RequestParam("onlyAvailable") boolean onlyAvailable) {
        return warehouseService.getWarehouseProductAvailability(onlyAvailable);
    }

    @GetMapping(value = "{productName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductAvailability getProductAvailability(@PathVariable("productName") String name) {
        return warehouseService.getProductAvailability(name);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductBasicInfo sellProduct(@RequestBody ProductSale productSale) {
        return warehouseService.sellProduct(productSale.getName(), productSale.getQuantity());
    }
}