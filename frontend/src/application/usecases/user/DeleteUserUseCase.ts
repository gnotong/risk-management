import type { UserRepository } from '../../../domain/repositories/UserRepository';

export class DeleteUserUseCase {
    constructor(private userRepository: UserRepository) { }

    async execute(id: string): Promise<void> {
        if (!id) {
            throw new Error("User ID is required");
        }

        return this.userRepository.delete(id);
    }
}
