package org.crue.hercules.sgi.eti.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.specification.MemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.util.Constantes;
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
 * Service Implementation para la gestión de {@link Memoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemoriaServiceImpl implements MemoriaService {

  /** Estado Memoria Repository. */
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  private final MemoriaRepository memoriaRepository;

  public MemoriaServiceImpl(MemoriaRepository memoriaRepository, EstadoMemoriaRepository estadoMemoriaRepository) {
    this.memoriaRepository = memoriaRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
  }

  /**
   * Guarda la entidad {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a guardar.
   * @return la entidad {@link Memoria} persistida.
   */
  @Transactional
  @Override
  public Memoria create(Memoria memoria) {
    log.debug("Memoria create(Memoria memoria) - start");
    Assert.isNull(memoria.getId(), "Memoria id tiene que ser null para crear un nueva memoria");

    return memoriaRepository.save(memoria);
  }

  /**
   * Obtiene todas las entidades {@link MemoriaPeticionEvaluacion} paginadas y
   * filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link MemoriaPeticionEvaluacion} paginadas y
   *         filtradas.
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<Memoria> spec = new QuerySpecification<Memoria>(query);
    Specification<Memoria> specActivos = MemoriaSpecifications.activos();
    Specification<Memoria> specs = Specification.where(specActivos).and(spec);
    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findAllMemoriasEvaluaciones(specs, paging, null);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * 
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoriaReunion Identificador del {@link ConvocatoriaReunion}
   * @param pageable              la información de paginación.
   * @return lista de memorias asignables a la convocatoria.
   */
  @Override
  public Page<Memoria> findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) {
    log.debug("findAllMemoriasAsignables(Long idConvocatoriaReunion, Pageable pageable) - start");
    Page<Memoria> returnValue = memoriaRepository.findAllMemoriasAsignablesConvocatoria(idConvocatoriaReunion,
        pageable);
    log.debug("findAllMemoriasAsignables(Long idConvocatoriaReunion, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Ordinaria" o "Extraordinaria".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param query    filtro de {@link QueryCriteria}.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaOrdExt(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(List<QueryCriteria> query,Pageable pageable) - start");

    // idComite y fechaLimite
    Specification<Memoria> specByQuery = new QuerySpecification<Memoria>(query);
    // Memorias activas
    Specification<Memoria> specActivos = MemoriaSpecifications.activos();
    // Estado actual
    Specification<Memoria> specEstadoActual = MemoriaSpecifications
        .estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA));
    // Estado retrospectiva
    Specification<Memoria> specEstadoRetrospectiva = MemoriaSpecifications
        .estadoRetrospectivaIn(Arrays.asList(Constantes.ESTADO_RETROSPECTIVA_EN_SECRETARIA));

    // Estado Actual AND idComite y fechaLimite
    Specification<Memoria> condicion = Specification.where(specEstadoActual).and(specByQuery);
    // (Estado Actual AND idComite y fechaLimite) OR Estado retrospectiva
    Specification<Memoria> condicionFinal = Specification.where(condicion).or(specEstadoRetrospectiva);

    // Memorias Activas AND ((Estado Actual AND idComite y fechaLimite) OR Estado
    // retrospectiva)
    Specification<Memoria> specs = Specification.where(specActivos).and(condicionFinal);

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, pageable);

    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(List<QueryCriteria> query,Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Seguimiento".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité y Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * @param query    filtro de {@link QueryCriteria}.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaSeguimiento(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(List<QueryCriteria> query,Pageable pageable) - start");

    // idComite y fechaLimite
    Specification<Memoria> specByQuery = new QuerySpecification<Memoria>(query);
    // Memorias activas
    Specification<Memoria> specActivos = MemoriaSpecifications.activos();
    // Estado actual
    Specification<Memoria> specEstadoActual = MemoriaSpecifications
        .estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL,
            Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL));

    // Memorias Activas AND (Estado Actual AND idComite y fechaLimite)
    Specification<Memoria> specs = Specification.where(specActivos).and(specEstadoActual).and(specByQuery);

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, pageable);

    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(List<QueryCriteria> query,Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   * @return la entidad {@link Memoria}.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  @Override
  public Memoria findById(final Long id) throws MemoriaNotFoundException {
    log.debug("Petición a get Memoria : {}  - start", id);
    final Memoria Memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));
    log.debug("Petición a get Memoria : {}  - end", id);
    return Memoria;

  }

  /**
   * Elimina una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  @Transactional
  @Override
  public void delete(Long id) throws MemoriaNotFoundException {
    log.debug("Petición a delete Memoria : {}  - start", id);
    Assert.notNull(id, "El id de Memoria no puede ser null.");
    if (!memoriaRepository.existsById(id)) {
      throw new MemoriaNotFoundException(id);
    }
    memoriaRepository.deleteById(id);
    log.debug("Petición a delete Memoria : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Memoria}.
   * 
   * @param memoriaActualizar {@link Memoria} con los datos actualizados.
   * @return El {@link Memoria} actualizado.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Memoria} no tiene id.
   */

  @Transactional
  @Override
  public Memoria update(final Memoria memoriaActualizar) {
    log.debug("update(Memoria MemoriaActualizar) - start");

    Assert.notNull(memoriaActualizar.getId(), "Memoria id no puede ser null para actualizar un tipo memoria");

    return memoriaRepository.findById(memoriaActualizar.getId()).map(memoria -> {
      memoria.setNumReferencia(memoriaActualizar.getNumReferencia());
      memoria.setPeticionEvaluacion(memoriaActualizar.getPeticionEvaluacion());
      memoria.setComite((memoriaActualizar.getComite()));
      memoria.setTitulo(memoriaActualizar.getTitulo());
      memoria.setPersonaRef(memoriaActualizar.getPersonaRef());
      memoria.setTipoMemoria(memoriaActualizar.getTipoMemoria());
      memoria.setEstadoActual(memoriaActualizar.getEstadoActual());
      memoria.setFechaEnvioSecretaria(memoriaActualizar.getFechaEnvioSecretaria());
      memoria.setRequiereRetrospectiva(memoriaActualizar.getRequiereRetrospectiva());
      memoria.setRetrospectiva(memoriaActualizar.getRetrospectiva());
      memoria.setVersion(memoriaActualizar.getVersion());
      memoria.setActivo(memoriaActualizar.getActivo());

      Memoria returnValue = memoriaRepository.save(memoria);
      log.debug("update(Memoria memoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(memoriaActualizar.getId()));
  }

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @param pageable             información de paginación
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion,
      Pageable pageable) {
    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findMemoriasEvaluacion(idPeticionEvaluacion,
        pageable, null);
    return returnValue;
  }

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param memoria             {@link Memoria} a actualizar estado.
   * @param idTipoEstadoMemoria identificador del estado nuevo de la memoria.
   */
  @Override
  public void updateEstadoMemoria(Memoria memoria, long idTipoEstadoMemoria) {
    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - start");

    // se crea el nuevo estado para la memoria
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);
    EstadoMemoria estadoMemoria = new EstadoMemoria(null, memoria, tipoEstadoMemoria, LocalDateTime.now());

    estadoMemoriaRepository.save(estadoMemoria);

    // Se actualiza la memoria con el nuevo tipo estado memoria

    memoria.setEstadoActual(tipoEstadoMemoria);
    memoriaRepository.save(memoria);

    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - end");
  }

  /**
   * Obtiene todas las entidades {@link Memoria} paginadas y filtadas.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef la referencia de la persona
   * @return el listado de entidades {@link Memoria} paginadas y filtradas.
   */
  @Override
  public Page<Memoria> findAllByPersonaRef(List<QueryCriteria> query, Pageable paging, String personaRef) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<Memoria> specByQuery = new QuerySpecification<Memoria>(query);
    Specification<Memoria> specActivos = MemoriaSpecifications.activos();
    Specification<Memoria> specByPersonaRef = MemoriaSpecifications.byPersonaRef(personaRef);

    Specification<Memoria> specs = Specification.where(specActivos).and(specByQuery).and(specByPersonaRef);

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve las memorias de las peticiones de evaluación con su fecha límite y
   * de evaluación.
   * 
   * @param pageable información de paginación
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAllMemoriasPeticionesEvaluacion(Pageable pageable) {
    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findMemoriasEvaluacion(null, pageable, null);
    return returnValue;
  }

  /**
   * Devuelve las memorias de las peticiones de evaluación con su fecha límite y
   * de evaluación de una personaRef con filtros
   * 
   * @param personaRef persona creadora de la memoria
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAllByPersonaRefPeticionEvaluacion(List<QueryCriteria> query,
      Pageable paging, String personaRef) {
    Specification<Memoria> spec = new QuerySpecification<Memoria>(query);
    Specification<Memoria> specActivos = MemoriaSpecifications.activos();
    Specification<Memoria> specByPersonaRefPeticionEvaluacion = MemoriaSpecifications
        .byPersonaRefPeticionEvaluacion(personaRef);
    Specification<Memoria> specByPersonaRef = MemoriaSpecifications.byPersonaRef(personaRef);

    Specification<Memoria> specs = Specification.where(spec).and(specActivos)
        .and((specByPersonaRef).or(specByPersonaRefPeticionEvaluacion));
    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findAllMemoriasEvaluaciones(specs, paging,
        personaRef);
    return returnValue;
  }

}
