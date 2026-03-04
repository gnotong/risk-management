<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
      <div class="w-full text-center md:text-left">
        <h2 class="text-3xl sm:text-4xl font-black text-gray-900 dark:text-white tracking-tight transition-colors">{{ $t('incidents.title') }}</h2>
        <p class="text-gray-400 mt-2 text-base sm:text-lg">{{ $t('incidents.subtitle') }}</p>
      </div>
      <router-link to="/incidents/new" class="btn-primary flex items-center gap-2 whitespace-nowrap bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors font-medium">
        {{ $t('incidents.new_incident') }}
      </router-link>
    </div>

    <!-- Search Filter -->
    <div class="mt-4 flex flex-col sm:flex-row gap-4 justify-between items-center bg-white dark:bg-white/5 p-4 rounded-xl border border-gray-200 dark:border-white/10 shadow-sm">
      <div class="relative w-full sm:w-1/2">
        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <Search class="h-5 w-5 text-gray-400" />
        </div>
        <input 
          v-model="searchQuery" 
          type="text" 
          class="block w-full pl-10 pr-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg leading-5 bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm transition-colors" 
          placeholder="Rechercher par titre ou description..." 
        />
      </div>
      <div class="w-full sm:w-1/3">
        <select 
          v-model="searchStatus"
          class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white transition-colors"
        >
          <option value="">Tous les statuts</option>
          <option :value="StatutIncident.OUVERT">{{ $t('status.OUVERT') }}</option>
          <option :value="StatutIncident.EN_COURS">{{ $t('status.EN_COURS') }}</option>
          <option :value="StatutIncident.RESOLU">{{ $t('status.RESOLU') }}</option>
          <option :value="StatutIncident.CLOTURE">{{ $t('status.CLOTURE') }}</option>
        </select>
      </div>
    </div>

    <div v-if="store.loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
    
    <div v-else-if="filteredIncidents.length === 0" class="glass-card p-12 mt-6 text-center text-gray-400 flex flex-col items-center justify-center">
      <FileWarning class="h-20 w-20 mb-6 text-gray-500 opacity-50" />
      <p class="text-lg">{{ $t('incidents.empty') }}</p>
    </div>

    <div v-else class="glass-card p-6 lg:p-8 space-y-6">
      <router-link 
        v-for="incident in paginatedIncidents" 
        :key="incident.id" 
        :to="`/incidents/${incident.id}`"
        class="block p-6 rounded-xl bg-white dark:bg-white/5 border border-gray-200 dark:border-white/10 hover:bg-gray-50 dark:hover:bg-white/10 hover:border-blue-500/30 dark:hover:border-blue-500/30 shadow-sm dark:shadow-none hover:shadow-lg dark:hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all group"
      >
        <div class="flex flex-col sm:flex-row justify-between items-start mb-4 gap-4">
          <div class="w-full sm:w-auto">
            <div class="flex gap-3 items-center mb-1 flex-wrap">
              <h3 class="font-bold text-base sm:text-lg text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">{{ incident.titre }}</h3>
              <span class="px-2 py-0.5 text-[10px] sm:text-xs font-bold rounded" :class="getStatusStyle(incident.statut)">{{ $t(`status.${incident.statut || StatutIncident.OUVERT}`) }}</span>
              <span class="px-2 py-0.5 text-[10px] sm:text-xs font-bold rounded" :class="getSeverityStyle(incident.severite)">{{ $t(`severity.${incident.severite || SeveriteIncident.FAIBLE}`) }}</span>
            </div>
            <p class="text-xs sm:text-sm text-gray-600 dark:text-gray-400 max-w-2xl">{{ incident.description || '' }}</p>
          </div>
          <div class="text-left sm:text-right flex sm:flex-col items-center sm:items-end gap-2 w-full sm:w-auto justify-between sm:justify-start">
            <div class="flex flex-col">
              <div class="text-[10px] sm:text-xs font-semibold text-gray-500 uppercase tracking-wider mb-0.5 sm:mb-1">{{ $t('incidents.reporter') }}</div>
              <div class="font-medium text-gray-900 dark:text-gray-200">{{ incident.signaleur?.nom || $t('dashboard.unassigned') }}</div>
            </div>
            <!-- Delete Button -->
            <button 
              @click.prevent="openDeleteModal(incident.id, incident.titre)"
              class="text-red-500 hover:text-red-700 bg-red-50 dark:bg-red-900/20 hover:bg-red-100 dark:hover:bg-red-900/40 p-2 rounded-lg transition-colors opacity-0 group-hover:opacity-100"
              :title="$t('incidents.delete_incident_title')"
            >
              <Trash2 class="h-4 w-4" />
            </button>
          </div>
        </div>

        <div class="mt-4 flex flex-wrap gap-4 text-xs sm:text-sm text-gray-500 dark:text-gray-400">
          <div v-if="incident.dateOccurence" class="flex gap-1 items-center">
            <Calendar class="w-4 h-4" />
            <span>{{ $t('incidents.date_occurence') }} {{ new Date(incident.dateOccurence).toLocaleDateString() }}</span>
          </div>
          <div v-if="incident.risque?.libelle" class="flex gap-1 items-center font-medium px-2 py-1 bg-gray-100 dark:bg-gray-800 rounded">
            <span>{{ $t('incidents.risk') }} {{ incident.risque.libelle }}</span>
          </div>
        </div>
      </router-link>

      <!-- Pagination Controls -->
      <div v-if="totalPages > 1" class="flex justify-between items-center mt-6 pt-4 border-t border-gray-200 dark:border-white/5">
        <div class="text-sm text-gray-500 dark:text-gray-400">
          Total: <span class="font-bold text-gray-900 dark:text-white">{{ filteredIncidents.length }}</span> {{ $t('nav.incidents').toLowerCase() }}
        </div>
        <div class="flex justify-center items-center gap-2 sm:gap-4">
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
        </div>
      </div>
    </div>
    
    <ConfirmationModal
      :isOpen="deleteModal.isOpen"
      :title="$t('form.delete') + ' ' + (deleteModal.itemName || '')"
      :message="$t('incidents.delete_incident_msg')"
      type="danger"
      :loading="deleteModal.loading"
      @confirm="executeDeleteIncident"
      @cancel="closeDeleteModal"
    />
    <ConfirmationModal
      :isOpen="errorModal.isOpen"
      :title="$t('risk_detail.delete_error_title')"
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
import { useIncidentStore } from '../stores/incidentStore';
import { useI18n } from 'vue-i18n';
import { StatutIncident, SeveriteIncident } from '../domain/entities/Incident';
import { Search, FileWarning, Trash2, Calendar } from 'lucide-vue-next';

