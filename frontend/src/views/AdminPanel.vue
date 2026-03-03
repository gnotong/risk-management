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

    <div v-if="isAdmin" class="space-y-6">
      <div class="flex justify-end">
        <button @click="openCreateModal" class="bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white px-5 py-2.5 rounded-xl font-semibold shadow-sm transition-colors flex items-center gap-2">
          <span>+</span>
          {{ $t('admin.create_user_btn') }}
        </button>
      </div>

      <!-- Users List -->
      <div>
        <div class="bg-white dark:bg-slate-800/80 backdrop-blur-lg rounded-xl shadow-xl dark:shadow-2xl border border-slate-200 dark:border-slate-700/50 p-6 flex flex-col h-full">
          <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
            <h2 class="text-2xl font-bold text-slate-900 dark:text-white">{{ $t('admin.users_list') }}</h2>
            <div class="text-sm text-slate-500 dark:text-slate-400 font-medium bg-slate-100 dark:bg-slate-700/50 px-3 py-1 rounded-full border border-slate-200 dark:border-slate-600">
              {{ $t('admin.total_records', { count: filteredUsers.length }) }}
            </div>
          </div>

          <!-- Filters -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <!-- Search -->
            <div>
              <input
                v-model="searchQuery"
                type="text"
                class="w-full px-4 py-2 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                :placeholder="$t('admin.search_placeholder')"
              />
            </div>
            <!-- Role Filter -->
            <div>
              <select
                v-model="selectedRole"
                class="w-full px-4 py-2 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              >
                <option value="">{{ $t('admin.all_roles') }}</option>
                <option value="ADMIN">{{ $t('role.admin') }}</option>
                <option value="AUDITEUR">{{ $t('role.auditeur') }}</option>
                <option value="RESPONSABLE">{{ $t('role.responsable') }}</option>
                <option value="LECTEUR">{{ $t('role.lecteur') }}</option>
              </select>
            </div>
            <!-- Status Filter -->
            <div>
              <select
                v-model="selectedStatus"
                class="w-full px-4 py-2 bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              >
                <option value="">{{ $t('admin.all_statuses') }}</option>
                <option value="active">{{ $t('admin.active') }}</option>
                <option value="inactive">{{ $t('admin.inactive') }}</option>
              </select>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="userStore.loading" class="text-center text-slate-600 dark:text-slate-300">
            {{ $t('common.loading') }}...
          </div>

          <!-- Empty State -->
          <div v-else-if="paginatedUsers.length === 0" class="text-center text-slate-600 dark:text-slate-300 py-8 border border-dashed border-slate-300 dark:border-slate-600 rounded-lg">
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
                  <th class="px-4 py-3 text-right text-sm font-semibold text-slate-700 dark:text-slate-300">{{ $t('admin.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in paginatedUsers" :key="user.id" class="border-b border-slate-200 dark:border-slate-700/50 hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors">
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
                  <td class="px-4 py-3 text-right space-x-3">
                    <button @click="editUser(user)" class="text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300 font-medium text-sm transition-colors" :title="$t('admin.edit_user')">
                      🖍️
                    </button>
                    <button @click="deleteUserLocal(user)" class="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 font-medium text-sm transition-colors" :title="$t('admin.delete_user')">
                      🗑️
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination Controls -->
          <div v-if="totalPages > 1 || paginatedUsers.length > 0" class="mt-auto pt-6 flex flex-col sm:flex-row items-center justify-between gap-4 border-t border-slate-200 dark:border-slate-700 mt-6">
            <div class="flex items-center gap-4">
              <div class="text-sm text-slate-600 dark:text-slate-400">
                {{ $t('admin.page_info', { current: currentPage, total: totalPages }) }}
              </div>
              <div class="flex items-center gap-2">
                <span class="text-sm text-slate-600 dark:text-slate-400">{{ $t('admin.items_per_page') }}:</span>
                <select 
                  v-model="itemsPerPage" 
                  class="bg-white dark:bg-slate-700/50 border border-slate-300 dark:border-slate-600 rounded text-slate-700 dark:text-slate-200 text-sm focus:outline-none focus:ring-1 focus:ring-blue-500 py-1"
                >
                  <option :value="5">5</option>
                  <option :value="10">10</option>
                  <option :value="20">20</option>
                  <option :value="50">50</option>
                  <option :value="100">100</option>
                </select>
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <button 
                @click="firstPage" 
                :disabled="currentPage === 1"
                class="px-3 py-1 text-sm bg-white dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                :title="$t('admin.first')"
              >
                &laquo;
              </button>
              <button 
                @click="prevPage" 
                :disabled="currentPage === 1"
                class="px-3 py-1 text-sm bg-white dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {{ $t('admin.prev') }}
              </button>
              <button 
                @click="nextPage" 
                :disabled="currentPage === totalPages"
                class="px-3 py-1 text-sm bg-white dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {{ $t('admin.next') }}
              </button>
              <button 
                @click="lastPage" 
                :disabled="currentPage === totalPages"
                class="px-3 py-1 text-sm bg-white dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                :title="$t('admin.last')"
              >
                &raquo;
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Toast Messages (Optional, if we want to show generic success) -->
      <div v-if="successMessage" class="fixed bottom-4 right-4 bg-green-100 dark:bg-green-500/20 border border-green-200 dark:border-green-500 text-green-800 dark:text-green-200 px-4 py-3 rounded-lg shadow-lg z-50 flex items-center space-x-2 animate-in slide-in-from-bottom-5">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
        <span>{{ successMessage }}</span>
      </div>
      <div v-if="errorMessage" class="fixed bottom-4 right-4 bg-red-100 dark:bg-red-500/20 border border-red-200 dark:border-red-500 text-red-800 dark:text-red-200 px-4 py-3 rounded-lg shadow-lg z-50 flex items-center space-x-2 animate-in slide-in-from-bottom-5">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        <span>{{ errorMessage }}</span>
      </div>

      <UserFormModal 
        :is-open="isUserModalOpen" 
        :user-to-edit="userToEdit" 
        @close="closeUserModal" 
        @saved="onUserSaved" 
      />

      <ConfirmationModal
        :is-open="isDeleteModalOpen"
        :title="$t('admin.delete_user')"
        :message="$t('admin.confirm_delete')"
        :loading="isDeleting"
        @confirm="confirmDeleteUser"
        @cancel="closeDeleteModal"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/userStore';

import UserFormModal from '../components/UserFormModal.vue';
import ConfirmationModal from '../components/ConfirmationModal.vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const router = useRouter();
const userStore = useUserStore();

// Modals State
const isUserModalOpen = ref(false);
const userToEdit = ref<any>(null);
const isDeleteModalOpen = ref(false);
const userToDelete = ref<any>(null);
const isDeleting = ref(false);

const errorMessage = ref('');
const successMessage = ref('');

let timeout: number;

const showSuccess = (msg: string) => {
  successMessage.value = msg;
  clearTimeout(timeout);
  timeout = window.setTimeout(() => successMessage.value = '', 4000);
};

const showError = (msg: string) => {
  errorMessage.value = msg;
  clearTimeout(timeout);
  timeout = window.setTimeout(() => errorMessage.value = '', 4000);
};

// List Filters & Pagination state
const searchQuery = ref('');
const selectedRole = ref('');
const selectedStatus = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(10); // Dynamic items per page

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

// Computed properties for filtering and pagination
const filteredUsers = computed(() => {
  let result = userStore.users;

  // Search by username or email
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(u => 
      u.username?.toLowerCase().includes(query) || 
      u.email?.toLowerCase().includes(query)
    );
  }

  // Filter by role
  if (selectedRole.value) {
    result = result.filter(u => u.role === selectedRole.value);
  }

  // Filter by status
  if (selectedStatus.value !== '') {
    const isActiveTarget = selectedStatus.value === 'active';
    result = result.filter(u => u.isActive === isActiveTarget);
  }

  return result;
});

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredUsers.value.length / itemsPerPage.value));
});

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredUsers.value.slice(start, end);
});

