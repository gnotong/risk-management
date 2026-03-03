import type { UserRepository } from '../../../domain/repositories/UserRepository';
import type { User, UpdateUserRequest } from '../../../domain/entities/Risk';

export class UpdateUserUseCase {
    constructor(private userRepository: UserRepository) { }

    async execute(id: string, request: UpdateUserRequest): Promise<User> {
        if (!request.username) {
            throw new Error("Username is required");
        }
        if (!request.email) {
            throw new Error("Email is required");
        }
        if (!request.firstName) {
            throw new Error("First name is required");
        }
        if (!request.lastName) {
            throw new Error("Last name is required");
        }
        if (!request.role) {
            throw new Error("Role is required");
        }

        return this.userRepository.update(id, request);
    }
}
