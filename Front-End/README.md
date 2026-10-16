# DevSuivi — Front-End

Interface web de l'application de suivi d'activités de développeurs.

## Technologies

- Nuxt 4, Vue 3, Vue Router, TypeScript
- pnpm (lockfile `pnpm-lock.yaml`)
- [Pico CSS](https://picocss.com/) 2 (chargé via CDN) et un thème personnalisé (`app/assets/css/theme.css`, styles partagés dans `AppPage.vue`)

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
├── components/     Cartes, tableaux et filtres réutilisables
│                   (ProjectCards, ContributionTable, UserCards + variantes « Me », ProjectFilter,
│                    DateFilter, ProjectDurationTable, ProjectStatusSelect, OverQuotaPill,
│                    RoleBadge, EntityPicker, DataState, ConfirmDialog...)
├── composables/    useApiXxx : appels REST par ressource ; useAuth : lecture du JWT et des rôles
├── middleware/     auth.global.ts : redirection vers /login et contrôle d'accès par rôle
├── types/          Types TypeScript alignés sur les DTO du Back-End
├── utils/          format.ts (affichage, durées, états de projet, rôles), projectRules.ts (règles
│                   métier, quota, filtres de projets), dateFilter.ts (filtres de contributions),
│                   roleTheme.ts (rôle dominant), confirm.ts (boîte de confirmation), jetons
├── app.vue         Pose le thème du rôle sur <html data-role="..."> ; contient ConfirmDialog
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
| `/user/me` | Mon espace : durée cumulée de mes contributions, durée par projet, projets et contributions récents |
| `/user/me/contributions` | Mes contributions (récentes, `/all` avec filtres, `/save`, détail) |
| `/user/me/projects` | Mes projets (récents, `/all` avec filtres, détail, membres, `/contributions`) |
| `/profil` | Mon profil, déconnexion |
| `/user/me/save` | Modifier mon profil (mot de passe facultatif ; changer d'email déconnecte) |

### Responsable

| Route | Page | Rôles |
|---|---|---|
| `/project` | Projets : liste (`/all`), détail (temps cumulé / quota, changement d'état), création, modification, membres, contributions | ADMIN, MANAGER |
| `/contribution` | Toutes les contributions (`/all`), détail, création, modification | ADMIN, MANAGER |
| `/assign` | Affectations développeur ↔ projet | ADMIN, MANAGER |
| `/user` | Gestion des comptes (création, modification, fiche avec durée par projet) | ADMIN |
| `/user/projects`, `/user/contributions` | Projets et contributions d'un utilisateur choisi | ADMIN |
| `/server` | Gestion des serveurs et projets hébergés (`/server/projects`) | ADMIN |

Les sous-pages « à choisir » (`/project/contributions`, `/user/projects`...) reçoivent l'élément choisi dans l'URL (`?id=`).

Le middleware `auth.global.ts` renvoie une erreur 403 si l'utilisateur ouvre une section sans le rôle requis. Un non-admin n'accède qu'aux projets auxquels il est affecté et qu'à ses propres contributions. Le contrôle décisif reste celui de l'API : l'interface ne fait que masquer ce qui est interdit.

## Thème selon le rôle

`app.vue` lit les rôles du JWT et pose `data-role` sur `<html>` : `admin`, `manager`, `developer`, ou `guest` quand personne n'est connecté. Le rôle dominant est `ADMIN`, puis `MANAGER`, puis `DEVELOPER`. Le CSS de `app.vue` en déduit la couleur d'accent `--ap-accent` (clair et sombre) utilisée par la barre de navigation : bande supérieure, logo, lien actif, pied de page et badge du rôle (`RoleBadge`). Le reste de l'interface garde le thème indigo.

## Durées, quotas et dépassement

- Le quota d'un projet est en **heures**, la durée d'une contribution en **minutes**. `quotaInMinutes()` et `isOverQuota()` (dans `projectRules.ts`) font la conversion ; `formatDuration()` (dans `format.ts`) affiche `444` en « 7 h 24 ».
- Un projet est **en dépassement** quand sa durée cumulée dépasse son quota. C'est un simple signalement (`OverQuotaPill`, barre de progression, durée en rouge) : l'état du projet ne change pas.
- La durée cumulée d'un projet vient de `totalDuration`, fourni par l'API pour **toutes** les contributions, même quand un développeur ne reçoit que les siennes.

## Filtres des listes

| Liste | Filtres |
|---|---|
| Projets (`/project/all`, `/user/me/projects/all`, `/user/projects`, `/server/projects`) | Statut ; « uniquement les projets en dépassement » (la case n'apparaît que s'il y en a) |
| Toutes les contributions (`/contribution/all`) | Développeur et projet (les choix se restreignent mutuellement), période |
| Contributions d'un projet (`/project/contributions`) | Développeur, période |
| Contributions d'un utilisateur (`/user/contributions`) et les miennes (`/user/me/contributions/all`) | Projet, période |
| Mes contributions sur un projet (`/user/me/projects/contributions`) | Période |

Les tableaux de contributions affichent le développeur et/ou le projet selon la page : le développeur est omis quand la liste est celle d'un seul utilisateur, le projet quand la liste est celle d'un seul projet.

## Règles métier dans l'interface

Définies dans [app/utils/projectRules.ts](app/utils/projectRules.ts) :

- `canBeAssigned` : un développeur ne peut être affecté qu'à un projet `PLANNED` ou `IN_PROGRESS`. Le formulaire d'affectation ne propose que ces projets.
- `canReceiveContributions` : une contribution ne peut être saisie que sur un projet `IN_PROGRESS`. Les formulaires de contribution ne proposent que les projets en cours de l'utilisateur.
- `isOverQuota`, `filterProjects` : dépassement de quota et filtres des listes (voir plus bas).

Les formulaires ne proposent que des utilisateurs `DEVELOPER` pour les affectations et les contributions. L'API refuse de toute façon les cas interdits.

## À faire

- Créer le tableau de bord du responsable (temps par développeur sur une période, projets en dépassement, projets clôturés récemment).
