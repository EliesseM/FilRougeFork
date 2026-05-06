# Projet fil Rouge (Backend)

## Pré-Requis

- maven 3.9.x 
- Java 21
- Docker
- (optionnel) IntelliJ - pour les configurations de run

## Installation

### Création de votre token Github

- Dans github, aller dans les settings de votre compte
- Aller dans la section _Developer settings_ (la dernière)
- Choisir _personal access token > Tokens (classic)_
- Cliquer sur **Generate new token**
- Dans le champ **Note** saisir `maven` (ou un nom proche si ce nom est déjà utilisé)
- Dans la liste des checkbox cliquer sur `read:packages`
- Puis sauvegarder
- Github vous donnera un token au format `ghp_[...]`, copier ce token
- 
### Ajout de la conf maven

- Aller dans votre dossier user sur votre PC
- Créer un dossier `.m2` si ce n'est pas déjà fait
- Dans ce dossier créer un fichier `settings.xml` (ou éditer le s'il est déjà présent)

```xml

<settings xmlns="https://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="https://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <!-- Nouveau server à ajouter -->
    <server>
      <id>github</id>
      <username>[votre compte github]</username>
      <password>[le token créé précédemment]</password>
    </server>
  </servers>

</settings>
```

## Lancement de l'application

- la configuration de run de l'application springboot démarre un conteneur postgresql avec le port 5432 exposé.
- la configuration de la BDD peut être surchargée via les variable d'environnement :
  - DB_JDBC_URL
  - DB_USERNAME
  - DB_PASSWORD
