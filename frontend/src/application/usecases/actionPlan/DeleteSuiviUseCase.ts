import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class DeleteSuiviUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(planId: string, suiviId: string): Promise<void> {
        await this.actionPlanRepository.deleteSuivi(planId, suiviId);
    }
}
