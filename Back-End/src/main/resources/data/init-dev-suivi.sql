-- Supprime toutes les données
-------
DELETE FROM Assign;
DELETE FROM Contribution;
DELETE FROM Project;
DELETE FrOM Server;
DELETE FrOM Users;

-- Ajoute  une extension à PostgreSQL
-------
CREATE SCHEMA IF NOT EXISTS ext;
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA ext;

-- Règles sur les rôles :
--   * DEVELOPER est exclusif : seul un développeur peut être affecté à un projet
--     et saisir des contributions ;
--   * combinaisons autorisées : ADMIN, MANAGER, ADMIN + MANAGER, DEVELOPER ;
--   * exception de test : Teddy cumule les trois rôles.
-- A. Utilisateurs (Membres d'une équipe produit / tech)
INSERT INTO Users (id_user, first_name, last_name, email, password, roles) VALUES
(1, 'Alice', 'Martin', 'alice.martin@company.com', ext.crypt( 'alice_martin', ext.gen_salt('bf')), ARRAY['ADMIN', 'MANAGER']),
(2, 'Thomas', 'Dubois', 'thomas.dubois@company.com', ext.crypt( 'thomas_dubois', ext.gen_salt('bf')), ARRAY['MANAGER']),
(3, 'Sophie', 'Bernard', 'sophie.bernard@company.com', ext.crypt( 'sophie_bernard', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(4, 'Lucas', 'Moreau', 'lucas.moreau@company.com', ext.crypt( 'lucas_moreau', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(5, 'Emma', 'Petit', 'emma.petit@company.com', ext.crypt( 'emma_petit', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(6, 'Alexandre', 'Roux', 'alexandre.roux@company.com', ext.crypt( 'alex_roux', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(7, 'Camille', 'David', 'camille.david@company.com', ext.crypt( 'camille_david', ext.gen_salt('bf')), ARRAY['MANAGER']),
(8, 'Antoine', 'Bertrand', 'antoine.bertrand@company.com', ext.crypt( 'antoine_bertrand', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(9, 'Julie', 'Girard', 'julie.girard@company.com', ext.crypt( 'julie_girard', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(10, 'Nicolas', 'Bonnet', 'nicolas.bonnet@company.com', ext.crypt( 'nico_bonnet', ext.gen_salt('bf')), ARRAY['ADMIN']),
(11, 'Marie', 'Vincent', 'marie.vincent@company.com', ext.crypt( 'marie_vincent', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(12, 'Julien', 'Fournier', 'julien.fournier@company.com', ext.crypt( 'julien_fournier', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(13, 'Clara', 'Morel', 'clara.morel@company.com', ext.crypt( 'clara_morel', ext.gen_salt('bf')), ARRAY['MANAGER']),
(14, 'Hugo', 'Lefebvre', 'hugo.lefebvre@company.com', ext.crypt( 'hugo_lefebvre', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(15, 'Chloé', 'Mercier', 'chloe.mercier@company.com', ext.crypt( 'chloe_mercier', ext.gen_salt('bf')), ARRAY['DEVELOPER']),
(16, 'Teddy', 'Teddy', 'teddy.teddy@company.com', ext.crypt( 'teddy_teddy', ext.gen_salt('bf')), ARRAY['ADMIN','MANAGER','DEVELOPER']);

ALTER TABLE Users      ALTER COLUMN id_user     RESTART WITH 17;

-- B. Serveurs d'infrastructure
INSERT INTO Server (id_server, name, description) VALUES
(1, 'srv-prod-01', 'Serveur principal de production (AWS EC2 us-east-1, Ubuntu 22.04 LTS, 16 vCPU, 64 Go RAM)'),
(2, 'srv-staging-01', 'Serveur de pré-production et recette client (OVH Cloud, Debian 12, 8 vCPU, 32 Go RAM)'),
(3, 'srv-dev-ci', 'Serveur d’intégration continue & runners GitLab CI (On-Premise, 8 vCPU, 16 Go RAM)'),
(4, 'srv-db-prod', 'Cluster PostgreSQL principal (Scaleway, 32 vCPU, 128 Go RAM)'),
(5, 'srv-k8s-cluster', 'Cluster Kubernetes pour microservices (GCP, 64 vCPU, 256 Go RAM)'),
(6, 'srv-backup-01', 'Serveur de sauvegarde et d archivage (On-Premise, 4 vCPU, 16 Go RAM)'),
(7, 'srv-teddy', 'Serveur de Teddy');

ALTER TABLE Server      ALTER COLUMN id_server     RESTART WITH 8;

-- C. Projets de développement
--    Jeu de test volontairement varié : les 5 états (PLANNED, IN_PROGRESS, PAUSE, FINISH,
--    CANCELED) et plusieurs projets en dépassement de quota (quota en HEURES,
--    contributions en MINUTES) : 1, 3 (clôturé), 7, 11 et 15.
--    Projets proches du quota (≥ 90 %) : 4, 6 et 16.
INSERT INTO Project (id_project, name, description, quota, status, id_server) VALUES
(1, 'API Portal RH', 'Refonte de l’API REST pour la gestion des collaborateurs et des congés', 20, 'IN_PROGRESS', 1),
(2, 'Dashboard Analytics', 'Tableau de bord de suivi en temps réel des ventes et métriques clés', 80, 'IN_PROGRESS', 2),
(3, 'Migration Cloud AWS', 'Migration de l’ancienne infrastructure locale vers AWS ECS / Fargate', 14, 'FINISH', 1),
(4, 'Application Mobile Client', 'Développement de l’application mobile React Native (Android/iOS)', 24, 'IN_PROGRESS', 2),
(5, 'Refonte Design System', 'Création de la bibliothèque de composants UI réutilisables (Vue.js / Nuxt)', 4, 'FINISH', 3),
(6, 'Pipeline Data ETL', 'Mise en place de pipelines Airflow pour l analyse BI', 30, 'IN_PROGRESS', 4),
(7, 'Microservices Payment', 'Service d intégration Stripe et gestion des factures', 18, 'IN_PROGRESS', 5),
(8, 'Sécurité & Audit IAM', 'Mise en conformité RGPD et SSO OpenID Connect', 75, 'PLANNED', 1),
(9, 'Monitoring Grafana', 'Centralisation des logs (Loki) et métriques Prometheus', 40, 'IN_PROGRESS', 3),
(10, 'Plan Reprise Activité', 'Automatisation du basculement de secours en cas de panne', 24, 'PAUSE', 6),
(11, 'Project de Teddy', 'Il s agit du project de Teddy', 5, 'IN_PROGRESS', 7),
(12, 'Portail Client B2B', 'Espace client en libre-service pour les commandes et factures', 60, 'PLANNED', 2),
(13, 'Refonte Site Vitrine', 'Nouveau site institutionnel (abandonné après la fusion des marques)', 40, 'CANCELED', 3),
(14, 'Migration Base Legacy', 'Reprise des données de l’ancienne base Oracle vers PostgreSQL', 30, 'PAUSE', 4),
(15, 'Outil de Facturation', 'Génération automatique des factures et relances clients', 16, 'IN_PROGRESS', 5),
(16, 'Audit Accessibilité', 'Mise en conformité RGAA des interfaces web existantes', 12, 'FINISH', 6),
(17, 'Plateforme E-learning', 'Parcours de formation interne et suivi des certifications', 150, 'PLANNED', 1),
(18, 'Chatbot Support', 'Assistant conversationnel pour le support de premier niveau', 35, 'IN_PROGRESS', 2);

ALTER TABLE Project      ALTER COLUMN id_project     RESTART WITH 19;

-- D. Assignation des utilisateurs aux projets (développeurs uniquement)
INSERT INTO Assign (id_user, id_project) VALUES
(5, 1), (3, 1), (3, 2), (4, 2), (5, 3), (4, 4), (12, 4),
(8, 4), (3, 5), (6, 6), (8, 6), (9, 7), (11, 7), (6, 7),
(14, 8), (12, 8), (15, 9), (14, 9), (11, 10), (9, 10), (16, 11),
(4, 12), (5, 12), (14, 13), (15, 13), (6, 14), (12, 14), (8, 15),
(15, 15), (3, 16), (14, 16), (12, 17), (15, 17), (4, 18), (5, 18),
(3, 18);

-- E. Saisie des temps / contributions (en minutes), par des développeurs affectés au projet.
--    Jamais sur un projet PLANNED ; les projets PAUSE, FINISH et CANCELED n'ont que des
--    contributions anciennes.
INSERT INTO Contribution (id_contribution, duration, date_contribution, id_project, id_user) VALUES
(1, 444, '2025-11-14', 11, 16),
(2, 240, '2026-08-19', 13, 14),
(3, 180, '2026-08-20', 13, 15),
(4, 480, '2026-08-25', 3, 5),
(5, 120, '2026-08-26', 13, 14),
(6, 240, '2026-08-31', 16, 3),
(7, 420, '2026-09-01', 3, 5),
(8, 300, '2026-09-01', 16, 14),
(9, 360, '2026-09-02', 14, 6),
(10, 120, '2026-09-02', 16, 3),
(11, 300, '2026-09-03', 14, 12),
(12, 480, '2026-09-08', 10, 11),
(13, 210, '2026-09-09', 10, 9),
(14, 420, '2026-09-14', 1, 3),
(15, 240, '2026-09-14', 1, 5),
(16, 360, '2026-09-15', 2, 4),
(17, 180, '2026-09-17', 5, 3),
(18, 300, '2026-09-17', 6, 6),
(19, 420, '2026-09-18', 6, 8),
(20, 240, '2026-09-18', 7, 9),
(21, 450, '2026-09-21', 1, 3),
(22, 300, '2026-09-21', 1, 5),
(23, 240, '2026-09-22', 4, 4),
(24, 360, '2026-09-23', 2, 4),
(25, 300, '2026-09-24', 7, 11),
(26, 480, '2026-09-25', 4, 12),
(27, 360, '2026-09-25', 9, 14),
(28, 210, '2026-09-25', 9, 15),
(29, 420, '2026-09-28', 6, 6),
(30, 300, '2026-09-28', 7, 9),
(31, 360, '2026-09-29', 1, 3),
(32, 240, '2026-09-29', 4, 8),
(33, 300, '2026-09-30', 2, 4),
(34, 360, '2026-09-30', 4, 12),
(35, 480, '2026-09-30', 6, 8),
(36, 420, '2026-09-30', 7, 6),
(37, 180, '2026-09-30', 9, 15),
(38, 300, '2026-10-01', 18, 4),
(39, 240, '2026-10-02', 18, 5),
(40, 300, '2026-10-05', 15, 8),
(41, 360, '2026-10-05', 18, 3),
(42, 240, '2026-10-06', 15, 15),
(43, 420, '2026-10-08', 18, 4),
(44, 360, '2026-10-12', 15, 8),
(45, 420, '2026-10-13', 15, 15),
(46, 180, '2026-10-13', 18, 5);

ALTER TABLE Contribution      ALTER COLUMN id_contribution     RESTART WITH 47;
