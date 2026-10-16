# DevSuivi — Back-End

API REST de l'application de suivi d'activités de développeurs.

## Technologies

- Java 25 (toolchain Gradle)
- Spring Boot 4.1 : Web MVC, Data JPA, Validation, Security, OAuth2 Resource Server (JWT)
- PostgreSQL (H2 disponible en dépendance)
- Lombok, MapStruct 1.6
- springdoc-openapi (Swagger UI)

## Prérequis

- JDK 25
- PostgreSQL avec une base `dev_suivi`

## Configuration

Fichier [application.properties](src/main/resources/application.properties) :

| Propriété | Valeur par défaut |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/dev_suivi` |
| `spring.datasource.username` / `password` | `postgres` / `postgres` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `jwt.secret` | clé HMAC (à changer en production) |
| `jwt.expiration` | durée de l'access token, en secondes (86400) |
| `jwt.refresh.expiration` | durée du refresh token, en secondes (86400) |

Le CORS autorise uniquement `http://localhost:3000` (le front Nuxt), voir `SecurityConfig`.

## Initialiser la base

Scripts SQL dans [src/main/resources/data/](src/main/resources/data/) :

1. `dev-suivi.sql` : supprime puis recrée le schéma `public` et les tables.
2. `init-dev-suivi.sql` : insère le jeu de test (16 utilisateurs, 7 serveurs, 18 projets, 36 affectations, 46 contributions). Les mots de passe sont hachés en bcrypt via l'extension `pgcrypto`.

```bash
psql -U postgres -d dev_suivi -f src/main/resources/data/dev-suivi.sql
psql -U postgres -d dev_suivi -f src/main/resources/data/init-dev-suivi.sql
```

### Jeu de test des projets

Les 5 états sont représentés (`IN_PROGRESS` 9 projets, `PLANNED` 3, `FINISH` 3, `PAUSE` 2, `CANCELED` 1) et 5 projets sont **en dépassement de quota** : API Portal RH (29,5 h pour 20 h), Migration Cloud AWS (clôturé, 15 h pour 14 h), Microservices Payment (21 h pour 18 h), Outil de Facturation (22 h pour 16 h) et le projet de Teddy (7 h 24 pour 5 h). Application Mobile Client, Pipeline Data ETL et Audit Accessibilité sont à plus de 90 % de leur quota sans le dépasser. Les contributions ne portent que sur des développeurs affectés, jamais sur un projet `PLANNED`.

### Comptes de test

| Email | Mot de passe | Rôles |
|---|---|---|
| alice.martin@company.com | alice_martin | ADMIN, MANAGER |
| thomas.dubois@company.com | thomas_dubois | MANAGER |
| lucas.moreau@company.com | lucas_moreau | DEVELOPER |
| sophie.bernard@company.com | sophie_bernard | DEVELOPER |
| teddy.teddy@company.com | teddy_teddy | ADMIN, MANAGER, DEVELOPER (exception de test) |

Les autres comptes suivent le format `prenom.nom@company.com` / `prenom_nom`.

## Lancer

```bash
./gradlew bootRun      # démarre l'API sur http://localhost:8080
./gradlew test         # lance les tests
./gradlew build        # compile et produit le jar dans build/libs
```

Documentation interactive : `http://localhost:8080/swagger-ui.html`. Pour tester les routes protégées, appeler `/auth/login` puis coller l'access token dans « Authorize ».

## Architecture

```text
src/main/java/devSuivi/
├── controller/   Endpoints REST (Assign, Contribution, Project, Server, User)
├── service/      Logique métier, transactions
├── repository/   Interfaces Spring Data JPA
├── entity/       Entités JPA (User, Project, Server, Contribution, Assign + AssignId)
├── dto/          Records d'entrée/sortie (*NewDto, *ListDto, *DetailDto)
├── mapper/       Mappers MapStruct entité ↔ DTO (générés dans src/main/generated)
├── security/     JWT, SecurityConfig, AuthController/AuthService, config OpenAPI
└── exception/    GestionnaireExceptions : erreurs au format ProblemDetail
                  (404, 400 avec champs en erreur)
```

## Authentification

- `POST /auth/login` avec `{ "email", "password" }` renvoie `{ accessToken, refreshToken, accessExpireAt, refreshExpireAt }`.
- `POST /auth/register` avec `{ firstName, lastName, email, password }` crée un compte. Le rôle est toujours forcé à `DEVELOPER`, quels que soient les rôles envoyés.
- `POST /auth/refresh` avec `{ "refreshToken" }` renvoie une nouvelle paire de jetons.
- L'access token est un JWT HS256 : `sub` = email, claim `roles` = `["ADMIN", ...]`.
- Les refresh tokens sont gardés en mémoire : ils sont perdus au redémarrage de l'API.
- Comme `sub` contient l'email, un utilisateur qui change son email doit se reconnecter : l'ancien jeton ne désigne plus personne. L'interface le déconnecte automatiquement.

