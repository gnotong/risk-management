import { defineStore } from 'pinia';
import { ref } from 'vue';
import { apiUserRepository } from '../infrastructure/repositories/ApiUserRepository';
import { GetUsersUseCase } from '../application/usecases/user/GetUsersUseCase';

const getUsersUseCase = new GetUsersUseCase(apiUserRepository);

export const useUserStore = defineStore('user', () => {
    const users = ref<any[]>([]);
    const loading = ref(false);

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

    return { users, loading, fetchUsers };
});
