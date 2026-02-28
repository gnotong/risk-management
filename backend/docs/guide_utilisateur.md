# Guide Utilisateur - Risk Management Application

Bienvenue dans l'application de gestion des risques. Ce guide vous explique comment utiliser simplement les fonctionnalités principales.

## 1. Naviguer dans l'application
- **Tableau de bord (Dashboard)** : À votre connexion, vous arrivez sur le tableau de bord. Il affiche une vue globale de tous vos risques et inclut une **"Heatmap" (carte de chaleur)** colorée permettant de trier visuellement les risques par gravité et probabilité.
- **Plans d'action** : Accessible depuis le menu de navigation (en haut), cette page liste tous les plans d'actions en cours et terminés à travers l'entreprise.

## 2. Gérer les Risques
### Créer un risque
1. Sur le tableau de bord, cliquez sur le bouton violet **"Nouveau Risque"**.
2. Remplissez le **Libellé** (titre), la **Description**, et donnez une note de 1 à 3 pour la **Probabilité** et la **Gravité**.
3. **Propriétaire (Obligatoire)** : Vous devez choisir à qui est affecté le risque dans la liste déroulante.
4. Cliquez sur "Enregistrer le risque".

### Modifier le propriétaire d'un risque
1. Cliquez sur "Voir Détails" sur la carte d'un risque.
2. Dans la section Propriétaire, utilisez le **menu déroulant** pour choisir une nouvelle personne. L'enregistrement est automatique !
*Note : Si le risque est "CLOTURE" (fermé), le changement de propriétaire est bloqué en lecture seule.*

## 3. Gérer les Plans d'Action
Les plans d'actions sont des mesures prises pour réduire ou résoudre un risque.

### Ajouter un plan d'action
1. Depuis la page **Détails** d'un risque, cliquez le bouton **"+ Nouveau Plan"**.
2. Renseignez un nom, des dates de début/fin, et une description.

### Faire avancer un plan d'action (et suivre le progrès)
1. Allez sur le menu "Plans d'action" et cliquez sur le plan que vous voulez modifier, ou bien trouvez-le directement depuis les détails de son risque associé.
2. Utilisez le **curseur de progression (slider de 0 à 100%)** pour indiquer l'avancement.
3. Vous pouvez changer le statut manuellement (Non commencé, En cours...).
4. Cliquez sur **Enregistrer**.

*Note magique :* Si vous glissez le curseur à **100%**, le plan d'action passe automatiquement au statut "Terminé" (TERMINE), et le risque lié se ferme instantanément (CLOTURE).

### Ajouter un commentaire ou journaliser (Logs)
1. Dans les détails d'un plan d'action, écrivez votre note dans la boîte **"Nouveau Commentaire"** et cliquez sur "Ajouter".
2. Votre commentaire apparaîtra dans l'historique (Journal) à droite avec la date et l'heure exactes.
3. Si vous avez fait une erreur d'écriture, vous pouvez survoler votre commentaire dans le journal et **cliquer sur la petite corbeille rouge** pour le supprimer. (Sauf si le plan est "Terminé" !)

## 4. Options supplémentaires
- **Langues** : Utilisez le sélecteur en haut à droite (FR/EN) pour traduire complètement l'interface à la volée.
- **Recherche et Filtres** : Le tableau de bord et la liste des plans possèdent une large barre de recherche intelligente. Tapez un nom ou le propriétaire pour trouver immédiatement ce que vous cherchez, sans vous soucier des majuscules ou des accents.
- **Exportation** : Utilisez les boutons `PDF` ou `Excel` pour extraire vos listes de risques pour du reporting externe.
