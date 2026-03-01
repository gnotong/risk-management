<template>
  <div v-if="isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6" @click.self="cancel">
    <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="cancel"></div>
    
    <div class="relative w-full max-w-md bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/10 rounded-2xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200">
      <div class="p-6 sm:p-8">
        
        <div class="flex items-center gap-4 mb-4">
          <div :class="iconBgClass" class="w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0">
             <span class="text-2xl">{{ icon }}</span>
          </div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-white">{{ title }}</h3>
        </div>

        <div class="text-gray-600 dark:text-gray-300 mb-8 space-y-3 whitespace-pre-wrap">
          {{ message }}
        </div>
        
        <div class="flex justify-end gap-3">
          <button 
            type="button" 
            @click="cancel" 
            class="px-5 py-2.5 rounded-xl text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-white/5 transition-colors focus:ring-2 focus:ring-gray-300 outline-none"
          >
            {{ cancelText }}
          </button>
          
          <button 
            type="button" 
            @click="confirm" 
            :disabled="loading" 
            :class="confirmButtonClass"
            class="px-5 py-2.5 rounded-xl text-sm font-bold text-white shadow-sm transition-colors flex items-center gap-2 focus:ring-2 focus:ring-offset-2 dark:focus:ring-offset-[#0f172a] outline-none disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="loading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  isOpen: boolean;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'info';
  loading?: boolean;
}>(), {
  confirmText: 'Confirmer',
  cancelText: 'Annuler',
  type: 'danger',
  loading: false
});

const emit = defineEmits(['confirm', 'cancel']);

const confirm = () => {
  if (!props.loading) emit('confirm');
};

const cancel = () => {
  if (!props.loading) emit('cancel');
};

const icon = computed(() => {
  if (props.type === 'danger') return '🗑️';
  if (props.type === 'warning') return '⚠️';
  return 'ℹ️';
});

const iconBgClass = computed(() => {
  if (props.type === 'danger') return 'bg-red-100 dark:bg-red-500/20 text-red-600 dark:text-red-400';
  if (props.type === 'warning') return 'bg-amber-100 dark:bg-amber-500/20 text-amber-600 dark:text-amber-400';
  return 'bg-blue-100 dark:bg-blue-500/20 text-blue-600 dark:text-blue-400';
});

const confirmButtonClass = computed(() => {
  if (props.type === 'danger') return 'bg-red-600 hover:bg-red-700 dark:bg-red-500/90 dark:hover:bg-red-500 focus:ring-red-500';
  if (props.type === 'warning') return 'bg-amber-500 hover:bg-amber-600 focus:ring-amber-500 text-white';
  return 'bg-blue-600 hover:bg-blue-700 dark:bg-blue-500/90 dark:hover:bg-blue-500 focus:ring-blue-500';
});
</script>
