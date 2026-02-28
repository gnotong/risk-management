<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <!-- Header with Back Button -->
    <div class="flex items-center gap-4">
      <router-link to="/" class="p-2 bg-white/5 hover:bg-white/10 rounded-full border border-white/10 transition-colors text-white">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
      </router-link>
      <div>
        <h2 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight">Détails du Risque</h2>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>

    <!-- Not Found -->
    <div v-else-if="!risque" class="glass-card p-10 text-center text-gray-400">
      <p class="text-xl">Risque introuvable.</p>
      <router-link to="/" class="mt-4 inline-block text-blue-400 hover:text-blue-300 underline">Retour au tableau de bord</router-link>
    </div>

    <!-- Content -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Main Info -->
      <div class="lg:col-span-2 glass-card p-8">
        <div class="flex items-start justify-between mb-6">
          <h3 class="text-3xl font-bold text-white">{{ risque.libelle }}</h3>
          <span class="px-4 py-1.5 rounded-full text-sm font-bold shadow-lg" :class="getStatusStyle(risque.statut)">
            {{ risque.statut }}
          </span>
        </div>
        
        <div class="prose prose-invert max-w-none mb-8">
          <p class="text-gray-300 text-lg leading-relaxed">{{ risque.description || 'Aucune description fournie.' }}</p>
        </div>

        <div class="grid grid-cols-2 gap-6 p-6 rounded-2xl bg-black/40 border border-white/5">
          <div>
            <span class="block text-sm text-gray-500 uppercase font-bold mb-1">Responsable</span>
            <span class="text-white text-lg flex items-center gap-2">
              <span class="w-8 h-8 rounded-full bg-blue-500/20 text-blue-400 flex items-center justify-center font-bold">
                {{ risque.proprietaire?.nom ? risque.proprietaire.nom.charAt(0) : '?' }}
              </span>
              {{ risque.proprietaire?.nom || 'Non assigné' }}
            </span>
          </div>
          <div>
            <span class="block text-sm text-gray-500 uppercase font-bold mb-1">Date de création</span>
            <span class="text-white text-lg">{{ formatDate(risque.dateCreation) }}</span>
          </div>
        </div>
      </div>

      <!-- Metrics side panel -->
      <div class="space-y-6">
        <div class="glass-card p-8 flex flex-col items-center justify-center text-center">
          <span class="text-gray-400 uppercase tracking-widest font-semibold mb-2">Score de Criticité</span>
          <div class="text-6xl font-black" :class="getScoreColor(risque.score)">
            {{ risque.score }}
          </div>
          <div class="mt-4 flex gap-4 text-sm text-gray-400">
            <div>Prob: <span class="font-bold text-white">{{ risque.probabilite }}</span>/3</div>
            <div>Grav: <span class="font-bold text-white">{{ risque.gravite }}</span>/3</div>
          </div>
        </div>

        <!-- Future extension: Linked Audits/Plans -->
        <div class="glass-card p-6">
          <h4 class="font-bold text-white mb-4 flex items-center gap-2">
            <span class="w-1.5 h-4 bg-purple-500 rounded-full"></span> Plans d'action liés
          </h4>
          
          <div v-if="actionPlanStore.loading" class="animate-pulse flex space-x-4">
            <div class="flex-1 space-y-4 py-1">
              <div class="h-4 bg-gray-700 rounded w-3/4"></div>
              <div class="space-y-2">
                <div class="h-4 bg-gray-700 rounded"></div>
                <div class="h-4 bg-gray-700 rounded w-5/6"></div>
              </div>
            </div>
          </div>
          <div v-else-if="linkedPlans.length === 0" class="text-gray-500 text-sm italic py-2">
            Aucun plan d'action défini pour ce risque.
          </div>
          <div v-else class="space-y-4">
            <router-link 
              v-for="plan in linkedPlans" 
              :key="plan.id" 
              :to="`/action-plans/${plan.id}`"
              class="block p-4 rounded-xl bg-black/30 border border-white/5 hover:bg-white/5 transition-colors group"
            >
              <div class="flex justify-between items-start mb-2">
                <h5 class="font-bold text-gray-200 group-hover:text-purple-400 transition-colors">{{ plan.nom }}</h5>
                <span class="text-xs px-2 py-0.5 rounded font-bold" :class="plan.statut === 'TERMINE' ? 'bg-green-500/20 text-green-400' : 'bg-orange-500/20 text-orange-400'">
                  {{ plan.statut || 'NON_COMMENCE' }}
                </span>
              </div>
              <div class="flex justify-between items-center mt-3 text-sm">
                <span class="text-gray-500">Avancement</span>
                <div class="flex items-center gap-2">
                  <div class="w-20 bg-gray-700 rounded-full h-1.5">
                    <div class="bg-purple-500 h-1.5 rounded-full" :style="`width: ${plan.tauxAvancement}%`"></div>
                  </div>
                  <span class="text-gray-300">{{ plan.tauxAvancement }}%</span>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useActionPlanStore } from '../stores/actionPlanStore';

const route = useRoute();
const id = route.params.id;
const loading = ref(true);
const risque = ref<any>(null);
const actionPlanStore = useActionPlanStore();

onMounted(async () => {
  try {
    const res = await fetch(`/api/risques/${id}`);
    if (res.ok) {
      risque.value = await res.json();
    }
    await actionPlanStore.fetchPlans();
  } catch (e) {
    console.error("Failed to load risk", e);
  } finally {
    loading.value = false;
  }
});

const linkedPlans = computed(() => {
  if (!actionPlanStore.plans) return [];
  return actionPlanStore.plans.filter((plan: any) => plan.risque?.id === id);
});

const getStatusStyle = (statut: string) => {
  if (statut === 'OUVERT') return 'bg-red-500/20 text-red-400 border border-red-500/30';
  if (statut === 'EN_COURS') return 'bg-orange-500/20 text-orange-400 border border-orange-500/30';
  return 'bg-green-500/20 text-green-400 border border-green-500/30';
};

const getScoreColor = (score: number) => {
  if (score >= 6) return 'text-red-500 drop-shadow-[0_0_15px_rgba(239,68,68,0.5)]';
  if (score >= 3) return 'text-orange-400 drop-shadow-[0_0_15px_rgba(251,146,60,0.5)]';
  return 'text-green-400 drop-shadow-[0_0_15px_rgba(74,222,128,0.5)]';
};

const formatDate = (dateString?: string) => {
  if (!dateString) return 'Inconnue';
  return new Date(dateString).toLocaleDateString('fr-FR', {
    day: '2-digit', month: 'long', year: 'numeric'
  });
};
</script>
