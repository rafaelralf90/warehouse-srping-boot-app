package util;

import it.raffaele.esposito.app.entities.Component;
import it.raffaele.esposito.app.entities.Item;
import it.raffaele.esposito.app.entities.Product;

import java.math.BigDecimal;
import java.util.List;

public class TestUtil {

    public static Product getProduct(boolean isProductInStock) {

        final int inStock = isProductInStock ? 1 : 0;
        return getProduct(inStock);
    }

    public static Product getProduct(int numberOfProductsInStock) {

        int componentQuantity = 2;
        int quantityInStock = numberOfProductsInStock * componentQuantity;
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
