import type { User, CreateUserRequest } from '../entities/Risk';
export interface UserRepository {
    findAll(): Promise<User[]>;
    create(request: CreateUserRequest): Promise<User>;
}
