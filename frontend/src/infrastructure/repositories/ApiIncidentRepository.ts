import { httpClient } from '../http/httpClient';
import type { Incident, SuiviIncident } from '../../domain/entities/Incident';
import type { IncidentRepository } from '../../domain/repositories/IncidentRepository';

export class ApiIncidentRepository implements IncidentRepository {
    async findAll(): Promise<Incident[]> {
        return await httpClient.get<any, Incident[]>('/incidents');
    }

    async getById(id: string): Promise<Incident> {
        return await httpClient.get<any, Incident>(`/incidents/${id}`);
    }

    async create(incident: Omit<Incident, 'id'>): Promise<Incident> {
        return await httpClient.post<any, Incident>('/incidents', incident);
    }

    async update(id: string, incident: Incident): Promise<Incident> {
        return await httpClient.put<any, Incident>(`/incidents/${id}`, incident);
    }

    async delete(id: string): Promise<void> {
        await httpClient.delete<any, void>(`/incidents/${id}`);
    }

    async getSuivis(id: string): Promise<SuiviIncident[]> {
        return await httpClient.get<any, SuiviIncident[]>(`/incidents/${id}/suivis`);
    }

    async addSuivi(id: string, commentaire: string): Promise<SuiviIncident> {
        return await httpClient.post<any, SuiviIncident>(`/incidents/${id}/suivis`, { commentaire });
    }

    async deleteSuivi(incidentId: string, suiviId: string): Promise<void> {
        await httpClient.delete<any, void>(`/incidents/${incidentId}/suivis/${suiviId}`);
    }
}

export const apiIncidentRepository = new ApiIncidentRepository();
