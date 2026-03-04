import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';

export class DeleteIncidentSuiviUseCase {
    constructor(private repository: IncidentRepository) { }

    async execute(incidentId: string, suiviId: string): Promise<void> {
        return await this.repository.deleteSuivi(incidentId, suiviId);
    }
}
