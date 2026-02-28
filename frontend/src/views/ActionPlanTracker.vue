<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight">Suivi des Plans d'Action</h2>
        <p class="text-gray-400 mt-2 text-lg">Visualisation de l'avancement global et gestion des tâches.</p>
      </div>
    </div>

    <div v-if="store.loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
    
    <div v-else-if="store.plans.length === 0" class="glass-card p-12 mt-6 text-center text-gray-400 flex flex-col items-center justify-center">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-20 w-20 mb-6 text-gray-500 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
      </svg>
      <h3 class="text-2xl font-bold text-white mb-3">Aucun Plan d'Action</h3>
      <p class="text-lg">Il n'y a actuellement aucun plan d'action défini dans le système.</p>
    </div>

    <div v-else class="glass-card p-6 lg:p-8 space-y-6">
      <router-link 
        v-for="plan in store.plans" 
        :key="plan.id" 
        :to="`/action-plans/${plan.id}`"
        class="block p-6 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 hover:border-blue-500/30 hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all group"
      >
        <div class="flex justify-between items-start mb-4">
          <div>
            <div class="flex gap-3 items-center mb-1">
              <h3 class="font-bold text-lg text-white group-hover:text-blue-400 transition-colors">{{ plan.nom }}</h3>
              <span class="px-2 py-0.5 text-xs font-bold rounded" :class="getStatusStyle(plan.statut)">{{ plan.statut || 'NON_COMMENCE' }}</span>
            </div>
            <p class="text-sm text-gray-400 max-w-2xl">{{ plan.description || 'Aucune description' }}</p>
          </div>
          <div class="text-right">
            <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">Responsable</div>
            <div class="font-medium text-gray-200">{{ plan.responsable?.nom || 'Non assigné' }}</div>
          </div>
        </div>

        <div class="mt-4">
          <div class="flex justify-between text-sm mb-1 font-medium">
            <span class="text-gray-400">Avancement</span>
            <span :class="plan.tauxAvancement > 80 ? 'text-green-400' : 'text-blue-400'">{{ plan.tauxAvancement }}%</span>
          </div>
          <div class="w-full bg-gray-700/50 rounded-full h-3 backdrop-blur-sm overflow-hidden border border-gray-600">
            <div 
              class="h-full rounded-full transition-all duration-1000 ease-out relative"
              :class="getProgressColor(plan.tauxAvancement)"
              :style="`width: ${plan.tauxAvancement}%`"
            >
              <div class="absolute inset-0 bg-white/20 animate-[pulse_2s_infinite]"></div>
            </div>
          </div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useActionPlanStore } from '../stores/actionPlanStore';

const store = useActionPlanStore();

onMounted(() => {
  store.fetchPlans();
});

const getStatusStyle = (statut: string) => {
  if (statut === 'TERMINE') return 'bg-green-500/20 text-green-400';
  if (statut === 'EN_COURS') return 'bg-orange-500/20 text-orange-400';
  return 'bg-gray-500/20 text-gray-400';
};

const getProgressColor = (val: number) => {
  if (val === 100) return 'bg-gradient-to-r from-green-500 to-emerald-400 shadow-[0_0_10px_rgba(52,211,153,0.5)]';
  if (val > 50) return 'bg-gradient-to-r from-blue-500 to-cyan-400 shadow-[0_0_10px_rgba(56,189,248,0.5)]';
  return 'bg-gradient-to-r from-orange-500 to-amber-400 shadow-[0_0_10px_rgba(251,191,36,0.5)]';
};
</script>
