<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" @click.self="close">
    <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="close"></div>
    <div class="relative w-full max-w-lg bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/10 rounded-2xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200 max-h-[90vh] flex flex-col">
      <div class="px-6 py-5 border-b border-gray-100 dark:border-white/10 flex justify-between items-center bg-gray-50/50 dark:bg-white/5">
        <h3 class="text-xl font-bold text-gray-900 dark:text-white">
          {{ editingUserId ? $t('admin.edit_user') : $t('admin.create_user') }}
        </h3>
        <button @click="close" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors p-1 rounded-lg hover:bg-gray-100 dark:hover:bg-white/10">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="p-6 overflow-y-auto custom-scrollbar">
        <form @submit.prevent="submitForm" class="space-y-4">
          <!-- Username -->
          <div>
            <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left mb-1">{{ $t('admin.username') }} <span class="text-red-500">*</span></label>
            <input v-model="formData.username" type="text" class="w-full px-4 py-2 bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all" :placeholder="$t('admin.username_placeholder')" required />
          </div>

          <!-- First Name -->
          <div>
            <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left mb-1">{{ $t('admin.first_name') }} <span class="text-red-500">*</span></label>
            <input v-model="formData.firstName" type="text" class="w-full px-4 py-2 bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all" :placeholder="$t('admin.first_name_placeholder')" required />
          </div>

          <!-- Last Name -->
          <div>
            <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left mb-1">{{ $t('admin.last_name') }} <span class="text-red-500">*</span></label>
            <input v-model="formData.lastName" type="text" class="w-full px-4 py-2 bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all" :placeholder="$t('admin.last_name_placeholder')" required />
          </div>

          <!-- Email -->
          <div>
            <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left mb-1">{{ $t('admin.email') }} <span class="text-red-500">*</span></label>
            <input v-model="formData.email" type="email" class="w-full px-4 py-2 bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all" :placeholder="$t('admin.email_placeholder')" required />
          </div>

          <!-- Password -->
          <div>
            <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left mb-1">{{ $t('admin.password') }} <span v-if="!editingUserId" class="text-red-500">*</span></label>
            <input v-model="formData.password" type="password" class="w-full px-4 py-2 bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all" :placeholder="editingUserId ? $t('admin.password_placeholder_edit') : $t('admin.password_placeholder')" :required="!editingUserId" />
          </div>

          <!-- Role -->
          <div>
            <label class="text-sm font-medium text-slate-700 dark:text-slate-200 block text-left mb-1">{{ $t('admin.role') }} <span class="text-red-500">*</span></label>
            <select v-model="formData.role" class="w-full px-4 py-2 bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all" required>
              <option value="" disabled>{{ $t('admin.select_role') }}</option>
              <option v-for="roleValue in Object.values(Role)" :key="roleValue" :value="roleValue">
                {{ $t('role.' + roleValue.toLowerCase()) }}
              </option>
            </select>
          </div>

          <!-- Status Toggle (Only on Edit) -->
          <div v-if="editingUserId" class="flex items-center space-x-2 mt-2 py-2">
            <input type="checkbox" v-model="formData.isActive" id="isActiveCheckbox" class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500 bg-white dark:bg-black/40">
            <label for="isActiveCheckbox" class="text-sm font-medium text-gray-700 dark:text-gray-300 cursor-pointer">{{ $t('admin.active') }}</label>
          </div>

          <!-- Messages -->
          <div v-if="errorMessage" class="bg-red-50 dark:bg-red-500/10 text-red-600 dark:text-red-400 p-3 rounded-lg text-sm border border-red-100 dark:border-red-500/20">
            {{ errorMessage }}
          </div>

          <div class="pt-4 flex justify-end gap-3 border-t border-gray-100 dark:border-white/10 mt-6">
            <button type="button" @click="close" class="px-5 py-2.5 rounded-xl text-sm font-semibold text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-white/5 transition-colors">
              {{ $t('admin.cancel') }}
            </button>
            <button type="submit" :disabled="isLoading" class="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500/90 dark:hover:bg-blue-500 text-white px-5 py-2.5 rounded-xl text-sm font-semibold shadow-sm transition-colors flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed">
              <span v-if="isLoading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
              {{ isLoading ? $t('common.loading') : (editingUserId ? $t('admin.update_user_btn') : $t('admin.create_user_btn')) }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useUserStore } from '../stores/userStore';
import { Role } from '../domain/entities/Risk';

const props = defineProps<{
  isOpen: boolean;
  userToEdit?: any;
}>();

const emit = defineEmits(['close', 'saved']);
const userStore = useUserStore();

const editingUserId = ref<string | null>(null);
const isLoading = ref(false);
const errorMessage = ref('');

const formData = ref({
  username: '',
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  role: '' as Role | '',
  isActive: true
});

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    if (props.userToEdit) {
      editingUserId.value = props.userToEdit.id;
      formData.value = {
        username: props.userToEdit.username || '',
        firstName: props.userToEdit.firstName || props.userToEdit.prenom || '',
        lastName: props.userToEdit.lastName || props.userToEdit.nom || '',
        email: props.userToEdit.email || '',
        password: '',
        role: (props.userToEdit.role as Role) || '',
        isActive: props.userToEdit.isActive ?? true
      };
    } else {
      editingUserId.value = null;
      formData.value = {
        username: '',
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        role: '',
        isActive: true
      };
    }
    errorMessage.value = '';
  }
});

const close = () => {
  emit('close');
};

const submitForm = async () => {
  if (!formData.value.username || !formData.value.email || !formData.value.role) {
    errorMessage.value = 'Certains champs requis sont manquants';
    return;
  }
  if (!editingUserId.value && !formData.value.password) {
    errorMessage.value = 'Le mot de passe est requis pour un nouvel utilisateur';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  try {
    const payload: any = {
      username: formData.value.username,
      firstName: formData.value.firstName,
      lastName: formData.value.lastName,
      email: formData.value.email,
      role: formData.value.role
    };

    if (formData.value.password) {
      payload.password = formData.value.password;
    }

    if (editingUserId.value) {
      payload.isActive = formData.value.isActive;
      await userStore.updateUser(editingUserId.value, payload);
    } else {
      await userStore.createUser(payload);
    }
    
    await userStore.fetchUsers();
    emit('saved');
    close();
  } catch (error) {
    errorMessage.value = (error as Error).message || 'Erreur lors de la sauvegarde';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5);
  border-radius: 20px;
}
.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: rgba(71, 85, 105, 0.5);
}
</style>
