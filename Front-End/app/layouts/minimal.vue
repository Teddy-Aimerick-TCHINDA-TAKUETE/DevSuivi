<template>
  <div class="mn-wrap">
    <main class="mn-main">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
  import '~/assets/css/theme.css'
</script>

<style>
  .mn-wrap {
    min-height: 100vh;
    display: grid;
    place-items: center;
    padding: 1.5rem;
    background:
      radial-gradient(
        60rem 30rem at 15% -10%,
        rgba(99, 102, 241, 0.18),
        transparent 60%
      ),
      radial-gradient(
        50rem 30rem at 100% 110%,
        rgba(219, 39, 119, 0.14),
        transparent 60%
      );
  }
  .mn-main {
    width: 100%;
    max-width: 34rem;
  }
</style>
