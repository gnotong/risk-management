import type { UserRepository } from '../../../domain/repositories/UserRepository';

export class SyncUserUseCase {
    constructor(private userRepository: UserRepository) { }

    async execute(id: string): Promise<void> {
        return this.userRepository.sync(id);
    }
}
