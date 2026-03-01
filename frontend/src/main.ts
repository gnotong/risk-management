import './index.css'

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import router from './router';
import { i18n } from './i18n';
import App from './App.vue';
import keycloak from './plugins/keycloak';

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(i18n);

app.config.globalProperties.$keycloak = keycloak;

const enableKeycloak = import.meta.env.VITE_ENABLE_KEYCLOAK !== 'false';

if (enableKeycloak) {
    keycloak.init({ onLoad: 'login-required', checkLoginIframe: false }).then((authenticated) => {
        if (authenticated) {
            app.mount('#app');

            // Auto-refresh token if needed
            setInterval(() => {
                keycloak.updateToken(70).catch(() => {
                    console.error('Failed to refresh token');
                    keycloak.logout();
                });
            }, 60000);
        } else {
            window.location.reload();
        }
    }).catch(() => {
        console.error("Failed to initialize Keycloak");
        document.body.innerHTML = '<div style="color:red; font-family:sans-serif; text-align:center; margin-top:50px;"><h2>Erreur Authentification</h2><p>Impossible de joindre le serveur Keycloak.</p></div>';
    });
} else {
    console.warn("Keycloak authentication is locally DISABLED via configuration.");
    app.mount('#app');
}
