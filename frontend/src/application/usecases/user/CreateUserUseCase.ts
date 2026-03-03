import type { User, CreateUserRequest } from '../../../domain/entities/Risk';
import type { UserRepository } from '../../../domain/repositories/UserRepository';

export class CreateUserUseCase {
    constructor(private userRepository: UserRepository) { }

    async execute(request: CreateUserRequest): Promise<User> {
        return await this.userRepository.create(request);
    }
}
