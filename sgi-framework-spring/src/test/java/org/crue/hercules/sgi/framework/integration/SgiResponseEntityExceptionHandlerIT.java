package org.crue.hercules.sgi.framework.integration;

import org.crue.hercules.sgi.framework.web.servlet.mvc.method.annotation.SgiResponseEntityExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebMvcTest(SgiResponseEntityExceptionHandlerIT.TestWebConfig.class)
public class SgiResponseEntityExceptionHandlerIT {

  @Autowired
  private MockMvc mockMvc;

  @Configuration
  public static class TestWebConfig implements WebMvcConfigurer {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ControllerAdvice
    public static class TestResponseEntityExceptionHandler extends SgiResponseEntityExceptionHandler {
    }

    @Bean
    public TestResponseEntityExceptionHandler testResponseEntityExceptionHandler() {
      return new TestResponseEntityExceptionHandler();
    }
  }

  @TestConfiguration
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