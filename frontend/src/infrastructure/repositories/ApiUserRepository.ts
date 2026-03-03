import { httpClient } from '../http/httpClient';
import type { User, CreateUserRequest } from '../../domain/entities/Risk';
import type { UserRepository } from '../../domain/repositories/UserRepository';

export class ApiUserRepository implements UserRepository {
    async findAll(): Promise<User[]> {
        return await httpClient.get<any, User[]>('/utilisateurs');
    }

    async create(request: CreateUserRequest): Promise<User> {
        return await httpClient.post<any, User>('/utilisateurs/register', request);
    }
}

export const apiUserRepository = new ApiUserRepository();
