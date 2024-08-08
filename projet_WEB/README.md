---
title: Projet React 
author:  
- LU Yuxuan
- THOR Yung Pheng
--- 

## Cahier des charges

Ici vous décrivez les fonctionnalités souhaitées et celles effectivement mises en oeuvre. Avec un diagramme UML des cas d'usage et des maquettes des vues souhaitées et des captures d'écran de ce qui a été réalisé.

### API mise en place

Donner le lien vers la documentation swagger et/ou faire un tableau récapitulant l'API

|       ENDPOINT            |        GET         |            POST            |        PUT          |      DELETE      |
|---------------------------|--------------------|----------------------------|---------------------|------------------|
|/login||Obtention du token de l utilisateur en postant les informations de connexion/<b>email, password|||
|/register||Créer un password utilisateur /<b>name,email,|||
|/api/users|(TOKEN)Lister tous les utilisateurs||||
|/api/password|||(TOKEN)Mettre à jour le mot de passe de l'utilisateur connecté/<b>password||
|/api/users/{id}|||(TOKEN)(ADMIN)Mettre à jour les informations de l'utilisateur /<b>name,email, password,isAdmin|(TOKEN)(ADMIN)Supprimer un utilisateur|
|/api/mygroups|(TOKEN)Lister les groupes créés et donc gérés par l'utilisateur|(TOKEN)Créer un groupe il appartiendra à son créateur/<b>name|||
|/api/mygroups/{gid}|(TOKEN)Lister les membres du groupe|||<b>(TOKEN)(ADMIN/CREATEUR)Supprime un groupe|
|/api/mygroups/{gid}/{uid}|||(TOKEN)Ajouter un utilisateur dans un groupe|(TOKEN)Supprimer un utilisateur d'un groupe|
|/api/groupsmember|(TOKEN)Lister les groupes dont l'utilisateur est membre||||
|/api/messages/{gid}|(TOKEN)(MEMBER) Lister tous les messages postés dans un groupe|(TOKEN)(MEMBER)Ajouter un message dans un groupe /<b>content|||

## Architecture du code

### FrontEnd

Le frontend correspond à la partie Vue dans l'architecture MVC de l'application. Le point d'entrée de partie frontend est src/main.jsx. Les codes sources sont organisés en 2 répertoire: components et views. 

i. View (src/views)
- On a choisit d'utiliser uniquement une page dans notre application. Donc, ce répertoire contient un seul fichier jsx (src/views/App.jsx) qui correspond au page principal de notre application et un fichier App.css qui contient tous les styles liées à ce fichier. 

ii. Components (src/components)
Tous les éléments dans l'application sont des composants contenant dans le composant principal (src/views/App.js). On voit le composant Login (src/components/Login.jsx) et Register (src/components/Register.jsx). Une fois connecté, le composant Accueil (src/components/Accueil.jsx) qui contient le composant MyGroups (src/components/MyGroups.jsx) lui-même contient un case input pour créer les groupes et deux sous composants:
- GroupesMembre (src/components/GroupesMembre.jsx) qui contient des groupes dont utilisateur est membre
- GroupesAdmin (src/components/GroupesAdmin.jsx) qui contient des groupes dont utilisateur est administrateur. 

Quand l'utilisateur clique sur un group, le composant Extra (src/components/Extra.jsx) est affiché. Ce composant est un sous-composant du composant Accueil. En fonction de ce que l'utilisateur a choisit, le composant va contenir le sous-composant different. 

Si l'utilisateur choisit un groupe dont il est membre, le composant Chat (src/components/Chat.jsx) est affiché. Si l'utilisateur choisit un groupes dont il est administrateur, le composant GroupAdministration (src/components/GroupAdministration.jsx) est affiché. 


### Backend

#### Schéma de votre base de donnée

```plantuml
class Users{
  id
  name
  email
  passhash
  isAdmin : boolean
}

class Messages{
  id
  content
  createAt
}

class Groups{
  id
  name
}

Users "1" -- "n" Messages : posts
Groups "1" -- "n" Messages : contains

Users "n" -- "n"  Groups : is member 
Users "1" -- "n"  Groups : create and own
```

Pour réaliser ce diagramme Entité-Association, on a créé quatre tables:
- User (id, name, email, passhash, isAdmin)
- Groups (id, name, ownerId)
  - avec ownerId un foreign key de User(id)
- UserGroups (memberId, groupId)
  - avec memberId un foreign key de User(id)
  - et groupId un foreign key de Groups(id)
- messagesGroups (id, content, createdAt, userId, groupId)
  - avec userId un foreign key de User(id)
  - et groupId un foreign key de Groups(id)

#### Architecture de votre code

L'architecture de l'appication utilisée est de l'architecture MVC, où la partie vue gérée par le frontend, tandis que le model et le controller gérés par le backend. Le point d'entré de l'application est app.js aui utilise src/routes/router.js pour router les differents points d'API. 

Le backend est donc diviser en 3 parties, avec chaque partie divisé encore en 3 parties qui gèrent les besoins d'utilisateur differents. On a donc:

