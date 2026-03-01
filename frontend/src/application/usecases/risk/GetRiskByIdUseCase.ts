import type { Risk } from '../../../domain/entities/Risk';
import type { RiskRepository } from '../../../domain/repositories/RiskRepository';

export class GetRiskByIdUseCase {
    constructor(private riskRepository: RiskRepository) { }
    async execute(id: string): Promise<Risk> {
        return await this.riskRepository.findById(id);
    }
}
