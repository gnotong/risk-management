<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm animate-in fade-in">
    <div class="glass-card w-full max-w-lg p-6 lg:p-8 animate-in zoom-in-95 duration-300">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-bold text-gray-900 dark:text-white">Nouveau Risque</h3>
        <button @click="close" class="text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <form @submit.prevent="submitForm" class="space-y-5">
        
        <div>
          <label class="block text-sm text-left font-medium text-gray-700 dark:text-gray-300 mb-1">Libellé <span class="text-red-500">*</span></label>
          <input 
            v-model="form.libelle" 
            @blur="v$.libelle.$touch()"
            :class="{'border-red-500 focus:ring-red-500 bg-red-50 dark:bg-red-500/10': v$.libelle.$error}"
            type="text" 
            class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none" 
            placeholder="Titre du risque"
          />
          <div v-if="v$.libelle.$error" class="text-red-500 dark:text-red-400 text-xs mt-1 text-left">Le libellé est requis (min 3 caractères).</div>
        </div>

        <div>
          <label class="block text-sm text-left font-medium text-gray-700 dark:text-gray-300 mb-1">Description</label>
          <textarea 
            v-model="form.description" 
            rows="3"
            class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none" 
            placeholder="Détails du risque..."
          ></textarea>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm text-left font-medium text-gray-700 dark:text-gray-300 mb-1">Probabilité (1-3) <span class="text-red-500">*</span></label>
            <input 
              v-model.number="form.probabilite" 
              @blur="v$.probabilite.$touch()"
              :class="{'border-red-500 focus:ring-red-500 bg-red-50 dark:bg-red-500/10': v$.probabilite.$error}"
              type="number" 
              min="1" max="3"
              class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none" 
            />
            <div v-if="v$.probabilite.$error" class="text-red-500 dark:text-red-400 text-xs mt-1 text-left">Valeur entre 1 et 3.</div>
          </div>
          <div>
            <label class="block text-sm text-left font-medium text-gray-700 dark:text-gray-300 mb-1">Gravité (1-3) <span class="text-red-500">*</span></label>
            <input 
              v-model.number="form.gravite" 
              @blur="v$.gravite.$touch()"
              :class="{'border-red-500 focus:ring-red-500 bg-red-50 dark:bg-red-500/10': v$.gravite.$error}"
              type="number" 
              min="1" max="3"
              class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none" 
            />
            <div v-if="v$.gravite.$error" class="text-red-500 dark:text-red-400 text-xs mt-1 text-left">Valeur entre 1 et 3.</div>
          </div>
        </div>

        <div>
          <label class="block text-sm text-left font-medium text-gray-700 dark:text-gray-300 mb-1">{{ $t('risk_detail.owner') }} <span class="text-red-500">*</span></label>
          <select 
            v-model="form.proprietaire"
            @blur="v$.proprietaire.$touch()"
            :class="{'border-red-500 focus:ring-red-500 bg-red-50 dark:bg-red-500/10': v$.proprietaire.$error}"
            class="w-full bg-white dark:bg-black/40 border border-gray-300 dark:border-white/10 rounded-xl px-4 py-2.5 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none" 
          >
            <option :value="null" disabled>-- {{ $t('dashboard.unassigned') }} --</option>
            <option v-for="user in users" :key="user.id" :value="{ id: user.id }">{{ user.nom }} ({{ user.role }})</option>
          </select>
          <div v-if="v$.proprietaire.$error" class="text-red-500 dark:text-red-400 text-xs mt-1 text-left">{{ $t('form.required') }}</div>
        </div>

        <div class="pt-4 flex justify-end gap-3">
          <button type="button" @click="close" class="px-5 py-2.5 rounded-xl text-sm font-semibold text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-white/5 hover:text-gray-900 dark:hover:text-white transition-all cursor-pointer">
            Annuler
          </button>
          <button type="submit" :disabled="loading" class="bg-blue-600 hover:bg-blue-700 dark:bg-blue-600 dark:hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed text-white px-5 py-2.5 rounded-xl text-sm font-semibold shadow-sm transition-colors cursor-pointer flex items-center gap-2">
            <span v-if="loading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
            {{ loading ? 'Enregistrement...' : 'Enregistrer le risque' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useVuelidate } from '@vuelidate/core';
import { required, minLength, minValue, maxValue } from '@vuelidate/validators';
import { useRiskStore } from '../stores/riskStore';

const props = defineProps<{
  isOpen: boolean
}>();

const emit = defineEmits(['close']);
const store = useRiskStore();

const loading = ref(false);
const users = ref<any[]>([]);

onMounted(async () => {
  try {
    const response = await fetch('/api/utilisateurs');
    if (response.ok) {
      users.value = await response.json();
    }
  } catch (error) {
    console.error("Failed to load users", error);
  }
});

const form = reactive({
  libelle: '',
  description: '',
  probabilite: 1,
  gravite: 1,
  statut: 'OUVERT',
  proprietaire: null as any
});

const rules = {
  libelle: { required, minLength: minLength(3) },
  probabilite: { required, minValue: minValue(1), maxValue: maxValue(3) },
  gravite: { required, minValue: minValue(1), maxValue: maxValue(3) },
  proprietaire: { required }
};

const v$ = useVuelidate(rules, form);

const close = () => {
  v$.value.$reset();
  form.libelle = '';
  form.description = '';
  form.probabilite = 1;
  form.gravite = 1;
  form.proprietaire = null;
  emit('close');
};

const submitForm = async () => {
  const isFormCorrect = await v$.value.$validate();
  if (!isFormCorrect) return;

  loading.value = true;
  try {
    const response = await fetch('/api/risques', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(form)
    });

    if (!response.ok) throw new Error("Erreur de sauvegarde");

    await store.fetchRisques(); // Refresh list
    close();
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};
</script>
