<template>
  <div class="glass-card p-6 lg:p-8">
    <h3 class="text-2xl font-bold mb-6 text-white flex items-center gap-2">
      <span class="w-1.5 h-6 bg-red-500 rounded-full shadow-[0_0_10px_rgba(239,68,68,0.8)]"></span> Heatmap des Risques
    </h3>
    <div class="grid grid-cols-4 gap-2 select-none">
      <!-- Empty top-left cell -->
      <div class="flex items-center justify-center font-bold text-gray-500 uppercase text-xs tracking-wider">Gravité <span class="ml-1 text-lg">↘</span></div>
      <div class="flex items-center justify-center font-bold text-gray-400 bg-white/5 rounded-lg py-2">1 (Faible)</div>
      <div class="flex items-center justify-center font-bold text-gray-400 bg-white/5 rounded-lg py-2">2 (Moyen)</div>
      <div class="flex items-center justify-center font-bold text-gray-400 bg-white/5 rounded-lg py-2">3 (Élevé)</div>

      <!-- Rows by Probability -->
      <template v-for="p in [3, 2, 1]" :key="'p'+p">
        <div class="flex items-center justify-center font-bold text-gray-400 bg-white/5 rounded-lg px-2">Probabilité {{p}}</div>
        <template v-for="g in [1, 2, 3]" :key="'p'+p+'g'+g">
          <div 
            class="relative aspect-square rounded-xl flex items-center justify-center text-3xl font-black cursor-pointer transition-all hover:scale-[1.03] hover:z-10 group"
            :class="getColorClass(p, g)"
            @click="emitFilter(p, g)"
          >
            <span class="z-10 text-white drop-shadow-lg group-hover:scale-110 transition-transform">{{ countRisks(p, g) }}</span>
            <div v-if="countRisks(p, g) > 0" class="absolute inset-0 bg-white/20 rounded-xl animate-pulse"></div>
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

const getColorClass = (prob: number, grav: number) => {
  const score = prob * grav;
  if (score >= 6) return 'bg-gradient-to-br from-red-500 to-red-700 shadow-[0_0_15px_rgba(239,68,68,0.5)]';
  if (score >= 3) return 'bg-gradient-to-br from-orange-400 to-orange-600 shadow-[0_0_15px_rgba(249,115,22,0.5)]';
  return 'bg-gradient-to-br from-green-400 to-green-600 shadow-[0_0_15px_rgba(34,197,94,0.5)]';
};

const emit = defineEmits(['filterMatrix']);

const emitFilter = (p: number, g: number) => {
  emit('filterMatrix', { probabilite: p, gravite: g });
};
</script>
