import { httpClient } from '../http/httpClient';
import type { Risk } from '../../domain/entities/Risk';
import type { RiskRepository } from '../../domain/repositories/RiskRepository';

export class ApiRiskRepository implements RiskRepository {
    async findAll(): Promise<Risk[]> {
        return await httpClient.get<any, Risk[]>('/risques');
    }

    async findById(id: string): Promise<Risk> {
        return await httpClient.get<any, Risk>(`/risques/${id}`);
    }

    async create(risk: Partial<Risk>): Promise<Risk> {
        return await httpClient.post<any, Risk>('/risques', risk);
    }

    async update(id: string, risk: Partial<Risk>): Promise<Risk> {
        return await httpClient.put<any, Risk>(`/risques/${id}`, risk);
    }

    async delete(id: string): Promise<void> {
        await httpClient.delete<any, void>(`/risques/${id}`);
    }
}

export const apiRiskRepository = new ApiRiskRepository();
