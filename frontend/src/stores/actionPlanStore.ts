import { defineStore } from 'pinia';
import { ref } from 'vue';
import { apiActionPlanRepository } from '../infrastructure/repositories/ApiActionPlanRepository';
import { GetActionPlansUseCase } from '../application/usecases/actionPlan/GetActionPlansUseCase';
import { GetActionPlanByIdUseCase } from '../application/usecases/actionPlan/GetActionPlanByIdUseCase';
import { CreateActionPlanUseCase } from '../application/usecases/actionPlan/CreateActionPlanUseCase';
import { UpdateActionPlanUseCase } from '../application/usecases/actionPlan/UpdateActionPlanUseCase';
import { DeleteActionPlanUseCase } from '../application/usecases/actionPlan/DeleteActionPlanUseCase';
import { GetSuivisUseCase } from '../application/usecases/actionPlan/GetSuivisUseCase';
import { AddSuiviUseCase } from '../application/usecases/actionPlan/AddSuiviUseCase';
import { DeleteSuiviUseCase } from '../application/usecases/actionPlan/DeleteSuiviUseCase';

const getActionPlansUseCase = new GetActionPlansUseCase(apiActionPlanRepository);
const getActionPlanByIdUseCase = new GetActionPlanByIdUseCase(apiActionPlanRepository);
const createActionPlanUseCase = new CreateActionPlanUseCase(apiActionPlanRepository);
const updateActionPlanUseCase = new UpdateActionPlanUseCase(apiActionPlanRepository);
const deleteActionPlanUseCase = new DeleteActionPlanUseCase(apiActionPlanRepository);
const getSuivisUseCase = new GetSuivisUseCase(apiActionPlanRepository);
const addSuiviUseCase = new AddSuiviUseCase(apiActionPlanRepository);
const deleteSuiviUseCase = new DeleteSuiviUseCase(apiActionPlanRepository);

export const useActionPlanStore = defineStore('actionPlan', () => {
    const plans = ref<any[]>([]);
    const loading = ref(false);

    const fetchPlans = async () => {
        loading.value = true;
        try {
            plans.value = await getActionPlansUseCase.execute();
        } catch (e) {
            console.error(e);
        } finally {
            loading.value = false;
        }
    };

    const getPlanById = async (id: string) => {
        return await getActionPlanByIdUseCase.execute(id);
    };

    const createPlan = async (plan: any) => {
        try {
            const newPlan = await createActionPlanUseCase.execute(plan);
            plans.value.push(newPlan);
            return newPlan;
        } catch (e: any) {
            throw new Error(e.message || "Erreur lors de la création.");
        }
    };

    const updatePlan = async (id: string, plan: any) => {
        try {
            return await updateActionPlanUseCase.execute(id, plan);
        } catch (e: any) {
            throw new Error(e.message || "Erreur de validation. Vérifiez les règles métier.");
        }
    };

    const deletePlan = async (id: string) => {
        try {
            await deleteActionPlanUseCase.execute(id);
            // Remove from local list if present
            plans.value = plans.value.filter(p => p.id !== id);
        } catch (e: any) {
            throw new Error(e.message || "Impossible de supprimer ce plan d'action.");
        }
    };

    const fetchSuivis = async (id: string) => {
        try {
            return await getSuivisUseCase.execute(id);
        } catch (e) {
            return [];
        }
    };

    const addSuivi = async (id: string, commentaire: string) => {
        return await addSuiviUseCase.execute(id, commentaire);
    };

    const deleteSuivi = async (planId: string, suiviId: string) => {
        try {
            await deleteSuiviUseCase.execute(planId, suiviId);
        } catch (e: any) {
            throw new Error(e.message || "Impossible de supprimer ce suivi.");
        }
    };

    return { plans, loading, fetchPlans, getPlanById, createPlan, updatePlan, deletePlan, fetchSuivis, addSuivi, deleteSuivi };
});
