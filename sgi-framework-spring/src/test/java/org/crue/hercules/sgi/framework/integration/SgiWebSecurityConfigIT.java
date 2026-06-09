package org.crue.hercules.sgi.framework.integration;

import javax.servlet.http.Cookie;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.integration.SgiWebSecurityConfigIT.SecurityConfigTest;
import org.crue.hercules.sgi.framework.web.config.SgiWebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verifies that the CSRF (XSRF-TOKEN) cookie is emitted with the default
 * {@code /} path, so it is not scoped to a service specific servlet
 * context-path (which would lead to duplicated cookies and spurious 403).
 */
@WebMvcTest(SecurityConfigTest.class)
class SgiWebSecurityConfigIT {

  @Autowired
  private MockMvc mockMvc;

  // Required by SgiWebSecurityConfig to configure the oauth2 resource server.
  // Never invoked by these tests.
  @MockBean
  private JwtDecoder jwtDecoder;

  @Test
  void csrfCookie_isEmittedWithDefaultRootPath() throws Exception {
    // when: a request hits a public (permitAll) endpoint, the CsrfFilter writes
    // the XSRF-TOKEN cookie
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/public/test"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    // then: the cookie is scoped to the default root path "/"
    Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
    Assertions.assertThat(cookie).isNotNull();
    Assertions.assertThat(cookie.getPath()).isEqualTo("/");
  }

  @Configuration
  public static class SecurityConfigTest extends SgiWebSecurityConfig {
    @RestController
    public static class InnerSecurityConfigTestController {
      @GetMapping("/public/test")
      String test() {
        return "ok";
      }
    }
  }
}
