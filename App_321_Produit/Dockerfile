# Utilise une image officielle de Python
FROM python:3.11-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers de dépendances
COPY requirements.txt .

# Installer les dépendances
RUN pip install --no-cache-dir -r requirements.txt

# Copier le code source de l'application
COPY . .

# Make the start script executable
RUN chmod +x start.sh

EXPOSE 8000

# Lancer l'application avec le script de démarrage
CMD ["./start.sh"]
