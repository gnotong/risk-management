<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-500 tracking-tight">Suivi des Plans d'Action</h2>
        <p class="text-gray-400 mt-2 text-lg">Visualisation de l'avancement global et gestion des tâches.</p>
      </div>
    </div>

    <div class="glass-card p-6 lg:p-8 space-y-6">
      <div v-for="plan in plans" :key="plan.id" class="p-6 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 hover:border-blue-500/30 hover:shadow-[0_0_20px_rgba(59,130,246,0.1)] transition-all group">
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="font-bold text-lg text-white mb-1">{{ plan.nom }}</h3>
            <p class="text-sm text-gray-400 max-w-2xl">{{ plan.description }}</p>
          </div>
          <div class="text-right">
            <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">Responsable</div>
            <div class="font-medium text-gray-200">{{ plan.responsable.nom }}</div>
          </div>
        </div>

        <div class="mt-4">
          <div class="flex justify-between text-sm mb-1 font-medium">
            <span class="text-gray-400">Avancement</span>
            <span :class="plan.tauxAvancement > 80 ? 'text-green-400' : 'text-blue-400'">{{ plan.tauxAvancement }}%</span>
          </div>
          <div class="w-full bg-gray-700/50 rounded-full h-3 backdrop-blur-sm overflow-hidden border border-gray-600">
            <div 
              class="h-full rounded-full transition-all duration-1000 ease-out relative"
              :class="getProgressColor(plan.tauxAvancement)"
              :style="`width: ${plan.tauxAvancement}%`"
            >
              <div class="absolute inset-0 bg-white/20 animate-[pulse_2s_infinite]"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Mock data
const plans = ref([
  { id: 1, nom: "Mise en place Firewall NGFW", description: "Déploiement du nouveau cluster de pare-feu pour le réseau interne.", tauxAvancement: 45, responsable: { nom: "Dupont" } },
  { id: 2, nom: "Revue des accès AD", description: "Audit et nettoyage des comptes de l'Active Directory.", tauxAvancement: 85, responsable: { nom: "Martin" } },
  { id: 3, nom: "Formation Phishing", description: "Campagne de sensibilisation pour tous les employés.", tauxAvancement: 100, responsable: { nom: "Durand" } }
]);

const getProgressColor = (val: number) => {
  if (val === 100) return 'bg-gradient-to-r from-green-500 to-emerald-400 shadow-[0_0_10px_rgba(52,211,153,0.5)]';
  if (val > 50) return 'bg-gradient-to-r from-blue-500 to-cyan-400 shadow-[0_0_10px_rgba(56,189,248,0.5)]';
  return 'bg-gradient-to-r from-orange-500 to-amber-400 shadow-[0_0_10px_rgba(251,191,36,0.5)]';
};
</script>