// Reset to first page when filtering conditions change
watch([searchQuery, selectedRole, selectedStatus, itemsPerPage], () => {
  currentPage.value = 1;
});

// Pagination Navigation Methods
const firstPage = () => { currentPage.value = 1; };
const prevPage = () => { if (currentPage.value > 1) currentPage.value--; };
const nextPage = () => { if (currentPage.value < totalPages.value) currentPage.value++; };
const lastPage = () => { currentPage.value = totalPages.value; };

const openCreateModal = () => {
  userToEdit.value = null;
  isUserModalOpen.value = true;
};

const editUser = (user: any) => {
  userToEdit.value = user;
  isUserModalOpen.value = true;
};

const closeUserModal = () => {
  isUserModalOpen.value = false;
  userToEdit.value = null;
};

const onUserSaved = () => {
  showSuccess(userToEdit.value ? t('admin.update_success') : 'Utilisateur créé avec succès!');
};

const deleteUserLocal = (user: any) => {
  userToDelete.value = user;
  isDeleteModalOpen.value = true;
};

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false;
  userToDelete.value = null;
  isDeleting.value = false;
};

const confirmDeleteUser = async () => {
  if (!userToDelete.value) return;
  
  isDeleting.value = true;
  try {
    await userStore.deleteUser(userToDelete.value.id);
    showSuccess(t('admin.delete_success'));
    await userStore.fetchUsers();
    closeDeleteModal();
  } catch (error) {
    showError((error as Error).message || 'Erreur lors de la suppression');
    isDeleting.value = false;
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
