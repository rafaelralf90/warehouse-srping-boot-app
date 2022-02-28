package it.raffaele.esposito.app.repository;

import it.raffaele.esposito.app.entities.Component;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentRepo extends JpaRepository<Component, Long> {
}