package it.raffaele.esposito.app.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Component")
public class Component {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @Getter
    private long id;
    @Column(name = "quantity")
    @Getter
    @Setter
    private Integer quantity;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productid")
    @Getter
    @Setter
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemid")
    @Getter
    @Setter
    private Item item;
}