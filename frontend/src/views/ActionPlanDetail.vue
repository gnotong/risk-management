<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <!-- Header -->
    <div class="flex flex-wrap items-center gap-3 sm:gap-4">
      <button @click="goBack" class="p-2 bg-gray-100 dark:bg-white/5 hover:bg-gray-200 dark:hover:bg-white/10 rounded-full border border-gray-200 dark:border-white/10 transition-colors text-gray-700 dark:text-white cursor-pointer flex-shrink-0 shadow-sm dark:shadow-none">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 sm:h-6 sm:w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
      </button>
      <div class="flex-1 min-w-[200px]">
        <h2 class="text-2xl sm:text-3xl md:text-4xl font-black text-gray-900 dark:text-white tracking-tight break-words transition-colors">{{ $t('action_plan_detail.title') }}</h2>
      </div>
      <div class="w-full sm:w-auto sm:ml-auto flex justify-end">
        <button 
          @click="deletePlanModal.isOpen = true"
          :disabled="plan.tauxAvancement > 0"
          :class="plan.tauxAvancement > 0 ? 'opacity-50 cursor-not-allowed bg-gray-400 dark:bg-gray-600 text-white' : 'bg-red-600 dark:bg-red-500/20 hover:bg-red-700 dark:hover:bg-red-500/30 text-white dark:text-red-400'"
          class="px-4 py-2 rounded-xl text-sm font-bold transition-colors flex items-center gap-2 shadow-sm dark:shadow-none"
          :title="plan.tauxAvancement > 0 ? 'Impossible de supprimer un plan ayant un avancement > 0%' : ''"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
          {{ $t('form.delete') || 'Supprimer' }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-emerald-500"></div>
    </div>

    <div v-else-if="!plan" class="glass-card p-10 text-center text-gray-400">
      <p class="text-xl">{{ $t('action_plan_detail.not_found') }}</p>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Left Column: Edit Form & Progress -->
      <div class="lg:col-span-2 space-y-6">
        <div class="glass-card p-4 sm:p-8">
          <div class="flex flex-col sm:flex-row justify-between items-start mb-6 gap-4">
            <div class="w-full sm:w-auto">
              <h3 class="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white mb-2 break-words">{{ plan.nom }}</h3>
              <p class="text-sm sm:text-base text-gray-600 dark:text-gray-400">{{ plan.description || $t('action_plan_detail.no_description') }}</p>
            </div>
            <router-link v-if="plan.risque" :to="`/risques/${plan.risque?.id}`" class="text-xs sm:text-sm bg-blue-500/20 text-blue-400 px-3 py-1.5 rounded-lg hover:bg-blue-500/30 transition-colors flex items-center gap-2 flex-shrink-0 w-full justify-center sm:w-auto">
              <span>🔗 {{ $t('action_plans.risk') }} {{ plan.risque?.libelle }}</span>
            </router-link>
          </div>

          <form @submit.prevent="savePlan" class="space-y-6 mt-8">
            <div v-if="error" class="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl flex items-start gap-3">
              <span class="text-xl">⚠️</span>
              <p class="text-sm">{{ error }}</p>
            </div>

            <!-- Taux Avancement Slider -->
            <div class="p-6 rounded-2xl bg-white dark:bg-black/40 border border-gray-200 dark:border-white/5 space-y-4 shadow-sm dark:shadow-none">
              <div class="flex justify-between items-center">
                <label class="block text-sm font-bold text-gray-700 dark:text-gray-300 uppercase tracking-wider">{{ $t('action_plan_detail.progress') }} ({{ editForm.tauxAvancement }}%)</label>
                <div class="flex gap-2" v-if="plan.statut !== 'TERMINE'">
                  <button type="button" @click="editForm.tauxAvancement = 0" class="text-xs px-2 py-1 bg-gray-100 dark:bg-white/5 rounded hover:bg-gray-200 dark:hover:bg-white/10 text-gray-600 dark:text-gray-400 transition-colors">0%</button>
                  <button type="button" @click="editForm.tauxAvancement = 50" class="text-xs px-2 py-1 bg-gray-100 dark:bg-white/5 rounded hover:bg-gray-200 dark:hover:bg-white/10 text-gray-600 dark:text-gray-400 transition-colors">50%</button>
                  <button type="button" @click="editForm.tauxAvancement = 100" class="text-xs px-2 py-1 bg-gray-100 dark:bg-white/5 rounded hover:bg-gray-200 dark:hover:bg-white/10 text-gray-600 dark:text-gray-400 transition-colors">100%</button>
                </div>
              </div>
              <input 
                v-model.number="editForm.tauxAvancement" 
                type="range" min="0" max="100" 
                :disabled="plan.statut === 'TERMINE'"
                class="w-full h-2 bg-gray-700 rounded-lg appearance-none cursor-pointer accent-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed"
              />
              <div class="w-full bg-gray-800 rounded-full h-1.5 mt-2">
                <div class="h-full rounded-full transition-all duration-300" :class="getProgressColor(editForm.tauxAvancement)" :style="`width: ${editForm.tauxAvancement}%`"></div>
              </div>
            </div>

            <!-- Editors -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('action_plan_detail.status') }}</label>
                <select v-model="editForm.statut" :disabled="plan.statut === 'TERMINE'" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none disabled:opacity-50 disabled:cursor-not-allowed">
                  <option value="NON_COMMENCE">{{ $t('status.NON_COMMENCE') }}</option>
                  <option value="EN_COURS">{{ $t('status.EN_COURS') }}</option>
                  <option value="EN_RETARD">{{ $t('status.EN_RETARD') }}</option>
                  <option value="TERMINE">{{ $t('status.TERMINE') }}</option>
                </select>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('action_plan_detail.start_date') }}</label>
                <input v-model="editForm.dateDebut" type="date" :disabled="plan.statut === 'TERMINE'" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none disabled:opacity-50 disabled:cursor-not-allowed" />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('action_plan_detail.end_date') }}</label>
                <input v-model="editForm.dateFin" type="date" :min="editForm.dateDebut" :disabled="plan.statut === 'TERMINE'" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none disabled:opacity-50 disabled:cursor-not-allowed" />
              </div>

            </div>

            <div class="pt-4 border-t border-gray-200 dark:border-white/10" v-if="plan.statut !== 'TERMINE'">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('action_plan_detail.update_comment_mandatory') }} <span class="text-red-500">*</span></label>
              <textarea v-model="editForm.commentaire" required rows="3" class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-3 text-gray-900 dark:text-white focus:ring-2 focus:ring-emerald-500 outline-none placeholder-gray-400 dark:placeholder-gray-500 shadow-sm dark:shadow-inner" :placeholder="$t('action_plan_detail.update_comment_placeholder')"></textarea>
            </div>

            <div class="flex justify-end pt-4 gap-4">
              <button v-if="plan.statut === 'TERMINE'" type="button" @click="openReopenModal" :disabled="saving" class="bg-amber-500 hover:bg-amber-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold shadow-sm transition-colors flex items-center gap-2">
                <span v-if="saving" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                {{ $t('action_plan_detail.reopen_plan') }}
              </button>
              <button v-if="plan.statut !== 'TERMINE'" type="submit" :disabled="saving" class="bg-emerald-600 dark:bg-emerald-600 hover:bg-emerald-700 dark:hover:bg-emerald-500 text-white px-6 py-2.5 rounded-xl text-sm font-semibold shadow-sm transition-colors flex items-center gap-2">
                <span v-if="saving" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                {{ $t('action_plan_detail.save') }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Right Column: Journal -->
      <div class="space-y-6">
        <div class="glass-card p-6 h-full border border-gray-200 dark:border-white/5 flex flex-col">
          <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            {{ $t('action_plan_detail.journal') }}
          </h3>
          
          <div v-if="loadingSuivis" class="space-y-4">
            <div v-for="i in 3" :key="i" class="h-16 bg-gray-100 dark:bg-gray-800 rounded-lg animate-pulse"></div>
          </div>
          <div v-else-if="suivis.length === 0" class="text-gray-500 text-center py-10 flex-1">
            {{ $t('action_plan_detail.no_comments') }}
          </div>
          <div v-else class="space-y-4 overflow-y-auto pr-2 flex-1" style="max-height: 600px;">
            <div v-for="s in suivis" :key="s.id" class="p-4 rounded-xl bg-gray-50 dark:bg-black/40 border border-gray-200 dark:border-white/5 relative group shadow-sm dark:shadow-none">
              <div class="flex justify-between items-start mb-2">
                <div class="text-xs text-emerald-600 dark:text-emerald-400 font-mono">{{ formatDateTime(s.dateSuivi) }}</div>
                <button 
                  @click="openDeleteSuiviModal(s.id)"
                  class="text-red-500 dark:text-red-400 hover:text-red-600 dark:hover:text-red-300 opacity-0 group-hover:opacity-100 transition-opacity p-1 bg-red-50 dark:bg-red-500/10 rounded"
                  title="Supprimer le journal"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
              </div>
              <p class="text-sm text-gray-700 dark:text-gray-300 leading-relaxed">{{ s.commentaire }}</p>
            </div>
          </div>
        </div>
      </div>

    </div>
    
    <ConfirmationModal
      :isOpen="reopenModal.isOpen"
      :title="$t('action_plan_detail.reopen_plan_title')"
      :message="reopenModal.message"
      type="warning"
      :loading="reopenModal.loading"
      :confirmText="$t('action_plan_detail.reopen_plan_confirm')"
      @confirm="executeReopenPlan"
      @cancel="reopenModal.isOpen = false"
    />
    <ConfirmationModal
      :isOpen="deleteSuiviModal.isOpen"
      :title="$t('action_plan_detail.delete_comment_title')"
      :message="$t('action_plan_detail.delete_comment_msg')"
      type="danger"
      :loading="deleteSuiviModal.loading"
      @confirm="executeRemoveSuivi"
      @cancel="deleteSuiviModal.isOpen = false"
    />
    <ConfirmationModal
      :isOpen="deletePlanModal.isOpen"
      :title="$t('action_plan_detail.delete_plan_title')"
      :message="$t('action_plan_detail.delete_plan_msg')"
      type="danger"
      :loading="deletePlanModal.loading"
      @confirm="executeDeletePlan"
      @cancel="deletePlanModal.isOpen = false"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useActionPlanStore } from '../stores/actionPlanStore';
