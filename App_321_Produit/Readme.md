# Microservice Catalogue & Commandes

## 📌 Instructions pour exécution

### 1️⃣ Installer les dépendances
Assurez-vous d'avoir Python installé, puis exécutez :
```bash
pip install -r requirements.txt
```

### 2️⃣ Lancer l'application

#### Option A: Utiliser les scripts de démarrage
Ces scripts lancent à la fois l'API FastAPI et le consommateur RabbitMQ.

**Sur Linux/Mac:**
```bash
chmod +x start.sh
./start.sh
```

**Sur Windows:**
```
start.bat
```

#### Option B: Lancer les composants séparément

**Terminal 1 - API FastAPI:**
```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

**Terminal 2 - Consommateur RabbitMQ:**
```bash
python consumer.py
```

### 3️⃣ Utiliser Docker
Pour exécuter l'application avec Docker:

```bash
docker build -t app_321_produit .
docker run -p 8000:8000 app_321_produit
```

Ou avec docker-compose:
```bash
docker-compose up
```

### 4️⃣ Accéder à l'API
L'API sera disponible à l'adresse :
```
http://127.0.0.1:8000
```

## 🚀 URL d'accès à Swagger UI
Swagger UI permet de tester les endpoints de l'API facilement. Accédez à :
```
http://127.0.0.1:8000/docs
```

## 📝 Notes importantes
- Le consommateur RabbitMQ doit être en cours d'exécution pour recevoir les messages de l'application Spring
- L'application essaiera automatiquement de se connecter à RabbitMQ en utilisant différentes adresses ("rabbitmq", "localhost", "127.0.0.1"), ce qui permet de fonctionner à la fois dans Docker et en local
- L'application utilise les identifiants par défaut de RabbitMQ (guest/guest)
- Des paramètres de connexion supplémentaires ont été configurés pour améliorer la fiabilité de la connexion à RabbitMQ:
  - Port: 5672 (port AMQP par défaut)
  - Virtual host: / (virtual host par défaut)
  - Tentatives de connexion: 3
  - Délai entre les tentatives: 5 secondes
  - Timeout de socket: 15 secondes
- Si vous utilisez une autre adresse, d'autres identifiants ou d'autres paramètres pour RabbitMQ, vous devrez modifier les fichiers consumer.py et publisher.py
