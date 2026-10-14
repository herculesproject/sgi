package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.dto.ConfigParamOutput;
import org.crue.hercules.sgi.eti.model.Configuracion;

/**
 * Service Interface para gestionar {@link Configuracion}.
 */
public interface ConfiguracionService {

  /**
   * Actualizar {@link Configuracion}.
   *
   * Solo actualiza los parámetros que se gestionan desde la pantalla de
   * configuración del módulo de ética (permiso <code>ETI-CNF-E</code>). Los
   * parámetros que se gestionan por clave desde Administración (enum
   * {@link Configuracion.Param}, endpoint <code>/config/{name}</code>) no se
   * modifican con este método.
   *
   * @param configuracion la entidad {@link Configuracion} a actualizar.
   * @return la entidad {@link Configuracion} persistida.
   */
  Configuracion update(Configuracion configuracion);

  /**
   * Devuelve la {@link Configuracion}
   *
   * @return la {@link Configuracion}
   */
  Configuracion findConfiguracion();

  /**
   * Devuelve el valor del parámetro de {@link Configuracion} con la clave
   * indicada.
   *
   * @param name clave del parámetro ({@link Configuracion.Param}).
   * @return el {@link ConfigParamOutput} con la clave y el valor del parámetro.
   */
  ConfigParamOutput get(String name);

  /**
   * Actualiza el valor del parámetro de {@link Configuracion} con la clave
   * indicada.
   *
   * @param name  clave del parámetro ({@link Configuracion.Param}).
   * @param value nuevo valor del parámetro.
   * @return el {@link ConfigParamOutput} con la clave y el valor actualizado.
   */
  ConfigParamOutput updateValue(String name, String value);

}