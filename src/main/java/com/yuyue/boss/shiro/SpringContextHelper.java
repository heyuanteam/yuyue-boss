package com.yuyue.boss.shiro;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Create by lujun.chen on 2018/09/30
 */
@Component
public class SpringContextHelper implements ApplicationContextAware {

  private static ApplicationContext context = null;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }

  public static Object getBean(String name) {
    return context.getBean(name);
  }

  public static Object getBean(Class<?> c) {
    return context.getBean(c);
  }

}
