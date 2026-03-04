import type { Incident } from '../entities/Incident';

export interface IncidentRepository {
    findAll(): Promise<Incident[]>;
    getById(id: string): Promise<Incident>;
    create(incident: Omit<Incident, 'id'>): Promise<Incident>;
    update(id: string, incident: Incident): Promise<Incident>;
    delete(id: string): Promise<void>;
    getSuivis(id: string): Promise<any[]>;
    addSuivi(id: string, commentaire: string): Promise<any>;
    deleteSuivi(incidentId: string, suiviId: string): Promise<void>;
}
