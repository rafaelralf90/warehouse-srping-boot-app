package it.raffaele.esposito.app.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Item")
public class Item {
    @Id
    @Column(name = "id")
    @Setter
    @Getter
    private long id;
    @Column(name = "name")
    @Getter
    @Setter
    private String name;
    @Column(name = "stock")
    @Getter
    @Setter
    private int stock;
}