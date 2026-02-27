<template>
  <div class="glass-card p-5 flex flex-wrap gap-5 items-center">
    <div class="flex-1 min-w-[200px]">
      <label class="block text-xs font-semibold text-gray-400 mb-1.5 uppercase tracking-wider">Recherche libellé</label>
      <input 
        v-model="store.searchQuery" 
        type="text" 
        class="w-full bg-black/40 border border-white/10 rounded-xl py-2.5 px-4 text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/50 transition-all placeholder-gray-600 shadow-inner"
        placeholder="Rechercher un risque..."
      />
    </div>
    
    <div class="w-56">
      <label class="block text-xs font-semibold text-gray-400 mb-1.5 uppercase tracking-wider">Score Minimum</label>
      <select v-model="store.minScore" class="w-full bg-black/40 border border-white/10 rounded-xl py-2.5 px-4 text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/50 transition-all shadow-inner">
        <option :value="0">Tous les risques</option>
        <option :value="3">Score >= 3 (Moyen)</option>
        <option :value="6">Score >= 6 (Élevé)</option>
      </select>
    </div>

    <div class="w-56">
      <label class="block text-xs font-semibold text-gray-400 mb-1.5 uppercase tracking-wider">Propriétaire</label>
      <input 
        v-model="store.ownerFilter" 
        type="text" 
        class="w-full bg-black/40 border border-white/10 rounded-xl py-2.5 px-4 text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500/50 transition-all placeholder-gray-600 shadow-inner"
        placeholder="Nom du propriétaire..."
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRiskStore } from '../stores/riskStore';

const store = useRiskStore();
</script>
