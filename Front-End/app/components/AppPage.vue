<template>
  <div>
    <header class="ap-head">
      <div>
        <NuxtLink v-if="back" :to="back" class="ap-back">← Retour</NuxtLink>
        <h1>{{ title }}</h1>
        <p v-if="subtitle">{{ subtitle }}</p>
      </div>
      <div class="ap-actions"><slot name="actions" /></div>
    </header>
    <slot />
  </div>
</template>

<script setup lang="ts">
  defineProps<{ title: string; subtitle?: string; back?: string }>()
</script>

<style>
  /* ---------- En-tête de page ---------- */
  .ap-head {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: flex-end;
    gap: 1rem;
    padding: 1.75rem;
    margin-bottom: 1.75rem;
    border-radius: 1rem;
    color: #fff;
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 55%, #db2777 130%);
    box-shadow: 0 10px 30px -12px rgba(79, 70, 229, 0.6);
  }
  .ap-head h1 {
    color: #fff;
    margin: 0;
    font-size: clamp(1.5rem, 3vw, 2.1rem);
  }
  .ap-head p {
    margin: 0.35rem 0 0;
    color: #fff;
    opacity: 0.85;
  }
  .ap-back {
    display: inline-block;
    margin-bottom: 0.4rem;
    color: #fff;
    opacity: 0.85;
    font-size: 0.9rem;
    text-decoration: none;
  }
  .ap-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  .ap-actions .ap-btn {
    --pico-background-color: #fff;
    --pico-border-color: #fff;
    --pico-color: #4338ca;
  }
  .ap-actions .ap-ghost {
    --pico-background-color: transparent;
    --pico-border-color: rgba(255, 255, 255, 0.65);
    --pico-color: #fff;
  }

  /* ---------- Boutons ---------- */
  .ap-btn {
    width: auto;
    margin: 0;
    display: inline-block;
  }
  .ap-sm {
    padding: 0.3rem 0.7rem;
    font-size: 0.85rem;
  }
  .ap-btn.danger,
  .ap-actions .ap-btn.danger,
  html .ap-btn.danger:is(:hover, :active, :focus) {
    --pico-background-color: #dc2626;
    --pico-border-color: #dc2626;
    --pico-color: #fff;
  }
  html .ap-btn.danger:is(:hover, :active, :focus) {
    --pico-background-color: #b91c1c;
    --pico-border-color: #b91c1c;
  }
  .ap-cell-actions {
    text-align: right;
    white-space: nowrap;
  }
  .ap-cell-actions > * + * {
    margin-left: 0.4rem;
  }

  /* ---------- Cartes & grilles ---------- */
  .ap-grid {
    display: grid;
    gap: 1rem;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    margin-bottom: 1.5rem;
  }
  .ap-card {
    display: block;
    margin: 0;
    padding: 1.1rem 1.25rem;
    color: inherit;
    text-decoration: none;
    border: 1px solid var(--pico-muted-border-color);
    border-radius: var(--pico-border-radius);
    background: var(--pico-card-background-color);
    transition:
      transform 0.15s,
      box-shadow 0.15s,
      border-color 0.15s;
  }
  a.ap-card:hover {
    transform: translateY(-3px);
    border-color: var(--pico-primary);
    box-shadow: 0 12px 24px -14px rgba(79, 70, 229, 0.5);
  }
  .ap-card h4 {
    margin: 0 0 0.35rem;
  }
  .ap-card p {
    margin: 0;
    font-size: 0.9rem;
    color: var(--pico-muted-color);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .ap-card-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.6rem;
  }
  .ap-icon {
    font-size: 1.6rem;
    line-height: 1;
  }
  .ap-stat {
    margin: 0;
    padding: 1rem 1.25rem;
    border-left: 4px solid var(--pico-primary);
    background: var(--ap-soft);
  }
  .ap-stat strong {
    display: block;
    font-size: 1.9rem;
    line-height: 1.1;
  }
  .ap-stat span {
    font-size: 0.85rem;
    color: var(--pico-muted-color);
  }
  .ap-section-title {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    margin: 1.75rem 0 0.75rem;
  }
  .ap-section-title h3 {
    margin: 0;
  }

  /* ---------- Pastilles & avatars ---------- */
  .ap-pill {
    display: inline-block;
    padding: 0.12rem 0.6rem;
    border-radius: 999px;
    font-size: 0.75rem;
    font-weight: 600;
    white-space: nowrap;
    background: var(--ap-soft);
    color: var(--pico-primary);
  }
  .ap-pill[data-tone='planned'] {
    background: rgba(59, 130, 246, 0.15);
    color: #1d4ed8;
  }
  .ap-pill[data-tone='progress'] {
    background: rgba(139, 92, 246, 0.15);
    color: #6d28d9;
  }
  .ap-pill[data-tone='finish'] {
    background: rgba(16, 185, 129, 0.15);
    color: #059669;
  }
  .ap-pill[data-tone='canceled'] {
    background: rgba(239, 68, 68, 0.15);
    color: #b91c1c;
  }
  .ap-pill[data-tone='pause'] {
    background: rgba(245, 158, 11, 0.18);
    color: #b45309;
  }
  .ap-pill[data-tone='role-admin'] {
    background: rgba(236, 72, 153, 0.15);
    color: #be185d;
  }
  .ap-pill[data-tone='role-manager'] {
    background: rgba(20, 184, 166, 0.16);
    color: #0f766e;
  }
  .ap-pill[data-tone='role-developer'] {
    background: rgba(99, 102, 241, 0.15);
    color: #4338ca;
  }
  .ap-pill[data-tone='over'] {
    background: #dc2626;
    color: #fff;
  }
  .ap-stat.ap-over {
    border-left-color: #dc2626;
  }
  .ap-stat.ap-over strong {
    color: #dc2626;
  }
  .ap-pill + .ap-pill {
    margin-left: 0.25rem;
  }
  .ap-person {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }
  .ap-person h4,
  .ap-person h3 {
    margin: 0;
  }
  .ap-person small {
    color: var(--pico-muted-color);
  }
  .ap-avatar {
    flex: none;
    display: grid;
    place-items: center;
    width: 2.4rem;
    height: 2.4rem;
    border-radius: 50%;
    font-size: 0.85rem;
    font-weight: 700;
    color: #fff;
    background: linear-gradient(135deg, #6366f1, #a855f7);
  }
  .ap-avatar.ap-lg {
    width: 3.6rem;
    height: 3.6rem;
    font-size: 1.2rem;
  }

  /* ---------- Divers ---------- */
  .ap-table {
    overflow-x: auto;
  }
  .ap-toolbar {
    display: flex;
    flex-wrap: wrap;
    align-items: end;
    gap: 1rem;
    margin-bottom: 1rem;
  }
  .ap-toolbar > * {
    margin-bottom: 0;
    flex: 1 1 14rem;
  }
  .ap-muted {
    color: var(--pico-muted-color);
  }
  .ap-empty {
    text-align: center;
    padding: 2rem;
    border: 1px dashed var(--pico-muted-border-color);
    border-radius: var(--pico-border-radius);
  }
  .ap-error {
    border-left: 4px solid #dc2626;
    background: rgba(220, 38, 38, 0.08);
  }
  .ap-narrow {
    max-width: 34rem;
    margin-inline: auto;
  }
  .ap-form-grid {
    display: grid;
    gap: 0 1rem;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  }
  .ap-links {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-top: 1.5rem;
  }
</style>
