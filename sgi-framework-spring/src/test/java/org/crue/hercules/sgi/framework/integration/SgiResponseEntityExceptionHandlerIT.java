package org.crue.hercules.sgi.framework.integration;

import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class SgiResponseEntityExceptionHandlerIT {

  @Autowired
  private MockMvc mockMvc;

  @Configuration // A nested @Configuration class wild be used instead of the
                 // application’s primary configuration.
  @Import(SgiWebConfig.class)
  public static class TestWebConfig {
  }

  @TestConfiguration // Unlike a nested @Configuration class, which would be used instead of your
                     // application’s primary configuration, a nested @TestConfiguration class is
                     // used in addition to your application’s primary configuration.
  @RestController
  public static class InnerWebConfigTestController {
    @GetMapping("/test-illegal-argument-exception")
    void testIllegalArgumentException() {
      throw new IllegalArgumentException();
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void requestWithPageableAnnotation_and_PagingHeaders_returnsPageable() throws Exception {
    // given: a controller method that throws an IlleagalArgumenException

    // when: access /test-illegal-argument-exception
    mockMvc.perform(MockMvcRequestBuilders.get("/test-illegal-argument-exception").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: bad request is returned
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
  }
}