export enum Role {
    ADMIN = 'ADMIN',
    AUDITEUR = 'AUDITEUR',
    RESPONSABLE = 'RESPONSABLE',
    LECTEUR = 'LECTEUR'
}

export enum StatutRisque {
    OUVERT = 'OUVERT',
    EN_COURS = 'EN_COURS',
    CLOTURE = 'CLOTURE',
    ACCEPTE = 'ACCEPTE'
}

export enum StatutPlanAction {
    A_FAIRE = 'A_FAIRE',
    EN_COURS = 'EN_COURS',
    TERMINE = 'TERMINE',
    ANNULE = 'ANNULE',
    RETARD = 'RETARD'
}

export enum StatutRecommandation {
    OUVERTE = 'OUVERTE',
    EN_COURS = 'EN_COURS',
    CLOTUREE = 'CLOTUREE'
}

export enum StatutAudit {
    PLANIFIE = 'PLANIFIE',
    EN_COURS = 'EN_COURS',
    TERMINE = 'TERMINE',
    ANNULE = 'ANNULE'
}

export enum KeycloakSyncStatus {
    SYNCED = 'SYNCED',
    PENDING_CREATE = 'PENDING_CREATE',
    PENDING_UPDATE = 'PENDING_UPDATE',
    PENDING_DELETE = 'PENDING_DELETE'
}

export interface User {
    id?: string;
    nom?: string;
    email?: string;
    roles?: Role[];
    keycloakSyncStatus?: KeycloakSyncStatus;
}

export interface CreateUserRequest {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: Role | null;
    password?: string;
}

export interface UpdateUserRequest {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: Role | null;
    password?: string;
    isActive?: boolean;
}


export interface Risk {
    id: string;
    libelle: string;
    description?: string;
    probabilite: number;
    gravite: number;
    score: number;
    statut: StatutRisque;
    proprietaire?: User;
}
