package com.ghx.api.operations.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * @apiNote Aspect class to log method execution time
 * @author Mari Muthu Muthukrishnan
 *
 */

@Aspect
@Component
public class LoggerAspect {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(LoggerAspect.class);

    /**
     * Log execution time for a method annotated with '@LogExecutionTime'
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        final long startTime = System.currentTimeMillis();
        final Object proceed = joinPoint.proceed();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        //log the execution time for the annoatated method
        LOGGER.info("{}::{}() executed in {} ms", className, methodName,  System.currentTimeMillis() - startTime);
        return proceed;
    }
}