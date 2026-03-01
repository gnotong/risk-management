<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <!-- Header with Back Button -->
    <div class="flex items-center gap-4">
      <router-link to="/" class="p-2 bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 rounded-full border border-gray-200 dark:border-white/10 transition-colors text-gray-700 dark:text-white shadow-sm dark:shadow-none">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
      </router-link>
      <div>
        <h2 class="text-4xl font-black text-gray-900 dark:text-white tracking-tight transition-colors">{{ $t('risk_detail.title') }}</h2>
      </div>
      <div class="ml-auto">
        <button 
          v-if="!loading && risque"
          @click="confirmDeleteRisk"
          class="bg-red-50 dark:bg-red-500/10 hover:bg-red-100 dark:hover:bg-red-500/20 text-red-600 dark:text-red-400 border border-red-200 dark:border-red-500/20 px-4 py-2 rounded-xl text-sm font-bold transition-colors flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
          {{ $t('form.delete') || 'Supprimer' }}
        </button>
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
          <h3 class="text-3xl font-bold text-gray-900 dark:text-white">{{ risque.libelle }}</h3>
          <span class="px-4 py-1.5 rounded-full text-sm font-bold shadow-lg" :class="getStatusStyle(risque.statut)">
            {{ $t(`status.${risque.statut}`) }}
          </span>
        </div>
        
        <div class="prose prose-invert max-w-none mb-8">
          <p class="text-gray-700 dark:text-gray-300 text-lg leading-relaxed">{{ risque.description || 'Aucune description fournie.' }}</p>
        </div>

        <div class="grid grid-cols-2 gap-6 p-6 rounded-2xl bg-white dark:bg-black/40 border border-gray-200 dark:border-white/5 shadow-sm dark:shadow-none">
          <div>
            <span class="block text-sm text-gray-500 uppercase font-bold mb-1">{{ $t('risk_detail.owner') }}</span>
            
            <!-- Static View (Closed Risk) -->
            <span v-if="risque.statut === 'CLOTURE'" class="text-gray-900 dark:text-white text-lg flex items-center gap-2">
              <span class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-500/20 text-blue-600 dark:text-blue-400 flex items-center justify-center font-bold">
                {{ risque.proprietaire?.nom ? risque.proprietaire.nom.charAt(0) : '?' }}
              </span>
              {{ risque.proprietaire?.nom || $t('dashboard.unassigned') }}
            </span>
            
            <!-- Editable View (Open/InProgress Risk) -->
            <div v-else class="relative max-w-xs cursor-pointer group">
              <select 
                v-model="editForm.proprietaireId"
                @change="updateOwner"
                class="w-full bg-blue-50 dark:bg-blue-500/10 hover:bg-blue-100 dark:hover:bg-blue-500/20 text-blue-800 dark:text-white border border-blue-200 dark:border-blue-500/20 rounded-xl px-4 py-2 appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer font-medium transition-colors"
                title="Changer le propriétaire"
              >
                <option value="" disabled>-- {{ $t('dashboard.unassigned') }} --</option>
                <option v-for="user in userStore.users" :key="user.id" :value="user.id">{{ user.nom }} ({{ user.roles?.join(', ') || '' }})</option>
              </select>
              <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-blue-400">
                <svg class="w-4 h-4 fill-current" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/></svg>
              </div>
            </div>
            
            <div v-if="updateError" class="text-red-400 text-xs mt-2">{{ updateError }}</div>
          </div>
          <div>
            <span class="block text-sm text-gray-500 uppercase font-bold mb-1">{{ $t('risk_detail.created_at') }}</span>
            <span class="text-gray-900 dark:text-white text-lg">{{ formatDate(risque.dateCreation) }}</span>
          </div>
        </div>
      </div>

      <!-- Metrics side panel -->
      <div class="space-y-6">
        <div class="glass-card p-8 flex flex-col items-center justify-center text-center">
          <span class="text-gray-400 uppercase tracking-widest font-semibold mb-2">{{ $t('dashboard.score') }}</span>
          <div class="text-6xl font-black" :class="getScoreColor(risque.score)">
            {{ risque.score }}
          </div>
          <div class="mt-4 flex gap-4 text-sm text-gray-400">
            <div>{{ $t('heatmap.probability') }}: <span class="font-bold text-white">{{ risque.probabilite }}</span>/3</div>
            <div>{{ $t('heatmap.gravity') }}: <span class="font-bold text-white">{{ risque.gravite }}</span>/3</div>
          </div>
        </div>

        <!-- Future extension: Linked Audits/Plans -->
        <div class="glass-card p-6">
          <div class="flex items-center justify-between mb-4">
            <h4 class="font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1.5 h-4 bg-purple-500 rounded-full"></span> {{ $t('risk_detail.action_plans') }}
            </h4>
            <button 
              v-if="!actionPlanStore.loading" 
              @click="isActionPlanModalOpen = true"
              class="text-xs bg-purple-50 dark:bg-purple-500/20 text-purple-600 dark:text-purple-400 border border-purple-200 dark:border-transparent hover:bg-purple-100 dark:hover:bg-purple-500/30 px-3 py-1.5 rounded-lg transition-colors flex items-center gap-1 font-bold shadow-sm dark:shadow-none"
            >
              + {{ $t('action_plans.new_plan') }}
            </button>
          </div>
          
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
            {{ $t('dashboard.no_action_plan') }}
          </div>
          <div v-else class="space-y-4">
            <router-link 
              v-for="plan in linkedPlans" 
              :key="plan.id" 
              :to="`/action-plans/${plan.id}`"
              class="block p-4 rounded-xl bg-gray-50 dark:bg-black/30 border border-gray-200 dark:border-white/5 hover:bg-gray-100 dark:hover:bg-white/5 transition-colors group shadow-sm dark:shadow-none"
            >
              <div class="flex justify-between items-start mb-2">
                <h5 class="font-bold text-gray-800 dark:text-gray-200 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors">{{ plan.nom }}</h5>
                <span class="text-xs px-2 py-0.5 rounded font-bold" :class="plan.statut === 'TERMINE' ? 'bg-green-500/20 text-green-400' : 'bg-orange-500/20 text-orange-400'">
                  {{ $t(`status.${plan.statut || 'NON_COMMENCE'}`) }}
                </span>
              </div>
              <div class="flex justify-between items-center mt-3 text-sm">
                <span class="text-gray-600 dark:text-gray-500">Avancement</span>
                <div class="flex items-center gap-2">
                  <div class="w-20 bg-gray-200 dark:bg-gray-700 rounded-full h-1.5">
                    <div class="bg-purple-500 h-1.5 rounded-full" :style="`width: ${plan.tauxAvancement}%`"></div>
                  </div>
                  <span class="text-gray-700 dark:text-gray-300">{{ plan.tauxAvancement }}%</span>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <ActionPlanFormModal 
      :is-open="isActionPlanModalOpen" 
      :risk-id="id as string" 
      @close="isActionPlanModalOpen = false" 
      @created="onActionPlanCreated" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useActionPlanStore } from '../stores/actionPlanStore';
