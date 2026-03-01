import type { RiskRepository } from '../../../domain/repositories/RiskRepository';

export class DeleteRiskUseCase {
    constructor(private riskRepository: RiskRepository) { }

    async execute(id: string): Promise<void> {
        await this.riskRepository.delete(id);
    }
}
