package ru.boraldan.logaopstarter.starter.aspect;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;


@Aspect
public class LogAspect {

    private final Logger log;
    private final Level level;

    public LogAspect(Level level) {
        this.log = LogManager.getLogger(LogAspect.class);
        this.level = level;
    }

    @Before("@annotation(ru.boraldan.logaopstarter.starter.aspect.annotation.LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        log.log(level, "Started method : {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(
            value = "@annotation(ru.boraldan.logaopstarter.starter.aspect.annotation.LogAfterReturning)",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.log(level, "AfterReturning method: {}, Result: {}", joinPoint.getSignature().getName(), result != null ? result : "null");
    }

    @AfterThrowing(
            pointcut = "execution(* *(..)) && (@annotation(ru.boraldan.logaopstarter.starter.aspect.annotation.LogAfterThrowing) || @within(ru.boraldan.logaopstarter.starter.aspect.annotation.LogAfterThrowing))",
            throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception in method: {}, Exception: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }

    @Around("@annotation(ru.boraldan.logaopstarter.starter.aspect.annotation.LogAround)")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.log(level, "Around method (before): {}", proceedingJoinPoint.getSignature().getName());
        Object result;
        Long before = System.currentTimeMillis();
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Exception in method:  {}, Exception: {}", proceedingJoinPoint.getSignature().getName(), ex.getMessage());
            throw ex;
        }
        Long after = System.currentTimeMillis();
        log.log(level, "Around method (after): {}. Measuring the execution time : {}ms", proceedingJoinPoint.getSignature().getName(), after - before);
        return result;
    }

}
