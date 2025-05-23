package com.candas.order_management.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="commandes")
public class Commandes {
    public enum OrderStatus {
        EN_ATTENTE,
        EN_COURS,
        EXPEDIEE,
        ANNULEE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total", columnDefinition = "decimal(10,2)")
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", columnDefinition = "varchar(50)")
    private OrderStatus statut;

    @Column(name = "date_commande", updatable = false)
    @CreationTimestamp
    private LocalDateTime date_commande;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandesProduits> commandesProduits = new ArrayList<>();

    public Commandes() {}
    public Commandes(OrderStatus status) {
        this.statut = status;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public OrderStatus getStatut() {
        return this.statut;
    }

    public void setStatut(OrderStatus statut) {
        this.statut = statut;
    }

    public LocalDateTime getTimestamp() {
        return this.date_commande;
    }

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CommandesProduits commandes : commandesProduits) {
            sum = sum.add(BigDecimal.valueOf(commandes.getQuantite()));
        }
        this.total = sum;
    }

    public void addOrderProduct(CommandesProduits orderProduct) {
        commandesProduits.add(orderProduct);
        orderProduct.setCommande(this);
        this.calculateTotal();
    }

    public void removeOrderProduct(CommandesProduits orderProduct) {
        commandesProduits.remove(orderProduct);
        orderProduct.setCommande(null);
        this.calculateTotal();
    }
}
