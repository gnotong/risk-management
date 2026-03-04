import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';
import type { Incident } from '../../../domain/entities/Incident';

export class GetIncidentsUseCase {
    constructor(private incidentRepository: IncidentRepository) { }

    async execute(): Promise<Incident[]> {
        return await this.incidentRepository.findAll();
    }
}
