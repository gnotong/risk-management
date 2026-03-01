import type { Suivi } from '../../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class GetSuivisUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(planId: string): Promise<Suivi[]> {
        return await this.actionPlanRepository.getSuivis(planId);
    }
}
