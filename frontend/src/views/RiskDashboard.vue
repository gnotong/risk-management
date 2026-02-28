<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-3 sm:gap-4">
      <div class="w-full text-left min-w-0">
        <h2 class="text-xl sm:text-2xl md:text-3xl lg:text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight leading-tight truncate">{{ $t('dashboard.title') }}</h2>
        <p class="text-gray-400 mt-1 sm:mt-2 text-xs sm:text-sm md:text-base truncate">{{ $t('dashboard.subtitle') }}</p>
      </div>
      <div class="flex flex-wrap justify-start md:justify-end gap-2 sm:gap-3 mt-4 md:mt-0 w-full sm:w-auto">
        <button @click="isModalOpen = true" class="bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white px-2 sm:px-4 py-1.5 sm:py-2.5 rounded-xl text-[10px] sm:text-xs md:text-sm font-semibold shadow-[0_0_20px_rgba(16,185,129,0.4)] transition-all flex items-center gap-1 sm:gap-2 group cursor-pointer flex-1 sm:flex-none justify-center">
          <span class="text-sm sm:text-base md:text-lg group-hover:scale-110 transition-transform leading-none">+</span> <span class="truncate">{{ $t('dashboard.new_risk') }}</span>
        </button>
        <button @click="exportPDF" class="glass hover:bg-white/10 text-white px-2 sm:px-4 py-1.5 sm:py-2.5 rounded-xl text-[10px] sm:text-xs md:text-sm font-semibold transition-all flex items-center gap-1 sm:gap-2 group cursor-pointer hover:border-white/30 flex-1 sm:flex-none justify-center">
          <span class="group-hover:scale-110 transition-transform">📄</span> <span class="truncate">{{ $t('dashboard.export_pdf') }}</span>
        </button>
        <button @click="exportExcel" class="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white px-2 sm:px-4 py-1.5 sm:py-2.5 rounded-xl text-[10px] sm:text-xs md:text-sm font-semibold shadow-[0_0_20px_rgba(79,70,229,0.4)] transition-all flex items-center gap-1 sm:gap-2 group cursor-pointer flex-1 sm:flex-none justify-center">
          <span class="group-hover:scale-110 transition-transform">📊</span> <span class="truncate">{{ $t('dashboard.export_excel') }}</span>
        </button>
      </div>
    </div>

    <!-- Filters -->
    <FilterBar />

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
      
      <!-- Heatmap -->
      <div class="lg:col-span-1">
        <RiskHeatmap />
      </div>

      <!-- Risk List -->
      <div class="lg:col-span-2 glass-card p-4 sm:p-6 lg:p-8 w-full max-w-full overflow-hidden">
        <h3 class="text-lg sm:text-xl md:text-2xl font-bold mb-4 sm:mb-6 text-white flex items-center gap-2">
          <span class="w-1.5 h-6 bg-blue-500 rounded-full shadow-[0_0_10px_rgba(59,130,246,0.8)]"></span> {{ $t('nav.risks') }}
        </h3>
        
        <div v-if="store.loading" class="animate-pulse space-y-4">
          <div v-for="i in 3" :key="i" class="h-16 bg-gray-800 rounded-lg"></div>
        </div>
        
        <div v-else-if="store.filteredRisks.length === 0" class="text-gray-500 text-center py-10">
          {{ $t('dashboard.no_risks') }}
        </div>

        <div v-else class="space-y-3">
          <transition-group name="list">
            <router-link 
              v-for="r in paginatedRisks" 
              :key="r.id" 
              :to="`/risques/${r.id}`"
              class="flex justify-between items-center p-5 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 hover:border-blue-500/50 hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all cursor-pointer group"
            >
              <div class="min-w-0 flex-1">
                <h4 class="text-sm sm:text-base md:text-lg font-bold text-gray-100 group-hover:text-blue-400 transition-colors truncate">{{ r.libelle }}</h4>
                <div class="text-[10px] sm:text-xs md:text-sm text-gray-400 flex flex-wrap gap-2 sm:gap-4 mt-1 items-center">
                  <span class="truncate max-w-[100px] sm:max-w-none">{{ $t('dashboard.operator') }} {{ r.proprietaire?.nom || $t('dashboard.unassigned') }}</span>
                  <span class="flex items-center gap-1">
                    <span :class="getStatusColor(r.statut)" class="w-1.5 h-1.5 sm:w-2 sm:h-2 rounded-full flex-shrink-0"></span>
                    {{ $t(`status.${r.statut}`) }}
                  </span>
                  
                  <div v-if="getRiskPlanStats(r.id).hasPlans" class="flex flex-wrap items-center gap-2 ml-0 sm:ml-2 pl-0 sm:pl-4 border-l-0 sm:border-l border-white/10 relative group/tooltip mt-2 sm:mt-0 w-full sm:w-auto">
                    <span class="text-[10px] sm:text-xs text-gray-500 uppercase font-bold tracking-wider relative flex items-center">
                      PLANS
                      <span v-if="getRiskPlanStats(r.id).recentComments" class="ml-1 inline-flex items-center justify-center w-3 h-3 sm:w-4 sm:h-4 rounded-full bg-blue-500/20 text-blue-400 cursor-help">i</span>
                    </span>
                    <div class="w-12 sm:w-16 bg-gray-700 rounded-full h-1.5 flex overflow-hidden">
                      <div class="bg-emerald-500 h-1.5 rounded-full" :style="`width: ${getRiskPlanStats(r.id).avgProgress}%`"></div>
                    </div>
                    <span class="text-[10px] sm:text-xs font-bold text-gray-300">{{ getRiskPlanStats(r.id).avgProgress }}%</span>
                    
                    <!-- Tooltip -->
                    <div v-if="getRiskPlanStats(r.id).recentComments" class="absolute bottom-full left-0 sm:left-1/2 sm:-translate-x-1/2 mb-3 w-max max-w-[80vw] sm:w-72 bg-gray-900 border border-white/10 p-3 rounded-xl shadow-2xl opacity-0 invisible group-hover/tooltip:opacity-100 group-hover/tooltip:visible transition-all z-50 pointer-events-none">
                      <div class="text-xs text-gray-300 space-y-2 max-h-40 overflow-y-auto custom-scrollbar">
                        <div v-for="(comment, idx) in getRiskPlanStats(r.id).recentComments" :key="idx" class="border-b border-white/5 pb-2 last:border-0 last:pb-0">
                          <span class="text-emerald-400 font-bold block mb-0.5 truncate">{{ comment.plan }}</span>
                          <span class="text-gray-400 italic break-words line-clamp-2">"{{ comment.texte }}"</span>
                        </div>
                      </div>
                      <div class="absolute -bottom-2 left-4 sm:left-1/2 sm:-translate-x-1/2 w-4 h-4 bg-gray-900 border-r border-b border-white/10 transform rotate-45"></div>
                    </div>
                  </div>
                  <div v-else class="text-[10px] sm:text-xs text-gray-600 italic ml-0 sm:ml-2 pl-0 sm:pl-4 border-l-0 sm:border-l border-white/10 mt-2 sm:mt-0 w-full sm:w-auto">
                    {{ $t('dashboard.no_action_plan') }}
                  </div>
                </div>
              </div>
              <div class="flex items-center gap-4">
                <div class="text-center bg-black/40 px-5 py-2.5 rounded-xl border border-white/5 shadow-inner hidden sm:block">
                  <div class="text-xs text-gray-400 uppercase tracking-widest font-semibold mb-0.5">{{ $t('dashboard.score') }}</div>
                  <div class="font-bold text-xl" :class="getScoreColor(r.score)">{{ r.score }}</div>
                </div>
                <!-- Delete Button -->
                <button 
                  @click.prevent="confirmDeleteRisk(r.id, r.libelle)"
                  class="p-2 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500 hover:text-white transition-all border border-red-500/20 hover:border-red-500 opacity-0 group-hover:opacity-100"
                  title="Supprimer le risque"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
              </div>
            </router-link>
          </transition-group>
          
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
    </div>
    
    <RiskFormModal :isOpen="isModalOpen" @close="isModalOpen = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRiskStore } from '../stores/riskStore';
