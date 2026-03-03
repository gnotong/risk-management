# Authentification et Autorisation dans Risk Management

L'authentification et l'autorisation dans l'application Risk Management sont implémentées en s'appuyant sur le protocole standard OIDC (OpenID Connect) via un serveur Keycloak.

Voici le workflow complet, de bout en bout :

## 1. Front-end (Vue 3) : Verrouillage et Obtention du Token JWT

*   **Fermeture à la racine** : Dès le lancement de l'application dans `frontend/src/main.ts`, le script initialise le client `keycloak-js` (via le plugin `keycloak.ts`) **avant même** de monter la vue principale de l'application.
*   **Redirection Keycloak** : L'option `onLoad: 'login-required'` est utilisée. Si l'utilisateur n'est pas identifié, il est automatiquement redirigé vers la page de connexion hébergée sur le serveur Keycloak (port 8080, realm `risk-realm`).
*   **Maintien de session** : Une fois authentifié, Keycloak redirige l'utilisateur vers l'application avec un Token (JWT). Une boucle (`setInterval`) est mise en place pour vérifier la validité du token toutes les 60 secondes pour le rafraichir dynamiquement silencieusement. S'il expire et ne peut pas être rafraîchi, l'utilisateur est déconnecté.

## 2. Le relais Front vers Backend (Client HTTP Axios)

*   **Interceptor Axios** : Pour discuter avec le backend, l'application utilise une instance d'Axios définie dans `frontend/src/infrastructure/http/httpClient.ts`.
*   **Injection du Badge** : Un `interceptor` de requête agit comme un douanier côté front : pour chaque requête qui part vers le back-end, il récupère le Token JWT fourni par Keycloak et l'attache systématiquement aux Headers de la requête HTTP : `Authorization : Bearer <votre_token>`.

## 3. Back-end (Quarkus) : Validation cryptologique et Autorisation

*   **Authentification automatique** : Le backend Quarkus est configuré (`application.properties`) pour faire confiance au serveur Keycloak. Lorsqu'une requête arrive avec le Token Bearer, Quarkus vérifie immédiatement sa signature cryptographique (pour s'assurer qu'il a bien été généré par Keycloak et non falsifié) et son expiration.
*   **Autorisation via Annotations (RBAC)** : Une fois le token validé, Quarkus lit les rôles à l'intérieur. Dans les contrôleurs métiers du code Java (par exemple `RisqueResource.java`, `AuditResource.java`), l'application utilise des annotations de sécurité standard JAX-RS telles que `@RolesAllowed(...)`.
    *   Si le JWT contient l'un des rôles autorisés fixés pour cette route, la requête atteint le code logique métier.
    *   Sinon, Quarkus bloque purement et simplement la requête avant même qu'elle ne commence à s'exécuter, renvoyant une erreur `403 Forbidden` (Accès interdit) au frontend.

### Résumé

L'accès aux écrans Vue est bloqué tant que l'utilisateur ne s'est pas connecté via la mire Keycloak. Une passe technique (Token JWT) est alors délivrée et présentée à chaque appel API via Axios. Côté Quarkus, la porte ne s'ouvre que si le porteur de la passe dispose sur son badge des rôles (`@RolesAllowed`) exigés par la route ciblée.

---

## Gestion des utilisateurs synchronisée avec Keycloak

Afin de permettre aux administrateurs de créer des comptes directement depuis l'interface, le back-end expose désormais un endpoint protégé qui effectue simultanément :

1. **Création dans Keycloak** via l'API Admin (service `KeycloakAdminService`).
   * Obtention d'un jeton administratif (`admin-cli`) sur le realm `risk-realm`.
   * Envoi d'une requête POST `/admin/realms/{realm}/users` contenant le username, prénom, nom, email et statut actif.
   * Assignation du rôle adéquat (`ADMIN`, `AUDITEUR`, `RESPONSABLE`, `LECTEUR`, etc.) et définition du mot de passe.

2. **Persistance en base** de l'entité `Utilisateur` avec le même username et rôle métier.
   * Méthode `UtilisateurService.createWithKeycloak(CreateUserRequest)` réalise la synchronisation et lève des `BusinessException` en cas de doublon ou d'erreur.

3. **Routes REST** :
   * `POST /api/utilisateurs/register` accessible uniquement aux utilisateurs ayant le rôle `ADMIN`.
   * La ressource `UtilisateurResource` est annotée `@RolesAllowed("ADMIN")` sur ce chemin spécifique, garantissant qu'un simple "USER" ne peut pas appeler cette opération.

4. **Interface Frontend** :
   * Une nouvelle vue `AdminPanel.vue` (route `/admin`) propose un formulaire pour saisir les informations (username, prénom, nom, email, mot de passe, rôle).
   * Seuls les administrateurs (détectés via le rôle extrait du token Keycloak) voient le lien de navigation vers cette page.
   * Après soumission, le backend crée le compte et le reflète immédiatement dans la liste des utilisateurs affichés.

5. **Configuration** :
   * Ajout des propriétés `keycloak.server-url`, `keycloak.admin.username`, `keycloak.admin.password` dans `application.properties`.
   * `risk-realm.json` enrichi avec les rôles métiers et un utilisateur `martin` pour garder la cohérence entre Keycloak et la table `Utilisateur` existante.

**Conséquence** : chaque utilisateur créé via l'UI peut se connecter immédiatement avec les identifiants choisis, et ses droits sont gérés par Keycloak tout en restant référencé dans la base de données métier.

---