import { useRiskStore } from '../stores/riskStore';
import { useUserStore } from '../stores/userStore';
import ActionPlanFormModal from '../components/ActionPlanFormModal.vue';

const route = useRoute();
const router = useRouter();
const id = route.params.id;
const loading = ref(true);
const risque = ref<any>(null);
const actionPlanStore = useActionPlanStore();
const riskStore = useRiskStore();
const userStore = useUserStore();
const isActionPlanModalOpen = ref(false);
const updateError = ref('');

const editForm = ref({
  proprietaireId: ''
});

onMounted(async () => {
  try {
    risque.value = await riskStore.getRiskById(id as string);
    editForm.value.proprietaireId = risque.value.proprietaire?.id || '';
    
    await actionPlanStore.fetchPlans();
    await userStore.fetchUsers();
  } catch (e) {
    console.error("Failed to load risk", e);
  } finally {
    loading.value = false;
  }
});

const onActionPlanCreated = async () => {
  await actionPlanStore.fetchPlans();
};

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

const confirmDeleteRisk = async () => {
  if (confirm("Êtes-vous sûr de vouloir supprimer définitivement ce risque ?")) {
    try {
      await riskStore.deleteRisk(id as string);
      router.push('/');
    } catch (e: any) {
      alert(e.message || "Erreur lors de la suppression. Ce risque a probablement des plans d'action ou incidents liés.");
    }
  }
};

const updateOwner = async () => {
  if (!editForm.value.proprietaireId) return;
  
  updateError.value = '';
  
  try {
    const payload = {
      ...risque.value,
      proprietaire: { id: editForm.value.proprietaireId }
    };
    
    risque.value = await riskStore.updateRisk(id as string, payload);
  } catch (e: any) {
    updateError.value = e.message;
    // Revert locally on fail
    editForm.value.proprietaireId = risque.value.proprietaire?.id || '';
  }
};
</script>
