import type { User, CreateUserRequest, UpdateUserRequest } from '../entities/Risk';
export interface UserRepository {
    findAll(): Promise<User[]>;
    getById(id: string): Promise<User>;
    create(request: CreateUserRequest): Promise<User>;
    update(id: string, request: UpdateUserRequest): Promise<User>;
    delete(id: string): Promise<void>;
    sync(id: string): Promise<void>;
}