import { useI18n } from 'vue-i18n';
import ConfirmationModal from '../components/ConfirmationModal.vue';

const route = useRoute();
const router = useRouter();
const id = route.params.id as string;
const store = useActionPlanStore();
const { t } = useI18n();

const goBack = () => {
  if (window.history.state && window.history.state.back) {
    router.back();
  } else {
    router.push('/action-plans');
  }
};

const loading = ref(true);
const saving = ref(false);
const plan = ref<any>(null);
const error = ref('');

const suivis = ref<any[]>([]);
const loadingSuivis = ref(false);

const editForm = reactive({
  tauxAvancement: 0,
  statut: 'NON_COMMENCE',
  dateDebut: '',
  dateFin: '',
  commentaire: ''
});

const loadData = async () => {
  loading.value = true;
  error.value = '';
  try {
    const data = await store.getPlanById(id);
    plan.value = data;
    editForm.tauxAvancement = data.tauxAvancement || 0;
    editForm.statut = data.statut || 'NON_COMMENCE';
    editForm.dateDebut = data.dateDebut || '';
    editForm.dateFin = data.dateFin || '';

    loadSuivis();
  } catch (e) {
    error.value = t('action_plan_detail.error_load');
  } finally {
    loading.value = false;
  }
};

const loadSuivis = async () => {
  loadingSuivis.value = true;
  try {
    suivis.value = await store.fetchSuivis(id);
  } finally {
    loadingSuivis.value = false;
  }
};

