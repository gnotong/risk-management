import type { User } from '../entities/Risk';
export interface UserRepository {
    findAll(): Promise<User[]>;
}
