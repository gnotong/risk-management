import { httpClient } from '../http/httpClient';
import type { ActionPlan, Suivi } from '../../domain/entities/ActionPlan';
import type { ActionPlanRepository } from '../../domain/repositories/ActionPlanRepository';

export class ApiActionPlanRepository implements ActionPlanRepository {
    async findAll(): Promise<ActionPlan[]> {
        return await httpClient.get<any, ActionPlan[]>('/planactions');
    }

    async findById(id: string): Promise<ActionPlan> {
        return await httpClient.get<any, ActionPlan>(`/planactions/${id}`);
    }

    async create(plan: Partial<ActionPlan>): Promise<ActionPlan> {
        return await httpClient.post<any, ActionPlan>('/planactions', plan);
    }

    async update(id: string, plan: Partial<ActionPlan>): Promise<ActionPlan> {
        // Simulated auth via header: Admin or generic user id logic previously in store
        const userId = plan.responsable?.id || 'admin-override';
        const res = await httpClient.put<any, ActionPlan>(`/planactions/${id}`, plan, {
            headers: {
                'X-User-Id': userId
            }
        });
        return res;
    }

    async delete(id: string): Promise<void> {
        await httpClient.delete<any, void>(`/planactions/${id}`);
    }

    async getSuivis(planId: string): Promise<Suivi[]> {
        return await httpClient.get<any, Suivi[]>(`/planactions/${planId}/suivis`);
    }

    async addSuivi(planId: string, commentaire: string): Promise<Suivi> {
        return await httpClient.post<any, Suivi>(`/planactions/${planId}/suivis`, { commentaire });
    }

    async deleteSuivi(planId: string, suiviId: string): Promise<void> {
        await httpClient.delete<any, void>(`/planactions/${planId}/suivis/${suiviId}`);
    }
}

export const apiActionPlanRepository = new ApiActionPlanRepository();
