package it.raffaele.esposito.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.raffaele.esposito.app.model.ItemModel;
import it.raffaele.esposito.app.model.BulkItemImport;
import it.raffaele.esposito.app.model.BulkProductImport;
import it.raffaele.esposito.app.model.ProductBom;
import it.raffaele.esposito.app.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
@Profile("dev")
@Component
public class DataPopulator {

    @Value("${products.file}")
    private String productsFile;

    @Value("${inventory.file}")
    private String itemsFile;

    @Autowired
    private WarehouseService warehouseService;

    @EventListener
    public void populateData(ApplicationReadyEvent event) throws IOException {
        log.info("Populating database on application startup.");

        final ObjectMapper mapper = new ObjectMapper();
        final File fileItem = ResourceUtils.getFile("classpath:" + itemsFile);
        final BulkItemImport bulkItemImport = mapper.readValue(fileItem, BulkItemImport.class);
        int itemsCount = populateItems(bulkItemImport);
        log.info("Items inserted on startup: {}.", itemsCount);

        final File fileProduct = ResourceUtils.getFile("classpath:" + productsFile);
        final BulkProductImport bulkProductImport = mapper.readValue(fileProduct, BulkProductImport.class);
        int productsCount = populateProducts(bulkProductImport);
        log.info("Products inserted on startup: {}.", productsCount);
    }

    private int populateItems(BulkItemImport bulkItemImport) {
        int itemsCount = 0;
        for (ItemModel itemModel : bulkItemImport.getArticles()) {
            itemsCount++;
            warehouseService.updateOrCreateItem(itemModel);
        }
        return itemsCount;
    }

    private int populateProducts(BulkProductImport bulkProductImport) {
        int productsCount = 0;
        for(ProductBom productBom : bulkProductImport.getProducts()){
            productsCount++;
            warehouseService.createProduct(productBom);
        }
        return productsCount;
    }
}