<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div v-if="!loading && !isNew && incident" class="flex flex-wrap gap-2 mb-2">
      <span class="px-3 py-1 rounded-full text-xs font-bold font-mono shadow-sm" :class="getSeverityStyle(incident.severite)">
        {{ $t(`severity.${incident.severite}`) }}
      </span>
      <span class="px-3 py-1 rounded-full text-xs font-bold font-mono shadow-sm" :class="getStatusStyle(incident.statut)">
        {{ $t(`status.${incident.statut}`) }}
      </span>
      <span class="px-3 py-1 rounded-full text-xs font-bold font-mono shadow-sm bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400">
        {{ incident.tauxAvancement }}%
      </span>
    </div>

    <!-- Header -->
    <div class="flex flex-wrap items-center gap-3 sm:gap-4 mt-2">
      <button @click="goBack" class="p-2 bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 rounded-full border border-gray-200 dark:border-white/10 transition-colors text-gray-700 dark:text-white cursor-pointer flex-shrink-0 shadow-sm dark:shadow-none">
        <ArrowLeft class="h-5 w-5 sm:h-6 sm:w-6" />
      </button>
      <div class="flex-1 min-w-[200px]">
        <h2 class="text-2xl sm:text-3xl md:text-4xl font-black text-gray-900 dark:text-white tracking-tight break-words transition-colors">
          {{ isNew ? $t('incidents.new_incident') : $t('incident_detail.title') }}
        </h2>
      </div>
      <div v-if="!isNew" class="w-full sm:w-auto sm:ml-auto flex justify-end gap-2">
        <button 
          v-if="incident?.statut !== StatutIncident.CLOTURE"
          type="button"
          @click="cloturerIncident"
          class="bg-gray-800 hover:bg-gray-900 dark:bg-gray-700 dark:hover:bg-gray-600 text-white font-bold flex items-center gap-2 px-4 py-2 rounded-xl transition-colors shadow-sm"
        >
          <Lock class="h-4 w-4" />
          Clôturer
        </button>
        <button 
          @click="deleteModal.isOpen = true"
          class="btn-icon-danger font-bold flex items-center gap-2 px-4 py-2"
        >
          <Trash2 class="h-4 w-4" />
          {{ $t('form.delete') }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="!isNew && !incident" class="glass-card p-10 text-center text-gray-400">
      <p class="text-xl">{{ $t('incident_detail.not_found') }}</p>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Edit Form -->
      <div class="lg:col-span-2 space-y-6">
        <div class="glass-card p-4 sm:p-8">
          
          <form @submit.prevent="saveIncident" class="space-y-6">
            <div v-if="error" class="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl flex items-start gap-3">
              <span class="text-xl">⚠️</span>
              <p class="text-sm">{{ error }}</p>
            </div>

            <!-- Titre & Description -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('form.name') }} <span class="text-red-500">*</span></label>
              <input v-model="editForm.titre" required type="text" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none placeholder-gray-400 dark:placeholder-gray-500 shadow-sm" :placeholder="$t('form.name_placeholder')" />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('form.description') }} <span class="text-red-500">*</span></label>
              <textarea v-model="editForm.description" required rows="4" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-3 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none placeholder-gray-400 dark:placeholder-gray-500 shadow-sm" :placeholder="$t('form.desc_placeholder')"></textarea>
            </div>

            <!-- Selectors -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-4 flex justify-between items-end">
                  <span>{{ $t('action_plan_detail.progress') || 'Taux d\'avancement' }} <span class="text-red-500">*</span></span>
                  <span class="text-2xl font-black text-blue-600 dark:text-blue-400 font-mono">{{ editForm.tauxAvancement }}%</span>
                </label>
                <div class="relative pt-1">
                  <input 
                    v-model.number="editForm.tauxAvancement" 
                    type="range" 
                    min="0" 
                    max="100" 
                    step="5"
                    class="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-lg appearance-none cursor-pointer accent-blue-600 dark:accent-blue-500"
                  />
                  <div class="w-full bg-gray-200 dark:bg-gray-800 rounded-full h-1.5 mt-2">
                    <div class="h-full rounded-full transition-all duration-300" :class="getProgressColor(editForm.tauxAvancement)" :style="`width: ${editForm.tauxAvancement}%`"></div>
                  </div>
                  <div class="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-2 font-mono">
                    <span>0% (Ouvert)</span>
                    <span>50% (En cours)</span>
                    <span>100% (Résolu)</span>
                  </div>
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('incident_detail.severity') }}</label>
                <select v-model="editForm.severite" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none">
                  <option :value="SeveriteIncident.FAIBLE">{{ $t('severity.FAIBLE') }}</option>
                  <option :value="SeveriteIncident.MOYEN">{{ $t('severity.MOYEN') }}</option>
                  <option :value="SeveriteIncident.ELEVE">{{ $t('severity.ELEVE') }}</option>
                  <option :value="SeveriteIncident.CRITIQUE">{{ $t('severity.CRITIQUE') }}</option>
                </select>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('incident_detail.date_occurence') }}</label>
                <input v-model="editForm.dateOccurence" type="datetime-local" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>

              <div v-if="isNew">
                <!-- Select Risk dynamically if new -->
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('incident_detail.linked_risk') }}</label>
                <select v-model="editForm.risqueId" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none">
                  <option value="">-- Sans risque --</option>
                  <option v-for="r in risks" :key="r.id" :value="r.id">{{ r.libelle }}</option>
                </select>
              </div>

              <div>
                <!-- Select User dynamically -->
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('incident_detail.reporter') }}</label>
                <select v-model="editForm.signaleurId" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none">
                  <option value="">-- {{ $t('dashboard.unassigned') }} --</option>
                  <option v-for="user in userStore.users" :key="user.id" :value="user.id">{{ user.nom || user.username }} {{ user.roles?.join(', ') || '' }}</option>
                </select>
              </div>

            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('incident_detail.impact_reel') }}</label>
              <textarea v-model="editForm.impactReel" rows="2" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-3 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none placeholder-gray-400 dark:placeholder-gray-500 shadow-sm" :placeholder="$t('incident_detail.impact_placeholder')"></textarea>
            </div>

            <div class="pt-4 border-t border-gray-200 dark:border-white/10" v-if="!isNew && incident?.statut !== StatutIncident.CLOTURE">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('action_plan_detail.update_comment_mandatory') || 'Commentaire de suivi' }}</label>
              <textarea v-model="editForm.commentaire" rows="3" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-3 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none placeholder-gray-400 dark:placeholder-gray-500 shadow-sm dark:shadow-inner" placeholder="Décrivez la mise à jour, les actions entreprises, etc..."></textarea>
            </div>

            <div class="flex justify-end pt-4 gap-4">
              <button type="submit" :disabled="saving" class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-xl text-sm font-semibold shadow-sm transition-colors flex items-center gap-2">
                <span v-if="saving" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                {{ isNew ? $t('form.create') : $t('incident_detail.save') }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Right Column: Info -->
      <div v-if="!isNew" class="space-y-6">
        <div class="glass-card p-6 h-full border border-gray-200 dark:border-white/5 flex flex-col">
          <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
            ℹ️ Informations
          </h3>
          <div class="space-y-4">
            <div>
              <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">{{ $t('incident_detail.linked_risk') }}</div>
              <router-link v-if="incident?.risque" :to="`/risques/${incident.risque.id}`" class="font-medium text-blue-600 dark:text-blue-400 hover:underline">
                {{ incident.risque.libelle }}
              </router-link>
              <div v-else class="text-gray-500">N/A</div>
            </div>
            
            <div>
              <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">{{ $t('incident_detail.reporter') }}</div>
              <div class="font-medium text-gray-900 dark:text-gray-200">{{ incident?.signaleur?.nom || 'N/A' }}</div>
            </div>

            <div>
              <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">{{ $t('incident_detail.date_creation') }}</div>
              <div class="font-medium text-gray-900 dark:text-gray-200">{{ formatDateTime(incident?.dateCreation) }}</div>
            </div>

            <div v-if="incident?.dateResolution">
              <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">{{ $t('incident_detail.date_resolution') }}</div>
              <div class="font-medium text-green-600">{{ formatDateTime(incident?.dateResolution) }}</div>
            </div>
          </div>
          
          <!-- Journal de Suivi intégré -->
          <h3 class="text-xl font-bold text-gray-900 dark:text-white mt-8 mb-6 flex items-center gap-2 border-t border-gray-200 dark:border-white/5 pt-6">
            <span class="w-2 h-2 rounded-full bg-blue-500 animate-pulse"></span>
            {{ $t('action_plan_detail.journal') || 'Journal de bord' }}
          </h3>
          
          <div v-if="loadingSuivis" class="space-y-4">
            <div v-for="i in 3" :key="i" class="h-16 bg-gray-100 dark:bg-gray-800 rounded-lg animate-pulse"></div>
          </div>
          <div v-else class="space-y-4 overflow-y-auto pr-2 flex-1 mb-2" style="max-height: 500px;">
            <div v-if="suivis.length === 0" class="text-gray-500 text-center py-5">
              {{ $t('action_plan_detail.no_comments') || 'Aucun suivi' }}
            </div>
            <div v-else v-for="s in suivis" :key="s.id" class="p-4 rounded-xl bg-gray-50 dark:bg-black/40 border border-gray-200 dark:border-white/5 relative group shadow-sm dark:shadow-none">
              <div class="flex justify-between items-start mb-2">
                <div class="text-xs text-blue-600 dark:text-blue-400 font-mono">{{ formatDateTime(s.dateSuivi) }}</div>
                <button 
                  @click="deleteSuiviModal.id = s.id; deleteSuiviModal.isOpen = true"
                  class="text-red-500 hover:text-red-700 opacity-0 group-hover:opacity-100 transition-opacity"
                  title="Supprimer le suivi"
                >
                  <Trash2 class="h-4 w-4" />
                </button>
              </div>
              <p class="text-sm text-gray-700 dark:text-gray-300 leading-relaxed">{{ s.commentaire }}</p>
            </div>
          </div>
        </div>
      </div>

    </div>
    
    <ConfirmationModal
      :isOpen="deleteModal.isOpen"
      :title="$t('incidents.delete_incident_title')"
      :message="$t('incidents.delete_incident_msg')"
      type="danger"
      :loading="deleteModal.loading"
      @confirm="executeDeleteIncident"
      @cancel="deleteModal.isOpen = false"
    />

    <!-- Delete Suivi Modal -->
    <ConfirmationModal
      :isOpen="deleteSuiviModal.isOpen"
      title="Supprimer ce suivi"
      message="Êtes-vous sûr de vouloir supprimer ce commentaire du journal ?"
      type="danger"
      :loading="deleteSuiviModal.loading"
      @confirm="executeDeleteSuivi"
      @cancel="deleteSuiviModal.isOpen = false"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useIncidentStore } from '../stores/incidentStore';
