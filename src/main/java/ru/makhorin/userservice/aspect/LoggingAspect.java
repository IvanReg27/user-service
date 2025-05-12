package ru.makhorin.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Логирование методов помеченых аннотацией @Loggable.
     */
    @Pointcut("@annotation(ru.makhorin.userservice.annotation.Loggable)")
    public void loggableMethods() {
    }

    /**
     * Логирование начала работы метода на уровне (INFO).
     * Логирование параметров, возвращаемых значений и исключений на уровне (DEBUG) и (ERROR).
     */
    @Around("loggableMethods()")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Начало работы метода: {}", methodName);

        log.debug("Метод {} вызван с параметрами: {}", methodName, args != null ? Arrays.toString(args) : "null");
        try {
            Object result = joinPoint.proceed();
            if (result != null) {
                log.debug("Метод {} завершен, возвращаемое значение: {}", methodName, result);
            } else {
                log.debug("Метод {} завершен, возвращаемое значение: void", methodName);
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка в методе {}: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}
