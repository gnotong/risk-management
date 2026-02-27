package com.notgabs.corp.aop;

import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class AuditLogInterceptor implements Interceptor {

    private static final Logger LOGGER = Logger.getLogger(AuditLogInterceptor.class);

    @Override
    public boolean onSave(
            Object entity,
            Object id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        LOGGER.infof("Action: CREATE | Entity: %s | Id: %s", entity.getClass().getSimpleName(), id);
        return false;
    }

    @Override
    public boolean onFlushDirty(
            Object entity,
            Object id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        LOGGER.infof("Action: UPDATE | Entity: %s | Id: %s", entity.getClass().getSimpleName(), id);
        return false;
    }

    @Override
    public void onDelete(
            Object entity,
            Object id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        LOGGER.infof("Action: DELETE | Entity: %s | Id: %s", entity.getClass().getSimpleName(), id);
    }
}
