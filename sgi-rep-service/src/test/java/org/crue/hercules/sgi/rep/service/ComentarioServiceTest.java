package org.crue.hercules.sgi.rep.service;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.eti.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

/**
 * ComentarioServiceTest
 */
class ComentarioServiceTest extends BaseReportServiceTest {

  private ComentarioService comentarioService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    comentarioService = new ComentarioService(restApiProperties, restTemplate);
  }

  @Test
  void findById_ReturnsException() throws Exception {
    Long idComentario = 1L;

    Assertions.assertThatThrownBy(() -> comentarioService.findById(idComentario))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findAll_ReturnsException() throws Exception {
    String query = "query";
    Pageable paging = PageRequest.of(1, 2);

    Assertions.assertThatThrownBy(() -> comentarioService.findAll(
        query,
        paging,
        new ParameterizedTypeReference<List<ComentarioDto>>() {
        }))
        .isInstanceOf(GetDataReportException.class);
  }

}
