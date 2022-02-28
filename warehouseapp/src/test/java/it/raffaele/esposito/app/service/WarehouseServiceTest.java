package it.raffaele.esposito.app.service;

import it.raffaele.esposito.app.entities.Item;
import it.raffaele.esposito.app.entities.Product;
import it.raffaele.esposito.app.exceptions.ItemNotFoundException;
import it.raffaele.esposito.app.exceptions.MissingStockForSaleException;
import it.raffaele.esposito.app.model.*;
import it.raffaele.esposito.app.repository.ComponentRepo;
import it.raffaele.esposito.app.repository.ItemRepo;
import it.raffaele.esposito.app.repository.ProductRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestUtil;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private ComponentRepo componentRepo;
    @Mock
    private ItemRepo itemRepo;
    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private WarehouseService warehouseService;

    @Test
    public void testProductAvailability() {
        Mockito.when(productRepo.findByName("product test name 1")).thenReturn(Optional.of(TestUtil.getProduct(true)));
        final ProductAvailability productAvailability = warehouseService.getProductAvailability("product test name 1");

        Assertions.assertNotNull(productAvailability);
        Assertions.assertEquals(productAvailability.getId(), 1L);
        Assertions.assertTrue(productAvailability.isItemBomAvailable());
        Assertions.assertEquals(1, productAvailability.getQuantityInStock());
    }

    @Test
    public void testSellSuccessful() {

        final Product product = TestUtil.getProduct(true);
        Mockito.when(productRepo.findByName("product to sell test success 1")).thenReturn(Optional.of(product));
        final ProductBasicInfo soldProductBasicInfo = warehouseService.sellProduct("product to sell test success 1", 1);

        Assertions.assertNotNull(soldProductBasicInfo);
        Assertions.assertEquals("product to sell test success 1", soldProductBasicInfo.getName());
    }

    @Test
    public void testSellNotSuccessfulBecauseOfMissingStock() {

        final Product product = TestUtil.getProduct(false);
        Mockito.when(productRepo.findByName("product to sell test success 1")).thenReturn(Optional.of(product));

        Assertions.assertThrows(MissingStockForSaleException.class, () -> warehouseService.sellProduct("product to sell test success 1", 2));
    }

    @Test
    public void testSellUpdateItemInStock() {

        final Item itemAlreadyInStock = Item.builder().stock(10).build();
        final ItemModel itemModel = new ItemModel();
        itemModel.setId(1L);
        itemModel.setStock(5);
        Mockito.when(itemRepo.findById(1L)).thenReturn(Optional.of(itemAlreadyInStock));
        final ItemModel itemModelFromService = warehouseService.updateOrCreateItem(itemModel);

        Assertions.assertEquals(15, itemModelFromService.getStock());
    }

    @Test
    public void testProductCreationSuccessfull() {
        final Item itemAlreadyInStock = Item.builder().stock(10).build();
        Mockito.when(itemRepo.findById(1L)).thenReturn(Optional.of(itemAlreadyInStock));
        final Composition composition = new Composition();
        composition.setQuantity(1);
        composition.setItemId(1L);
        final ProductBom productBom = new ProductBom();
        productBom.setName("test");
        productBom.setCompositions(List.of(composition));
        warehouseService.createProduct(productBom);
    }

    @Test
    public void testProductCreationFailsBecauseItemNotFound() {
        final Composition composition = new Composition();
        composition.setQuantity(1);
        composition.setItemId(1L);
        final ProductBom productBom = new ProductBom();
        productBom.setName("test");
        productBom.setCompositions(List.of(composition));

        Assertions.assertThrows(ItemNotFoundException.class, () -> warehouseService.createProduct(productBom));
    }
}