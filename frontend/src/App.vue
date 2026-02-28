<template>
  <div class="min-h-screen bg-[#0f1115] text-gray-100 font-sans selection:bg-blue-500 selection:text-white">
    <nav class="sticky top-0 z-50 bg-[#161920]/80 backdrop-blur-md border-b border-gray-800 px-6 py-4 flex justify-between items-center">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center font-bold text-xl shadow-lg shadow-blue-500/20">
          GRC
        </div>
        <h1 class="text-2xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-indigo-400">
          Risk Management
        </h1>
      </div>
      <div class="flex gap-6 items-center">
        <router-link to="/" class="text-gray-400 hover:text-white transition-colors uppercase text-sm tracking-wider font-semibold">{{ $t('nav.risks') }}</router-link>
        <router-link to="/action-plans" class="text-gray-400 hover:text-white transition-colors uppercase text-sm tracking-wider font-semibold">{{ $t('nav.action_plans') }}</router-link>
        
        <div class="w-px h-6 bg-white/10 mx-2"></div>
        
        <button @click="toggleLanguage" class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 border border-white/10 transition-colors text-xs font-bold tracking-widest text-white cursor-pointer select-none">
          <span :class="{ 'text-blue-400': locale === 'fr', 'text-gray-500': locale !== 'fr' }">FR</span>
          <span class="text-gray-600">/</span>
          <span :class="{ 'text-blue-400': locale === 'en', 'text-gray-500': locale !== 'en' }">EN</span>
        </button>
      </div>
    </nav>
    <main class="max-w-7xl mx-auto p-6">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { locale } = useI18n();

const toggleLanguage = () => {
  locale.value = locale.value === 'fr' ? 'en' : 'fr';
};
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
