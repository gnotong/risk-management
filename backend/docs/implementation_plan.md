# Implementation Plan: Risk Management Application

This document outlines the proposed architecture and implementation steps for the GRC (Governance, Risk & Compliance) application based on `antigravity.json` and `specs_risk.md`.

## User Review Required
Before proceeding to execution, please review the newly added business rules:
> [!IMPORTANT]
> - **[NEW] SuiviPlanAction Entity**: We will generate a new database entity to track history of action plans.
> - **[NEW] Form Validations**: Adding robust validation for the frontend forms.
> - **[NEW] Real Exports**: Using actual libraries (`jspdf`, `xlsx`) instead of simulated alerts in the frontend.
> - **[NEW] Backend constraints**: Adding deletion preventions and business state validations (e.g. Audit closure checks) prior to persistence.
> - **[NEW] Action Plan Tracking**: Enhancing `PlanAction` with interactive UI, Status (NON COMMENCÉ, EN COURS, TERMINÉ), progress slider, history journal, and restricted access to progress updates.

## Proposed Changes

### Monorepo Structure
The codebase will be divided into:
- `/backend`: Quarkus Java 21 Application
- `/frontend`: Vue 3 + TypeScript + Tailwind Application
- `docker-compose.yml`: Root orchestrator including PostgreSQL and Keycloak for local dev.

---

### Backend Components
We will use Maven for Quarkus building.

#### Entities
- `Utilisateur`: Manages user data.
- `Risque`: Risk entity with auto-calculated score.
- `Audit`: Audit records.
- `Recommandation`: Recommendations linked to Audits.
- `PlanAction`: Action plans with `%` progress, `StatutPlanAction`, and eager list of `SuiviPlanAction`.
- `Incident`: Incidents linked to risks.
- `SuiviPlanAction`: Entity representing a historical log entry. It ignores JSON recursive mapping to `PlanAction`.

#### Services / Interceptors
- **Audit Logging**: Hibernate interceptor for change tracking.
- **Mailer Service**: Using `quarkus-mailer` to send alerts for "Very High" risk or delayed action plans.
- **Security**: Keycloak OIDC integration.
- **REST APIs**: Controllers to serve JSON for the frontend. Includes specific `/api/planactions/{id}/suivis` endpoints.

#### [NEW] Business Integrity Engine
- Prevent `Risque` deletion if linked to Incident, Audit, or PlanAction.
- Prevent `Utilisateur` deletion if they own risks or plans.
- Validate `PlanAction.dateFin` > `PlanAction.dateDebut`.
- Validate `Audit` closure: Cannot be `TERMINÉ` if any recommendation is not treated.
- Automatically create `SuiviPlanAction` on `PlanAction` update and dedicated comments.
- Prevent `PlanAction` closure (`TERMINÉ`) if `tauxAvancement < 100`.
- Control Access: Only ADMIN or `responsable_id` can modify progress.

---

### Frontend Components
Using `npm` (Vite, TS, Pinia, Tailwind V4).

#### Directories
- `/src/components`: Reusable UI elements (`Heatmap.vue`, `FilterBar.vue`).
- `/src/views`: Main pages (`Dashboard.vue`, `RiskList.vue`, `ActionPlanDetail.vue`).
- `/src/stores`: Pinia stores for risks, auth, action plans.

#### [NEW] Validation & Exports
- Implement reactive form validation on inputs.
- Integrate `jspdf` and `xlsx` (sheetjs) for real PDF and Excel exports.
- Implement Action Plan interactive view with Tailwind Progress bar, Journal insertion, and Date editors.
- Smart Navigation in `ActionPlanDetail.vue` using Vue router history logic to return intelligently.
- Dashboard Risques enhanced with Action Plan average completion scores and CSS Tooltips containing tracking journals.
- **[NEW] Heatmap UX Overhaul**: Redesign `RiskHeatmap.vue` to be an explicit visual filter. Include active state highlights, explicit tooltips explaining the action, and a 'Clear Filter' button.

#### [NEW] Internationalisation (i18n)
- Install and configure `vue-i18n@9`
- Create `src/locales/fr.json` and `src/locales/en.json` dictionaries to store static strings and dynamic database statuses (e.g `status.OUVERT`).
- Update the main layout to include a Language Switcher toggle (Fr / En).
- Wrap all static string templating across components (Dashboard, Filters, Action Plans, Heatmaps, Statuses) inside the `$t('...')` translation helper.

---

### Docker
- **Backend Dockerfile**: `backend/Dockerfile` as uber-jar.
- **Frontend Dockerfile**: `frontend/Dockerfile` using Nginx Reverse Proxy.
- **docker-compose.yml**: At the root of the project to orchestrate PostgreSQL, Keycloak, Backend, and Frontend.

## Verification Plan

### Automated Tests
- Validating business constraints using REST tools.

### Manual Verification
- Testing the Frontend UI (Exports, Form validation).
- Verifying the Nginx reverse proxy end-to-end interactions via Docker Compose.

### Automated Tests
- Validating business constraints using REST tools.

### Manual Verification
- Deploy containers using `docker compose up -d --build`.
- Verify UI and validations manually through the browser interface.
- Complete an Action Plan to 100% and watch the parent Risk switch to `CLOTURE`.

## Plan for Mandatory Risk Owners
### Proposed Changes
1. **Quarkus `RisqueResource.java`**:
   - `create()` / `update()`: Add validation `if (risque.proprietaire == null) throw ...`
   - `update()`: Add validation `if(entity.statut == StatutRisque.CLOTURE) throw ...` to prevent updating closed risks.
2. **Vue `RiskFormModal.vue`**:
   - Add Vuelidate rule `proprietaire: { required }`.
   - Update select option to force a choice.
3. **Vue `RiskDetail.vue`**:
   - Fetch users (`/api/utilisateurs`).
   - Show an inline `<select>` for the owner if `risque.statut !== 'CLOTURE'`. When changed, dispatch PUT request to update the risk's owner via `riskStore` or direct API call.

### Verification Plan
- Create a risk with an empty owner (should block).
- Update an existing risk's owner from details page (should save).
- Test updating an owner when the risk is `CLOTURE` (should be disabled).
