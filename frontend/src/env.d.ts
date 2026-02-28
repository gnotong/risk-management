declare module 'vue-i18n' {
    import { DefineComponent } from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
    export * from 'vue-i18n/dist/vue-i18n'
}