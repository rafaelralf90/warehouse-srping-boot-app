package it.raffaele.esposito.app.repository;

import it.raffaele.esposito.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);
}