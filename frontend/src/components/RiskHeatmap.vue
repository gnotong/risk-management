<template>
  <div class="glass-card p-6 lg:p-8 flex flex-col h-full relative">
    <div class="flex justify-between items-start mb-6">
      <div>
        <h3 class="text-2xl font-bold text-white flex items-center gap-2">
          <span class="w-1.5 h-6 bg-red-500 rounded-full shadow-[0_0_10px_rgba(239,68,68,0.8)]"></span> Heatmap 
        </h3>
        <p class="text-xs text-gray-400 mt-1">Cliquez sur une case pour filtrer les risques</p>
      </div>

      <button 
        v-if="store.selectedProb !== null" 
        @click="store.clearHeatmapFilter()"
        class="text-xs bg-white/10 hover:bg-white/20 text-white px-3 py-1.5 rounded-lg transition-colors border border-white/10 flex items-center gap-1 cursor-pointer"
      >
        <span>✕</span> Effacer filtre
      </button>
    </div>
    
    <div class="grid grid-cols-4 gap-2 select-none flex-1 content-center">
      <!-- Empty top-left cell -->
      <div class="flex flex-col items-center justify-center font-bold text-gray-400 uppercase text-xs tracking-wider">
        <span>Gravité</span><span class="text-lg leading-none">↘</span>
      </div>
      <div class="flex items-center justify-center font-bold text-gray-500 bg-white/5 rounded-lg py-2 text-sm border border-white/5">1 (Faible)</div>
      <div class="flex items-center justify-center font-bold text-gray-500 bg-white/5 rounded-lg py-2 text-sm border border-white/5">2 (Moyen)</div>
      <div class="flex items-center justify-center font-bold text-gray-500 bg-white/5 rounded-lg py-2 text-sm border border-white/5">3 (Élevé)</div>

      <!-- Rows by Probability -->
      <template v-for="p in [3, 2, 1]" :key="'p'+p">
        <div class="flex flex-col items-center justify-center font-bold text-gray-500 bg-white/5 rounded-lg px-2 text-sm border border-white/5 text-center leading-tight">
          Probabilité<br/>{{p}}
        </div>
        <template v-for="g in [1, 2, 3]" :key="'p'+p+'g'+g">
          <div 
            class="relative aspect-square rounded-xl flex items-center justify-center text-3xl font-black transition-all group border-2 border-transparent"
            :class="[
              getColorClass(p, g),
              countRisks(p, g) > 0 ? 'cursor-pointer hover:scale-[1.05] hover:z-10 hover:shadow-2xl' : 'opacity-40 cursor-not-allowed grayscale',
              isCellSelected(p, g) ? 'ring-4 ring-white shadow-[0_0_30px_rgba(255,255,255,0.4)] z-20 scale-[1.05] border-transparent' : '',
               (store.selectedProb !== null && !isCellSelected(p, g)) ? 'opacity-30 grayscale-[50%]' : ''
            ]"
            @click="toggleFilter(p, g)"
            :title="countRisks(p, g) > 0 ? `Filtrer ${countRisks(p, g)} risque(s)` : 'Aucun risque'"
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
  if (score >= 6) return 'bg-gradient-to-br from-red-500 to-red-700 shadow-[0_0_15px_rgba(239,68,68,0.3)]';
  if (score >= 3) return 'bg-gradient-to-br from-orange-400 to-orange-600 shadow-[0_0_15px_rgba(249,115,22,0.3)]';
  return 'bg-gradient-to-br from-green-400 to-green-600 shadow-[0_0_15px_rgba(34,197,94,0.3)]';
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
