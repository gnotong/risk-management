package com.notgabs.corp.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@CheckAccess
@Interceptor
public class CheckAccessInterceptor {
    @AroundInvoke
    public Object checkAccess(InvocationContext context) throws Exception {
        return context.proceed();
    }
}
