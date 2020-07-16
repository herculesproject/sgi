package org.crue.hercules.sgi.framework.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.crue.hercules.sgi.framework.web.config.SgiSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest
public class SgiMethodSecurityExpressionRootIT {

  @Autowired
  private MockMvc mockMvc;

  @Configuration // A nested @Configuration class wild be used instead of the
                 // application’s primary configuration.
  @Import(SgiSecurityConfig.class)
  public static class TestWebConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable() //
          .authorizeRequests().antMatchers("/error").permitAll() //
          .antMatchers("/**").authenticated() //
          .anyRequest().denyAll() //
          .and() //
          .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
          .authenticationEntryPoint(authenticationEntryPoint) //
          .and() //
          .httpBasic();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper mapper) {
      return new SgiAccessDeniedHandler(mapper);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper mapper) {
      return new SgiAuthenticationEntryPoint(mapper);
    }
  }

  @TestConfiguration // Unlike a nested @Configuration class, which would be used instead of your
                     // application’s primary configuration, a nested @TestConfiguration class is
                     // used in addition to your application’s primary configuration.
  @RestController
  public static class InnerWebConfigTestController {
    @PreAuthorize("hasAuthority('AUTH_UO1')")
    @GetMapping("/test-auth")
    void testAuth() {
    }

    @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
    @GetMapping("/test-auth-for-any-uo")
    void testAuthForAnyUO() {
    }
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO1" })
  public void requestTestAuth_WithAuthForUO1_returnsOk() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth")).andDo(MockMvcResultHandlers.print())
        // then:
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO2" })
  public void requestTestAuth_WithAuthForUO2_returnsUnauthorized() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth")).andDo(MockMvcResultHandlers.print())
        // then:
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO1" })
  public void requestTestAuthForAnyUO_WithAuthForUO1_returnsOk() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth-for-any-uo")).andDo(MockMvcResultHandlers.print())
        // then:
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO2" })
  public void requestTestAuthForAnyUO_WithAuthForUO2_returnsOk() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth-for-any-uo")).andDo(MockMvcResultHandlers.print())
        // then:
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

}