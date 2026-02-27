<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
      <div>
        <h2 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight">Tableau de Bord des Risques</h2>
        <p class="text-gray-400 mt-2 text-lg">Vue globale de la cartographie des risques et filtrage dynamique.</p>
      </div>
      <div class="flex gap-3">
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
        <RiskHeatmap @filterMatrix="handleMatrixFilter"/>
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
            <div 
              v-for="r in store.filteredRisks" 
              :key="r.id" 
              class="flex justify-between items-center p-5 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 hover:border-blue-500/50 hover:shadow-[0_0_20px_rgba(59,130,246,0.15)] transition-all cursor-pointer group"
            >
              <div>
                <h4 class="text-lg font-bold text-gray-100 group-hover:text-blue-400 transition-colors">{{ r.libelle }}</h4>
                <div class="text-sm text-gray-400 flex gap-4 mt-1">
                  <span>Opérateur: {{ r.proprietaire?.nom || 'Non assigné' }}</span>
                  <span class="flex items-center gap-1">
                    <span :class="getStatusColor(r.statut)" class="w-2 h-2 rounded-full"></span>
                    {{ r.statut }}
                  </span>
                </div>
              </div>
              <div class="text-center bg-black/40 px-5 py-2.5 rounded-xl border border-white/5 shadow-inner">
                <div class="text-xs text-gray-400 uppercase tracking-widest font-semibold mb-0.5">Score</div>
                <div class="font-bold text-xl" :class="getScoreColor(r.score)">{{ r.score }}</div>
              </div>
            </div>
          </transition-group>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRiskStore } from '../stores/riskStore';
import FilterBar from '../components/FilterBar.vue';
import RiskHeatmap from '../components/RiskHeatmap.vue';

const store = useRiskStore();

onMounted(() => {
  store.fetchRisques();
});

const handleMatrixFilter = ({ probabilite, gravite }: { probabilite: number, gravite: number }) => {
  // Simplistic filter application
  store.minScore = probabilite * gravite; 
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
  alert("Génération PDF (Simulation jsPDF)...");
};

const exportExcel = () => {
  alert("Génération Excel (Simulation)...");
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
