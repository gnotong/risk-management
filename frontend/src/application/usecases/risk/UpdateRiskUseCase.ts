import type { Risk } from '../../../domain/entities/Risk';
import type { RiskRepository } from '../../../domain/repositories/RiskRepository';

export class UpdateRiskUseCase {
    constructor(private riskRepository: RiskRepository) { }
    async execute(id: string, risk: Partial<Risk>): Promise<Risk> {
        return await this.riskRepository.update(id, risk);
    }
}
