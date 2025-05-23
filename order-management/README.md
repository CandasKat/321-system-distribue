## Instructions pour exécution

1. **Cloner le dépôt :**  
   Clonez ce dépôt sur votre machine :
   ```bash
   git clone https://github.com/CandasKat/order-management.git
   ```

2. **Naviguer dans le répertoire du projet :**
   ```bash
   cd order-management
   ```

3. **Prérequis :** 
   Assurez-vous que les outils suivants sont installés sur votre machine :
   - Java (version 17 ou supérieure recommandée)
   - Gradle (le projet inclut le wrapper Gradle, ce qui permet d'utiliser la commande ./gradlew)

4. **Compiler et construire le projet :** Utilisez le wrapper Gradle pour nettoyer et construire le projet :
   ```bash
   ./gradlew clean build
   ```
   (Sous Windows, utilisez gradlew.bat à la place de ./gradlew)

5. **Exécuter l'application :** Démarrez l'application avec la commande suivante :
   ```bash
   ./gradlew bootRun
   ```

L'application démarrera par défaut sur le port **8080**.

## URL d'accès à Swagger UI
Une fois l'application démarrée, vous pouvez accéder à l'interface Swagger UI via l'URL suivante :
[swagger ui](http://localhost:8080/swagger-ui/index.html)

## Configuration Docker

### Construction de l'image Docker

Pour construire l'image Docker :

```bash
docker build -t order-management:latest .
```

### Exécution du conteneur Docker

Pour exécuter le conteneur de manière autonome (non recommandé pour la production) :

```bash
docker run -p 8080:8080 order-management:latest
```

## Intégration avec Docker Compose

Ce service est conçu pour être intégré avec Docker Compose. Voici un exemple de configuration docker-compose.yml qui peut être utilisé :

```yaml
version: '3.8'

services:
  order-management:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/commandes
      - SPRING_DATASOURCE_USERNAME=candas
      - SPRING_DATASOURCE_PASSWORD=2003
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_RABBITMQ_PORT=5672
      - SPRING_RABBITMQ_USERNAME=guest
      - SPRING_RABBITMQ_PASSWORD=guest
    depends_on:
      - postgres
      - rabbitmq

  postgres:
    image: postgres:latest
    environment:
      - POSTGRES_DB=commandes
      - POSTGRES_USER=candas
      - POSTGRES_PASSWORD=2003
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq

volumes:
  postgres-data:
  rabbitmq-data:
```

## Variables d'environnement

Les variables d'environnement suivantes peuvent être configurées :

| Variable | Description | Valeur par défaut |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | URL JDBC pour PostgreSQL | jdbc:postgresql://postgres:5432/commandes |
| SPRING_DATASOURCE_USERNAME | Nom d'utilisateur de la base de données | candas |
| SPRING_DATASOURCE_PASSWORD | Mot de passe de la base de données | 2003 |
| SPRING_RABBITMQ_HOST | Hôte RabbitMQ | rabbitmq |
| SPRING_RABBITMQ_PORT | Port RabbitMQ | 5672 |
| SPRING_RABBITMQ_USERNAME | Nom d'utilisateur RabbitMQ | guest |
| SPRING_RABBITMQ_PASSWORD | Mot de passe RabbitMQ | guest |
