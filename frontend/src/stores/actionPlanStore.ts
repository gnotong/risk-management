import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useActionPlanStore = defineStore('actionPlan', () => {
    const plans = ref<any[]>([]);
    const loading = ref(false);

    const fetchPlans = async () => {
        loading.value = true;
        try {
            const res = await fetch('/api/planactions');
            plans.value = await res.json();
        } catch (e) {
            console.error(e);
        } finally {
            loading.value = false;
        }
    };

    const getPlanById = async (id: string) => {
        const res = await fetch(`/api/planactions/${id}`);
        if (!res.ok) throw new Error('Not found');
        return await res.json();
    };

    const updatePlan = async (id: string, plan: any) => {
        // Simulated auth via header: Admin or generic user id
        const userId = plan.responsable?.id || 'admin-override';
        const res = await fetch(`/api/planactions/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Id': userId
            },
            body: JSON.stringify(plan)
        });

        if (!res.ok) {
            let errorMsg = "Erreur de validation. Vérifiez les règles métier.";
            try {
                const text = await res.text();
                if (text) {
                    try {
                        const json = JSON.parse(text);
                        if (json.message) errorMsg = json.message;
                    } catch {
                        errorMsg = text;
                    }
                }
            } catch (e) { }
            throw new Error(errorMsg);
        }
        return await res.json();
    };

    const deletePlan = async (id: string) => {
        const res = await fetch(`/api/planactions/${id}`, {
            method: 'DELETE'
        });
        if (!res.ok) {
            throw new Error("Impossible de supprimer ce plan d'action.");
        }
        // Remove from local list if present
        plans.value = plans.value.filter(p => p.id !== id);
    };

    const fetchSuivis = async (id: string) => {
        const res = await fetch(`/api/planactions/${id}/suivis`);
        if (!res.ok) return [];
        return await res.json();
    };

    const addSuivi = async (id: string, commentaire: string) => {
        const res = await fetch(`/api/planactions/${id}/suivis`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ commentaire })
        });
        if (!res.ok) throw new Error('Erreur');
        return await res.json();
    };

    const deleteSuivi = async (planId: string, suiviId: string) => {
        const res = await fetch(`/api/planactions/${planId}/suivis/${suiviId}`, {
            method: 'DELETE'
        });
        if (!res.ok) {
            let msg = "Impossible de supprimer ce suivi.";
            try {
                const err = await res.json();
                if (err.message) msg = err.message;
            } catch (e) { }
            throw new Error(msg);
        }
    };

    return { plans, loading, fetchPlans, getPlanById, updatePlan, deletePlan, fetchSuivis, addSuivi, deleteSuivi };
});
