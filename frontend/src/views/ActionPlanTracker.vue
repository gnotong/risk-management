<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
      <div class="w-full text-center md:text-left">
        <h2 class="text-3xl sm:text-4xl font-black text-gray-900 dark:text-white tracking-tight transition-colors">{{ $t('action_plans.title') }}</h2>
        <p class="text-gray-400 mt-2 text-base sm:text-lg">{{ $t('action_plans.subtitle') }}</p>
      </div>
    </div>

    <!-- Search Filter -->
    <div class="mt-4 flex flex-col sm:flex-row gap-4 justify-between items-center bg-white dark:bg-white/5 p-4 rounded-xl border border-gray-200 dark:border-white/10 shadow-sm">
      <div class="relative w-full sm:w-1/2">
        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
            <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
          </svg>
        </div>
        <input 
          v-model="searchQuery" 
          type="text" 
          class="block w-full pl-10 pr-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg leading-5 bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm transition-colors" 
          placeholder="Rechercher par nom ou description..." 
        />
      </div>
      <div class="w-full sm:w-1/3">
        <select 
          v-model="searchStatus"
          class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white transition-colors"
        >
          <option value="">Tous les statuts</option>
          <option value="NON_COMMENCE">{{ $t('status.NON_COMMENCE') }}</option>
          <option value="EN_COURS">{{ $t('status.EN_COURS') }}</option>
          <option value="TERMINE">{{ $t('status.TERMINE') }}</option>
        </select>
      </div>
    </div>

    <div v-if="store.loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
    
    <div v-else-if="filteredPlans.length === 0" class="glass-card p-12 mt-6 text-center text-gray-400 flex flex-col items-center justify-center">
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
        class="block p-6 rounded-xl bg-white dark:bg-white/5 border border-gray-200 dark:border-white/10 hover:bg-gray-50 dark:hover:bg-white/10 hover:border-blue-500/30 dark:hover:border-blue-500/30 shadow-sm dark:shadow-none hover:shadow-lg dark:hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all group"
      >
        <div class="flex flex-col sm:flex-row justify-between items-start mb-4 gap-4">
          <div class="w-full sm:w-auto">
            <div class="flex gap-3 items-center mb-1 flex-wrap">
              <h3 class="font-bold text-base sm:text-lg text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">{{ plan.nom }}</h3>
              <span class="px-2 py-0.5 text-[10px] sm:text-xs font-bold rounded" :class="getStatusStyle(plan.statut)">{{ $t(`status.${plan.statut || 'NON_COMMENCE'}`) }}</span>
            </div>
            <p class="text-xs sm:text-sm text-gray-600 dark:text-gray-400 max-w-2xl">{{ plan.description || '' }}</p>
          </div>
          <div class="text-left sm:text-right flex sm:flex-col items-center sm:items-end gap-2 w-full sm:w-auto justify-between sm:justify-start">
            <div class="flex flex-col">
              <div class="text-[10px] sm:text-xs font-semibold text-gray-500 uppercase tracking-wider mb-0.5 sm:mb-1">{{ $t('action_plans.responsible') }}</div>
              <div class="font-medium text-gray-900 dark:text-gray-200">{{ plan.responsable?.nom || $t('dashboard.unassigned') }}</div>
            </div>
            <!-- Delete Button -->
            <button 
              v-if="plan.statut !== 'TERMINE'"
              @click.prevent="openDeleteModal(plan.id, plan.nom)"
              class="p-1.5 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500 hover:text-white transition-all border border-red-500/20 hover:border-red-500 opacity-0 group-hover:opacity-100"
              title="Supprimer le plan d'action"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
            </button>
          </div>
        </div>

        <div class="mt-4">
          <div class="flex justify-between text-sm mb-1 font-medium">
            <span class="text-gray-600 dark:text-gray-400">{{ $t('action_plans.progress') }}</span>
            <span :class="plan.tauxAvancement > 80 ? 'text-green-600 dark:text-green-400' : 'text-blue-600 dark:text-blue-400'">{{ plan.tauxAvancement }}%</span>
          </div>
          <div class="w-full bg-gray-200 dark:bg-gray-700/50 rounded-full h-3 backdrop-blur-sm overflow-hidden border border-gray-300 dark:border-gray-600">
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
      <div v-if="totalPages > 1" class="flex justify-between items-center mt-6 pt-4 border-t border-gray-200 dark:border-white/5">
        <div class="text-sm text-gray-500 dark:text-gray-400">
          Total: <span class="font-bold text-gray-900 dark:text-white">{{ filteredPlans.length }}</span> {{ $t('nav.action_plans').toLowerCase() }}
        </div>
        <div class="flex justify-center items-center gap-2 sm:gap-4">
          <button 
            @click="currentPage = 1" 
            :disabled="currentPage === 1"
            class="px-2 sm:px-3 py-1 rounded-lg bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 disabled:opacity-30 transition-colors text-gray-800 dark:text-white text-sm"
            title="Première page"
          >
            &laquo;&laquo;
          </button>
          <button 
            @click="currentPage--" 
            :disabled="currentPage === 1"
            class="px-2 sm:px-3 py-1 rounded-lg bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 disabled:opacity-30 transition-colors text-gray-800 dark:text-white text-sm"
          >
            Précédent
          </button>
          <span class="text-gray-600 dark:text-gray-400 text-xs sm:text-sm">Page <span class="text-gray-900 dark:text-white font-bold">{{ currentPage }}</span> sur {{ totalPages }}</span>
          <button 
            @click="currentPage++" 
            :disabled="currentPage === totalPages"
            class="px-2 sm:px-3 py-1 rounded-lg bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 disabled:opacity-30 transition-colors text-gray-800 dark:text-white text-sm"
          >
            Suivant
          </button>
          <button 
            @click="currentPage = totalPages" 
            :disabled="currentPage === totalPages"
            class="px-2 sm:px-3 py-1 rounded-lg bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 disabled:opacity-30 transition-colors text-gray-800 dark:text-white text-sm"
            title="Dernière page"
          >
            &raquo;&raquo;
          </button>
        </div>
      </div>
    </div>
    
    <ConfirmationModal
      :isOpen="deleteModal.isOpen"
      :title="$t('form.delete') + ' ' + (deleteModal.itemName || '')"
      :message="`Êtes-vous sûr de vouloir supprimer définitivement le plan d'action \&quot;${deleteModal.itemName}\&quot; ainsi que tout son historique ?`"
      type="danger"
      :loading="deleteModal.loading"
      @confirm="executeDeletePlan"
      @cancel="closeDeleteModal"
    />
    <ConfirmationModal
      :isOpen="errorModal.isOpen"
      title="Erreur de suppression"
      :message="errorModal.message"
      type="danger"
      confirmText="Fermer"
      @confirm="errorModal.isOpen = false"
      @cancel="errorModal.isOpen = false"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue';
