package org.crue.hercules.sgi.prc.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiPiiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiPiiServiceTest
 */
class SgiApiPiiServiceTest extends BaseServiceTest {

  private SgiApiPiiService sgiApiPiiService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiPiiService = new SgiApiPiiService(restApiProperties, restTemplate);
  }

  @Test
  void findInvencionesProduccionCientifica_ko() throws Exception {
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiPiiService.findInvencionesProduccionCientifica(
        anio)).isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findInvencionInventorByInvencionIdAndAnio_ko() throws Exception {
    Long invencionId = 1L;
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiPiiService.findInvencionInventorByInvencionIdAndAnio(invencionId, anio))
        .isInstanceOf(MicroserviceCallException.class);
  }

}
