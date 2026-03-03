export interface User {
    id?: string;
    nom?: string;
    email?: string;
    roles?: string[];
}

export interface CreateUserRequest {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password?: string;
    role: string;
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
