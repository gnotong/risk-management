package com.notgabs.corp.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@LogExecution
@Interceptor
public class LogExecutionInterceptor {

    private static final Logger LOG = Logger.getLogger(LogExecutionInterceptor.class);

    @AroundInvoke
    public Object logMethodEntryAndExit(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();
        
        LOG.infof("Entering %s.%s", className, methodName);
        long start = System.currentTimeMillis();
        
        try {
            return context.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            LOG.infof("Exiting %s.%s (Duration: %d ms)", className, methodName, duration);
        }
    }
}
