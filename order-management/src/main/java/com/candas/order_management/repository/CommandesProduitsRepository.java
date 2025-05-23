package com.candas.order_management.repository;

import com.candas.order_management.models.CommandesProduits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandesProduitsRepository extends JpaRepository<CommandesProduits, Long> {
}