import { useActionPlanStore } from '../stores/actionPlanStore';
import FilterBar from '../components/FilterBar.vue';
import RiskHeatmap from '../components/RiskHeatmap.vue';
import RiskFormModal from '../components/RiskFormModal.vue';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { useI18n } from 'vue-i18n';

const store = useRiskStore();
const actionPlanStore = useActionPlanStore();
const { t } = useI18n();
const isModalOpen = ref(false);

const currentPage = ref(1);
const itemsPerPage = ref(5);

const totalPages = computed(() => Math.ceil(store.filteredRisks.length / itemsPerPage.value));

const paginatedRisks = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return store.filteredRisks.slice(start, end);
});

watch(() => store.filteredRisks, () => {
  currentPage.value = 1; // Reset page on filter change
}, { deep: true });

const confirmDeleteRisk = async (id: string, name: string) => {
  if (confirm(`Êtes-vous sûr de vouloir supprimer définitivement le risque "${name}" ?`)) {
    try {
      await store.deleteRisk(id);
    } catch (e: any) {
      alert(e.message || "Erreur lors de la suppression. Ce risque a probablement des plans d'action ou incidents liés.");
    }
  }
};

onMounted(async () => {
  store.fetchRisques();
  await actionPlanStore.fetchPlans();
});

