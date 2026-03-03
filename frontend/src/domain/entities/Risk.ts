export interface User {
    id?: string;
    nom?: string;
    email?: string;
    roles?: string[];
}

export interface CreateUserRequest {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string | null;
    password?: string;
}

export interface UpdateUserRequest {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string | null;
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
    statut: string;
    proprietaire?: User;
}
