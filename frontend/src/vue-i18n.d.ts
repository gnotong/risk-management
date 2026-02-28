// Forcer la reconnaissance des exports nommés pour vue-i18n
declare module 'vue-i18n' {
    export * from 'vue-i18n/dist/vue-i18n.d.ts';
    import { createI18n, useI18n } from 'vue-i18n/dist/vue-i18n.d.ts';
    export { createI18n, useI18n };
}