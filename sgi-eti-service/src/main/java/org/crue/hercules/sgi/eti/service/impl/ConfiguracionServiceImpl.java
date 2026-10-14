package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.dto.ConfigParamOutput;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionParamDependencyException;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.UnknownConfiguracionParamException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Configuracion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionServiceImpl implements ConfiguracionService {

  private final ConfiguracionRepository configuracionRepository;

  public ConfiguracionServiceImpl(ConfiguracionRepository configuracionRepository) {
    this.configuracionRepository = configuracionRepository;
  }

  public Configuracion findConfiguracion() {
    log.debug("findConfiguracion - ()");
    return configuracionRepository.findFirstByOrderByIdAsc()
        .orElseThrow(ConfiguracionNotFoundException::new);
  }

  @Transactional
  public Configuracion update(final Configuracion configuracionActualizar) {
    log.debug("update - configuracionActualizar: {}", configuracionActualizar);

    AssertHelper.idNotNull(configuracionActualizar.getId(), Configuracion.class);

    return configuracionRepository.findById(configuracionActualizar.getId()).map(configuracion -> {
      configuracion
          .setMesesArchivadaPendienteCorrecciones(configuracionActualizar.getMesesArchivadaPendienteCorrecciones());
      configuracion.setDiasLimiteEvaluador(configuracionActualizar.getDiasLimiteEvaluador());
      configuracion.setDiasArchivadaInactivo(configuracionActualizar.getDiasArchivadaInactivo());
      configuracion.setDiasAvisoRetrospectiva(configuracionActualizar.getDiasAvisoRetrospectiva());
      configuracion.setDuracionProyectoEvaluacion(configuracionActualizar.getDuracionProyectoEvaluacion());

      return configuracionRepository.save(configuracion);
    }).orElseThrow(() -> new ConfiguracionNotFoundException(configuracionActualizar.getId()));
  }

  @Override
  public ConfigParamOutput get(String name) {
    log.debug("get - name: {}", name);
    Configuracion.Param param = findParam(name);

    return ConfigParamOutput.builder()
        .name(param.getKey())
        .value(getParamValueAsString(findConfiguracion(), param))
        .build();
  }

  @Override
  @Transactional
  public ConfigParamOutput updateValue(String name, String value) {
    log.debug("updateValue - name: {}, value: {}", name, value);
    Configuracion.Param param = findParam(name);

    return configuracionRepository.findFirstByOrderByIdAsc().map(data -> {
      data.updateParamValue(param, value);
      assertParamDependencyEnabled(data, param);
      Configuracion saved = configuracionRepository.save(data);
      return ConfigParamOutput.builder()
          .name(param.getKey())
          .value(getParamValueAsString(saved, param))
          .build();
    }).orElseThrow(ConfiguracionNotFoundException::new);
  }

  /**
   * Devuelve el {@link Configuracion.Param} con la clave indicada.
   *
   * @param name clave del parámetro.
   * @return el {@link Configuracion.Param}.
   * @throws UnknownConfiguracionParamException si la clave no corresponde a
   *                                            ningún
   *                                            {@link Configuracion.Param}.
   */
  private Configuracion.Param findParam(String name) {
    Configuracion.Param param = Configuracion.Param.fromKey(name);
    if (param == null) {
      throw new UnknownConfiguracionParamException(name);
    }

    return param;
  }

  /**
   * Comprueba que, si el parámetro queda habilitado, también lo esté el
   * parámetro del que depende.
   *
   * @param configuracion la {@link Configuracion} con el nuevo valor aplicado.
   * @param param         el {@link Configuracion.Param} actualizado.
   * @throws ConfiguracionParamDependencyException si el parámetro queda
   *                                               habilitado sin estarlo el
   *                                               parámetro del que depende.
   */
  private void assertParamDependencyEnabled(Configuracion configuracion, Configuracion.Param param) {
    Configuracion.Param dependsOn = param.getDependsOn();
    if (dependsOn != null && configuracion.isParamEnabled(param) && !configuracion.isParamEnabled(dependsOn)) {
      throw new ConfiguracionParamDependencyException(param.getKey(), dependsOn.getKey());
    }
  }

  private String getParamValueAsString(Configuracion configuracion, Configuracion.Param param) {
    Object value = configuracion.getParamValue(param);
    return value != null ? value.toString() : "";
  }

}
