package it.raffaele.esposito.app.controller;

import it.raffaele.esposito.app.model.ItemModel;
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
@RequestMapping(value = "/api/items/")
public class ItemResourceController {

    private WarehouseService warehouseService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemModel> getItems() {
        return warehouseService.listItems();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemModel updateOrCreateItem(@RequestBody ItemModel itemModel) {
        return warehouseService.updateOrCreateItem(itemModel);
    }
}