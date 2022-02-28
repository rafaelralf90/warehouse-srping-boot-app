package it.raffaele.esposito.app.service;

import it.raffaele.esposito.app.entities.Component;
import it.raffaele.esposito.app.entities.Item;
import it.raffaele.esposito.app.entities.Product;
import it.raffaele.esposito.app.exceptions.ItemNotFoundException;
import it.raffaele.esposito.app.exceptions.MissingStockForSaleException;
import it.raffaele.esposito.app.exceptions.ProductBomNotFoundException;
import it.raffaele.esposito.app.exceptions.ProductNotFoundException;
import it.raffaele.esposito.app.model.*;
import it.raffaele.esposito.app.repository.ComponentRepo;
import it.raffaele.esposito.app.repository.ItemRepo;
import it.raffaele.esposito.app.repository.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Represents a warehouse service to perform basic management operations and queries regarding items and products.
 */
@Slf4j
@AllArgsConstructor
@Validated
@Transactional
@Service
public class WarehouseService {

    private ComponentRepo componentRepo;
    private ItemRepo itemRepo;
    private ProductRepo productRepo;

    /**
     * @param  onlyAvailable if true filters will return only product that are available in the inventory, give its current status
     * @return Information about the availability of the products in the warehouse inventory
     */
    public List<ProductAvailability> getWarehouseProductAvailability(boolean onlyAvailable) {
        log.info(String.format("Requested warehouse product availability, only available %b", onlyAvailable));
        final List<Product> products = productRepo.findAll();
        return ModelMapper.INSTANCE.mapToWarehouseProductsAvailability(products, onlyAvailable);
    }

    /**
     * @param productName name of the product to retrieve availability info for
     * @return Information about product availability
     */
    public ProductAvailability getProductAvailability(@Size(min = 2) String productName) {
        log.info(String.format("Requested availability information for product %s", productName));
        final Product product = productRepo.findByName(productName).orElseThrow(() -> new ProductNotFoundException(productName));
        return ModelMapper.INSTANCE.mapToProductAvailability(product);
    }

    /**
     * @param productName name of the product to perform a sell operation for
     * @param quantity    number of products to sell
     * @return Product info of the product being sold
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProductBasicInfo sellProduct(@Size(min = 2) String productName, @Positive int quantity) {
        log.info(String.format("Requested sale for product %s and quantity %d", productName, quantity));
        final Product product = productRepo.findByName(productName).orElseThrow(() -> new ProductNotFoundException(productName));
        if (product.getComponents().size() == 0) {
            throw new ProductBomNotFoundException(productName);
        }
        final List<Component> components = product.getComponents();
        for (Component component : components) {
            final Item item = component.getItem();
            final int quantityAfterSell = getRemainingQuantityInStock(component, quantity);
            if (quantityAfterSell >= 0) {
                item.setStock(quantityAfterSell);
            } else {
                throw new MissingStockForSaleException(String.format("Missing quantity %d for item id %d", Math.abs(quantityAfterSell), item.getId()));
            }
        }
        return ModelMapper.INSTANCE.mapToProduct(product, quantity);
    }

    private int getRemainingQuantityInStock(Component component, int quantity) {
        final Item item = component.getItem();
        final int neededQuantity = component.getQuantity() * quantity;
        return item.getStock() - neededQuantity;
    }

    /**
     * @param itemModel used to create item or update the quantity summing it to the item already existing
     * @return Status of the item after update/creation
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ItemModel updateOrCreateItem(@Valid ItemModel itemModel) {
        Item newItem = itemRepo.findById(itemModel.getId()).orElse(null);
        if (newItem == null) {
            log.info(String.format("Creating new item name %s, id %d with quantity %d", itemModel.getName(), itemModel.getId(), itemModel.getStock()));
            newItem = new Item();
            newItem.setName(itemModel.getName());
            newItem.setId(itemModel.getId());
        } else {
            log.info(String.format("Updating item name %s, id %d with quantity %d", itemModel.getName(), itemModel.getId(), itemModel.getStock()));
        }
        newItem.setStock(newItem.getStock() + itemModel.getStock());
        itemRepo.save(newItem);
        return ModelMapper.INSTANCE.mapToItem(newItem);
    }

    /**
     * @param productBom Product bill of material description
     */
    public void createProduct(@Valid ProductBom productBom) {
        log.info(String.format("Creating new product %s", productBom.getName()));
        final Product product = new Product();
        product.setName(productBom.getName());
        product.setPrice(productBom.getPrice());
        productRepo.save(product);

        for (Composition compositionModel : productBom.getCompositions()) {
            final Component component = new Component();
            component.setProduct(product);
            component.setQuantity(compositionModel.getQuantity());
            final Item item = itemRepo.findById(compositionModel.getItemId()).orElse(null);
            if (item == null) {
                throw new ItemNotFoundException(compositionModel.getItemId());
            }
            component.setItem(item);
            componentRepo.save(component);
        }
    }

    /**
     * @return items in the warehouse
     */
    public List<ItemModel> listItems() {
        return ModelMapper.INSTANCE.mapToItem(itemRepo.findAll());
    }
}