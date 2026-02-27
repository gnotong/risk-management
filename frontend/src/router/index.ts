import { createRouter, createWebHistory } from 'vue-router';
import RiskDashboard from '../views/RiskDashboard.vue';
import ActionPlanTracker from '../views/ActionPlanTracker.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'risks',
            component: RiskDashboard
        },
        {
            path: '/action-plans',
            name: 'action-plans',
            component: ActionPlanTracker
        }
    ]
});

export default router;
