import { defineStore } from 'pinia';
import { ref } from 'vue';
import { apiIncidentRepository } from '../infrastructure/repositories/ApiIncidentRepository';
import { GetIncidentsUseCase } from '../application/usecases/incident/GetIncidentsUseCase';
import { GetIncidentByIdUseCase } from '../application/usecases/incident/GetIncidentByIdUseCase';
import { CreateIncidentUseCase } from '../application/usecases/incident/CreateIncidentUseCase';
import { UpdateIncidentUseCase } from '../application/usecases/incident/UpdateIncidentUseCase';
import { DeleteIncidentUseCase } from '../application/usecases/incident/DeleteIncidentUseCase';
import { GetIncidentSuivisUseCase } from '../application/usecases/incident/GetIncidentSuivisUseCase';
import { AddIncidentSuiviUseCase } from '../application/usecases/incident/AddIncidentSuiviUseCase';
import { DeleteIncidentSuiviUseCase } from '../application/usecases/incident/DeleteIncidentSuiviUseCase';

const getIncidentsUseCase = new GetIncidentsUseCase(apiIncidentRepository);
const getIncidentByIdUseCase = new GetIncidentByIdUseCase(apiIncidentRepository);
const createIncidentUseCase = new CreateIncidentUseCase(apiIncidentRepository);
const updateIncidentUseCase = new UpdateIncidentUseCase(apiIncidentRepository);
const deleteIncidentUseCase = new DeleteIncidentUseCase(apiIncidentRepository);
const getIncidentSuivisUseCase = new GetIncidentSuivisUseCase(apiIncidentRepository);
const addIncidentSuiviUseCase = new AddIncidentSuiviUseCase(apiIncidentRepository);
const deleteIncidentSuiviUseCase = new DeleteIncidentSuiviUseCase(apiIncidentRepository);

export const useIncidentStore = defineStore('incident', () => {
    const incidents = ref<any[]>([]);
    const loading = ref(false);

    const fetchIncidents = async () => {
        loading.value = true;
        try {
            incidents.value = await getIncidentsUseCase.execute();
        } catch (e) {
            console.error(e);
        } finally {
            loading.value = false;
        }
    };

    const getIncidentById = async (id: string) => {
        return await getIncidentByIdUseCase.execute(id);
    };

    const createIncident = async (incident: any) => {
        try {
            const newIncident = await createIncidentUseCase.execute(incident);
            incidents.value.push(newIncident);
            return newIncident;
        } catch (e: any) {
            throw new Error(e.message || "Erreur lors de la création de l'incident.");
        }
    };

    const updateIncident = async (id: string, incident: any) => {
        try {
            return await updateIncidentUseCase.execute(id, incident);
        } catch (e: any) {
            throw new Error(e.message || "Erreur lors de la mise à jour de l'incident.");
        }
    };

    const deleteIncident = async (id: string) => {
        try {
            await deleteIncidentUseCase.execute(id);
            incidents.value = incidents.value.filter(i => i.id !== id);
        } catch (e: any) {
            throw new Error(e.message || "Impossible de supprimer cet incident.");
        }
    };

    const fetchSuivis = async (id: string) => {
        try {
            return await getIncidentSuivisUseCase.execute(id);
        } catch (e) {
            return [];
        }
    };

    const addSuivi = async (id: string, commentaire: string) => {
        return await addIncidentSuiviUseCase.execute(id, commentaire);
    };

    const deleteSuivi = async (incidentId: string, suiviId: string) => {
        try {
            await deleteIncidentSuiviUseCase.execute(incidentId, suiviId);
        } catch (e: any) {
            throw new Error(e.message || "Impossible de supprimer ce suivi.");
        }
    };

    return { incidents, loading, fetchIncidents, getIncidentById, createIncident, updateIncident, deleteIncident, fetchSuivis, addSuivi, deleteSuivi };
});