import ConfirmationModal from '../components/ConfirmationModal.vue';
import { useActionPlanStore } from '../stores/actionPlanStore';

const store = useActionPlanStore();

const searchQuery = ref('');
const searchStatus = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(5);

const filteredPlans = computed(() => {
  let result = store.plans;
  
  if (searchStatus.value) {
    result = result.filter(plan => (plan.statut || 'NON_COMMENCE') === searchStatus.value);
  }

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(plan => {
      const n = plan.nom?.toLowerCase() || '';
      const d = plan.description?.toLowerCase() || '';
      return n.includes(q) || d.includes(q);
    });
  }

  return result;
});

const totalPages = computed(() => Math.ceil(filteredPlans.value.length / itemsPerPage.value));

const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredPlans.value.slice(start, end);
});

// Watch plans or search query in case they change
watch([() => store.plans, searchQuery, searchStatus], () => {
  currentPage.value = 1; 
}, { deep: true });

const deleteModal = ref({
  isOpen: false,
  itemId: '',
  itemName: '',
  loading: false
});

const errorModal = ref({
  isOpen: false,
  message: ''
});

const openDeleteModal = (id: string, name: string) => {
  deleteModal.value.itemId = id;
  deleteModal.value.itemName = name;
  deleteModal.value.isOpen = true;
};

const closeDeleteModal = () => {
  deleteModal.value.isOpen = false;
  deleteModal.value.itemId = '';
  deleteModal.value.itemName = '';
};

const executeDeletePlan = async () => {
  if (!deleteModal.value.itemId) return;
  
  deleteModal.value.loading = true;
  try {
    await store.deletePlan(deleteModal.value.itemId);
    closeDeleteModal();
  } catch (e: any) {
    closeDeleteModal();
    errorModal.value.message = e.message || "Erreur lors de la suppression.";
    errorModal.value.isOpen = true;
  } finally {
    deleteModal.value.loading = false;
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
  if (val === 100) return 'bg-emerald-500 shadow-sm';
  if (val > 50) return 'bg-blue-500 shadow-sm';
  return 'bg-orange-500 shadow-sm';
};
</script>
