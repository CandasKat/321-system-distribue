package com.candas.order_management.service;

import com.candas.order_management.dto.CommandesDTO;
import com.candas.order_management.models.Commandes;
import com.candas.order_management.repository.CommandesRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommandesService {
    private static final Logger logger = LoggerFactory.getLogger(CommandesService.class);
    private final CommandesRepository commandesRepository;

    @Autowired
    public CommandesService(CommandesRepository commandesRepository) {
        logger.info("CommandesService created");
        this.commandesRepository = commandesRepository;
    }

    public Commandes saveCommandes(CommandesDTO dto) {
        Commandes commande = new Commandes(dto.status);
        logger.info("Saving commandes " + commande);
        return this.commandesRepository.save(commande);
    }

    public List<Commandes> getAllCommandes() {
        logger.info("Getting all commandes");
        return this.commandesRepository.findAll();
    }

    public List<Commandes> getCommandesByStatut(Commandes.OrderStatus statut) {
        logger.info("Getting commandes by statut: " + statut);
        return this.commandesRepository.findByStatut(statut);
    }

    public Optional<Commandes> getCommandesById(Long id) {
        logger.info("Getting commandes by id: " + id);
        return this.commandesRepository.findById(id);
    }

    public Commandes updateCommandes(Long id, CommandesDTO dto) {
        Commandes existing = this.commandesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommandesProduits not found with id: " + id));
        existing.setStatut(dto.status);
        logger.info("Updating commandes with id: " + id + " with status: " + existing.getStatut());
        return this.commandesRepository.save(existing);

    }

    public void deleteCommandes(Long id) {
        this.commandesRepository.deleteById(id);
        logger.info("Deleting commandes with id: " + id);
    }
}
