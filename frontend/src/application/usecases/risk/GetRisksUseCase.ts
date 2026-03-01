import type { Risk } from '../../../domain/entities/Risk';
import type { RiskRepository } from '../../../domain/repositories/RiskRepository';

export class GetRisksUseCase {
    constructor(private riskRepository: RiskRepository) { }

    async execute(): Promise<Risk[]> {
        return await this.riskRepository.findAll();
    }
}
