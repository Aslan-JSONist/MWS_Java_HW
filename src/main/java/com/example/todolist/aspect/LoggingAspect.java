package com.example.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Logging aspect for service layer.
 *
 * This aspect intercepts all method calls inside
 * the service package and logs when a method starts
 * and when it finishes execution.
 *
 * It uses @Around advice to wrap method execution.
 */
@Aspect
@Component
public class LoggingAspect {

  /**
   * Logs method execution start and end for all service methods.
   *
   * @param joinPoint provides access to the intercepted method
   * @return result of the executed method
   * @throws Throwable if the intercepted method throws an exception
   */
  @Around("execution(* com.example.todolist.service.*.*(..))")
  public Object log(ProceedingJoinPoint joinPoint) throws Throwable {

    System.out.println("START: " + joinPoint.getSignature());

    Object result = joinPoint.proceed();

    System.out.println("END: " + joinPoint.getSignature());

    return result;
  }
}