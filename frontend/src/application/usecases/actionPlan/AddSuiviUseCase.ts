import type { Suivi } from '../../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class AddSuiviUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(planId: string, commentaire: string): Promise<Suivi> {
        return await this.actionPlanRepository.addSuivi(planId, commentaire);
    }
}
