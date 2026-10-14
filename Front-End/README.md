# DevSuivi — Front-End

Interface web de l'application de suivi d'activités de développeurs.

## Technologies

- Nuxt 4, Vue 3, Vue Router, TypeScript
- pnpm (lockfile `pnpm-lock.yaml`)
- [Pico CSS](https://picocss.com/) 2 (chargé via CDN) et un thème personnalisé (`app/assets/css/theme.css`)

## Prérequis

- Node.js (LTS)
- pnpm
- Le Back-End démarré sur `http://localhost:8080` (voir [Back-End](../Back-End/README.md))

## Installation et lancement

```bash
pnpm install     # installe les dépendances (lance aussi nuxt prepare)
pnpm dev         # serveur de développement sur http://localhost:3000
pnpm build       # build de production
pnpm preview     # prévisualise le build de production
```

L'URL de l'API se règle dans [nuxt.config.ts](nuxt.config.ts) (`runtimeConfig.public.apiBase`, par défaut `http://localhost:8080`). On peut aussi la surcharger avec la variable d'environnement `NUXT_PUBLIC_API_BASE`.

Le front doit tourner sur le port 3000 : c'est la seule origine autorisée par le CORS du Back-End.

## Structure

```text
app/
├── pages/          Routes (routage par fichiers Nuxt)
├── layouts/        default (barre de navigation), loginLayout, minimal
├── components/     Cartes et tableaux réutilisables
│                   (ProjectCards, ProjectStatusSelect, ContributionTable, UserCards,
│                    EntityPicker, DataState, ConfirmDialog...)
├── composables/    useApiXxx : appels REST par ressource ; useAuth : lecture du JWT et des rôles
├── middleware/     auth.global.ts : redirection vers /login et contrôle d'accès par rôle
├── types/          Types TypeScript alignés sur les DTO du Back-End
├── utils/          format.ts (affichage, états de projet, rôles), projectRules.ts (règles
│                   métier sur les états), confirm.ts (boîte de confirmation), stockage des jetons
└── error.vue       Page d'erreur (404 / 403)
```

## Authentification

1. `/login` envoie email et mot de passe à `POST /auth/login`. `/register` crée un compte développeur via `POST /auth/register`.
2. L'access token et le refresh token sont stockés dans les cookies `accessToken` et `refreshToken`.
3. `useAuth()` décode le JWT côté client (expiration, claim `roles`) et expose `isLoggedIn`, `roles` et `hasRole(...)`.
4. Les appels aux routes protégées envoient `Authorization: Bearer <accessToken>`.

## Plan du site

### Développeur (espace `/user/me`)

| Route | Page |
|---|---|
| `/user/me` | Mon espace |
| `/user/me/contributions` | Mes contributions (liste, ajout, détail) |
| `/user/me/projects` | Mes projets (liste, détail, membres, contributions) |
| `/profil` | Mon profil |

### Responsable

| Route | Page | Rôles |
|---|---|---|
| `/project` | Projets : liste, détail (temps cumulé / quota, changement d'état), création, modification, membres, contributions | ADMIN, MANAGER |
| `/contribution` | Toutes les contributions | ADMIN, MANAGER |
| `/assign` | Affectations développeur ↔ projet | ADMIN, MANAGER |
| `/user` | Gestion des comptes utilisateurs | ADMIN |
| `/user/projects`, `/user/contributions` | Projets et contributions d'un utilisateur choisi | ADMIN |
| `/server` | Gestion des serveurs et projets hébergés (`/server/projects`) | ADMIN |

Le middleware `auth.global.ts` renvoie une erreur 403 si l'utilisateur ouvre une section sans le rôle requis. Un non-admin n'accède qu'aux projets auxquels il est affecté et qu'à ses propres contributions.

## Règles métier dans l'interface

Définies dans [app/utils/projectRules.ts](app/utils/projectRules.ts) :

- `canBeAssigned` : un développeur ne peut être affecté qu'à un projet `PLANNED` ou `IN_PROGRESS`. Le formulaire d'affectation ne propose que ces projets.
- `canReceiveContributions` : une contribution ne peut être saisie que sur un projet `IN_PROGRESS`. Les formulaires de contribution ne proposent que les projets en cours de l'utilisateur.

Les formulaires ne proposent que des utilisateurs `DEVELOPER` pour les affectations et les contributions. L'API refuse de toute façon les cas interdits.

## À faire

- Afficher clairement le dépassement de quota dans la liste des projets (le détail montre déjà une barre temps cumulé / quota).
- Créer le tableau de bord du responsable (temps par développeur sur une période, projets en dépassement, projets clôturés récemment).
