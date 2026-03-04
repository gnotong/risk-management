import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';
import type { SuiviIncident } from '../../../domain/entities/Incident';

export class GetIncidentSuivisUseCase {
    constructor(private repository: IncidentRepository) { }

    async execute(incidentId: string): Promise<SuiviIncident[]> {
        return await this.repository.getSuivis(incidentId);
    }
}
