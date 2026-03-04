import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';
import type { Incident } from '../../../domain/entities/Incident';

export class GetIncidentByIdUseCase {
    constructor(private incidentRepository: IncidentRepository) { }

    async execute(id: string): Promise<Incident> {
        return await this.incidentRepository.getById(id);
    }
}
