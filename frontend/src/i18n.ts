import { createI18n } from 'vue-i18n';
import fr from './locales/fr.json';
import en from './locales/en.json';

type MessageSchema = typeof fr;

export const i18n = createI18n<{
    message: MessageSchema;
}, 'fr' | 'en'>({
    legacy: false, // Requis pour Composition API
    locale: 'fr',
    fallbackLocale: 'en',
    messages: {
        fr,
        en
    }
});