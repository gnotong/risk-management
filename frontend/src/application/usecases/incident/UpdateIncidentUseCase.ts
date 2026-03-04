import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';
import type { Incident } from '../../../domain/entities/Incident';

export class UpdateIncidentUseCase {
    constructor(private incidentRepository: IncidentRepository) { }

    async execute(id: string, incident: Incident): Promise<Incident> {
        return await this.incidentRepository.update(id, incident);
    }
}
