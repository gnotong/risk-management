import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';
import type { SuiviIncident } from '../../../domain/entities/Incident';

export class AddIncidentSuiviUseCase {
    constructor(private repository: IncidentRepository) { }

    async execute(incidentId: string, commentaire: string): Promise<SuiviIncident> {
        return await this.repository.addSuivi(incidentId, commentaire);
    }
}
