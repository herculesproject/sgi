package org.crue.hercules.sgi.eti.controller;

import java.util.TimeZone;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.ConfigParamOutput;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConfigController
 */
@RestController
@RequestMapping(ConfigController.MAPPING)
@Slf4j
public class ConfigController {

  /** The URL path delimiter */
  public static final String PATH_DELIMITER = "/";
  /** The controller base path mapping */
  public static final String MAPPING = PATH_DELIMITER + "config";
  /** The path used to request by name */
  public static final String PATH_NAME = PATH_DELIMITER + "{name}";
  /** The path to request the Time Zone */
  public static final String PATH_TIMEZONE = PATH_DELIMITER + "time-zone";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SgiConfigProperties sgiConfigProperties;
  private final ConfiguracionService configuracionService;

  /**
   * Instancia un nuevo ConfigController.
   *
   * @param sgiConfigProperties  {@link SgiConfigProperties}
   * @param configuracionService {@link ConfiguracionService}
   */
  public ConfigController(SgiConfigProperties sgiConfigProperties, ConfiguracionService configuracionService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.configuracionService = configuracionService;
  }

  /**
   * Devuelve el identificador de {@link TimeZone} configurado en la aplicación.
   * 
   * @return {@link String} con el identificador de {@link TimeZone} configurado.
   */
  @GetMapping(value = PATH_TIMEZONE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String timeZone() {
    log.debug("timeZone() - start");
    String returnValue = sgiConfigProperties.getTimeZone().getID();
    log.debug("timeZone() - end");
    return returnValue;
  }

  /**
   * Devuelve el valor del parámetro de {@link Configuracion} con la clave
   * indicada.
   *
   * Con solo autoridades de investigador únicamente se pueden consultar los
   * parámetros legibles desde el perfil de investigación.
   *
   * @param name clave del parámetro ({@link Configuracion.Param}).
   * @return el {@link ConfigParamOutput} con la clave y el valor del parámetro.
   */
  @GetMapping(PATH_NAME)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAnyAuthorityForAnyUO('ADM-CNF-E', 'ETI-PEV-V', 'ETI-PEV-E', 'ETI-MEM-V', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
  public ConfigParamOutput get(@PathVariable String name) {
    log.debug("get - name: {}", name);
    return configuracionService.get(name);
  }

  /**
   * Actualiza el valor del parámetro de {@link Configuracion} con la clave
   * indicada.
   *
   * @param name  clave del parámetro ({@link Configuracion.Param}).
   * @param value nuevo valor del parámetro.
   * @return el {@link ConfigParamOutput} con la clave y el valor actualizado.
   */
  @PatchMapping(PATH_NAME)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ConfigParamOutput updateValue(@PathVariable String name,
      @RequestBody(required = false) String value) {
    log.debug("updateValue - name: {}, value: {}", name, value);
    return configuracionService.updateValue(name, value);
  }
}
