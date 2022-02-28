package it.raffaele.esposito.app.service;

import it.raffaele.esposito.app.entities.Component;
import it.raffaele.esposito.app.entities.Item;
import it.raffaele.esposito.app.entities.Product;
import it.raffaele.esposito.app.model.ProductAvailability;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class ModelMapperTest {


    @Test
    public void mapProductAvailabilityTest() {
        final Product product = getProduct(2);

        ProductAvailability productAvailability = ModelMapper.INSTANCE.mapToProductAvailability(product);
        Assertions.assertEquals(2, productAvailability.getQuantityInStock());
    }


    @Test
    public void mapProductAvailabilityTestNoProductInStock() {
        final Product product = getProduct(0);

        ProductAvailability productAvailability = ModelMapper.INSTANCE.mapToProductAvailability(product);
        Assertions.assertEquals(0, productAvailability.getQuantityInStock());
    }

    private Product getProduct(int productsInStock) {

        int componentQuantity = 2;
        int quantityInStock = productsInStock * componentQuantity;
        return Product
                    .builder()
                    .id(1L)
                    .name("product to sell test success 1")
                    .price(new BigDecimal("28.00"))
                    .components(List.of(Component
                                            .builder()
                                            .quantity(componentQuantity)
                                            .item(Item
                                                    .builder()
                                                    .id(1L)
                                                    .name("very necessary item 1")
                                                    .stock(quantityInStock)
                                                    .build())
                                            .build(),
                                            Component
                                            .builder()
                                            .quantity(componentQuantity)
                                            .item(Item
                                                    .builder()
                                                    .id(2L)
                                                    .name("very necessary item 2")
                                                    .stock(quantityInStock)
                                                    .build())
                                            .build()))
                    .build();
    }

}