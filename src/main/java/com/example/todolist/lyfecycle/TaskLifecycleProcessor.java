package com.example.todolist.lifecycle;

import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * BeanPostProcessor that logs the lifecycle of
 * TaskService and TaskRepository beans.
 *
 * This class demonstrates how Spring creates
 * and initializes beans by printing messages
 * before and after initialization.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

  /**
   * Executes before bean initialization.
   *
   * @param bean     created bean instance
   * @param beanName  name of the bean
   * @return the same bean instance
   * @throws BeansException if an error occurs
   */
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {

    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      System.out.println("Before init: " + beanName);
    }

    return bean;
  }

  /**
   * Executes after bean initialization.
   *
   * @param bean     initialized bean instance
   * @param beanName  name of the bean
   * @return the same bean instance
   * @throws BeansException if an error occurs
   */
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {

    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      System.out.println("After init: " + beanName);
    }

    return bean;
  }
}