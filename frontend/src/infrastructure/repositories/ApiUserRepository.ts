import { httpClient } from '../http/httpClient';
import type { User } from '../../domain/entities/Risk';
import type { UserRepository } from '../../domain/repositories/UserRepository';

export class ApiUserRepository implements UserRepository {
    async findAll(): Promise<User[]> {
        return await httpClient.get<any, User[]>('/utilisateurs');
    }
}

export const apiUserRepository = new ApiUserRepository();
