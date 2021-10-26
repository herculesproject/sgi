package org.crue.hercules.sgi.rep.service.eti;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BloqueService extends BaseRestTemplateService<BloqueDto> {
  private static final String URL_API = "/bloques";

  public BloqueService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  public List<BloqueDto> findByFormularioId(Long idFormulario) {
    List<BloqueDto> resultados = null;
    try {

      String sort = "orden,asc";
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/" + idFormulario + "/formulario")
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<BloqueDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<BloqueDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  public List<ApartadoDto> findByBloqueId(Long idBloque) {
    List<ApartadoDto> resultados = null;
    try {

      String sort = "orden,asc";
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/" + idBloque + "/apartados")
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<ApartadoDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<ApartadoDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
