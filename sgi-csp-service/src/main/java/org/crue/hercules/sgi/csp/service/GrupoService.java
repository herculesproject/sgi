package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link Grupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class GrupoService {
  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_ID = "id";

  private final GrupoRepository repository;

  public GrupoService(GrupoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link Grupo}.
   * 
   * @param grupo la entidad {@link Grupo} a guardar.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo create(Grupo grupo) {
    log.debug("create(Grupo grupo) - start");

    assertIdGrupoIsNull(grupo.getId());
    Grupo returnValue = repository.save(grupo);

    log.debug("create(Grupo grupo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Grupo}.
   *
   * @param grupoActualizar {@link Grupo} con los datos actualizados.
   * @return {@link Grupo} actualizado.
   */
  @Transactional
  @Validated({ Grupo.OnActualizar.class })
  public Grupo update(@Valid Grupo grupoActualizar) {
    log.debug("update(Grupo grupoActualizar) - start");

    assertIdGrupoNotNull(grupoActualizar.getId());

    return repository.findById(grupoActualizar.getId()).map(data -> {
      data.setNombre(grupoActualizar.getNombre());
      data.setCodigo(grupoActualizar.getCodigo());
      data.setProyectoSgeRef(grupoActualizar.getProyectoSgeRef());
      data.setFechaInicio(grupoActualizar.getFechaInicio());
      data.setFechaFin(grupoActualizar.getFechaFin());
      data.setTipo(grupoActualizar.getTipo());
      data.setEspecialInvestigacionId(grupoActualizar.getEspecialInvestigacionId());

      Grupo returnValue = repository.save(data);

      log.debug("update(Grupo grupoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(grupoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link Grupo} por id.
   * 
   * @param id Identificador de la entidad {@link Grupo}.
   * @return la entidad {@link Grupo}.
   */
  public Grupo findById(Long id) {
    log.debug("findById(Long id) - start");

    assertIdGrupoNotNull(id);
    final Grupo returnValue = repository.findById(id).orElseThrow(() -> new GrupoNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Grupo} por id.
   *
   * @param id el id de la entidad {@link Grupo}.
   * @return <code>true</code> si existe y <code>false</code> en caso contrario.
   */
  public boolean existsById(Long id) {
    log.debug("existsById(Long id)  - start");

    assertIdGrupoNotNull(id);
    final boolean exists = repository.existsById(id);

    log.debug("existsById(Long id)  - end");
    return exists;
  }

  /**
   * Obtener todas las entidades {@link Grupo} paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Grupo} paginadas y/o
   *         filtradas.
   */
  public Page<Grupo> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Grupo> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<Grupo> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Grupo}.
   *
   * @param id Id del {@link Grupo}.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    assertIdGrupoNotNull(id);

    return repository.findById(id).map(grupo -> {
      if (Boolean.FALSE.equals(grupo.getActivo())) {
        // Si no esta activo no se hace nada
        return grupo;
      }

      grupo.setActivo(false);

      Grupo returnValue = repository.save(grupo);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(id));
  }

  /**
   * Activa el {@link Grupo}.
   *
   * @param id Id del {@link Grupo}.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo activar(Long id) {
    log.debug("activar(Long id) - start");

    assertIdGrupoNotNull(id);

    return repository.findById(id).map(grupo -> {
      if (Boolean.TRUE.equals(grupo.getActivo())) {
        // Si esta activo no se hace nada
        return grupo;
      }

      grupo.setActivo(true);

      Grupo returnValue = repository.save(grupo);
      log.debug("activar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(id));
  }

  /**
   * Comprueba que el id no sea null
   * 
   * @param id Id del {@link Grupo}.
   */
  private void assertIdGrupoIsNull(Long id) {
    Assert.isNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Grupo.class))
            .build());
  }

  /**
   * Comprueba que el id no sea null
   * 
   * @param id Id del {@link Grupo}.
   */
  private void assertIdGrupoNotNull(Long id) {
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Grupo.class))
            .build());
  }

}
