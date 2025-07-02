Ce projet a été réalisé dans le cadre d'un projet scolaire de fin d'études (CFC Développement d'applications). Il s'agit d'un système de gestion des commandes développé avec Spring Boot (Java) et FastAPI (Python), reposant sur une architecture distribuée.

Les services communiquent entre eux via RabbitMQ et utilisent PostgreSQL comme base de données. Le système peut être exécuté localement pour le développement ou déployé en production à l’aide de Docker Swarm.



## 📦 Contenu du projet
Le système se compose des services suivants :

| Nom du service   | Technologie      | Description                                    |
| ---------------- | ---------------- | ---------------------------------------------- |
| order-management | Spring Boot      | Gestion des commandes et des produits associés |
| product-service  | FastAPI          | Catalogue de produits et API                   |
| postgres         | PostgreSQL       | Base de données principale                     |
| rabbitmq         | RabbitMQ         | Système de messagerie                          |
| traefik          | Traefik          | Reverse proxy et routage                       |
| visualizer       | Swarm visualizer | Outil de visualisation du cluster              |

---
## ➤ Description du pattern Work Queues
Le pattern Work Queues permet de distribuer des tâches longues ou intensives entre plusieurs workers (consommateurs) pour équilibrer la charge. RabbitMQ joue ici le rôle de répartiteur de messages : un producteur (publisher) envoie un message dans une file (queue), et un ou plusieurs consommateurs (workers) se chargent de traiter chaque message.

## ➤ Utilisation dans l'application
L'API Spring Boot publie un message dans la queue product_queue chaque fois qu’un commande est créé, modifié ou supprimé. Un service worker écrit en Python consomme ces messages via RabbitMQ pour simuler un traitement en arrière-plan pour mettre à jour de stock global qui se trouve dans le service de FastAPI.

## ➤ Plus-value de RabbitMQ
- Asynchrone : l’API FastAPI reste rapide, même si le traitement est lourd.
- Fiabilité : les messages sont persistants (delivery_mode=2), donc même si le worker tombe, RabbitMQ les garde.
- Scalabilité : plusieurs workers peuvent consommer la même queue pour répartir la charge.
- Découplage : l’API ne dépend pas du traitement en aval. Elle peut évoluer indépendamment du consommateur.
## Fonctionnalités en bref

### ➤ Application micro-service : Spring Boot REST API et FastAPI
- CRUD Produits et Commandes (Create, Read, Update, Delete)
- Envoi d’un message RabbitMQ à chaque opération (création, mise à jour, suppression)

### ➤ Microservice Worker Python
- Consomme les messages de la queue product_queue
- Simule un traitement (par exemple : journalisation ou mise à jour d’un système tiers)


## 🚀  Démarrage rapide
### 🧰 Prérequis
* `Docker`
* `Docker Compose`
* `Docker Swarm` (pour la production)
* (Optionnel pour le développement) Java 17+, Python 3.10+
---
## 🐳 Exécution avec Docker
### Utilisation de Docker Compose (mode développement)
```bash
docker-compose up --build
```
Ce fichier compose démarre les services suivants :

* order-management (Spring Boot)
* product-service (FastAPI)
* postgres
* rabbitmq
---
## ⚙️ Déploiement avec Docker Swarm
### 1. Initialiser Swarm
```bash
docker swarm init
```
### 2. Déployer la stack
```bash
docker stack deploy -c docker-compose.yml ordersystem
```
### 3. Accès aux interfaces
| Service                | URL                                       | Credentials                     |
| ---------------------- | ----------------------------------------- | ------------------------------- |
| Dashboard Traefik      | http://localhost:8080                     |                                 |
| Swagger Spring Boot    | http://localhost:81/swagger-ui/index.html |                                 |
| Swagger FastAPI        | http://localhost:81/docs                  |                                 |
| Visualiseur Swarm      | http://localhost:8085                     |                                 |
| RabbitMQ Management UI | http://localhost:15672                    | user: guest,<br>password: guest |

---
## 🔁 Points d’accès API
### Spring Boot (Java)
* `GET /commandes` – Liste des commandes
* `GET /commandes/{id}` - Detail un commande
* `POST /commandes` – Créer une nouvelle commande
* `PUT /commandes/{id}` - Update un commande existant
* `DELETE /commandes/{id}` - Supprimer un commande

* `GET /commandes-produits` – Détails des produits d'une commande
* `GET /commandes-produits/{id}` - Detail des produit d'un commande
* `POST /commandes-produits` – Créer une nouvelle produit commande
* `PUT /commandes-produits/{id}` - Update de produit d'un commande existant
* `DELETE /commandes-produits/{id}` - Supprimer de produit d'un commande
---
## FastAPI (Python)
* `GET /produits/` – Liste des produits
* `GET /produits/{id}` - Detail d'un produit
* `POST /produits/` – Ajouter un nouveau produit
* `PUT /produits/{id}` - Editer un produit existant
* `DELETE /produits/{id}` - Supprimer un produit
---
## 📌 Variables d’environnement
Les variables suivantes peuvent être définies dans le fichier .env ou directement dans le docker-compose.yml :

| Variable                              | Description                  |
| ------------------------------------- | ---------------------------- |
| SPRING_DATASOURCE_URL                 | URL JDBC vers PostgreSQL     |
| SPRING_DATASOURCE_USERNAME / PASSWORD | Identifiants base de données |
| SPRING_RABBITMQ_HOST / PORT           | Adresse RabbitMQ             |
| POSTGRES_USER / PASSWORD              | Connexion PostgreSQL         |

---
## ⚠️ Remarques importantes
RabbitMQ utilise par défaut l’identifiant `guest/guest`.

Le service FastAPI dépend d’un consommateur (`consumer.py`) qui doit être actif pour recevoir les messages.

Traefik assure la gestion des routes et du reverse proxy.

Les deux services utilisent la même base de données commandes

---
## Utilisation de RabbitMQ (détail)

Chaque fois qu’un produit est :
- Créé : un message avec action CREATED est envoyé
- Mis à jour : message avec action UPDATED
- Supprimé : message avec action DELETED

Exemple de message JSON publié :
```json
{
  "action": "UPDATED",
  "produit_id": 5,
  "quantite": 12,
  "commande_id": 0
}
```

Le service consumer-python écoute la queue product_queue, extrait les informations, et effectue un traitement simulé avec un print ou une action métier.

## À venir ou suggestions d’évolution

- Ajouter un stockage ou une base de données pour tracer les actions des messages consommés
- Ajouter d’autres types d’événements (ex : "produit épuisé", "alerte stock bas")
- Intégration monitoring avec Prometheus + Grafana
