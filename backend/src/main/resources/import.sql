-- Insert a default user
INSERT INTO Utilisateur (id, username, nom, prenom, email, role, is_active) 
VALUES ('c9925e07-aa3a-4ef4-9e3f-67f781ea32a6', 'admin', 'Dupont', 'Jean', 'admin@local.test', 'ADMIN', true);

INSERT INTO Utilisateur (id, username, nom, prenom, email, role, is_active) 
VALUES ('b6537c35-ae53-488f-9a1d-7bb3d7f9f3ab', 'martin', 'Martin', 'Sophie', 'martin@local.test', 'RESPONSABLE', true);

-- Insert risks
INSERT INTO Risque (id, libelle, description, probabilite, gravite, score, statut, proprietaire_id, date_creation)
VALUES ('e72a8c3e-cb91-4e4b-b0b8-4c13a297fc91', 'Cyberattaque Ransomware', 'Risque d''attaque par ransomware sur les serveurs principaux', 3, 3, 9, 'OUVERT', 'c9925e07-aa3a-4ef4-9e3f-67f781ea32a6', CURRENT_TIMESTAMP);

INSERT INTO Risque (id, libelle, description, probabilite, gravite, score, statut, proprietaire_id, date_creation)
VALUES ('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'Vol de données internes', 'Fuite de données causée par un employé', 2, 3, 6, 'OUVERT', 'b6537c35-ae53-488f-9a1d-7bb3d7f9f3ab', CURRENT_TIMESTAMP);

INSERT INTO Risque (id, libelle, description, probabilite, gravite, score, statut, proprietaire_id, date_creation)
VALUES ('f1e2d3c4-b5a6-9f8e-7d6c-5b4a3f2e1d0c', 'Panne serveur', 'Interruption de service prolongée', 2, 2, 4, 'EN_COURS', 'c9925e07-aa3a-4ef4-9e3f-67f781ea32a6', CURRENT_TIMESTAMP);

-- Insert action plans
INSERT INTO PlanAction (id, nom, description, date_debut, date_fin, taux_avancement, statut, responsable_id, risque_id)
VALUES ('5a6c1e95-7d5a-4b93-8408-fb9e27c0fae3', 'Audit Sécurité Q3', 'Inspection complète des systèmes et remédiations', '2023-01-01', '2023-12-31', 45, 'EN_COURS', 'c9925e07-aa3a-4ef4-9e3f-67f781ea32a6', 'e72a8c3e-cb91-4e4b-b0b8-4c13a297fc91');

INSERT INTO PlanAction (id, nom, description, date_debut, date_fin, taux_avancement, statut, responsable_id, risque_id)
VALUES ('1ea2315b-24cf-4ca6-a517-567fce727b3b', 'Formation de sensibilisation', 'Formation obligatoire pour tous les employés (Phishing)', '2023-05-01', '2023-06-30', 100, 'TERMINE', 'b6537c35-ae53-488f-9a1d-7bb3d7f9f3ab', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d');

INSERT INTO PlanAction (id, nom, description, date_debut, date_fin, taux_avancement, statut, responsable_id, risque_id)
VALUES ('9c8f7e6d-5a4b-3c2d-1e0f-9a8b7c6d5e4f', 'Migration Cloud Redondant', 'Déploiement des serveurs sur une infrastructure multi-zones', '2024-01-15', '2024-09-30', 10, 'EN_COURS', 'c9925e07-aa3a-4ef4-9e3f-67f781ea32a6', 'f1e2d3c4-b5a6-9f8e-7d6c-5b4a3f2e1d0c');