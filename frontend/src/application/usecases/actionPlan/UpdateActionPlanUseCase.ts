import type { ActionPlan } from '../../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class UpdateActionPlanUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(id: string, plan: Partial<ActionPlan>): Promise<ActionPlan> {
        return await this.actionPlanRepository.update(id, plan);
    }
}