- Le répertoire router (src/routes) qui contient les differents routes de l'API et les sous-routes de l'API divisés en 3 parties
  - src/routes/router.js qui est le router principal de l'application
  - src/routes/user.js qui contient les routes liées au utilisateur
  - src/routes/groups.js qui contient les routes liées au groupe
  - src/routes/messages.js qui contient les routes liées au message

- Le répertoire controller (src/controllers) qui contient le logic principal de l'application et sont appelés par le frontend, avec les 3 parties:
  - src/controllers/user.js qui gère les utilisateurs comme la création d'un utilisateur
  - src/controllers/groups.js qui gère les groupes comme la création d'un groupe
  - src/controllers/message.js qui gère les messages comme l'envoie d'un message dans un group

- Le répertoire model (src/models) qui contient les fichiers qui définis les schemas de la base de donnée
  - src/models/user.js qui défini le schema d'un utilisateur
  - src/models/groups.js qui défini le schema d'un groupe et un userGroup (qui stocke les membres des groupes)
  - src/models/messages.js qui défini le schema d'un message

Les tests se trouvent dans le répertoire \__tests__.
Le fichier de support qui n'entre pas dans ces catégories se trouve dans le répertoire utils (src/util). 

### Gestion des rôles et droits

- Coté backend  

1. Rôle Utilisateur  
- Description : Le rôle de base attribué à tout utilisateur enregistré.  
- Droits : Accès aux fonctionnalités de base (Login, Logout, creer des groupes). Modifier ses propres password.  
- Gestion : Identifié dans la base de données.  

2. Rôle Propriétaire d'un Groupe  
- Description : Le rôle attribué aux utilisateurs qui créent et gèrent un groupe.  
- Droits : Toutes les permissions des utilisateurs classiques. Ajouter/supprimer des membres.  
- Gestion : Un champ spécifique (ownerId) identifie le propriétaire du groupe dans la base de données.  

3. Rôle Admin  
- Description : Rôle d'administrateur ayant des permissions étendues pour gérer l'ensemble du système.  
- Droits : Toutes les permissions des utilisateurs et propriétaires. Accéder et modifier tous les utilisateurs (suppression). Créer ou supprimer n'importe quel groupe.  
- Gestion : Identifié dans la base de données avec un champ dédié (admin : true).  

- Coté frontend  

de meme que coté backend

## Test

### Backend

On a choisi de tester notre backend avec trois parties : l'API utilisateur, l'API groupes et l'API messages.

1. Test de l'API utilisateur (api.user.test.js) :   

1.1. Test de Connexion de l'Utilisateur :  
- Objectif : S'assurer que le processus d'authentification fonctionne correctement.  
- Détails : Ce test vérifie que l'utilisateur peut se connecter avec des identifiants valides et reçoit en retour un token d'authentification, conforme aux attentes.  

1.2. Test d'Enregistrement de l'Utilisateur :    
- Objectif : Confirmer que le système d'enregistrement crée des utilisateurs conformément aux critères définis et gère correctement les erreurs.  
- Détails : Le système doit permettre la création de nouveaux comptes avec des informations valides. Les erreurs sont testées et gérées en cas de données non valides telles qu'un courriel incorrect, un mot de passe faible ou des mots de passe de confirmation non concordants.  

1.3. Test de Récupération des Données Utilisateur :   
- Objectif : Vérifier que les utilisateurs authentifiés peuvent récupérer correctement une liste des utilisateurs.  
- Détails : Ce test s'assure que, après une authentification réussie, les utilisateurs ont accès aux données appropriées selon leurs permissions.  

1.4. Connexion Utilisateur & Mise à jour du Mot de Passe :  
- Objectif : Valider que les utilisateurs connectés peuvent mettre à jour leur mot de passe en toute sécurité.  
- Détails : Les tests s'assurent que les utilisateurs peuvent changer leur mot de passe uniquement après une authentification réussie.  

1.5. Autorisation Admin :  
1.5.1. Suppression d'Utilisateur par l'Administrateur :  
- Objectif : Vérifier qu'un administrateur a le droit de supprimer des comptes utilisateur.  
- Détails : Les tests garantissent que seules les personnes d'administration peuvent effectuer cette action.  

1.5.2. Mise à Jour des Informations de l'Utilisateur par l'Administrateur :  
- Objectif : Confirmer que les administrateurs peuvent modifier les informations des utilisateurs.  
- Détails : Les tests garantissent que seules les personnes d'administration peuvent effectuer cette action.  

2. test de l'API des groupes (api.groups.test.js) :   

2.1. Gestion des Groupes :  

2.1.1. Création de Groupes :  
- Objectif : Assurer que les utilisateurs connectés peuvent créer de nouveaux groupes avec les paramètres corrects.  
- Détails : Vérification que le processus de création respecte les contraintes définies.  

2.1.2. Liste des Groupes :  
- Objectif : Vérifier que les utilisateurs connectés peuvent accéder à la liste de leurs groupes.  
- Détails : Tests pour s'assurer que seuls les groupes créés ou auxquels l'utilisateur est membre sont listés.  

