<template>
  <div class="glass-card p-4 sm:p-5 flex flex-col md:flex-row flex-wrap gap-4 sm:gap-5 items-center">
    <div class="w-full md:flex-1 min-w-0 md:min-w-[200px]">
      <label class="block text-[10px] sm:text-xs font-semibold text-gray-600 dark:text-gray-400 mb-1.5 uppercase tracking-wider truncate">{{ $t('filter.search_label') }}</label>
      <input 
        v-model="store.searchQuery" 
        type="text" 
        class="w-full bg-white dark:bg-black/40 border border-gray-200 dark:border-white/10 rounded-xl py-2 sm:py-2.5 px-3 sm:px-4 text-sm sm:text-base text-gray-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/50 transition-all placeholder-gray-400 dark:placeholder-gray-600 shadow-sm dark:shadow-inner"
        :placeholder="$t('filter.search_placeholder')"
      />
    </div>
    
    <div class="w-full md:flex-none md:w-56">
      <label class="block text-[10px] sm:text-xs font-semibold text-gray-600 dark:text-gray-400 mb-1.5 uppercase tracking-wider">{{ $t('filter.min_score') }}</label>
      <select v-model="store.minScore" class="w-full bg-white dark:bg-black/40 border border-gray-200 dark:border-white/10 rounded-xl py-2 sm:py-2.5 px-3 sm:px-4 text-sm sm:text-base text-gray-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/50 transition-all shadow-sm dark:shadow-inner">
        <option :value="0">{{ $t('filter.all_risks') }}</option>
        <option :value="3">{{ $t('filter.score_medium') }}</option>
        <option :value="6">{{ $t('filter.score_high') }}</option>
      </select>
    </div>

    <div class="w-full md:flex-none md:w-56">
      <label class="block text-[10px] sm:text-xs font-semibold text-gray-600 dark:text-gray-400 mb-1.5 uppercase tracking-wider">{{ $t('filter.owner') }}</label>
      <input 
        v-model="store.ownerFilter" 
        type="text" 
        class="w-full bg-white dark:bg-black/40 border border-gray-200 dark:border-white/10 rounded-xl py-2.5 px-4 text-gray-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/50 transition-all placeholder-gray-400 dark:placeholder-gray-600 shadow-sm dark:shadow-inner"
        :placeholder="$t('filter.owner_placeholder')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRiskStore } from '../stores/riskStore';

const store = useRiskStore();
</script>
