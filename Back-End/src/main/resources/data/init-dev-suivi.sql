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

-- A. Utilisateurs (Membres d'une équipe produit / tech)
INSERT INTO Users (id_user, first_name, last_name, email, password, roles) VALUES
(1, 'Alice', 'Martin', 'alice.martin@company.com', ext.crypt( 'alice_martin', ext.gen_salt('bf')), ARRAY['ADMIN', 'MANAGER']),
(2, 'Thomas', 'Dubois', 'thomas.dubois@company.com', ext.crypt( 'thomas_dubois', ext.gen_salt('bf')), ARRAY['MANAGER']),
(3, 'Sophie', 'Bernard', 'sophie.bernard@company.com', ext.crypt( 'sophie_bernard', ext.gen_salt('bf')), ARRAY['MANAGER', 'DEVELOPER']),
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
INSERT INTO Project (id_project, name, description, quota, status, id_server) VALUES
(1, 'API Portal RH', 'Refonte de l’API REST pour la gestion des collaborateurs et des congés', 120, 'IN_PROGRESS', 1),
(2, 'Dashboard Analytics', 'Tableau de bord de suivi en temps réel des ventes et métriques clés', 80, 'IN_PROGRESS', 2),
(3, 'Migration Cloud AWS', 'Migration de l’ancienne infrastructure locale vers AWS ECS / Fargate', 200, 'FINISH', 1),
(4, 'Application Mobile Client', 'Développement de l’application mobile React Native (Android/iOS)', 150, 'IN_PROGRESS', 2),
(5, 'Refonte Design System', 'Création de la bibliothèque de composants UI réutilisables (Vue.js / Nuxt)', 60, 'FINISH', 3),
(6, 'Pipeline Data ETL', 'Mise en place de pipelines Airflow pour l analyse BI', 100, 'IN_PROGRESS', 4),
(7, 'Microservices Payment', 'Service d intégration Stripe et gestion des factures', 90, 'IN_PROGRESS', 5),
(8, 'Sécurité & Audit IAM', 'Mise en conformité RGPD et SSO OpenID Connect', 75, 'PLANNED', 1),
(9, 'Monitoring Grafana', 'Centralisation des logs (Loki) et métriques Prometheus', 50, 'IN_PROGRESS', 3),
(10, 'Plan Reprise Activité', 'Automatisation du basculement de secours en cas de panne', 110, 'PLANNED', 6),
(11, 'Project de Teddy', 'Il s agit du project de Teddy', 222, 'IN_PROGRESS', 7);

ALTER TABLE Project      ALTER COLUMN id_project     RESTART WITH 12;

-- D. Assignation des utilisateurs aux projets
INSERT INTO Assign (id_user, id_project) VALUES
(2, 1), (3, 1), (3, 2), (4, 2), (5, 3), (4, 4), (3, 5),
(6, 6), (8, 6), (9, 7), (11, 7), (12, 4), (14, 8), (15, 9),
(7, 8), (10, 9), (1, 10), (13, 10), (6, 7), (8, 4), (16, 11);

-- E. Saisie des temps / contributions (en minutes)
INSERT INTO Contribution (id_contribution, duration, date_contribution, id_project, id_user) VALUES
-- Semaines précédentes
(1, 240, '2026-09-14', 1, 2),
(2, 420, '2026-09-14', 1, 3),
(3, 360, '2026-09-15', 2, 4),
(4, 480, '2026-09-16', 3, 5),
(5, 180, '2026-09-17', 5, 3),
(6, 300, '2026-09-17', 6, 6),
(7, 420, '2026-09-18', 6, 8),
(8, 240, '2026-09-18', 7, 9),

-- Semaine courante
(9, 300, '2026-09-21', 1, 2),
(10, 450, '2026-09-21', 1, 3),
(11, 240, '2026-09-22', 4, 4),
(12, 360, '2026-09-23', 2, 4),
(13, 420, '2026-09-24', 3, 5),
(14, 300, '2026-09-24', 7, 11),
(15, 480, '2026-09-25', 4, 12),
(16, 210, '2026-09-25', 9, 15),
(17, 360, '2026-09-25', 9, 10),
(18, 180, '2026-09-28', 8, 14),

-- Récents
(19, 420, '2026-09-28', 6, 6),
(20, 300, '2026-09-28', 7, 9),
(21, 240, '2026-09-29', 4, 8),
(22, 360, '2026-09-29', 1, 3),
(23, 480, '2026-09-29', 10, 1),
(24, 210, '2026-09-29', 10, 13),
(25, 300, '2026-09-30', 2, 4),
(26, 420, '2026-09-30', 7, 6),
(27, 240, '2026-09-30', 8, 7),
(28, 180, '2026-09-30', 9, 15),
(29, 360, '2026-09-30', 4, 12),
(30, 480, '2026-09-30', 6, 8),
(31, 444, '2026-11-14', 11, 16);

ALTER TABLE Contribution      ALTER COLUMN id_contribution     RESTART WITH 32;