onMounted(() => {
  loadData();
});

const savePlan = async () => {
  if (editForm.tauxAvancement === 100 && plan.value?.tauxAvancement !== 100) {
    if (!editForm.commentaire || editForm.commentaire.trim() === '') {
      error.value = t('action_plan_detail.error_close_comment');
      return;
    }
  }

  saving.value = true;
  error.value = '';
  try {
    const updated: any = {
      ...plan.value,
      tauxAvancement: editForm.tauxAvancement,
      statut: editForm.statut,
      dateDebut: editForm.dateDebut ? editForm.dateDebut : null,
      dateFin: editForm.dateFin ? editForm.dateFin : null
    };
    
    // Unify tracking comment into the same network request
    if (editForm.commentaire.trim()) {
      let prefix = "";
      if (editForm.tauxAvancement === 100 && plan.value?.tauxAvancement !== 100) {
        prefix = "✅ Preuve de réalisation : ";
      }
      updated.commentaireUpdate = prefix + editForm.commentaire.trim();
    }
    
    await store.updatePlan(id, updated);
    editForm.commentaire = ''; // clear upon success
    
    // Reload full data to refresh journal if any auto-logs were created
    await loadData();
  } catch (e: any) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
};

const reopenModal = ref({
  isOpen: false,
  message: '',
  newDateDebut: '' as string | null | undefined,
  newDateFin: '' as string | null | undefined,
  hasDateChanges: false,
  loading: false
});

