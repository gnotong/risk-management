import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { apiUserRepository } from '../infrastructure/repositories/ApiUserRepository';
import { GetUsersUseCase } from '../application/usecases/user/GetUsersUseCase';
import { CreateUserUseCase } from '../application/usecases/user/CreateUserUseCase';
import { UpdateUserUseCase } from '../application/usecases/user/UpdateUserUseCase';
import { DeleteUserUseCase } from '../application/usecases/user/DeleteUserUseCase';
import { SyncUserUseCase } from '../application/usecases/user/SyncUserUseCase';
import type { CreateUserRequest, UpdateUserRequest } from '../domain/entities/Risk';

const getUsersUseCase = new GetUsersUseCase(apiUserRepository);
const createUserUseCase = new CreateUserUseCase(apiUserRepository);
const updateUserUseCase = new UpdateUserUseCase(apiUserRepository);
const deleteUserUseCase = new DeleteUserUseCase(apiUserRepository);
const syncUserUseCase = new SyncUserUseCase(apiUserRepository);

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

    const updateUser = async (id: string, request: UpdateUserRequest) => {
        return await updateUserUseCase.execute(id, request);
    };

    const deleteUser = async (id: string) => {
        return await deleteUserUseCase.execute(id);
    };

    const syncUser = async (id: string) => {
        return await syncUserUseCase.execute(id);
    };

    const isAdmin = computed(() => userRole.value === 'ADMIN');

    return {
        users,
        loading,
        fetchUsers,
        createUser,
        updateUser,
        deleteUser,
        syncUser,
        userRole,
        setUserRole,
        username,
        setUsername,
        isAdmin
    };
});

