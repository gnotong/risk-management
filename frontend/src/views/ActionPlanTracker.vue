<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
      <div class="w-full text-center md:text-left">
        <h2 class="text-3xl sm:text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight">{{ $t('action_plans.title') }}</h2>
        <p class="text-gray-400 mt-2 text-base sm:text-lg">{{ $t('action_plans.subtitle') }}</p>
      </div>
    </div>

    <div v-if="store.loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
    
    <div v-else-if="store.plans.length === 0" class="glass-card p-12 mt-6 text-center text-gray-400 flex flex-col items-center justify-center">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-20 w-20 mb-6 text-gray-500 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
      </svg>
      <p class="text-lg">{{ $t('action_plans.empty') }}</p>
    </div>

    <div v-else class="glass-card p-6 lg:p-8 space-y-6">
      <router-link 
        v-for="plan in paginatedPlans" 
        :key="plan.id" 
        :to="`/action-plans/${plan.id}`"
        class="block p-6 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 hover:border-blue-500/30 hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all group"
      >
        <div class="flex flex-col sm:flex-row justify-between items-start mb-4 gap-4">
          <div class="w-full sm:w-auto">
            <div class="flex gap-3 items-center mb-1 flex-wrap">
              <h3 class="font-bold text-base sm:text-lg text-white group-hover:text-blue-400 transition-colors">{{ plan.nom }}</h3>
              <span class="px-2 py-0.5 text-[10px] sm:text-xs font-bold rounded" :class="getStatusStyle(plan.statut)">{{ $t(`status.${plan.statut || 'NON_COMMENCE'}`) }}</span>
            </div>
            <p class="text-xs sm:text-sm text-gray-400 max-w-2xl">{{ plan.description || '' }}</p>
          </div>
          <div class="text-left sm:text-right flex sm:flex-col items-center sm:items-end gap-2 w-full sm:w-auto justify-between sm:justify-start">
            <div class="flex flex-col">
              <div class="text-[10px] sm:text-xs font-semibold text-gray-500 uppercase tracking-wider mb-0.5 sm:mb-1">{{ $t('action_plans.responsible') }}</div>
              <div class="font-medium text-gray-200">{{ plan.responsable?.nom || $t('dashboard.unassigned') }}</div>
            </div>
            <!-- Delete Button -->
            <button 
              v-if="plan.statut !== 'TERMINE'"
              @click.prevent="confirmDeletePlan(plan.id, plan.nom)"
              class="p-1.5 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500 hover:text-white transition-all border border-red-500/20 hover:border-red-500 opacity-0 group-hover:opacity-100"
              title="Supprimer le plan d'action"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
            </button>
          </div>
        </div>

        <div class="mt-4">
          <div class="flex justify-between text-sm mb-1 font-medium">
            <span class="text-gray-400">{{ $t('action_plans.progress') }}</span>
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

      <!-- Pagination Controls -->
      <div v-if="totalPages > 1" class="flex justify-center items-center gap-4 mt-6 pt-4 border-t border-white/5">
        <button 
          @click="currentPage--" 
          :disabled="currentPage === 1"
          class="px-3 py-1 rounded-lg bg-white/5 hover:bg-white/10 disabled:opacity-30 transition-colors"
        >
          Précédent
        </button>
        <span class="text-gray-400 text-sm">Page <span class="text-white font-bold">{{ currentPage }}</span> sur {{ totalPages }}</span>
        <button 
          @click="currentPage++" 
          :disabled="currentPage === totalPages"
          class="px-3 py-1 rounded-lg bg-white/5 hover:bg-white/10 disabled:opacity-30 transition-colors"
        >
          Suivant
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue';
import { useActionPlanStore } from '../stores/actionPlanStore';

const store = useActionPlanStore();

const currentPage = ref(1);
const itemsPerPage = ref(5);

const totalPages = computed(() => Math.ceil(store.plans.length / itemsPerPage.value));

const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return store.plans.slice(start, end);
});

// Watch plans in case they reset
watch(() => store.plans, () => {
  currentPage.value = 1; 
}, { deep: true });

const confirmDeletePlan = async (id: string, name: string) => {
  if (confirm(`Êtes-vous sûr de vouloir supprimer définitivement le plan d'action "${name}" ainsi que tout son historique ?`)) {
    try {
      await store.deletePlan(id);
    } catch (e: any) {
      alert(e.message || "Erreur lors de la suppression.");
    }
  }
};

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
