import type { User } from '../../../domain/entities/Risk';
import type { UserRepository } from '../../../domain/repositories/UserRepository';

export class GetUsersUseCase {
    constructor(private userRepository: UserRepository) { }

    async execute(): Promise<User[]> {
        return await this.userRepository.findAll();
    }
}
