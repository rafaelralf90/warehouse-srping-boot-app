package it.raffaele.esposito.app.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Product")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @Getter
    private long id;
    @Column(name = "name")
    @Getter
    @Setter
    private String name;
    @Column(name = "price")
    @Getter
    @Setter
    private BigDecimal price;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Getter
    private List<Component> components;
}