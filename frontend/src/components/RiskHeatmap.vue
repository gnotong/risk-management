<template>
  <div class="glass-card w-full h-full p-4 sm:p-6 lg:p-8 flex flex-col relative self-start lg:sticky lg:top-8 overflow-hidden">
    <div class="flex flex-wrap justify-between items-start mb-4 sm:mb-6 gap-2">
      <div>
        <h3 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
          <span class="w-1.5 h-6 bg-red-500 rounded-full shadow-[0_0_10px_rgba(239,68,68,0.8)]"></span> {{ $t('heatmap.title') }} 
        </h3>
        <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ $t('heatmap.subtitle') }}</p>
      </div>

      <button 
        v-if="store.selectedProb !== null" 
        @click="store.clearHeatmapFilter()"
        class="text-xs bg-gray-100 dark:bg-white/10 hover:bg-gray-200 dark:hover:bg-white/20 text-gray-700 dark:text-white px-3 py-1.5 rounded-lg transition-colors border border-gray-200 dark:border-white/10 flex items-center gap-1 cursor-pointer"
      >
        <span>✕</span> {{ $t('heatmap.clear_filter') }}
      </button>
    </div>
    <div class="w-full max-w-lg mx-auto">
      <div class="grid grid-cols-4 gap-1 lg:gap-2 select-none content-center">
        <!-- Empty top-left cell -->
        <div class="flex flex-col items-center justify-center font-bold text-gray-500 dark:text-gray-400 uppercase text-[9px] sm:text-[10px] text-center tracking-wider">
          <span>{{ $t('heatmap.gravity') }}</span><span class="text-sm sm:text-base leading-none">↘</span>
        </div>
      <div class="flex flex-col sm:flex-row items-center justify-center font-bold text-gray-700 dark:text-gray-500 bg-gray-50 dark:bg-white/5 rounded-md lg:rounded-lg py-1 lg:py-2 text-[8px] sm:text-xs lg:text-sm border border-gray-200 dark:border-white/5 text-center leading-tight px-0.5 sm:gap-1 overflow-hidden">
        <span>1</span><span class="truncate opacity-70 text-[8px] sm:text-[10px] hidden sm:inline">({{ $t('heatmap.low') }})</span>
      </div>
      <div class="flex flex-col sm:flex-row items-center justify-center font-bold text-gray-700 dark:text-gray-500 bg-gray-50 dark:bg-white/5 rounded-md lg:rounded-lg py-1 lg:py-2 text-[8px] sm:text-xs lg:text-sm border border-gray-200 dark:border-white/5 text-center leading-tight px-0.5 sm:gap-1 overflow-hidden">
        <span>2</span><span class="truncate opacity-70 text-[8px] sm:text-[10px] hidden sm:inline">({{ $t('heatmap.medium') }})</span>
      </div>
      <div class="flex flex-col sm:flex-row items-center justify-center font-bold text-gray-700 dark:text-gray-500 bg-gray-50 dark:bg-white/5 rounded-md lg:rounded-lg py-1 lg:py-2 text-[8px] sm:text-xs lg:text-sm border border-gray-200 dark:border-white/5 text-center leading-tight px-0.5 sm:gap-1 overflow-hidden">
        <span>3</span><span class="truncate opacity-70 text-[8px] sm:text-[10px] hidden sm:inline">({{ $t('heatmap.high') }})</span>
      </div>

      <!-- Rows by Probability -->
      <template v-for="p in [3, 2, 1]" :key="'p'+p">
        <div class="flex flex-col items-center justify-center font-bold text-gray-700 dark:text-gray-500 bg-gray-50 dark:bg-white/5 rounded-md lg:rounded-lg p-0.5 lg:px-2 text-[8px] sm:text-xs lg:text-sm border border-gray-200 dark:border-white/5 text-center leading-tight overflow-hidden">
          <span class="truncate w-full hidden sm:block">{{ $t('heatmap.probability') }}</span>
          <span class="truncate w-full sm:hidden">Prob.</span>
          <span>{{p}}</span>
        </div>
        <template v-for="g in [1, 2, 3]" :key="'p'+p+'g'+g">
          <div 
            class="relative aspect-square rounded-md sm:rounded-xl flex items-center justify-center text-base sm:text-xl lg:text-3xl font-black transition-all group border-2 border-transparent w-full"
            :class="[
              getColorClass(p, g),
              countRisks(p, g) > 0 ? 'cursor-pointer hover:scale-[1.05] hover:z-10 hover:shadow-2xl' : 'opacity-40 cursor-not-allowed grayscale',
              isCellSelected(p, g) ? 'ring-4 ring-white shadow-[0_0_30px_rgba(255,255,255,0.4)] z-20 scale-[1.05] border-transparent' : '',
               (store.selectedProb !== null && !isCellSelected(p, g)) ? 'opacity-30 grayscale-[50%]' : ''
            ]"
            @click="toggleFilter(p, g)"
            :title="countRisks(p, g) > 0 ? $t('dashboard.filter_x_risks', { count: countRisks(p, g) }) : $t('dashboard.no_risks')"
          >
            <span class="z-10 text-white drop-shadow-md group-hover:scale-110 transition-transform" :class="isCellSelected(p, g) ? 'scale-110' : ''">
              {{ countRisks(p, g) }}
            </span>
            
            <!-- Pulse effect for active selection -->
            <div v-if="isCellSelected(p, g)" class="absolute inset-0 bg-white/20 rounded-xl animate-pulse"></div>
          </div>
        </template>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRiskStore } from '../stores/riskStore';

const store = useRiskStore();

const countRisks = (prob: number, grav: number) => {
  return store.risks.filter(r => r.probabilite === prob && r.gravite === grav).length;
};

const isCellSelected = (prob: number, grav: number) => {
  return store.selectedProb === prob && store.selectedGrav === grav;
};

const getColorClass = (prob: number, grav: number) => {
  const score = prob * grav;
  if (score >= 6) return 'bg-red-500 border border-red-600 shadow-sm';
  if (score >= 3) return 'bg-orange-500 border border-orange-600 shadow-sm';
  return 'bg-emerald-500 border border-emerald-600 shadow-sm';
};

const toggleFilter = (p: number, g: number) => {
  if (countRisks(p, g) === 0) return; // Ignore empty cells
  
  if (isCellSelected(p, g)) {
    store.clearHeatmapFilter(); // Toggle off
  } else {
    store.selectedProb = p;
    store.selectedGrav = g;
  }
};
</script>
