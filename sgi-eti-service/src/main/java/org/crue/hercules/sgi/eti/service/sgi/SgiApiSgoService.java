package org.crue.hercules.sgi.eti.service.sgi;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.sgo.UnidadVinculacionOutput;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.crue.hercules.sgi.eti.exceptions.rep.MicroserviceCallException;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Cliente del API del SGO.
 */
@Service
@Slf4j
public class SgiApiSgoService extends SgiApiBaseService {

  /**
   * Instancia un nuevo SgiApiSgoService.
   * 
   * @param restApiProperties {@link RestApiProperties}
   * @param restTemplate      {@link RestTemplate}
   */
  public SgiApiSgoService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene la unidad de vinculación del SGO con el id indicado.
   * 
   * @param id Identificador de la unidad de vinculación
   * @return la {@link UnidadVinculacionOutput} o vacío si no existe
   * @throws MicroserviceCallException si se produce un error en la llamada
   */
  public Optional<UnidadVinculacionOutput> findUnidadVinculacionById(String id) {
    log.debug("findUnidadVinculacionById - id: {}", id);
    AssertHelper.idNotNull(id, UnidadVinculacionOutput.class);

    String query = "id==\"" + id + "\"";
    try {
      String mergedURL = buildUri(ServiceType.SGO, "/unidades-vinculacion?q={query}");
      List<UnidadVinculacionOutput> response = super.<List<UnidadVinculacionOutput>>callEndpoint(mergedURL,
          HttpMethod.GET, new ParameterizedTypeReference<List<UnidadVinculacionOutput>>() {
          }, query).getBody();
      return response == null ? Optional.empty() : response.stream().findFirst();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
  }

}
