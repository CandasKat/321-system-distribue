package com.candas.order_management.service;

import com.candas.order_management.dto.CommandesProduitsDTO;
import com.candas.order_management.models.Commandes;
import com.candas.order_management.models.CommandesProduits;
import com.candas.order_management.repository.CommandesProduitsRepository;
import com.candas.order_management.repository.CommandesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommandesProduitsService {
    private static final Logger logger = LoggerFactory.getLogger(CommandesProduitsService.class);

    private final CommandesProduitsRepository commandesProduitsRepository;
    private final CommandesRepository commandesRepository;
    private final ProductMessageService productMessageService;

    @Autowired
    public CommandesProduitsService(CommandesProduitsRepository commandesProduitsRepository,
                                    CommandesRepository commandesRepository,
                                    ProductMessageService productMessageService) {
        logger.info("CommandesService created");
        this.commandesProduitsRepository = commandesProduitsRepository;
        this.commandesRepository = commandesRepository;
        this.productMessageService = productMessageService;
    }

    @Transactional
    public CommandesProduits saveCommandeProduits(CommandesProduitsDTO dto) {
        Commandes commande = commandesRepository.findById(dto.commande_id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + dto.commande_id));

        CommandesProduits entity = new CommandesProduits(commande, dto.produit_id, dto.quantite);
        commande.addOrderProduct(entity);
        logger.info("Saving commandes produits " + entity);
        CommandesProduits savedEntity = commandesProduitsRepository.save(entity);

        // Send message to product service
        productMessageService.sendProductCreatedMessage(savedEntity);

        return savedEntity;
    }

    public List<CommandesProduits> getAllCommandeProduits() {
        logger.info("Getting all commandes produits");
        return this.commandesProduitsRepository.findAll();
    }

    public Optional<CommandesProduits> getCommandeProduitsById(Long id) {
        logger.info("Getting commandes produits by id " + id);
        return this.commandesProduitsRepository.findById(id);
    }

    @Transactional
    public CommandesProduits updateCommandeProduits(Long id, CommandesProduitsDTO dto) {
        CommandesProduits existing = commandesProduitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommandesProduits not found with id: " + id));
        Commandes commande = commandesRepository.findById(dto.commande_id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + dto.commande_id));

        // Store old values for comparison
        Long oldProduitId = existing.getProduit_id();
        int oldQuantite = existing.getQuantite();

        existing.setProduit_id(dto.produit_id);
        existing.setQuantite(dto.quantite);
        commande.addOrderProduct(existing);
        existing.setCommande(commande);

        logger.info("Updating commandes produits with id: " + id);
        CommandesProduits updatedEntity = commandesProduitsRepository.save(existing);

        // Send message to product service if product ID or quantity changed
        if (!oldProduitId.equals(dto.produit_id) || oldQuantite != dto.quantite) {
            productMessageService.sendProductUpdatedMessage(updatedEntity);
        }

        return updatedEntity;
    }

    @Transactional
    public void deleteCommandeProduits(Long id) {
        CommandesProduits existing = commandesProduitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommandesProduits not found with id: " + id));

        // Send message to product service before deleting
        productMessageService.sendProductDeletedMessage(existing);

        Commandes commande = existing.getCommande();
        commande.removeOrderProduct(existing);
        commandesProduitsRepository.deleteById(id);
        logger.info("Deleting commandes produits with id: " + id);
        commandesRepository.save(commande);
    }
}
