import type { ActionPlan } from '../../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class GetActionPlansUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(): Promise<ActionPlan[]> {
        return await this.actionPlanRepository.findAll();
    }
}
