import type { ActionPlanRepository } from '../../../domain/repositories/ActionPlanRepository';

export class DeleteActionPlanUseCase {
    constructor(private actionPlanRepository: ActionPlanRepository) { }

    async execute(id: string): Promise<void> {
        await this.actionPlanRepository.delete(id);
    }
}
