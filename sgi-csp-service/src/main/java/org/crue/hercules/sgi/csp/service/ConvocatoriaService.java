package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Convocatoria}.
 */

public interface ConvocatoriaService {

  /**
   * Guarda la entidad {@link Convocatoria}.
   * 
   * @param convocatoria           la entidad {@link Convocatoria} a guardar.
   * @param acronimosUnidadGestion listado de acrónimos asociados a las unidades
   *                               de gestión del usuario logueado.
   * @return Convocatoria la entidad {@link Convocatoria} persistida.
   */
  Convocatoria create(Convocatoria convocatoria, List<String> acronimosUnidadGestion);

  /**
   * Actualiza los datos del {@link Convocatoria}.
   * 
   * @param convocatoria           {@link Convocatoria} con los datos
   *                               actualizados.
   * @param acronimosUnidadGestion lista de acronimos
   * @return Convocatoria {@link Convocatoria} actualizado.
   */
  Convocatoria update(final Convocatoria convocatoria, List<String> acronimosUnidadGestion);

  /**
   * Registra una {@link Convocatoria} actualizando su estado de 'Borrador' a
   * 'Registrada'
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} actualizada.
   */
  Convocatoria registrar(final Long id);

  /**
   * Reactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  Convocatoria enable(Long id);

  /**
   * Desactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  Convocatoria disable(Long id);

  /**
   * Comprueba si existen datos vinculados a la {@link Convocatoria} de
   * {@link TipoFase}, {@link TipoHito}, {@link TipoEnlace} y
   * {@link TipoDocumento} con el fin de permitir la edición de los campos
   * unidadGestionRef y modeloEjecucion.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true existen datos vinculados/false no existen datos vinculados.
   */
  Boolean tieneVinculaciones(Long id);

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede ser modificada. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con la propia
   * {@link Convocatoria}.
   *
   * @param id                 Id del {@link Convocatoria}.
   * @param unidadConvocatoria unidadGestionRef {@link Convocatoria}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  Boolean modificable(Long id, String unidadConvocatoria);

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede pasar a estado 'Registrada'.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true si puede ser registrada / false si no puede ser registrada
   */
  Boolean registrable(Long id);

  /**
   * Comprueba la existencia del {@link Convocatoria} por id.
   *
   * @param id el id de la entidad {@link Convocatoria}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene la Unidad de Gestión asignada a la {@link Convocatoria}.
   * 
   * @param id Id del {@link Convocatoria}.
   * @return unidadGestionRef asignada
   */
  String getUnidadGestionRef(Long id);

  /**
   * Obtiene el {@link ModeloEjecucion} asignada a la {@link Convocatoria}.
   * 
   * @param id Id de la {@link Convocatoria}.
   * @return {@link ModeloEjecucion} asignado
   */
  ModeloEjecucion getModeloEjecucion(Long id);

  /**
   * Obtiene una entidad {@link Convocatoria} por id.
   * 
   * @param id Identificador de la entidad {@link Convocatoria}.
   * @return Convocatoria la entidad {@link Convocatoria}.
   */
  Convocatoria findById(final Long id);

  /**
   * Obtiene todas las entidades {@link Convocatoria} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  Page<Convocatoria> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Convocatoria} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  Page<Convocatoria> findAllInvestigador(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Convocatoria} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  Page<Convocatoria> findAllTodos(String query, Pageable paging);

  /**
   * Devuelve todas las convocatorias activas registradas que se encuentren dentro
   * de la unidad de gestión del usuario logueado.
   * 
   * @param query                  información del filtro.
   * @param paging                 información de paginación.
   * @param acronimosUnidadGestion lista de acronimos de unidad de gestion a los
   *                               que se restringe la busqueda.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  Page<Convocatoria> findAllRestringidos(String query, Pageable paging, List<String> acronimosUnidadGestion);

  /**
   * Devuelve todas las convocatorias activas que se encuentren dentro de la
   * unidad de gestión del usuario logueado.
   * 
   * @param query                  información del filtro.
   * @param paging                 información de paginación.
   * @param acronimosUnidadGestion lista de acronimos de unidad de gestion a los
   *                               que se restringe la busqueda.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  Page<Convocatoria> findAllTodosRestringidos(String query, Pageable paging, List<String> acronimosUnidadGestion);

}