const deleteSuiviModal = ref({
  isOpen: false,
  suiviId: '',
  loading: false
});

const deletePlanModal = ref({
  isOpen: false,
  loading: false
});

const openReopenModal = () => {
  let confirmMessage = t('action_plan_detail.reopen_confirm_base');
  let newDateDebut = plan.value.dateDebut;
  let newDateFin = plan.value.dateFin;
  let hasDateChanges = false;

  if (plan.value.risque && plan.value.risque.dateCreation && newDateDebut) {
    const riskCreationDateStr = plan.value.risque.dateCreation.split('T')[0];
    if (newDateDebut < riskCreationDateStr) {
       newDateDebut = riskCreationDateStr;
       hasDateChanges = true;
       const dCreation = new Date(newDateDebut);
       confirmMessage += t('action_plan_detail.reopen_confirm_date_debut', { date: dCreation.toLocaleDateString(navigator.language || 'fr-FR') });
    }
  }

  if (newDateDebut && newDateFin) {
    const dDebut = new Date(newDateDebut);
    const dFin = new Date(newDateFin);
    if (dDebut >= dFin) {
      dDebut.setDate(dDebut.getDate() + 1);
      newDateFin = dDebut.toISOString().split('T')[0];
      hasDateChanges = true;
      confirmMessage += t('action_plan_detail.reopen_confirm_date_fin', { date: dDebut.toLocaleDateString(navigator.language || 'fr-FR') });
    }
  }

  reopenModal.value.message = confirmMessage;
  reopenModal.value.newDateDebut = newDateDebut;
  reopenModal.value.newDateFin = newDateFin;
  reopenModal.value.hasDateChanges = hasDateChanges;
  reopenModal.value.isOpen = true;
};

const executeReopenPlan = async () => {
  reopenModal.value.loading = true;
  saving.value = true;
  error.value = '';
  try {
    const updated: any = {
      ...plan.value,
      tauxAvancement: 99,
      statut: 'EN_COURS',
      commentaireUpdate: t('action_plan_detail.reopen_success_comment')
    };
    if (reopenModal.value.hasDateChanges) {
      if (reopenModal.value.newDateDebut) updated.dateDebut = reopenModal.value.newDateDebut;
      if (reopenModal.value.newDateFin) updated.dateFin = reopenModal.value.newDateFin;
    }
    
    await store.updatePlan(id, updated);
    await loadData();
    reopenModal.value.isOpen = false;
  } catch (e: any) {
    error.value = e.message;
  } finally {
    saving.value = false;
    reopenModal.value.loading = false;
  }
};

const openDeleteSuiviModal = (suiviId: string) => {
  deleteSuiviModal.value.suiviId = suiviId;
  deleteSuiviModal.value.isOpen = true;
};

const executeRemoveSuivi = async () => {
  if (!deleteSuiviModal.value.suiviId) return;
  
  deleteSuiviModal.value.loading = true;
  try {
    await store.deleteSuivi(id, deleteSuiviModal.value.suiviId);
    await loadSuivis();
    deleteSuiviModal.value.isOpen = false;
  } catch (e: any) {
    error.value = e.message || t('action_plan_detail.error_delete_comment');
  } finally {
    deleteSuiviModal.value.loading = false;
  }
};

const getProgressColor = (val: number) => {
  if (val === 100) return 'bg-green-500';
  if (val > 50) return 'bg-blue-500';
  return 'bg-orange-500';
};

const formatDateTime = (dateString: string) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleString('fr-FR', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute:'2-digit'
  });
};

const executeDeletePlan = async () => {
  deletePlanModal.value.loading = true;
  try {
    await store.deletePlan(id as string);
    deletePlanModal.value.isOpen = false;
    router.push('/action-plans');
  } catch (e: any) {
    error.value = e.message || t('action_plan_detail.error_delete_plan');
    deletePlanModal.value.isOpen = false;
  } finally {
    deletePlanModal.value.loading = false;
  }
};
</script>
