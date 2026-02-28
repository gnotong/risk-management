<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
      <div>
        <h2 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight">Tableau de Bord des Risques</h2>
        <p class="text-gray-400 mt-2 text-lg">Vue globale de la cartographie des risques et filtrage dynamique.</p>
      </div>
      <div class="flex flex-wrap justify-end gap-3 mt-4 md:mt-0">
        <button @click="isModalOpen = true" class="bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white px-5 py-2.5 rounded-xl text-sm font-semibold shadow-[0_0_20px_rgba(16,185,129,0.4)] transition-all flex items-center gap-2 group cursor-pointer lg:mr-4">
          <span class="text-lg group-hover:scale-110 transition-transform">+</span> Nouveau Risque
        </button>
        <button @click="exportPDF" class="glass hover:bg-white/10 text-white px-5 py-2.5 rounded-xl text-sm font-semibold transition-all flex items-center gap-2 group cursor-pointer hover:border-white/30">
          <span class="group-hover:scale-110 transition-transform">📄</span> Export PDF
        </button>
        <button @click="exportExcel" class="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white px-5 py-2.5 rounded-xl text-sm font-semibold shadow-[0_0_20px_rgba(79,70,229,0.4)] transition-all flex items-center gap-2 group cursor-pointer">
          <span class="group-hover:scale-110 transition-transform">📊</span> Export Excel
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
      <div class="lg:col-span-2 glass-card p-6 lg:p-8">
        <h3 class="text-2xl font-bold mb-6 text-white flex items-center gap-2">
          <span class="w-1.5 h-6 bg-blue-500 rounded-full shadow-[0_0_10px_rgba(59,130,246,0.8)]"></span> Liste des Risques
        </h3>
        
        <div v-if="store.loading" class="animate-pulse space-y-4">
          <div v-for="i in 3" :key="i" class="h-16 bg-gray-800 rounded-lg"></div>
        </div>
        
        <div v-else-if="store.filteredRisks.length === 0" class="text-gray-500 text-center py-10">
          Aucun risque trouvé pour ces critères.
        </div>

        <div v-else class="space-y-3">
          <transition-group name="list">
            <router-link 
              v-for="r in paginatedRisks" 
              :key="r.id" 
              :to="`/risques/${r.id}`"
              class="flex justify-between items-center p-5 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 hover:border-blue-500/50 hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all cursor-pointer group"
            >
              <div>
                <h4 class="text-lg font-bold text-gray-100 group-hover:text-blue-400 transition-colors">{{ r.libelle }}</h4>
                <div class="text-sm text-gray-400 flex gap-4 mt-1 items-center">
                  <span>Opérateur: {{ r.proprietaire?.nom || 'Non assigné' }}</span>
                  <span class="flex items-center gap-1">
                    <span :class="getStatusColor(r.statut)" class="w-2 h-2 rounded-full"></span>
                    {{ r.statut }}
                  </span>
                  
                  <!-- Action Plan Progress -->
                  <div v-if="getRiskPlanStats(r.id).hasPlans" class="flex items-center gap-2 ml-2 pl-4 border-l border-white/10 relative group/tooltip">
                    <span class="text-xs text-gray-500 uppercase font-bold tracking-wider relative">
                      Plans d'action 
                      <span v-if="getRiskPlanStats(r.id).recentComments" class="ml-1 inline-flex items-center justify-center w-4 h-4 rounded-full bg-blue-500/20 text-blue-400 cursor-help">i</span>
                    </span>
                    <div class="w-16 bg-gray-700 rounded-full h-1.5 flex overflow-hidden">
                      <div class="bg-emerald-500 h-1.5 rounded-full" :style="`width: ${getRiskPlanStats(r.id).avgProgress}%`"></div>
                    </div>
                    <span class="text-xs font-bold text-gray-300">{{ getRiskPlanStats(r.id).avgProgress }}%</span>
                    
                    <!-- Tooltip -->
                    <div v-if="getRiskPlanStats(r.id).recentComments" class="absolute bottom-full left-1/2 -translate-x-1/2 mb-3 w-72 bg-gray-900 border border-white/10 p-3 rounded-xl shadow-2xl opacity-0 invisible group-hover/tooltip:opacity-100 group-hover/tooltip:visible transition-all z-50 pointer-events-none">
                      <div class="text-xs text-gray-300 space-y-2 max-h-40 overflow-y-auto custom-scrollbar">
                        <div v-for="(comment, idx) in getRiskPlanStats(r.id).recentComments" :key="idx" class="border-b border-white/5 pb-2 last:border-0 last:pb-0">
                          <span class="text-emerald-400 font-bold block mb-0.5">{{ comment.plan }}</span>
                          <span class="text-gray-400 italic">"{{ comment.texte }}"</span>
                        </div>
                      </div>
                      <div class="absolute -bottom-2 left-1/2 -translate-x-1/2 w-4 h-4 bg-gray-900 border-r border-b border-white/10 transform rotate-45"></div>
                    </div>
                  </div>
                  <div v-else class="text-xs text-gray-600 italic ml-2 pl-4 border-l border-white/10">
                    Aucun plan d'action
                  </div>
                </div>
              </div>
              <div class="text-center bg-black/40 px-5 py-2.5 rounded-xl border border-white/5 shadow-inner hidden sm:block">
                <div class="text-xs text-gray-400 uppercase tracking-widest font-semibold mb-0.5">Score</div>
                <div class="font-bold text-xl" :class="getScoreColor(r.score)">{{ r.score }}</div>
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

const store = useRiskStore();
const actionPlanStore = useActionPlanStore();
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
  doc.text("Rapport des Risques", 14, 15);
  
  const tableData = store.filteredRisks.map(r => [
    r.libelle,
    r.score,
    r.statut,
    r.proprietaire?.nom || 'Non assigné'
  ]);

  autoTable(doc, {
    head: [['Libellé', 'Score', 'Statut', 'Opérateur']],
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
    'Score': r.score,
    'Statut': r.statut,
    'Opérateur': r.proprietaire?.nom || 'Non assigné'
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