## Endpoints

| Méthode | Route | Accès |
|---|---|---|
| POST | `/auth/login`, `/auth/register`, `/auth/refresh` | public |
| GET | `/user/me` | authentifié |
| PUT | `/user/me` | authentifié : modifie son propre profil (rôles inchangés, mot de passe facultatif) |
| GET | `/user/{id}/projects`, `/user/{id}/contributions` | authentifié : les siens uniquement (ADMIN et MANAGER : tous) |
| GET | `/user`, `/user/{id}` | ADMIN, MANAGER |
| POST/PUT/DELETE | `/user`, `/user/{id}` | ADMIN |
| GET | `/project` | ADMIN, MANAGER |
| GET | `/project/{id}`, `/project/{id}/users`, `/project/{id}/contributions` | authentifié et affecté au projet (ADMIN et MANAGER : tous) |
| POST/PUT/DELETE | `/project`, `/project/{id}` | ADMIN, MANAGER |
| GET/POST/PUT/DELETE | `/server`, `/server/{id}`, `/server/{id}/projects` | ADMIN, MANAGER |
| GET/POST/DELETE | `/assign` (corps `{ user, project }`), `/assign/id` | ADMIN, MANAGER |
| GET | `/contribution` (toutes) | ADMIN, MANAGER |
| GET/POST/PUT/DELETE | `/contribution/{id}`, `/contribution` | authentifié : les siennes uniquement (ADMIN et MANAGER : toutes) |

## Données métier

- `Project.quota` : quota en **heures**. `Contribution.duration` : durée en **minutes**. Toute comparaison doit convertir (quota × 60).
- `totalDuration` (minutes) est renvoyé dans `ProjectListDto` et `ProjectDetailDto` : c'est la somme de **toutes** les contributions du projet, même quand la liste `contributions` d'un développeur est réduite aux siennes. L'indicateur de dépassement (`totalDuration` > quota × 60) est calculé par l'interface ; ce n'est pas un champ de l'API.
- `ContributionListDto` contient aussi `idUser`, `userName`, `idProject` et `projectName`, ce qui permet aux listes de filtrer et d'afficher le développeur et le projet sans requête supplémentaire.
- `Project.status` : `PLANNED`, `IN_PROGRESS`, `PAUSE`, `FINISH` (clôturé), `CANCELED`. Le jeu de test utilise `PLANNED`, `IN_PROGRESS` et `FINISH`.
- Supprimer un projet ou un utilisateur supprime en cascade ses affectations et ses contributions. Supprimer un serveur supprime aussi ses projets.

### Règles vérifiées par l'API

| Règle | Service | Erreur |
|---|---|---|
| Le mot de passe est obligatoire à la création d'un compte ; en modification, un mot de passe vide conserve l'ancien | `UserService` | 400 |
| L'email est unique (sans tenir compte de la casse), à la création comme en modification | `UserService` | 409 |
| Rôles connus uniquement (`ADMIN`, `MANAGER`, `DEVELOPER`) ; `DEVELOPER` ne se combine pas avec `ADMIN` ou `MANAGER` | `UserService` | 400 |
| Un utilisateur qui a des affectations ou des contributions ne peut pas perdre le rôle `DEVELOPER` | `UserService` | 409 |
| Seul un `DEVELOPER` peut être affecté à un projet | `AssignService` | 400 |
| Seul un `DEVELOPER` peut déclarer une contribution | `ContributionService` | 400 |
| Une contribution n'est acceptée que sur un projet `IN_PROGRESS` auquel le développeur est affecté | `ContributionService` | 409 |
| Hors ADMIN et MANAGER, un utilisateur ne lit, déclare, modifie ou supprime que ses propres contributions, et ne lit que ses propres projets et contributions via `/user/{id}/...` | `UserService.verifierAcces` | 403 |
| Hors ADMIN et MANAGER, on ne lit un projet, ses membres et ses contributions que si l'on y est affecté ; la liste des contributions est alors réduite aux siennes | `ProjectService` | 403 |
| Via `PUT /user/me`, un utilisateur ne peut pas changer ses propres rôles (ignorés) | `UserService.updateMe` | — |

Modifier la durée ou la date d'une contribution existante reste possible même si le projet a été clôturé depuis. Changer son utilisateur ou son projet repasse par les contrôles ci-dessus.

## Limites actuelles

Pas encore géré côté API :

- vérifier l'état du projet lors d'une affectation (`PLANNED` ou `IN_PROGRESS`) : seule l'interface le contrôle pour l'instant ;
- fournir les données du tableau de bord (temps par développeur sur une période, projets en dépassement, projets clôturés récemment). Le temps consommé (`totalDuration`) est déjà fourni, mais pas de route d'agrégation par période.

Le dépassement de quota n'est qu'un signalement : il ne change jamais l'état du projet.
