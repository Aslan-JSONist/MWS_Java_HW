package com.example.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect responsible for logging method calls
 * within the service layer.
 *
 * This aspect logs the start and completion of
 * each service method invocation.
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger log =
      LoggerFactory.getLogger(LoggingAspect.class);

  /**
   * Logs execution of all methods in the service package.
   */
  @Around("execution(* com.example.todolist.service.*.*(..))")
  public Object log(ProceedingJoinPoint joinPoint) throws Throwable {

    log.info("START {}", joinPoint.getSignature());

    Object result = joinPoint.proceed();

    log.info("END {}", joinPoint.getSignature());

    return result;
  }
}