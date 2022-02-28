package test;

import com.jayway.restassured.response.Response;
import it.raffaele.esposito.app.Application;
import it.raffaele.esposito.app.controller.APIError;
import it.raffaele.esposito.app.model.*;
import it.raffaele.esposito.app.service.WarehouseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("itport")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WarehouseServiceIT {

    @Autowired
    private WarehouseService warehouseService;

    private static int port = 8081;
    public final String ENDPOINT_URL_PRODUCTS = "http://localhost:" + port + "/api/products/";
    public final String ENDPOINT_URL_ITEMS = "http://localhost:" + port + "/api/items/";

    @Test
    @DirtiesContext
    public void saleSuccess() {

        final String productName = "DiningTable";
        insertItemsAndProduct(productName);

        // list products available in warehouse
        final Response warehouseAvailabilityResponse = given()
                .queryParam("onlyAvailable", false)
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS);
        Assertions.assertEquals(200, warehouseAvailabilityResponse.getStatusCode());
        final ProductAvailability[] products = warehouseAvailabilityResponse.as(ProductAvailability[].class);
        Assertions.assertEquals(1, products.length);
        Assertions.assertEquals("DiningTable", products[0].getName());

        // check availability
        final Response availabilityResponse = given()
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS + productName);
        Assertions.assertEquals(200, availabilityResponse.getStatusCode());
        final ProductAvailability productAvailability = availabilityResponse.as(ProductAvailability.class);
        Assertions.assertEquals(2, productAvailability.getQuantityInStock());

        // sell successful
        final ProductSale productSale = new ProductSale();
        productSale.setName(productName);
        productSale.setQuantity(1);
        final Response saleResponse = given()
                .contentType("application/json")
                .body(productSale)
                .post(ENDPOINT_URL_PRODUCTS);
        Assertions.assertEquals(200, saleResponse.getStatusCode());

        // check availability after sale
        final Response availabilityResponseAfterSale = given()
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS + productName);
        Assertions.assertEquals(200, availabilityResponseAfterSale.getStatusCode());
        final ProductAvailability productAvailabilityAfterSale = availabilityResponseAfterSale.as(ProductAvailability.class);
        Assertions.assertEquals(1, productAvailabilityAfterSale.getQuantityInStock());
    }

    @Test
    public void saleSuccessThenSaleNotSuccessDueToItemNotInStock() {

        final String productName = "DiningTable";
        insertItemsAndProduct(productName);

        // check availability
        final Response availabilityResponse = given()
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS + productName);
        Assertions.assertEquals(200, availabilityResponse.getStatusCode());
        final ProductAvailability productAvailability = availabilityResponse.as(ProductAvailability.class);
        Assertions.assertEquals(2, productAvailability.getQuantityInStock());

        // sell successful
        final ProductSale productSale = new ProductSale();
        productSale.setName(productName);
        productSale.setQuantity(2);
        final Response saleResponse = given()
                .contentType("application/json")
                .body(productSale)
                .post(ENDPOINT_URL_PRODUCTS);
        Assertions.assertEquals(200, saleResponse.getStatusCode());

        // check availability after sale
        final Response availabilityResponseAfterSale = given()
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS + productName);
        Assertions.assertEquals(200, availabilityResponseAfterSale.getStatusCode());
        final ProductAvailability productAvailabilityAfterSale = availabilityResponseAfterSale.as(ProductAvailability.class);
        Assertions.assertEquals(0, productAvailabilityAfterSale.getQuantityInStock());

        // list products available in warehouse after sale, filter only available
        final Response warehouseAvailabilityResponseOnlyAvailable = given()
                .queryParam("onlyAvailable", true)
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS);
        Assertions.assertEquals(200, warehouseAvailabilityResponseOnlyAvailable.getStatusCode());
        final ProductAvailability[] availableProducts = warehouseAvailabilityResponseOnlyAvailable.as(ProductAvailability[].class);
        Assertions.assertEquals(0, availableProducts.length);

        // sale not successful due to stock
        productSale.setQuantity(1);
        final Response newSaleResponse = given()
                .contentType("application/json")
                .body(productSale)
                .post(ENDPOINT_URL_PRODUCTS);
        Assertions.assertEquals(404, newSaleResponse.getStatusCode());
        final APIError[] apiError = newSaleResponse.as(APIError[].class);
        Assertions.assertEquals(3, apiError[0].getCode());
    }

    @Test
    public void testProductInfoOnNonExistingProduct() {

        final Response availabilityResponse = given()
                .contentType("application/json")
                .get(ENDPOINT_URL_PRODUCTS + "NonExistingProduct");
        Assertions.assertEquals(404, availabilityResponse.getStatusCode());
        final APIError[] apiError = availabilityResponse.as(APIError[].class);
        Assertions.assertEquals(2, apiError[0].getCode());
    }

    @Test
    public void itemInsertPreconditionsNotMet() {

        final ItemModel itemModelScrewNameTooShort = new ItemModel();
        itemModelScrewNameTooShort.setStock(10);
        itemModelScrewNameTooShort.setId(1L);
        itemModelScrewNameTooShort.setName("S");
        Assertions.assertThrows(ConstraintViolationException.class, () -> warehouseService.updateOrCreateItem(itemModelScrewNameTooShort));

        final ItemModel itemModelChairNegativeIndex = new ItemModel();
        itemModelChairNegativeIndex.setStock(5);
        itemModelChairNegativeIndex.setId(-2L);
        itemModelChairNegativeIndex.setName("Chair");
        Assertions.assertThrows(ConstraintViolationException.class, () -> warehouseService.updateOrCreateItem(itemModelChairNegativeIndex));

        final ItemModel itemModelChairNegativeQuantityInStock = new ItemModel();
        itemModelChairNegativeIndex.setStock(-5);
        itemModelChairNegativeIndex.setId(3L);
        itemModelChairNegativeIndex.setName("Chair");
        Assertions.assertThrows(ConstraintViolationException.class, () -> warehouseService.updateOrCreateItem(itemModelChairNegativeQuantityInStock));
    }

    @Test
    public void testProductInsertPreconditionsNotMet() {

        final ProductBom productBom = new ProductBom();
        productBom.setPrice(new BigDecimal("1.001"));
        productBom.setName("Living room sofa");
        final Composition composition = new Composition();
        composition.setItemId(1L);
        composition.setQuantity(10);
        productBom.setCompositions(List.of(composition));
        Assertions.assertThrows(ConstraintViolationException.class, () -> warehouseService.createProduct(productBom));

    }

    @Test
    public void testAddAndItemsSuccess() {

        final ItemModel item = new ItemModel();
        item.setId(1000L);
        item.setName("wardrobe door");
        item.setStock(50);
        final Response itemsAddResponse = given()
                .contentType("application/json")
                .body(item)
                .put(ENDPOINT_URL_ITEMS);
        Assertions.assertEquals(200, itemsAddResponse.getStatusCode());
        final ItemModel itemResponse = itemsAddResponse.as(ItemModel.class);
        Assertions.assertEquals(1000L, itemResponse.getId());


        final Response itemsResponse = given()
                .contentType("application/json")
                .get(ENDPOINT_URL_ITEMS);
        Assertions.assertEquals(200, itemsResponse.getStatusCode());
        final ItemModel[] items = itemsResponse.as(ItemModel[].class);
        Assertions.assertTrue(items.length > 0);
    }

    // Item screw 1L 21 stocks
    // Item chair 2L 10 stocks
    // Item table 3L 2 stocks
    // Product productName composed by 1 table, 5 chairs, 10 screws
    private void insertItemsAndProduct(String productName) {
        final ItemModel itemModelScrew = new ItemModel();
        itemModelScrew.setStock(21);
        itemModelScrew.setId(1L);
        itemModelScrew.setName("Screw");

        final ItemModel itemModelChair = new ItemModel();
        itemModelChair.setStock(10);
        itemModelChair.setId(2L);
        itemModelChair.setName("Chair");

        final ItemModel itemModelTable = new ItemModel();
        itemModelTable.setStock(2);
        itemModelTable.setId(3L);
        itemModelTable.setName("Table");

        warehouseService.updateOrCreateItem(itemModelChair);
        warehouseService.updateOrCreateItem(itemModelScrew);
        warehouseService.updateOrCreateItem(itemModelTable);

        final Composition compositionScrew = new Composition();
        compositionScrew.setItemId(1L);
        compositionScrew.setQuantity(10);

        final Composition compositionChair = new Composition();
        compositionChair.setItemId(2L);
        compositionChair.setQuantity(5);

        final Composition compositionTable = new Composition();
        compositionTable.setItemId(3L);
        compositionTable.setQuantity(1);

        final ProductBom diningSet = new ProductBom();
        diningSet.setName(productName);
        diningSet.setPrice(new BigDecimal("15.00"));
        diningSet.setCompositions(List.of(compositionScrew, compositionChair, compositionTable));

        warehouseService.createProduct(diningSet);
    }
}