2.1.3. Liste des Membres :  
- Objectif : Confirmer que les membres d'un groupe peuvent être listés par les utilisateurs connectés.  
- Détails : Les tests valident que les membres sont affichés correctement en fonction des permissions.  

2.1.4. Ajout de Membres :  
- Objectif : Vérifier que les membres peuvent être ajoutés aux groupes créés par l'utilisateur connecté.  
- Détails : Le test simule l'ajout de membres pour s'assurer que seuls les utilisateurs éligibles sont ajoutés.  

2.1.5. Suppression des Membres :  
- Objectif : Confirmer que les membres peuvent être retirés des groupes créés par l'utilisateur connecté.  
- Détails : Le test vérifie que seuls les membres autorisés peuvent être retirés.  

2.2 Fonction Propriétaire/Administrateur de Groupe :  

2.2.1. Suppression de Groupe :  
- Objectif : Assurer que le propriétaire/administrateur peut supprimer un groupe.  
- Détails : Le test s'assure que seul le propriétaire ou l'administrateur peut supprimer le groupe.  

3. Tests de l'API Messages (api.messages.test.js) :  

3.1. Récupération des Messages :  
- Objectif : Vérifier que les membres d'un groupe peuvent récupérer l'ensemble des messages échangés au sein de leur groupe.  
- Détails : Les tests valident que seules les membres ont accès aux messages du groupe.  

3.2. Envoi de Messages :  
- Objectif : Assurer que les membres d'un groupe peuvent envoyer des messages et que ces messages sont sauvegardés avec précision dans la base de données.  
- Détails : Les tests vérifient que les messages envoyés par les membres du groupe sont correctement reçus et enregistrés.  

4. Couverture  

File             | % Stmts | % Branch | % Funcs | % Lines | Uncovered Line #s
-----------------|---------|----------|---------|---------|-------------------
All files        |   89.61 |    67.34 |   96.42 |   96.21 |
 src             |   96.15 |      100 |      50 |   96.15 |
  app.js         |   96.15 |      100 |      50 |   96.15 | 44
 src/controllers |   85.57 |     67.7 |     100 |   94.88 |
  groups.js      |   84.09 |    64.28 |     100 |    93.5 | 13,16,37-38,57
  message.js     |   85.36 |    72.72 |     100 |   94.28 | 14,17
  user.js        |    87.5 |    68.75 |     100 |   96.87 | 17,20
 src/models      |     100 |      100 |     100 |     100 |
  database.js    |     100 |      100 |     100 |     100 |
  groups.js      |     100 |      100 |     100 |     100 |
  messages.js    |     100 |      100 |     100 |     100 |
  users.js       |     100 |      100 |     100 |     100 |
 src/routes      |     100 |      100 |     100 |     100 |
  groups.js      |     100 |      100 |     100 |     100 |
  messages.js    |     100 |      100 |     100 |     100 |
  router.js      |     100 |      100 |     100 |     100 |
  user.js        |     100 |      100 |     100 |     100 |
 src/util        |     100 |       50 |     100 |     100 |
  CodeError.js   |     100 |      100 |     100 |     100 |
  logger.js      |     100 |       50 |     100 |     100 | 10

### Frontend

Nous avons divisé le test en quatre parties :  

- Connexion de l'utilisateur
- Création de groupes par les utilisateurs connectés
- Chat par les utilisateurs connectés en tant que membres du groupe
- Ajout et suppression de membres du groupe par les utilisateurs en tant que propriétaires du groupe.

## Intégration + déploiement (/3)

Le backend est hebergé sur Scalingo et il est lié au Gitlab avec yml en utilisant le API token du Scalingo et le personal token du Gitlab. 

Les étapes de CI/CD peuvent être diviser en 3 parties.
- Verification de la style de programmation avec lint
- Test du backend et du frontend avec Cypress
- deployement du frontend sur le serveur Gitlab Ensimag et deployment du backend sur Scalingo

Les étapes sont décrits sur un fichier yml et sera déclencher pour chaque push sur Gitlab. 

Le page final est disponible sur: https://tp5-6-backend-react-yung-pheng-thor-yuxuan-lu-gr-49d584e949f43a.pages.ensimag.fr

## Installation

Après cloner le dépôt Git, naviger sur le répertoire backend depuis le répertoire principal, installer tous les dépendances de l'application et lancer du server node. 
```bash
cd backend
npm install
npm run start
```
Le server backend est lancé sur la port 3000. 

Sur un autre terminal, naviger sur le répertoire frontend depuis le répertoire principal. Il faut changer tous les liens dans fetch du frontend à http://localhost:3000. Puis, il faut installer tous les dépandances de l'application et lancer du server frontend. 

```bash
cd frontend
npm install
npm run dev
```
Le server frontend est donc lancé sur la port 5173.

L'application est donc prêt à utiliser. 

Dans votre navigateur préféré, accédez à http://localhost:5173 pour consulter la page de l'application.




