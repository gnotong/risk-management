<template>
  <div :class="{ dark: isDark }">
    <div class="min-h-screen bg-[#f6f8fa] dark:bg-dark-bg text-[#24292e] dark:text-gray-100 font-sans selection:bg-blue-500 selection:text-white overflow-x-hidden w-full max-w-[100vw] transition-colors duration-200">
      <nav class="sticky w-full top-0 z-50 bg-white/90 dark:bg-[#161920]/80 backdrop-blur-md border-b border-[#e1e4e8] dark:border-gray-800 px-4 sm:px-6 py-2.5 flex flex-col lg:flex-row justify-between items-center gap-4 lg:gap-6 relative transition-colors duration-200">
        <div class="flex items-center gap-3 w-full lg:w-auto justify-center lg:justify-start">
          <div class="w-6 h-6 sm:w-8 sm:h-8 rounded-lg bg-blue-600 dark:bg-blue-500 flex items-center justify-center font-bold text-sm sm:text-base text-white shadow-md shadow-blue-500/20 whitespace-nowrap transition-colors">
            GRC
          </div>
          <h1 class="text-lg sm:text-xl font-bold tracking-tight text-blue-700 dark:text-blue-400 whitespace-nowrap transition-colors">
            {{ $t('nav.app_title') }}
          </h1>
        </div>
        <div class="flex flex-wrap gap-4 sm:gap-6 items-center w-full lg:w-auto justify-center lg:justify-end">
          <router-link to="/" class="text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors uppercase text-xs tracking-wider font-semibold">{{ $t('nav.risks') }}</router-link>
          <router-link to="/action-plans" class="text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors uppercase text-xs tracking-wider font-semibold">{{ $t('nav.action_plans') }}</router-link>
          
          <div class="w-px h-6 bg-gray-200 dark:bg-white/10 mx-1 sm:mx-2"></div>
          
          <!-- User Info Panel -->
          <div v-if="username" class="flex items-center gap-2 bg-gray-100 dark:bg-white/5 pl-2 pr-4 py-1.5 rounded-full border border-gray-200 dark:border-white/5">
            <div class="w-6 h-6 bg-indigo-500/20 text-indigo-600 dark:text-indigo-400 rounded-full flex items-center justify-center font-bold text-xs uppercase">
              {{ username.charAt(0) }}
            </div>
            <span class="text-xs font-medium text-gray-700 dark:text-gray-300">{{ username }}</span>
            <button v-if="isKeycloakEnabled" @click="logout" class="ml-2 text-xs bg-red-600 dark:bg-red-500/20 px-2 py-1 rounded text-white dark:text-red-400 hover:bg-red-700 dark:hover:bg-red-500/30 transition-colors font-semibold shadow-sm dark:shadow-none" :title="$t('auth.logout')">
              {{ $t('auth.logout') }}
            </button>
          </div>
          
          <div class="w-px h-6 bg-gray-200 dark:bg-white/10 mx-1 sm:mx-2"></div>

          <div class="flex items-center gap-3">
            <!-- Theme Toggle -->
            <button @click="toggleTheme" class="relative inline-flex h-7 w-14 shrink-0 cursor-pointer items-center justify-between rounded-full border border-gray-300 dark:border-transparent transition-colors duration-200 ease-in-out shadow-inner" :class="isDark ? 'bg-[#24292e]' : 'bg-gray-200'" role="switch" :aria-checked="isDark">
              <span class="sr-only">Toggle theme</span>
              <!-- Sun Icon (Left side when light mode) -->
              <svg class="w-4 h-4 ml-1 z-10 transition-opacity duration-200" :class="isDark ? 'opacity-0' : 'opacity-100 text-[#24292e]'" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <!-- Moon Icon (Right side when dark mode) -->
              <svg class="w-4 h-4 mr-1 z-10 absolute right-0 transition-opacity duration-200" :class="isDark ? 'opacity-100 text-white' : 'opacity-0'" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
              </svg>
              <!-- Sliding knob -->
              <span aria-hidden="true" class="absolute left-0 inline-block h-6 w-6 transform rounded-full shadow ring-0 transition duration-200 ease-in-out" :class="isDark ? 'translate-x-7 bg-white' : 'translate-x-0 bg-[#24292e]'"></span>
            </button>

            <button @click="toggleLanguage" class="flex items-center gap-1.5 px-2 py-1.5 rounded-lg bg-gray-100 dark:bg-[#1c2128] hover:bg-gray-200 dark:hover:bg-[#2d333b] border border-gray-200 dark:border-white/10 transition-colors text-xs font-bold tracking-widest text-gray-800 dark:text-white cursor-pointer select-none shadow-sm">
              <span :class="{ 'text-blue-600 dark:text-blue-400': locale === 'fr', 'text-gray-400 dark:text-gray-500': locale !== 'fr' }">FR</span>
              <span class="text-gray-400 dark:text-gray-600">/</span>
              <span :class="{ 'text-blue-600 dark:text-blue-400': locale === 'en', 'text-gray-400 dark:text-gray-500': locale !== 'en' }">EN</span>
            </button>
          </div>
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
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { computed, ref, onMounted } from 'vue';
import keycloak from './plugins/keycloak';

const { locale } = useI18n();

const toggleLanguage = () => {
  locale.value = locale.value === 'fr' ? 'en' : 'fr';
};

const isDark = ref(true);

onMounted(() => {
  const storedTheme = localStorage.getItem('theme');
  if (storedTheme === 'dark' || (!storedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    isDark.value = true;
    document.documentElement.classList.add('dark');
  } else {
    isDark.value = false;
    document.documentElement.classList.remove('dark');
  }
});

const toggleTheme = () => {
  isDark.value = !isDark.value;
  if (isDark.value) {
    document.documentElement.classList.add('dark');
    localStorage.setItem('theme', 'dark');
  } else {
    document.documentElement.classList.remove('dark');
    localStorage.setItem('theme', 'light');
  }
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
