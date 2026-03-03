import { httpClient } from '../http/httpClient';
import type { User, CreateUserRequest, UpdateUserRequest } from '../../domain/entities/Risk';
import type { UserRepository } from '../../domain/repositories/UserRepository';

export class ApiUserRepository implements UserRepository {
    async getById(id: string): Promise<User> {
        const response = await httpClient.get<User>(`/utilisateurs/${id}`);
        return response.data;
    }
    async findAll(): Promise<User[]> {
        return await httpClient.get<any, User[]>('/utilisateurs');
    }

    async create(request: CreateUserRequest): Promise<User> {
        const response = await httpClient.post<User>('/utilisateurs/register', request);
        return response.data;
    }

    async update(id: string, request: UpdateUserRequest): Promise<User> {
        const response = await httpClient.put<User>(`/utilisateurs/${id}`, request);
        return response.data;
    }

    async delete(id: string): Promise<void> {
        await httpClient.delete(`/utilisateurs/${id}`);
    }

    async sync(id: string): Promise<void> {
        await httpClient.post(`/utilisateurs/${id}/sync`);
    }
}

export const apiUserRepository = new ApiUserRepository();
