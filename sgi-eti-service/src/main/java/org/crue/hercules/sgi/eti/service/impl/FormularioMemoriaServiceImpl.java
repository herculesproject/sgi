package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.FormularioMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.repository.FormularioMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.specification.FormularioMemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.FormularioMemoriaService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link FormularioMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FormularioMemoriaServiceImpl implements FormularioMemoriaService {
  private final FormularioMemoriaRepository formularioMemoriaRepository;

  public FormularioMemoriaServiceImpl(FormularioMemoriaRepository formularioMemoriaRepository) {
    this.formularioMemoriaRepository = formularioMemoriaRepository;
  }

  /**
   * Guarda la entidad {@link FormularioMemoria}.
   *
   * @param formularioMemoria la entidad {@link FormularioMemoria} a guardar.
   * @return la entidad {@link FormularioMemoria} persistida.
   */
  @Transactional
  public FormularioMemoria create(FormularioMemoria formularioMemoria) {
    log.debug("Petición a create FormularioMemoria : {} - start", formularioMemoria);
    Assert.isNull(formularioMemoria.getId(),
        "FormularioMemoria id tiene que ser null para crear un nuevo formulario memoria");

    return formularioMemoriaRepository.save(formularioMemoria);
  }

  /**
   * Obtiene todas las entidades {@link FormularioMemoria} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link FormularioMemoria} paginadas y
   *         filtradas.
   */
  public Page<FormularioMemoria> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Specification<FormularioMemoria> specByQuery = new QuerySpecification<FormularioMemoria>(query);
    Specification<FormularioMemoria> specActivos = FormularioMemoriaSpecifications.activos();

    Specification<FormularioMemoria> specs = Specification.where(specActivos).and(specByQuery);

    Page<FormularioMemoria> returnValue = formularioMemoriaRepository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link FormularioMemoria} por id.
   *
   * @param id el id de la entidad {@link FormularioMemoria}.
   * @return la entidad {@link FormularioMemoria}.
   * @throws FormularioMemoriaNotFoundException Si no existe ningún
   *                                            {@link FormularioMemoria} con ese
   *                                            id.
   */
  public FormularioMemoria findById(final Long id) throws TareaNotFoundException {
    log.debug("Petición a get FormularioMemoria : {}  - start", id);
    final FormularioMemoria formularioMemoria = formularioMemoriaRepository.findById(id)
        .orElseThrow(() -> new FormularioMemoriaNotFoundException(id));
    log.debug("Petición a get FormularioMemoria : {}  - end", id);
    return formularioMemoria;

  }

  /**
   * Elimina una entidad {@link FormularioMemoria} por id.
   *
   * @param id el id de la entidad {@link FormularioMemoria}.
   * @throws FormularioMemoriaNotFoundException Si no existe ningún
   *                                            {@link FormularioMemoria} con ese
   *                                            id.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete FormularioMemoria : {}  - start", id);
    Assert.notNull(id, "El id de FormularioMemoria no puede ser null.");
    if (!formularioMemoriaRepository.existsById(id)) {
      throw new FormularioMemoriaNotFoundException(id);
    }
    formularioMemoriaRepository.deleteById(id);
    log.debug("Petición a delete FormularioMemoria : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link FormularioMemoria}.
   * 
   * @param formularioMemoriaActualizar {@link FormularioMemoria} con los datos
   *                                    actualizados.
   * @return El {@link FormularioMemoria} actualizado.
   * @throws FormularioMemoriaNotFoundException Si no existe ningún
   *                                            {@link FormularioMemoria} con ese
   *                                            id.
   * @throws IllegalArgumentException           Si el {@link FormularioMemoria} no
   *                                            tiene id.
   */
  @Transactional
  public FormularioMemoria update(final FormularioMemoria formularioMemoriaActualizar) {
    log.debug("update(FormularioMemoria formularioMemoriaActualizar) - start");

    Assert.notNull(formularioMemoriaActualizar.getId(),
        "FormularioMemoria id no puede ser null para actualizar un formularioMemoria");

    return formularioMemoriaRepository.findById(formularioMemoriaActualizar.getId()).map(formularioMemoria -> {
      formularioMemoria.setMemoria(formularioMemoriaActualizar.getMemoria());
      formularioMemoria.setFormulario(formularioMemoriaActualizar.getFormulario());
      formularioMemoria.setActivo(formularioMemoriaActualizar.getActivo());

      FormularioMemoria returnValue = formularioMemoriaRepository.save(formularioMemoria);
      log.debug("update(FormularioMemoria formularioMemoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new FormularioMemoriaNotFoundException(formularioMemoriaActualizar.getId()));
  }

}
