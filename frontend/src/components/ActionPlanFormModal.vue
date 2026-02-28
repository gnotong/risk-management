<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" @click.self="closeModal">
    <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="closeModal"></div>
    
    <div class="relative w-full max-w-2xl bg-[#0f172a] border border-white/10 rounded-2xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200">
      <div class="p-6 sm:p-8">
        <h3 class="text-2xl font-bold text-white mb-6">{{ $t('action_plans.new_plan') }}</h3>
        
        <form @submit.prevent="submitForm" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-300 mb-2">{{ $t('form.name') }}</label>
            <input 
              v-model="form.nom" 
              type="text" 
              required
              class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-purple-500 outline-none placeholder-gray-500"
              :class="{ 'border-red-500': v$.nom.$error }"
              :placeholder="$t('form.name_placeholder')"
            />
            <span v-if="v$.nom.$error" class="text-red-400 text-xs mt-1 block">{{ $t('form.required') }}</span>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-300 mb-2">{{ $t('form.description') }}</label>
            <textarea 
              v-model="form.description"
              rows="3"
              class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-purple-500 outline-none placeholder-gray-500"
              :placeholder="$t('form.desc_placeholder')"
            ></textarea>
          </div>
          
          <div class="grid grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">{{ $t('action_plan_detail.start_date') }}</label>
              <input 
                v-model="form.dateDebut" 
                type="date" 
                class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-purple-500 outline-none"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">{{ $t('action_plan_detail.end_date') }}</label>
              <input 
                v-model="form.dateFin" 
                type="date" 
                class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-purple-500 outline-none"
                :class="{ 'border-red-500': v$.dateFin.$error || dateError }"
              />
               <span v-if="dateError" class="text-red-400 text-xs mt-1 block">La date de fin doit être après le début.</span>
            </div>
          </div>
          
          <div class="pt-6 border-t border-white/10 flex justify-end gap-4">
            <button type="button" @click="closeModal" class="px-6 py-2.5 rounded-xl text-sm font-medium text-gray-400 hover:text-white hover:bg-white/5 transition-colors">
              {{ $t('form.cancel') }}
            </button>
            <button type="submit" :disabled="loading" class="bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white px-8 py-2.5 rounded-xl text-sm font-bold shadow-[0_0_20px_rgba(147,51,234,0.3)] transition-all flex items-center gap-2">
              <span v-if="loading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
              {{ $t('form.create') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useVuelidate } from '@vuelidate/core';
import { required } from '@vuelidate/validators';

const props = defineProps<{
  isOpen: boolean;
  riskId: string;
}>();

const emit = defineEmits(['close', 'created']);

const loading = ref(false);
const dateError = ref(false);

const form = reactive({
  nom: '',
  description: '',
  dateDebut: '',
  dateFin: ''
});

const rules = {
  nom: { required },
  dateFin: { }
};

const v$ = useVuelidate(rules, form);

const closeModal = () => {
  form.nom = '';
  form.description = '';
  form.dateDebut = '';
  form.dateFin = '';
  v$.value.$reset();
  dateError.value = false;
  emit('close');
};

const submitForm = async () => {
  const isFormCorrect = await v$.value.$validate();
  if (!isFormCorrect) return;

  if (form.dateDebut && form.dateFin) {
    if (new Date(form.dateFin) <= new Date(form.dateDebut)) {
      dateError.value = true;
      return;
    }
  }
  dateError.value = false;

  loading.value = true;
  try {
    const payload = {
      nom: form.nom,
      description: form.description,
      dateDebut: form.dateDebut || null,
      dateFin: form.dateFin || null,
      risque: { id: props.riskId },
      tauxAvancement: 0,
      statut: 'NON_COMMENCE'
    };

    const response = await fetch('/api/planactions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      emit('created');
      closeModal();
    } else {
      console.error("Failed to create action plan");
    }
  } catch (error) {
    console.error("Error creating action plan:", error);
  } finally {
    loading.value = false;
  }
};
</script>
