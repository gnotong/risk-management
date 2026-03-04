import type { IncidentRepository } from '../../../domain/repositories/IncidentRepository';

export class DeleteIncidentUseCase {
    constructor(private incidentRepository: IncidentRepository) { }

    async execute(id: string): Promise<void> {
        return await this.incidentRepository.delete(id);
    }
}
