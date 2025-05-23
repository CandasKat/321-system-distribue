package com.candas.order_management.controller;

import com.candas.order_management.dto.CommandesProduitsDTO;
import com.candas.order_management.models.CommandesProduits;
import com.candas.order_management.service.CommandesProduitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CommandesProduitsController {
    private final CommandesProduitsService commandesProduitsService;

    @Autowired
    public CommandesProduitsController(CommandesProduitsService commandesProduitsService) {
        this.commandesProduitsService = commandesProduitsService;
    }

    @PostMapping("/commandes_produits")
    public ResponseEntity<CommandesProduits> saveCommandes(@RequestBody CommandesProduitsDTO dto) {
        CommandesProduits newCommandes = commandesProduitsService.saveCommandeProduits(dto);
        return ResponseEntity.ok(newCommandes);
    }

    @GetMapping("/commandes_produits")
    public List<CommandesProduits> getAllCommandes() {
        return commandesProduitsService.getAllCommandeProduits();
    }

    @GetMapping("/commandes_produits/{id}")
    public ResponseEntity<CommandesProduits> getCommandeById(@PathVariable long id) {
        Optional<CommandesProduits> commandesProduits = commandesProduitsService.getCommandeProduitsById(id);
        return commandesProduits.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/commandes_produits/{id}")
    public ResponseEntity<CommandesProduits> updateCommande(@PathVariable long id, @RequestBody CommandesProduitsDTO dto) {
        CommandesProduits commandesProduits = commandesProduitsService.updateCommandeProduits(id, dto);
        return ResponseEntity.ok(commandesProduits);
    }

    @DeleteMapping("/commandes_produits/{id}")
    public ResponseEntity<String> deleteCommandeProduits(@PathVariable long id) {
        commandesProduitsService.deleteCommandeProduits(id);
        return ResponseEntity.ok("Commandes deleted successfully");
    }
}
