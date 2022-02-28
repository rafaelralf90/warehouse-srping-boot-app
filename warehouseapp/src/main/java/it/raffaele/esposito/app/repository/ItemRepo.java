package it.raffaele.esposito.app.repository;

import it.raffaele.esposito.app.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<Item, Long> {
}