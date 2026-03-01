package com.notgabs.corp.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@ValidateBusinessRules
@Interceptor
public class ValidateBusinessRulesInterceptor {
    @AroundInvoke
    public Object validate(InvocationContext context) throws Exception {
        return context.proceed();
    }
}
