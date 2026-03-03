<template>
  <div class="min-h-screen bg-slate-50 dark:bg-gradient-to-br dark:from-slate-900 dark:via-slate-800 dark:to-slate-700 p-6">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-4xl font-bold text-slate-900 dark:text-white mb-2">{{ $t('admin.title') }}</h1>
      <p class="text-slate-600 dark:text-slate-300">{{ $t('admin.description') }}</p>
    </div>

    <!-- Alert for non-admin users -->
    <div v-if="!isAdmin" class="bg-red-100 dark:bg-red-500/20 border border-red-200 dark:border-red-500 text-red-700 dark:text-red-200 px-6 py-4 rounded-lg mb-6 shadow-sm">
      <p>{{ $t('admin.access_denied') }}</p>
    </div>

    <div v-if="isAdmin" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Create User Form -->
      <div class="lg:col-span-1">
        <div class="bg-white dark:bg-slate-800/80 backdrop-blur-lg rounded-xl shadow-xl dark:shadow-2xl border border-slate-200 dark:border-slate-700/50 p-6">
          <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">{{ $t('admin.create_user') }}</h2>
          
          <form @submit.prevent="submitForm" class="space-y-4">
            <!-- Username -->
            <div>
              <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left">{{ $t('admin.username') }}</label>
              <input
                v-model="formData.username"
                type="text"
                class="w-full px-4 py-2 mt-1 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('admin.username_placeholder')"
                required
              />
            </div>

            <!-- First Name -->
            <div>
              <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left">{{ $t('admin.first_name') }}</label>
              <input
                v-model="formData.firstName"
                type="text"
                class="w-full px-4 py-2 mt-1 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('admin.first_name_placeholder')"
                required
              />
            </div>

            <!-- Last Name -->
            <div>
              <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left">{{ $t('admin.last_name') }}</label>
              <input
                v-model="formData.lastName"
                type="text"
                class="w-full px-4 py-2 mt-1 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('admin.last_name_placeholder')"
                required
              />
            </div>

            <!-- Email -->
            <div>
              <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left">{{ $t('admin.email') }}</label>
              <input
                v-model="formData.email"
                type="email"
                class="w-full px-4 py-2 mt-1 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('admin.email_placeholder')"
                required
              />
            </div>

            <!-- Password -->
            <div>
              <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left">{{ $t('admin.password') }}</label>
              <input
                v-model="formData.password"
                type="password"
                class="w-full px-4 py-2 mt-1 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('admin.password_placeholder')"
                required
              />
            </div>

            <!-- Role -->
            <div>
              <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left">{{ $t('admin.role') }}</label>
              <select
                v-model="formData.role"
                class="w-full px-4 py-2 mt-1 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">{{ $t('admin.select_role') }}</option>
                <option value="ADMIN">{{ $t('role.admin') }}</option>
                <option value="AUDITEUR">{{ $t('role.auditor') }}</option>
                <option value="RESPONSABLE">{{ $t('role.responsible') }}</option>
                <option value="LECTEUR">{{ $t('role.reader') }}</option>
              </select>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              :disabled="isLoading"
              class="w-full mt-6 px-4 py-2 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 disabled:opacity-50 font-semibold transition-all"
            >
              {{ isLoading ? $t('common.loading') : $t('admin.create_user_btn') }}
            </button>
          </form>

          <!-- Success Message -->
          <div v-if="successMessage" class="mt-4 bg-green-100 dark:bg-green-500/20 border border-green-200 dark:border-green-500 text-green-800 dark:text-green-200 px-4 py-2 rounded-lg shadow-sm">
            {{ successMessage }}
          </div>

          <!-- Error Message -->
          <div v-if="errorMessage" class="mt-4 bg-red-100 dark:bg-red-500/20 border border-red-200 dark:border-red-500 text-red-800 dark:text-red-200 px-4 py-2 rounded-lg shadow-sm">
            {{ errorMessage }}
          </div>
        </div>
      </div>

      <!-- Users List -->
      <div class="lg:col-span-2">
        <div class="bg-white dark:bg-slate-800/80 backdrop-blur-lg rounded-xl shadow-xl dark:shadow-2xl border border-slate-200 dark:border-slate-700/50 p-6">
          <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">{{ $t('admin.users_list') }}</h2>

          <!-- Loading State -->
          <div v-if="userStore.loading" class="text-center text-slate-600 dark:text-slate-300">
            {{ $t('common.loading') }}...
          </div>

          <!-- Empty State -->
          <div v-else-if="userStore.users.length === 0" class="text-center text-slate-600 dark:text-slate-300 py-8">
            {{ $t('admin.no_users') }}
          </div>

          <!-- Users Table -->
          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-slate-200 dark:border-slate-700">
                  <th class="px-4 py-3 text-left text-sm font-semibold text-slate-700 dark:text-slate-300">{{ $t('admin.username') }}</th>
                  <th class="px-4 py-3 text-left text-sm font-semibold text-slate-700 dark:text-slate-300">{{ $t('admin.email') }}</th>
                  <th class="px-4 py-3 text-left text-sm font-semibold text-slate-700 dark:text-slate-300">{{ $t('admin.role') }}</th>
                  <th class="px-4 py-3 text-left text-sm font-semibold text-slate-700 dark:text-slate-300">{{ $t('admin.status') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in userStore.users" :key="user.id" class="border-b border-slate-200 dark:border-slate-700/50 hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors">
                  <td class="px-4 py-3 text-slate-700 dark:text-slate-200">{{ user.username }}</td>
                  <td class="px-4 py-3 text-slate-700 dark:text-slate-200">{{ user.email }}</td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium"
                      :class="getRoleBadgeClass(user.role)">
                      {{ $t('role.' + user.role.toLowerCase()) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span :class="user.isActive ? 'text-green-600 dark:text-green-400 font-medium' : 'text-red-600 dark:text-red-400 font-medium'">
                      {{ user.isActive ? $t('admin.active') : $t('admin.inactive') }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/userStore';

const router = useRouter();
const userStore = useUserStore();

const formData = ref({
  username: '',
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  role: ''
});

const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const isAdmin = computed(() => {
  return userStore.userRole === 'ADMIN';
});

onMounted(() => {
  if (!isAdmin.value) {
    router.push('/');
  } else {
    userStore.fetchUsers();
  }
});

const submitForm = async () => {
  if (!formData.value.username || !formData.value.email || !formData.value.password || !formData.value.role) {
    errorMessage.value = 'Tous les champs sont requis';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    const newUser = {
      username: formData.value.username,
      firstName: formData.value.firstName,
      lastName: formData.value.lastName,
      email: formData.value.email,
      password: formData.value.password,
      role: formData.value.role
    };

    await userStore.createUser(newUser);
    successMessage.value = 'Utilisateur créé avec succès!';
    formData.value = {
      username: '',
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      role: ''
    };
    await userStore.fetchUsers();
  } catch (error) {
    errorMessage.value = (error as Error).message || 'Erreur de connexion';
  } finally {
    isLoading.value = false;
  }
};

const getRoleBadgeClass = (role: string) => {
  const classes: { [key: string]: string } = {
    'ADMIN': 'bg-red-100 text-red-700 dark:bg-red-500/20 dark:text-red-200',
    'AUDITEUR': 'bg-yellow-100 text-yellow-800 dark:bg-yellow-500/20 dark:text-yellow-200',
    'RESPONSABLE': 'bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-200',
    'LECTEUR': 'bg-slate-200 text-slate-700 dark:bg-slate-500/20 dark:text-slate-200'
  };
  return classes[role] || 'bg-slate-200 text-slate-700 dark:bg-slate-500/20 dark:text-slate-200';
};
</script>
