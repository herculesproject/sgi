package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.framework.web.config.SgiI18nConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import({ SecurityConfig.class, SgiI18nConfig.class })
abstract class BaseControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @MockBean
  protected JwtDecoder jwtDecoder;

  @MockBean
  protected ClientRegistrationRepository clientRegistrationRepository;
}
