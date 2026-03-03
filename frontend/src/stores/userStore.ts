import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { apiUserRepository } from '../infrastructure/repositories/ApiUserRepository';
import { GetUsersUseCase } from '../application/usecases/user/GetUsersUseCase';
import { CreateUserUseCase } from '../application/usecases/user/CreateUserUseCase';
import type { CreateUserRequest } from '../domain/entities/Risk';

const getUsersUseCase = new GetUsersUseCase(apiUserRepository);
const createUserUseCase = new CreateUserUseCase(apiUserRepository);

export const useUserStore = defineStore('user', () => {
    const users = ref<any[]>([]);
    const loading = ref(false);
    const userRole = ref<string>('');
    const username = ref<string>('');

    const fetchUsers = async () => {
        loading.value = true;
        try {
            users.value = await getUsersUseCase.execute();
        } catch (e) {
            console.error(e);
        } finally {
            loading.value = false;
        }
    };

    const setUserRole = (role: string) => {
        userRole.value = role;
    };

    const setUsername = (name: string) => {
        username.value = name;
    };

    const createUser = async (request: CreateUserRequest) => {
        return await createUserUseCase.execute(request);
    };

    const isAdmin = computed(() => userRole.value === 'ADMIN');

    return {
        users,
        loading,
        fetchUsers,
        createUser,
        userRole,
        setUserRole,
        username,
        setUsername,
        isAdmin
    };
});

