# SpringBoot-projet
Version Spring Boot du projet de gestion RH, départements, projets et fiches de paie.


# Installer node 22 : 

Si tu es sur ubuntu :

## Etape 1 : Supprime l’ancien Node Ubuntu

```bash
sudo apt remove -y nodejs libnode-dev
sudo apt purge -y nodejs libnode-dev
sudo apt autoremove -y
```

## Nettoie les sources Node existantes

```bash
sudo rm -rf /etc/apt/sources.list.d/nodesource.list
sudo apt update
```

## Etape 3 : Installe Node.js 22

```bash
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt install -y nodejs
```

## Etape 4 : Vérifie 

```bash
node -v
npm -v
```

Tu devrais avoir : 
v22.12.0
npm 10 ou 11


# Puis installe angular CLI

```bash
npm install -g @angular/cli
```

# Lancer frontend angular 

## Installer tous les modules

```bash
npm install 
```

## Lancer le serveur 

```bash
ng serve
```
