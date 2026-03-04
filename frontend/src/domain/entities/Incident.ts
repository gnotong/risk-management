import type { Risk, User } from './Risk';

export enum StatutIncident {
    OUVERT = 'OUVERT',
    EN_COURS = 'EN_COURS',
    RESOLU = 'RESOLU',
    CLOTURE = 'CLOTURE'
}

export enum SeveriteIncident {
    FAIBLE = 'FAIBLE',
    MOYEN = 'MOYEN',
    ELEVE = 'ELEVE',
    CRITIQUE = 'CRITIQUE'
}

export interface Incident {
    id: string;
    titre: string;
    description: string;
    dateOccurence?: string;
    severite: SeveriteIncident;
    statut: StatutIncident;
    tauxAvancement: number;
    risque?: Partial<Risk>;
    signaleur?: User;
    impactReel?: string;
    commentaireUpdate?: string;
    dateCreation?: string;
    dateResolution?: string;
}

export interface SuiviIncident {
    id: string;
    incident?: { id: string };
    commentaire: string;
    dateSuivi: string;
}
