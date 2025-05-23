package com.candas.order_management.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Table(name="commandes_produits")
public class CommandesProduits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commandes commande;

    @Column(name = "produit_id")
    private Long produit_id;

    @Column(name = "quantite", nullable = false)
    @Min(1)
    private int quantite;

    public CommandesProduits() {}
    public CommandesProduits(Commandes commande, Long produit_id, int quantite) {
        this.commande = commande;
        this.produit_id = produit_id;
        this.quantite = quantite;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commandes getCommande() {
        return this.commande;
    }

    public void setCommande(Commandes commande) {
        this.commande = commande;
    }

    public Long getProduit_id() {
        return this.produit_id;
    }

    public void setProduit_id(Long produit_id) {
        this.produit_id = produit_id;
    }

    public int getQuantite() {
        return this.quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
