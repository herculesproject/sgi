package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.FormularioMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link FormularioMemoria}.
 */
public interface FormularioMemoriaService {

  /**
   * Guardar {@link FormularioMemoria}.
   *
   * @param formularioMemoria la entidad {@link FormularioMemoria} a guardar.
   * @return la entidad {@link FormularioMemoria} persistida.
   */
  FormularioMemoria create(FormularioMemoria formularioMemoria);

  /**
   * Actualizar {@link FormularioMemoria}.
   *
   * @param formularioMemoria la entidad {@link FormularioMemoria} a actualizar.
   * @return la entidad {@link FormularioMemoria} persistida.
   */
  FormularioMemoria update(FormularioMemoria formularioMemoria);

  /**
   * Obtener todas las entidades {@link FormularioMemoria} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FormularioMemoria} paginadas y/o
   *         filtradas.
   */
  Page<FormularioMemoria> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link FormularioMemoria} por id.
   *
   * @param id el id de la entidad {@link FormularioMemoria}.
   * @return la entidad {@link FormularioMemoria}.
   */
  FormularioMemoria findById(Long id);

  /**
   * Elimina el {@link FormularioMemoria} por id.
   *
   * @param id el id de la entidad {@link FormularioMemoria}.
   */
  void delete(Long id) throws FormularioMemoriaNotFoundException;

}