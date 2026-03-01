import type { Risk } from '../entities/Risk';

export interface RiskRepository {
    findAll(): Promise<Risk[]>;
    findById(id: string): Promise<Risk>;
    create(risk: Partial<Risk>): Promise<Risk>;
    update(id: string, risk: Partial<Risk>): Promise<Risk>;
    delete(id: string): Promise<void>;
}
