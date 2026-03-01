import type { ActionPlan } from '../../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class CreateActionPlanUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }
    async execute(plan: Partial<ActionPlan>): Promise<ActionPlan> {
        return await this.actionPlanRepository.create(plan);
    }
}