const getRiskPlanStats = (riskId: string) => {
  if (!actionPlanStore.plans) return { hasPlans: false, avgProgress: 0, recentComments: null };
  const plans = actionPlanStore.plans.filter((p: any) => p.risque?.id === riskId);
  if (plans.length === 0) return { hasPlans: false, avgProgress: 0, recentComments: null };
  
  const avgProgress = Math.round(plans.reduce((sum: number, p: any) => sum + (p.tauxAvancement || 0), 0) / plans.length);
  
  const allComments = plans.flatMap((p: any) => 
    (p.suivis || []).map((s: any) => ({
      plan: p.nom,
      texte: s.commentaire,
      date: new Date(s.dateSuivi).getTime()
    }))
  ).sort((a, b) => b.date - a.date).slice(0, 3); // top 3 recent comments
  
  return { 
    hasPlans: true, 
    avgProgress, 
    recentComments: allComments.length > 0 ? allComments : null 
  };
};

const getStatusColor = (statut: string) => {
  if (statut === 'OUVERT') return 'bg-red-500';
  if (statut === 'EN_COURS') return 'bg-orange-500';
  return 'bg-green-500';
};

const getScoreColor = (score: number) => {
  if (score >= 6) return 'text-red-500';
  if (score >= 3) return 'text-orange-400';
  return 'text-green-400';
};

const exportPDF = () => {
  const doc = new jsPDF();
  doc.text(t('dashboard.title'), 14, 15);
  
  const tableData = store.filteredRisks.map(r => [
    r.libelle,
    r.score,
    t(`status.${r.statut}`),
    r.proprietaire?.nom || t('dashboard.unassigned')
  ]);

  autoTable(doc, {
    head: [['Libellé', t('dashboard.score'), 'Statut', t('dashboard.operator')]],
    body: tableData,
    startY: 20,
    theme: 'grid',
    styles: { font: 'helvetica', fontSize: 10 }
  });

  doc.save('risques.pdf');
};

const exportExcel = () => {
  const wsData = store.filteredRisks.map(r => ({
    'Libellé': r.libelle,
    [t('dashboard.score')]: r.score,
    'Statut': t(`status.${r.statut}`),
    [t('dashboard.operator')]: r.proprietaire?.nom || t('dashboard.unassigned')
  }));
  
  const ws = XLSX.utils.json_to_sheet(wsData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "Risques");
  XLSX.writeFile(wb, "risques.xlsx");
};
</script>

<style scoped>
.list-enter-active,
.list-leave-active {
  transition: all 0.4s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(-15px);
}
</style>
