import { createRouter, createWebHistory } from 'vue-router';
import RiskDashboard from '../views/RiskDashboard.vue';
import ActionPlanTracker from '../views/ActionPlanTracker.vue';
import ActionPlanDetail from '../views/ActionPlanDetail.vue';
import IncidentTracker from '../views/IncidentTracker.vue';
import IncidentDetail from '../views/IncidentDetail.vue';
import RiskDetail from '../views/RiskDetail.vue';
import AdminPanel from '../views/AdminPanel.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'risks',
            component: RiskDashboard
        },
        {
            path: '/risques/:id',
            name: 'risk-detail',
            component: RiskDetail
        },
        {
            path: '/incidents',
            name: 'incidents',
            component: IncidentTracker
        },
        {
            path: '/incidents/:id',
            name: 'incident-detail',
            component: IncidentDetail
        },
        {
            path: '/action-plans',
            name: 'action-plans',
            component: ActionPlanTracker
        },
        {
            path: '/action-plans/:id',
            name: 'action-plan-detail',
            component: ActionPlanDetail
        },
        {
            path: '/admin',
            name: 'admin',
            component: AdminPanel
        }
    ]
});

export default router;
