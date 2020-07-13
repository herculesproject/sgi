package org.crue.hercules.sgi.framework.web.config;

import org.crue.hercules.sgi.framework.security.access.expression.SgiMethodSecurityExpressionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SgiSecurityConfig extends GlobalMethodSecurityConfiguration {
  private DefaultMethodSecurityExpressionHandler defaultMethodExpressionHandler = new SgiMethodSecurityExpressionHandler();

  /**
   * Provide a {@link MethodSecurityExpressionHandler} that is registered with the
   * {@link ExpressionBasedPreInvocationAdvice}. The default is
   * {@link DefaultMethodSecurityExpressionHandler} which optionally will Autowire
   * an {@link AuthenticationTrustResolver}.
   *
   * <p>
   * Subclasses may override this method to provide a custom
   * {@link MethodSecurityExpressionHandler}
   * </p>
   *
   * @return the {@link MethodSecurityExpressionHandler} to use
   */
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    return defaultMethodExpressionHandler;
  }
}