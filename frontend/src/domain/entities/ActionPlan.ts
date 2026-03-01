import type { Risk, User } from './Risk';

export interface Suivi {
    id?: string;
    commentaire: string;
    dateSuivi?: string;
}

export interface ActionPlan {
    id: string;
    nom: string;
    description?: string;
    dateDebut?: string;
    dateFin?: string;
    statut?: string;
    tauxAvancement?: number;
    risque?: Partial<Risk>;
    responsable?: User;
    suivis?: Suivi[];
}
