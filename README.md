# README - Projet SpringBoot + Angular

## 📋 Description du projet

Application de gestion d'entreprise développée avec **Spring Boot** (backend) et **Angular** (frontend), utilisant PostgreSQL comme base de données et Docker pour la conteneurisation.

## 🏗️ Architecture du projet

```
SpringBoot-projet/
├── backend/          # Application Spring Boot
├── frontend/         # Application Angular
└── docker/           # Configuration Docker
```

## 🔧 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java 17+** (pour Spring Boot)
- **Node.js 18+** et **npm** (pour Angular)
- **Docker** et **Docker Compose**
- **Maven** (inclus via wrapper mvnw)
- **Git**

## 🚀 Installation et lancement

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/SaadTr4/SpringBoot-projet.git
cd SpringBoot-projet
```

---

## 🐳 Configuration Docker & Base de données

### 2️⃣ Démarrer les conteneurs Docker

Depuis la racine du projet :

```bash
docker compose -f backend/docker/docker-compose.yml up -d
```

Cette commande lance :
- **PostgreSQL** (port 5433)
- **pgAdmin** (port 8081)

### 3️⃣ Vérifier que les conteneurs sont actifs

```bash
docker ps
```

### 4️⃣ Accéder à pgAdmin

Ouvrez votre navigateur et accédez à : **http://localhost:8081**

**Identifiants pgAdmin :**
- Email : `admin@admin.com`
- Mot de passe : `admin`

**Configuration du serveur PostgreSQL dans pgAdmin :**
- Nom : `SpringBoot-Server` (au choix)
- Hôte : `db`
- Port : `5432`
- Base de données : `cytech_entreprise`
- Nom d'utilisateur : `cytech_user`
- Mot de passe : `CyT3ch2025!`

### 5️⃣ Accéder directement à PostgreSQL (optionnel)

**Depuis un conteneur Docker :**
```bash
docker exec -it projetjee-db psql -U cytech_user -d cytech_entreprise
```

**Depuis votre machine (client psql installé) :**
```bash
psql -h localhost -p 5433 -U cytech_user -d cytech_entreprise
```

---

## ☕ Backend - Spring Boot

### 6️⃣ Configuration

Le fichier `backend/src/main/resources/application.properties` contient la configuration de connexion à la base de données. Vérifiez que les paramètres correspondent à votre environnement Docker.

### 7️⃣ Installer les dépendances et compiler

Depuis le dossier `backend/` :

```bash
cd backend
./mvnw clean install
```

Ou sous Windows :
```bash
mvnw.cmd clean install
```

### 8️⃣ Lancer l'application Spring Boot

```bash
./mvnw spring-boot:run
```

Ou sous Windows :
```bash
mvnw.cmd spring-boot:run
```

Le backend sera accessible sur : **http://localhost:8080**

### 9️⃣ Générer le fichier JAR (optionnel)

```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

## 🅰️ Frontend - Angular

### 🔟 Installer les dépendances

Depuis le dossier `frontend/` :

```bash
cd frontend
npm install
```

### 1️⃣1️⃣ Lancer le serveur de développement

```bash
ng serve
```

Ou :
```bash
npm start
```

L'application Angular sera accessible sur : **http://localhost:4200**

### 1️⃣2️⃣ Compiler pour la production

```bash
ng build
```

Les fichiers compilés seront dans le dossier `dist/`.

---

## 🧪 Tests

### Tests Backend (Spring Boot)

```bash
cd backend
./mvnw test
```

### Tests Frontend (Angular)

```bash
cd frontend
ng test
```

Pour les tests end-to-end :
```bash
ng e2e
```

---

## 🛠️ Commandes Docker utiles

### Arrêter les conteneurs

```bash
docker compose -f backend/docker/docker-compose.yml down
```

### Reconstruire complètement la base de données

```bash
docker compose -f backend/docker/docker-compose.yml down
docker volume rm docker_jee-dev_postgres_data
docker compose -f backend/docker/docker-compose.yml up -d
```

### Voir les logs des conteneurs

```bash
docker compose -f backend/docker/docker-compose.yml logs -f
```

---

## 📂 Structure du projet

### Backend

```
backend/
├── src/main/java/fr/springboot/backend/
│   ├── controller/       # Contrôleurs REST
│   ├── service/          # Logique métier
│   ├── repository/       # Accès aux données (JPA)
│   ├── model/            # Entités JPA
│   ├── dto/              # Data Transfer Objects
│   ├── enums/            # Énumérations
│   ├── util/             # Classes utilitaires
│   └── SecurityConfig.java
├── src/main/resources/
│   ├── application.properties
│   └── templates/        # Templates HTML
└── docker/               # Configuration Docker
```

### Frontend

```
frontend/
├── src/
│   ├── app/              # Composants Angular
│   ├── assets/           # Ressources statiques
│   └── environments/     # Configuration des environnements
├── public/               # Fichiers publics
└── angular.json          # Configuration Angular
```

---

## 🔐 Authentification

L'application utilise un système d'authentification basé sur les rôles. Les endpoints REST sont sécurisés selon les permissions définies dans `RolePermissions.java`.

---

## 📊 Fonctionnalités principales

- **Gestion des utilisateurs** (création, modification, suppression)
- **Gestion des départements**
- **Gestion des projets**
- **Gestion des positions/postes**
- **Génération de fiches de paie (payslips)**
- **Système d'authentification et d'autorisation**

---

## 🐛 Dépannage

### Problème : Port déjà utilisé (8080 ou 4200)

Changez le port dans les fichiers de configuration ou arrêtez le processus utilisant le port.

### Problème : Connexion à la base de données échoue

Vérifiez que les conteneurs Docker sont bien démarrés et que les paramètres dans `application.properties` sont corrects.

### Problème : Erreur CORS entre frontend et backend

Vérifiez la configuration CORS dans `SecurityConfig.java`.

---

## 📝 Développement

### Générer un nouveau composant Angular

```bash
ng generate component nom-du-composant
```

### Générer un nouveau service Angular

```bash
ng generate service nom-du-service
```

---

## 👥 Contributeurs

Projet développé dans le cadre d'un cours JEE à CY Tech.

---

## 📄 Licence

Ce projet est à usage éducatif.

---

## 📞 Support

Pour toute question, créez une issue sur le dépôt GitHub : https://github.com/SaadTr4/SpringBoot-projet