import { useRiskStore } from '../stores/riskStore';
import { useUserStore } from '../stores/userStore';
import { useI18n } from 'vue-i18n';
import ConfirmationModal from '../components/ConfirmationModal.vue';
import { StatutIncident, SeveriteIncident } from '../domain/entities/Incident';
import { ArrowLeft, Trash2, Lock } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const id = route.params.id as string;
const isNew = id === 'new';

const store = useIncidentStore();
const riskStore = useRiskStore();
const userStore = useUserStore();
const { t } = useI18n();

const goBack = () => {
  if (window.history.state && window.history.state.back) {
    router.back();
  } else {
    router.push('/incidents');
  }
};

const loading = ref(false);
const saving = ref(false);
const incident = ref<any>(null);
const risks = ref<any[]>([]);
const error = ref('');

// Suivi tracking state
const suivis = ref<any[]>([]);
const loadingSuivis = ref(false);

const editForm = reactive({
  titre: '',
  description: '',
  statut: StatutIncident.OUVERT,
  severite: SeveriteIncident.FAIBLE,
  tauxAvancement: 0,
  dateOccurence: '',
  impactReel: '',
  risqueId: '',
  signaleurId: '',
  commentaire: ''
});

const getStatusStyle = (statut: StatutIncident) => {
  if (statut === StatutIncident.RESOLU) return 'bg-emerald-500/20 text-emerald-600 dark:text-emerald-400';
  if (statut === StatutIncident.CLOTURE) return 'bg-gray-500/20 text-gray-600 dark:text-gray-400';
  if (statut === StatutIncident.EN_COURS) return 'bg-blue-500/20 text-blue-600 dark:text-blue-400';
  return 'bg-purple-500/20 text-purple-600 dark:text-purple-400';
};

