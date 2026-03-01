import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { apiRiskRepository } from '../infrastructure/repositories/ApiRiskRepository';
import { GetRisksUseCase } from '../application/usecases/risk/GetRisksUseCase';
import { DeleteRiskUseCase } from '../application/usecases/risk/DeleteRiskUseCase';
import { CreateRiskUseCase } from '../application/usecases/risk/CreateRiskUseCase';
import { GetRiskByIdUseCase } from '../application/usecases/risk/GetRiskByIdUseCase';
import { UpdateRiskUseCase } from '../application/usecases/risk/UpdateRiskUseCase';

const getRisksUseCase = new GetRisksUseCase(apiRiskRepository);
const deleteRiskUseCase = new DeleteRiskUseCase(apiRiskRepository);
const createRiskUseCase = new CreateRiskUseCase(apiRiskRepository);
const getRiskByIdUseCase = new GetRiskByIdUseCase(apiRiskRepository);
const updateRiskUseCase = new UpdateRiskUseCase(apiRiskRepository);

export const useRiskStore = defineStore('risk', () => {
  const risks = ref<any[]>([]);
  const loading = ref(false);

  // Filters
  const searchQuery = ref('');
  const minScore = ref(0);
  const ownerFilter = ref('');

  // Heatmap specific filter
  const selectedProb = ref<number | null>(null);
  const selectedGrav = ref<number | null>(null);

  const fetchRisques = async () => {
    loading.value = true;
    try {
      risks.value = await getRisksUseCase.execute();
    } catch (e) {
      console.error(e);
    } finally {
      loading.value = false;
    }
  };

  const getRiskById = async (id: string) => {
    return await getRiskByIdUseCase.execute(id);
  };

  const createRisk = async (risk: any) => {
    const newRisk = await createRiskUseCase.execute(risk);
    risks.value.push(newRisk);
    return newRisk;
  };

  const updateRisk = async (id: string, risk: any) => {
    const updatedRisk = await updateRiskUseCase.execute(id, risk);
    const index = risks.value.findIndex(r => r.id === id);
    if (index !== -1) {
      risks.value[index] = updatedRisk;
    }
    return updatedRisk;
  };

  const deleteRisk = async (id: string) => {
    try {
      await deleteRiskUseCase.execute(id);
      // Remove from local list
      risks.value = risks.value.filter(r => r.id !== id);
    } catch (e: any) {
      throw new Error(e.message || "Impossible de supprimer ce risque.");
    }
  };

  const filteredRisks = computed(() => {
    return risks.value.filter(r => {
      const normalize = (str: string) => str ? str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase() : "";

      const query = normalize(searchQuery.value);
      let matchSearch = true;

      // Only apply text search if query is 3 characters or more
      if (query.length >= 3) {
        const titleMatch = normalize(r.libelle).includes(query);
        const ownerMatch = r.proprietaire?.nom ? normalize(r.proprietaire.nom).includes(query) : false;
        matchSearch = titleMatch || ownerMatch;
      }

      const matchScore = r.score >= minScore.value;
      const matchOwner = ownerFilter.value
        ? r.proprietaire?.nom && normalize(r.proprietaire.nom).includes(normalize(ownerFilter.value))
        : true;

      const matchHeatmap = (selectedProb.value === null || r.probabilite === selectedProb.value) &&
        (selectedGrav.value === null || r.gravite === selectedGrav.value);

      return matchSearch && matchScore && matchOwner && matchHeatmap;
    });
  });

  const clearHeatmapFilter = () => {
    selectedProb.value = null;
    selectedGrav.value = null;
  };

  return {
    risks, loading,
    searchQuery, minScore, ownerFilter, selectedProb, selectedGrav,
    fetchRisques, getRiskById, createRisk, updateRisk, deleteRisk, filteredRisks, clearHeatmapFilter
  };
});
