import type { ActionPlan } from '../../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class GetActionPlanByIdUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(id: string): Promise<ActionPlan> {
        return await this.actionPlanRepository.findById(id);
    }
}
