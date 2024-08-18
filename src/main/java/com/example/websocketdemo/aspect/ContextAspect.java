package com.example.websocketdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@Slf4j
public class ContextAspect {

    @Around("@annotation(com.example.websocketdemo.aspect.annotaion.AttachContext)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            for(Object arg : joinPoint.getArgs()) {
                if(arg instanceof SimpMessageHeaderAccessor) {
                    log.info("user: {}", Objects.requireNonNull(((SimpMessageHeaderAccessor) arg).getUser()).getName());
                }
            }
            log.info("message {}, on thread: {}", joinPoint.hashCode(), Thread.currentThread().getName());
            Object proceed = joinPoint.proceed();
            return proceed;
        } finally {
            log.info("returning {} on thread: {}", joinPoint.hashCode(), Thread.currentThread().getName());
        }
    }
}
