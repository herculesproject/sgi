package org.crue.hercules.sgi.framework.spring.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextSupport implements ApplicationContextAware {
  private static ApplicationContext applicationContext;
  private static MessageSourceAccessor messageSourceAccessor;

  /*
   * This method is called from within the ApplicationContext once it is done
   * starting up, it will stick a reference to itself into this bean.
   *
   * @param ac a reference to the ApplicationContext.
   */
  @Override
  public void setApplicationContext(ApplicationContext ac) throws BeansException {
    ApplicationContextSupport.applicationContext = ac;
    ApplicationContextSupport.messageSourceAccessor = new MessageSourceAccessor(applicationContext);
  }

  /**
   * Return the ApplicationContext that this object is associated with.
   * 
   * @throws IllegalStateException if not running in an ApplicationContext
   */
  public static ApplicationContext getApplicationContext() throws IllegalStateException {
    if (applicationContext == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return applicationContext;
  }

  /**
   * Return a MessageSourceAccessor for the application context used by this
   * object, for easy message access.
   * 
   * @throws IllegalStateException if not running in an ApplicationContext
   */
  public static MessageSourceAccessor getMessageSourceAccessor() throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor;
  }

  public static String getMessage(String code) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(code);
  }

  public static String getMessage(String code, Object... args) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(code, args);
  }

  public static String getMessage(Class<?> clazz) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(clazz.getName() + ".message");
  }

  public static String getMessage(Class<?> clazz, Object... args) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(clazz.getName() + ".message", args);
  }

  public static String getMessage(Class<?> clazz, String property) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(clazz.getName() + "." + property + ".message");
  }

  public static String getMessage(Class<?> clazz, String property, Object... args) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(clazz.getName() + "." + property + ".message", args);
  }

  public static String getMessage(MessageSourceResolvable resolvable) throws IllegalStateException {
    if (messageSourceAccessor == null) {
      throw new IllegalStateException("ApplicationContextSupport does not run in an ApplicationContext");
    }
    return messageSourceAccessor.getMessage(resolvable);
  }
}