const store = useIncidentStore();
const { t } = useI18n();

const searchQuery = ref('');
const searchStatus = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(5);

const filteredIncidents = computed(() => {
  let result = store.incidents;
  
  if (searchStatus.value) {
    result = result.filter(incident => (incident.statut || StatutIncident.OUVERT) === searchStatus.value);
  }

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(incident => {
      const n = incident.titre?.toLowerCase() || '';
      const d = incident.description?.toLowerCase() || '';
      return n.includes(q) || d.includes(q);
    });
  }

  return result.sort((a, b) => {
    const dateA = a.dateCreation ? new Date(a.dateCreation).getTime() : 0;
    const dateB = b.dateCreation ? new Date(b.dateCreation).getTime() : 0;
    return dateB - dateA; // Descending
  });
});

const totalPages = computed(() => Math.max(1, Math.ceil(filteredIncidents.value.length / itemsPerPage.value)));

const paginatedIncidents = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredIncidents.value.slice(start, end);
});

// Watch incidents or search query in case they change
watch([() => store.incidents, searchQuery, searchStatus], () => {
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

const executeDeleteIncident = async () => {
  if (!deleteModal.value.itemId) return;
  
  deleteModal.value.loading = true;
  try {
    await store.deleteIncident(deleteModal.value.itemId);
    closeDeleteModal();
  } catch (e: any) {
    closeDeleteModal();
    errorModal.value.message = e.message || t('action_plans.delete_plan_error');
    errorModal.value.isOpen = true;
  } finally {
    deleteModal.value.loading = false;
  }
};

onMounted(() => {
  store.fetchIncidents();
});

const getStatusStyle = (statut: StatutIncident) => {
  if (statut === StatutIncident.RESOLU || statut === StatutIncident.CLOTURE) return 'bg-green-500/20 text-green-400';
  if (statut === StatutIncident.EN_COURS) return 'bg-blue-500/20 text-blue-400';
  return 'bg-gray-500/20 text-gray-400';
};

const getSeverityStyle = (severite: SeveriteIncident) => {
  if (severite === SeveriteIncident.CRITIQUE) return 'bg-red-600 text-white';
  if (severite === SeveriteIncident.ELEVE) return 'bg-orange-500/20 text-orange-400';
  if (severite === SeveriteIncident.MOYEN) return 'bg-yellow-500/20 text-yellow-500';
  return 'bg-green-500/20 text-green-400';
};
</script>
