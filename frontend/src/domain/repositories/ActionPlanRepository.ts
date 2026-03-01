import type { ActionPlan, Suivi } from '../entities/ActionPlan';

export interface ActionPlanRepository {
    findAll(): Promise<ActionPlan[]>;
    findById(id: string): Promise<ActionPlan>;
    create(plan: Partial<ActionPlan>): Promise<ActionPlan>;
    update(id: string, plan: Partial<ActionPlan>): Promise<ActionPlan>;
    delete(id: string): Promise<void>;

    getSuivis(planId: string): Promise<Suivi[]>;
    addSuivi(planId: string, commentaire: string): Promise<Suivi>;
    deleteSuivi(planId: string, suiviId: string): Promise<void>;
}
