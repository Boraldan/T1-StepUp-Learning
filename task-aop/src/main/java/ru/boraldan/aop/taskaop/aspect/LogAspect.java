package ru.boraldan.aop.taskaop.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LogManager.getLogger(LogAspect.class);

    @Before("@annotation(ru.boraldan.aop.taskaop.aspect.annotation.LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Started method : %s".formatted(joinPoint.getSignature().getName()));
    }

    @AfterReturning(
            value = "@annotation(ru.boraldan.aop.taskaop.aspect.annotation.LogAfterReturning)",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("AfterReturning method: {}, Result: {}", joinPoint.getSignature().getName(), result != null ? result : "null");
    }

    @AfterThrowing(
            value = "@annotation(ru.boraldan.aop.taskaop.aspect.annotation.LogAfterThrowing)",
            throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in method: {}, Exception: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }

    @Around("@annotation(ru.boraldan.aop.taskaop.aspect.annotation.LogAround)")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info("Around method (before): {}", proceedingJoinPoint.getSignature().getName());
        Long before = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        Long after = System.currentTimeMillis();
        logger.info("Around method (after): {}. Measuring the execution time : {}ms",  proceedingJoinPoint.getSignature().getName(), after - before);
        return result;
    }

}
