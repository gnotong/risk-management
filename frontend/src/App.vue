<template>
  <div class="min-h-screen bg-[#0f1115] text-gray-100 font-sans selection:bg-blue-500 selection:text-white overflow-x-hidden w-full max-w-[100vw]">
    <nav class="sticky w-full top-0 z-50 bg-[#161920]/80 backdrop-blur-md border-b border-gray-800 px-4 sm:px-6 py-4 flex flex-col lg:flex-row justify-between items-center gap-4 lg:gap-6 relative">
      <div class="flex items-center gap-3 w-full lg:w-auto justify-center lg:justify-start">
        <div class="w-8 h-8 sm:w-10 sm:h-10 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center font-bold text-lg sm:text-xl shadow-lg shadow-blue-500/20 whitespace-nowrap">
          GRC
        </div>
        <h1 class="text-xl sm:text-2xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-indigo-400 whitespace-nowrap">
          Risk Management
        </h1>
      </div>
      <div class="flex flex-wrap gap-4 sm:gap-6 items-center w-full lg:w-auto justify-center lg:justify-end">
        <router-link to="/" class="text-gray-400 hover:text-white transition-colors uppercase text-xs sm:text-sm tracking-wider font-semibold">{{ $t('nav.risks') }}</router-link>
        <router-link to="/action-plans" class="text-gray-400 hover:text-white transition-colors uppercase text-xs sm:text-sm tracking-wider font-semibold">{{ $t('nav.action_plans') }}</router-link>
        
        <div class="w-px h-6 bg-white/10 mx-2"></div>
        
        <!-- User Info Panel -->
        <div v-if="username" class="flex items-center gap-3 bg-white/5 pl-2 pr-4 py-1.5 rounded-full border border-white/5">
          <div class="w-7 h-7 bg-indigo-500/20 text-indigo-400 rounded-full flex items-center justify-center font-bold text-xs uppercase">
            {{ username.charAt(0) }}
          </div>
          <span class="text-sm font-medium text-gray-300">{{ username }}</span>
          <button v-if="isKeycloakEnabled" @click="logout" class="ml-2 text-xs text-red-400 hover:text-red-300 transition-colors font-semibold" :title="$t('auth.logout')">
            {{ $t('auth.logout') }}
          </button>
        </div>
        
        <div class="w-px h-6 bg-white/10 mx-2"></div>

        <button @click="toggleLanguage" class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 border border-white/10 transition-colors text-xs font-bold tracking-widest text-white cursor-pointer select-none">
          <span :class="{ 'text-blue-400': locale === 'fr', 'text-gray-500': locale !== 'fr' }">FR</span>
          <span class="text-gray-600">/</span>
          <span :class="{ 'text-blue-400': locale === 'en', 'text-gray-500': locale !== 'en' }">EN</span>
        </button>
      </div>
    </nav>
    <main class="w-full max-w-7xl mx-auto px-4 sm:px-6 py-6">
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
import { computed } from 'vue';
import keycloak from './plugins/keycloak';

const { locale } = useI18n();

const toggleLanguage = () => {
  locale.value = locale.value === 'fr' ? 'en' : 'fr';
};

const isKeycloakEnabled = computed(() => import.meta.env.VITE_ENABLE_KEYCLOAK !== 'false');

const username = computed(() => {
  if (!isKeycloakEnabled.value) {
    return 'Utilisateur Local';
  }
  return keycloak.tokenParsed?.preferred_username || keycloak.tokenParsed?.name || '';
});

const logout = () => {
  if (isKeycloakEnabled.value) {
    keycloak.logout();
  }
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
