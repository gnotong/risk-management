import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useRiskStore = defineStore('risk', () => {
  const risks = ref<any[]>([]);
  const loading = ref(false);

  // Filters
  const searchQuery = ref('');
  const minScore = ref(0);
  const ownerFilter = ref('');

  const fetchRisques = async () => {
    loading.value = true;
    try {
      const res = await fetch('/api/risques');
      const data = await res.json();
      risks.value = data;
    } catch (e) {
      console.error(e);
    } finally {
      loading.value = false;
    }
  };

  const filteredRisks = computed(() => {
    return risks.value.filter(r => {
      const normalize = (str: string) => str ? str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase() : "";
      const matchSearch = normalize(r.libelle).includes(normalize(searchQuery.value));
      const matchScore = r.score >= minScore.value;
      const matchOwner = ownerFilter.value ? r.proprietaire?.nom === ownerFilter.value : true;
      return matchSearch && matchScore && matchOwner;
    });
  });

  return { risks, loading, searchQuery, minScore, ownerFilter, fetchRisques, filteredRisks };
});