const getSeverityStyle = (severite: SeveriteIncident) => {
  if (severite === SeveriteIncident.CRITIQUE) return 'bg-red-500/20 text-red-600 dark:text-red-400';
  if (severite === SeveriteIncident.ELEVE) return 'bg-orange-500/20 text-orange-600 dark:text-orange-400';
  if (severite === SeveriteIncident.MOYEN) return 'bg-amber-500/20 text-amber-600 dark:text-amber-400';
  return 'bg-green-500/20 text-green-600 dark:text-green-400';
};

const getProgressColor = (val: number) => {
  if (val === 100) return 'bg-green-500';
  if (val > 50) return 'bg-blue-500';
  if (val > 0) return 'bg-amber-500';
  return 'bg-gray-400 dark:bg-gray-500';
};

const loadData = async () => {
  error.value = '';
  
  if (isNew) {
    editForm.tauxAvancement = 0;
    try {
      await riskStore.fetchRisques();
      risks.value = riskStore.risks;
      // Pre-select via query param if defined
      if (route.query.riskId) {
        editForm.risqueId = route.query.riskId as string;
      }
    } catch (e) {
      console.error(e);
    }
    return;
  }

  loading.value = true;
  try {
    const data = await store.getIncidentById(id);
    incident.value = data;
    editForm.titre = data.titre || '';
    editForm.description = data.description || '';
    editForm.statut = data.statut || StatutIncident.OUVERT;
    editForm.severite = data.severite || SeveriteIncident.FAIBLE;
    editForm.tauxAvancement = data.tauxAvancement || 0;
    editForm.dateOccurence = data.dateOccurence ? data.dateOccurence.substring(0, 16) : '';
    editForm.impactReel = data.impactReel || '';
    editForm.risqueId = data.risque?.id || '';
    editForm.signaleurId = data.signaleur?.id || '';
    
    await loadSuivis();
  } catch (e) {
    error.value = t('incident_detail.error_load') || "Impossible de charger l'incident.";
  } finally {
    loading.value = false;
  }
};

