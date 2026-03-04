import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';
import type { Incident } from '../../../domain/entities/Incident';

export class CreateIncidentUseCase {
    constructor(private incidentRepository: IncidentRepository) { }

    async execute(incident: Omit<Incident, 'id'>): Promise<Incident> {
        return await this.incidentRepository.create(incident);
    }
}
