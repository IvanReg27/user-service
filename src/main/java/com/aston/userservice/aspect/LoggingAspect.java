package com.aston.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Логирование всех методов контроллеров.
     */
    @Pointcut("execution(* com.aston.userservice.controller.impl..*(..))")
    public void controllerMethods() {
    }

    /**
     * Логирование всех методов сервисов.
     */
    @Pointcut("execution(* com.aston.userservice.service.impl..*(..))")
    public void serviceMethods() {
    }

    /**
     * Логирование начала работы метода на уровне INFO.
     */
    @Before("controllerMethods() || serviceMethods()")
    public void logMethodStart(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Начало работы метода: {}", methodName);
    }

    /**
     * Логирование параметров, возвращаемых значений и исключений на уровне DEBUG и ERROR.
     */
    @Around("controllerMethods() || serviceMethods()")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.debug("Метод {} вызван с параметрами: {}", methodName, args != null ? Arrays.toString(args) : "null");
        try {
            Object result = joinPoint.proceed();
            log.debug("Метод {} завершен, возвращаемое значение: {}", methodName, result);
            return result;
        } catch (Exception e) {
            log.error("Ошибка в методе {}: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}
