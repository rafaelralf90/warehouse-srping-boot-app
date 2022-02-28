package it.raffaele.esposito.app.service;

import it.raffaele.esposito.app.entities.Component;
import it.raffaele.esposito.app.entities.Item;
import it.raffaele.esposito.app.entities.Product;
import it.raffaele.esposito.app.model.ItemModel;
import it.raffaele.esposito.app.model.ProductAvailability;
import it.raffaele.esposito.app.model.ProductBasicInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ModelMapper {
    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    default ProductAvailability mapToProductAvailability(Product product) {
        final ProductAvailability productAvailability = new ProductAvailability();
        productAvailability.setId(product.getId());
        productAvailability.setName(product.getName());
        productAvailability.setPrice(product.getPrice());
        productAvailability.setItemBomAvailable(!product.getComponents().isEmpty());
        int maxQuantityProductInStock = 0;
        if (productAvailability.isItemBomAvailable()) {
            for (Component c : product.getComponents()) {
                int inStock = c.getItem().getStock();
                if (inStock == 0) {
                    maxQuantityProductInStock = 0;
                    break;
                }
                maxQuantityProductInStock = Math.max(inStock / c.getQuantity(), maxQuantityProductInStock);
            }
        }
        productAvailability.setQuantityInStock(maxQuantityProductInStock);
        return productAvailability;
    }

    default List<ProductAvailability> mapToWarehouseProductsAvailability(List<Product> products, boolean onlyAvailable) {
        return products.stream()
                .map(this::mapToProductAvailability)
                .filter(productAvailabilityDto -> productAvailabilityDto.getQuantityInStock() > 0 || !onlyAvailable)
                .collect(Collectors.toList());
    }

    default ProductBasicInfo mapToProduct(Product entityProduct, int quantity) {
        final ProductBasicInfo productBasicInfo = new ProductBasicInfo();
        productBasicInfo.setId(entityProduct.getId());
        productBasicInfo.setName(entityProduct.getName());
        productBasicInfo.setPrice(entityProduct.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return productBasicInfo;
    }

    ItemModel mapToItem(Item item);

    List<ItemModel> mapToItem(List<Item> item);
}