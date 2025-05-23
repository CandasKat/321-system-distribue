package com.candas.order_management.service;

import com.candas.order_management.config.RabbitMQConfig;
import com.candas.order_management.models.CommandesProduits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductMessageService {
    private static final Logger logger = LoggerFactory.getLogger(ProductMessageService.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ProductMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendProductCreatedMessage(CommandesProduits commandesProduits) {
        sendProductMessage(commandesProduits, "CREATED");
    }

    public void sendProductUpdatedMessage(CommandesProduits commandesProduits) {
        sendProductMessage(commandesProduits, "UPDATED");
    }

    public void sendProductDeletedMessage(CommandesProduits commandesProduits) {
        sendProductMessage(commandesProduits, "DELETED");
    }

    private void sendProductMessage(CommandesProduits commandesProduits, String action) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("produit_id", commandesProduits.getProduit_id());
        message.put("quantite", commandesProduits.getQuantite());
        message.put("commande_id", commandesProduits.getCommande().getId());

        logger.info("Sending {} message to product service for product ID: {}, quantity: {}", 
                action, commandesProduits.getProduit_id(), commandesProduits.getQuantite());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}