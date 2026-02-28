<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <!-- Header -->
    <div class="flex items-center gap-4">
      <router-link to="/action-plans" class="p-2 bg-white/5 hover:bg-white/10 rounded-full border border-white/10 transition-colors text-white">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
      </router-link>
      <div>
        <h2 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 via-teal-400 to-cyan-500 tracking-tight">Détails du Plan d'Action</h2>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-emerald-500"></div>
    </div>

    <div v-else-if="!plan" class="glass-card p-10 text-center text-gray-400">
      <p class="text-xl">Plan d'action introuvable.</p>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Left Column: Edit Form & Progress -->
      <div class="lg:col-span-2 space-y-6">
        <div class="glass-card p-8">
          <div class="flex justify-between items-start mb-6">
            <div>
              <h3 class="text-2xl font-bold text-white mb-2">{{ plan.nom }}</h3>
              <p class="text-gray-400">{{ plan.description || 'Aucune description fournie.' }}</p>
            </div>
            <router-link v-if="plan.risque" :to="`/risques/${plan.risque?.id}`" class="text-sm bg-blue-500/20 text-blue-400 px-3 py-1.5 rounded-lg hover:bg-blue-500/30 transition-colors flex items-center gap-2">
              <span>🔗 Risque: {{ plan.risque?.libelle }}</span>
            </router-link>
          </div>

          <form @submit.prevent="savePlan" class="space-y-6 mt-8">
            <div v-if="error" class="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl flex items-start gap-3">
              <span class="text-xl">⚠️</span>
              <p class="text-sm">{{ error }}</p>
            </div>

            <!-- Taux Avancement Slider -->
            <div class="p-6 rounded-2xl bg-black/40 border border-white/5 space-y-4">
              <div class="flex justify-between items-center">
                <label class="block text-sm font-bold text-gray-300 uppercase tracking-wider">Avancement ({{ editForm.tauxAvancement }}%)</label>
                <div class="flex gap-2">
                  <button type="button" @click="editForm.tauxAvancement = 0" class="text-xs px-2 py-1 bg-white/5 rounded hover:bg-white/10 text-gray-400">0%</button>
                  <button type="button" @click="editForm.tauxAvancement = 50" class="text-xs px-2 py-1 bg-white/5 rounded hover:bg-white/10 text-gray-400">50%</button>
                  <button type="button" @click="editForm.tauxAvancement = 100" class="text-xs px-2 py-1 bg-white/5 rounded hover:bg-white/10 text-gray-400">100%</button>
                </div>
              </div>
              <input 
                v-model.number="editForm.tauxAvancement" 
                type="range" min="0" max="100" 
                class="w-full h-2 bg-gray-700 rounded-lg appearance-none cursor-pointer accent-emerald-500"
              />
              <div class="w-full bg-gray-800 rounded-full h-1.5 mt-2">
                <div class="h-full rounded-full transition-all duration-300" :class="getProgressColor(editForm.tauxAvancement)" :style="`width: ${editForm.tauxAvancement}%`"></div>
              </div>
            </div>

            <!-- Editors -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Statut</label>
                <select v-model="editForm.statut" class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-2.5 text-white focus:ring-2 focus:ring-emerald-500 outline-none">
                  <option value="NON_COMMENCE">NON COMMENCÉ</option>
                  <option value="EN_COURS">EN COURS</option>
                  <option value="TERMINE">TERMINÉ</option>
                </select>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Date de début</label>
                <input v-model="editForm.dateDebut" type="date" class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-2.5 text-white focus:ring-2 focus:ring-emerald-500 outline-none" />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Date de fin</label>
                <input v-model="editForm.dateFin" type="date" class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-2.5 text-white focus:ring-2 focus:ring-emerald-500 outline-none" />
              </div>

            </div>

            <div class="flex justify-end pt-4">
              <button type="submit" :disabled="saving" class="bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white px-6 py-2.5 rounded-xl text-sm font-semibold shadow-[0_0_20px_rgba(16,185,129,0.4)] transition-all flex items-center gap-2">
                <span v-if="saving" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                {{ saving ? 'Sauvegarde...' : 'Enregistrer les modifications' }}
              </button>
            </div>
          </form>
        </div>
        
        <!-- Add manual comment -->
        <div class="glass-card p-6 border-l-4 border-l-emerald-500">
          <h4 class="font-bold text-white mb-4">Ajouter une note de suivi</h4>
          <form @submit.prevent="submitComment" class="flex flex-col items-end gap-3">
            <textarea v-model="newComment" required rows="2" class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-emerald-500 outline-none placeholder-gray-500" placeholder="Décrivez l'avancée ou le point bloquant..."></textarea>
            <button type="submit" :disabled="commenting" class="px-5 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg text-sm font-medium transition-colors">
              {{ commenting ? 'Envoi...' : 'Publier' }}
            </button>
          </form>
        </div>
      </div>

      <!-- Right Column: Journal -->
      <div class="space-y-6">
        <div class="glass-card p-6 h-full border border-white/5 flex flex-col">
          <h3 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            Journal de Suivi
          </h3>
          
          <div v-if="loadingSuivis" class="space-y-4">
            <div v-for="i in 3" :key="i" class="h-16 bg-gray-800 rounded-lg animate-pulse"></div>
          </div>
          <div v-else-if="suivis.length === 0" class="text-gray-500 text-center py-10 flex-1">
            Aucun historique.
          </div>
          <div v-else class="space-y-4 overflow-y-auto pr-2 flex-1" style="max-height: 600px;">
            <div v-for="s in suivis" :key="s.id" class="p-4 rounded-xl bg-black/40 border border-white/5">
              <div class="text-xs text-emerald-400 font-mono mb-2">{{ formatDateTime(s.dateSuivi) }}</div>
              <p class="text-sm text-gray-300 leading-relaxed">{{ s.commentaire }}</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useRoute } from 'vue-router';
import { useActionPlanStore } from '../stores/actionPlanStore';

const route = useRoute();
const id = route.params.id as string;
const store = useActionPlanStore();

const loading = ref(true);
const saving = ref(false);
const plan = ref<any>(null);
const error = ref('');

const suivis = ref<any[]>([]);
const loadingSuivis = ref(false);
const newComment = ref('');
const commenting = ref(false);

const editForm = reactive({
  tauxAvancement: 0,
  statut: 'NON_COMMENCE',
  dateDebut: '',
  dateFin: ''
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
    error.value = "Impossible de charger le plan d'action.";
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
  saving.value = true;
  error.value = '';
  try {
    const updated = {
      ...plan.value,
      tauxAvancement: editForm.tauxAvancement,
      statut: editForm.statut,
      dateDebut: editForm.dateDebut ? editForm.dateDebut : null,
      dateFin: editForm.dateFin ? editForm.dateFin : null
    };
    
    await store.updatePlan(id, updated);
    
    // Reload full data to refresh journal if any auto-logs were created
    await loadData();
  } catch (e: any) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) return;
  commenting.value = true;
  try {
    await store.addSuivi(id, newComment.value);
    newComment.value = '';
    await loadSuivis();
  } catch (e: any) {
    error.value = "Erreur lors de l'ajout du commentaire.";
  } finally {
    commenting.value = false;
  }
};

const getProgressColor = (val: number) => {
  if (val === 100) return 'bg-green-500';
  if (val > 50) return 'bg-blue-500';
  return 'bg-orange-500';
};

const formatDateTime = (dateString: string) => {
  if (!dateString) return '';
  const d = new Date(dateString);
  return d.toLocaleString('fr-FR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  });
};
</script>
