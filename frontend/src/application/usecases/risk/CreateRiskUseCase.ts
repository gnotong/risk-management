import type { Risk } from '../../../domain/entities/Risk';
import type { RiskRepository } from '../../../domain/repositories/RiskRepository';

export class CreateRiskUseCase {
    constructor(private riskRepository: RiskRepository) { }
    async execute(risk: Partial<Risk>): Promise<Risk> {
        return await this.riskRepository.create(risk);
    }
}
