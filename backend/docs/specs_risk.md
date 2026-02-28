# Spécifications Optimisées : GRC (Governance, Risk & Compliance)

## 🏗 Structure du Projet

* **Architecture** : Monorepo
* **Dossiers** : `/backend` (Quarkus), `/frontend` (VueJS), `/docker` (Orchestration)
* **Base de données** : PostgreSQL avec schémas relationnels stricts.

## 🔧 Backend (Quarkus + Hibernate Panache)

Le backend gère la logique de score de risque et la sécurité RBAC.

### Modèle de Données Enrichi (Entités)

* 
**Utilisateur** : `id (UUID)`, `username` (pour LDAP), `nom`, `prenom`, `email`, `role` (ADMIN, AUDITEUR, RESPONSABLE, LECTEUR), `last_login`, `is_active`. 


* 
**Risque** : `id`, `libelle`, `description`, `probabilite` (1-3), `gravite` (1-3), `score` (Calculé : $P \times G$), `statut` (OUVERT, EN_COURS, CLOTURE), `proprietaire_id`, `date_creation`. 


* 
**Audit** : `id`, `nom`, `description`, `date_realisation`, `auditeur_id`, `statut_audit` (PLANIFIE, TERMINE). 


* 
**Recommandation** : `id`, `description`, `statut` (A_TRAITER, EN_COURS, TERMINE), `audit_id`, `priorite` (HAUTE, MOYENNE, BASSE). 


* 
**PlanAction** : `id`, `nom`, `description`, `date_debut`, `date_fin`, `responsable_id`, `risque_id`, `taux_avancement` (0-100%). 


* 
**Incident** : `id`, `description`, `date_occurence`, `gravite`, `risque_id`, `impact_reel` (Financier, Image, Opérationnel). 



### Logique Métier & Sécurité

* **Calcul Automatique** : Tout changement de `probabilité` ou `gravité` déclenche une mise à jour du `score` de risque.
* 
**Validation LDAP/SAML** : Authentification via fournisseur d'identité externe obligatoire. 

* 
**Service de Notification** : Envoi d'emails via `quarkus-mailer` lors de la création d'un risque "Très Élevé" ou d'un retard de plan d'action. 


* 
**Audit Log** : Intercepteur Hibernate pour enregistrer chaque modification (Qui, Quand, Quoi) afin d'assurer la traçabilité. 


## 🎨 Frontend (Vue.js 3 + Pinia + Tailwind)

L'interface doit être réactive (Composition API) et strictement typée.

### Modules UI

* **Heatmap des Risques** : Matrice 3x3 interactive affichant le nombre de risques par intersection Probabilité/Gravité.
* 
**Filtres Dynamiques** : Recherche textuelle sur le libellé, filtrage par score de criticité et par propriétaire. 


* 
**Dashboard de Suivi** : Visualisation de l'avancement global des plans d'action (Progress bars). 


* 
**Gestion des Exports** : Génération de rapports PDF (via librairie type `jspdf`) et exports Excel. 


## Update

1. Gestion des Risques 

**Calcul Automatique** : Le système doit calculer $Score = Probabilité \times Gravité$ à chaque création/modification.


* **Règles CRUD** :
* 
**Statuts** : Passage entre `OUVERT`, `EN_COURS`, et `CLÔTURÉ`.


* 
**Suppression** : **Interdite** si le risque est lié à un Incident, un Audit ou un Plan d'action.


* 
**Recherche** : Filtres dynamiques par libellé, criticité et statut. Elle ne doit pas etre case sensitive.


2. Plans d'Action & Suivi 

* **Règles CRUD** :
* 
**Validation** : La `date_fin` doit être strictement supérieure à la `date_debut`.


* 
**Traçabilité** : Chaque mise à jour génère une entrée automatique dans `SuiviPlanAction` avec un commentaire et la date .


* 
**Notifications** : Envoi d'email automatique via `quarkus-mailer` si le plan est en retard.


3. Audits & Recommandations 

* **Règles CRUD** :
* 
**Intégrité** : Une recommandation est obligatoirement rattachée à un audit.

* 
**Clôture** : Un audit ne peut passer à `TERMINÉ` que si ses recommandations sont traitées.

4. Utilisateurs & Sécurité 

* **Règles CRUD** :
* 
**Authentification** : Support sécurisé via LDAP/OpenID/SAML.


* 
**Suppression** : **Interdite** si l'utilisateur est propriétaire d'un risque ou responsable d'un plan d'action.


* 
**Audit Log** : Enregistrement de chaque action (Qui, Quand, Quoi) pour la traçabilité.

## 🎨 Interface Utilisateur (UX/UI)

* 
**Dashboard** : Doit afficher une **Heatmap 3x3** (Matrice de criticité) interactive.


* 
**Formulaires** : Validation réactive (champs obligatoires, formats dates et emails).


* 
**Exports** : Boutons d'exportation en PDF et Excel pour les risques et rapports.


## 🐳 Déploiement & Migration

* **Docker-Compose** : Orchestration de 3 conteneurs (App, Web, DB) avec volumes persistants pour PostgreSQL.
* 
**Plan de Reprise ARENGIBOX** : Script de migration SQL/Java pour transformer les données sources vers le nouveau modèle PostgreSQL. 


* 
**Disponibilité** : Configuration de HealthChecks Quarkus pour garantir le SLA de 97,5%.