const loadSuivis = async () => {
  if (isNew) return;
  loadingSuivis.value = true;
  try {
    const data = await store.fetchSuivis(id);
    suivis.value = data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loadingSuivis.value = false;
  }
};

onMounted(() => {
  loadData();
  userStore.fetchUsers();
});

const saveIncident = async () => {
  saving.value = true;
  error.value = '';
  try {
    let payload = {
      titre: editForm.titre,
      description: editForm.description,
      statut: editForm.statut,
      severite: editForm.severite,
      tauxAvancement: editForm.tauxAvancement,
      dateOccurence: editForm.dateOccurence ? editForm.dateOccurence + ':00' : null,
      impactReel: editForm.impactReel,
      risque: editForm.risqueId ? { id: editForm.risqueId } : null,
      signaleur: editForm.signaleurId ? { id: editForm.signaleurId } : null,
      commentaireUpdate: editForm.commentaire
    };

    if (isNew) {
      await store.createIncident(payload);
      router.push('/incidents');
    } else {
      await store.updateIncident(id, payload);
      editForm.commentaire = '';
      await loadData(); // reload
    }
  } catch (e: any) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
};

const deleteModal = ref({
  isOpen: false,
  loading: false
});

const cloturerIncident = async () => {
  if (!incident.value || incident.value.statut === StatutIncident.CLOTURE) return;
  saving.value = true;
  try {
    let payload = {
      ...incident.value,
      statut: StatutIncident.CLOTURE,
      tauxAvancement: 100
    };
    await store.updateIncident(id, payload);
    await loadData();
  } catch (e: any) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
};

const executeDeleteIncident = async () => {
  deleteModal.value.loading = true;
  try {
    await store.deleteIncident(id);
    deleteModal.value.isOpen = false;
    router.push('/incidents');
  } catch (e: any) {
    error.value = e.message || 'Erreur lors de la suppression.';
    deleteModal.value.isOpen = false;
  } finally {
    deleteModal.value.loading = false;
  }
};

const deleteSuiviModal = reactive({
  isOpen: false,
  loading: false,
  id: ''
});



const executeDeleteSuivi = async () => {
  if (!deleteSuiviModal.id) return;
  deleteSuiviModal.loading = true;
  try {
    await store.deleteSuivi(id, deleteSuiviModal.id);
    deleteSuiviModal.isOpen = false;
    await loadSuivis();
  } catch (e: any) {
    error.value = e.message || 'Erreur lors de la suppression du suivi.';
    deleteSuiviModal.isOpen = false;
  } finally {
    deleteSuiviModal.loading = false;
  }
};

const formatDateTime = (dateString?: string) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleString('fr-FR', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute:'2-digit'
  });
};
</script>
