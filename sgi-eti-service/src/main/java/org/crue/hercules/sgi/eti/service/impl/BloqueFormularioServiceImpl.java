package org.crue.hercules.sgi.eti.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.exceptions.BloqueFormularioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.ComiteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TipoEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.repository.BloqueFormularioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteFormularioRepository;
import org.crue.hercules.sgi.eti.repository.TipoEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.specification.BloqueFormularioSpecifications;
import org.crue.hercules.sgi.eti.service.BloqueFormularioService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link BloqueFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class BloqueFormularioServiceImpl implements BloqueFormularioService {

  /** Bloque formulario repository. */
  private final BloqueFormularioRepository bloqueFormularioRepository;

  /** Comite Formulario repository. */
  private final ComiteFormularioRepository comiteFormularioRepository;

  /** Tipo Evaluación repository. */
  private final TipoEvaluacionRepository tipoEvaluacionRepository;

  /**
   * Instancia un nuevo bloque formulario.
   * 
   * @param bloqueFormularioRepository {@link BloqueFormularioRepository}.
   * @param comiteFormularioRepository {@link ComiteFormularioRepository}.
   * @param tipoEvaluacionRepository   {@link TipoEvaluacionRepository}.
   */
  public BloqueFormularioServiceImpl(BloqueFormularioRepository bloqueFormularioRepository,
      ComiteFormularioRepository comiteFormularioRepository, TipoEvaluacionRepository tipoEvaluacionRepository) {
    this.bloqueFormularioRepository = bloqueFormularioRepository;
    this.comiteFormularioRepository = comiteFormularioRepository;
    this.tipoEvaluacionRepository = tipoEvaluacionRepository;
  }

  /**
   * Guarda la entidad {@link BloqueFormulario}.
   *
   * @param bloqueFormulario la entidad {@link BloqueFormulario} a guardar.
   * @return la entidad {@link BloqueFormulario} persistida.
   */
  @Transactional
  public BloqueFormulario create(BloqueFormulario bloqueFormulario) {
    log.debug("Petición a create BloqueFormulario : {} - start", bloqueFormulario);
    Assert.isNull(bloqueFormulario.getId(),
        "BloqueFormulario id tiene que ser null para crear un nuevo bloqueFormulario");

    return bloqueFormularioRepository.save(bloqueFormulario);
  }

  /**
   * Obtiene todas las entidades {@link BloqueFormulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link BloqueFormulario} paginadas y
   *         filtradas.
   */
  public Page<BloqueFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<BloqueFormulario> specByQuery = new QuerySpecification<BloqueFormulario>(query);
    Specification<BloqueFormulario> specActivos = BloqueFormularioSpecifications.activos();

    Specification<BloqueFormulario> specs = Specification.where(specActivos).and(specByQuery);

    Page<BloqueFormulario> returnValue = bloqueFormularioRepository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link BloqueFormulario} por id.
   *
   * @param id el id de la entidad {@link BloqueFormulario}.
   * @return la entidad {@link BloqueFormulario}.
   * @throws BloqueFormularioNotFoundException Si no existe ningún
   *                                           {@link BloqueFormulario} con ese
   *                                           id.
   */
  public BloqueFormulario findById(final Long id) throws BloqueFormularioNotFoundException {
    log.debug("Petición a get BloqueFormulario : {}  - start", id);
    final BloqueFormulario BloqueFormulario = bloqueFormularioRepository.findById(id)
        .orElseThrow(() -> new BloqueFormularioNotFoundException(id));
    log.debug("Petición a get BloqueFormulario : {}  - end", id);
    return BloqueFormulario;

  }

  /**
   * Elimina una entidad {@link BloqueFormulario} por id.
   *
   * @param id el id de la entidad {@link BloqueFormulario}.
   */
  @Transactional
  public void delete(Long id) throws BloqueFormularioNotFoundException {
    log.debug("Petición a delete BloqueFormulario : {}  - start", id);
    Assert.notNull(id, "El id de BloqueFormulario no puede ser null.");
    if (!bloqueFormularioRepository.existsById(id)) {
      throw new BloqueFormularioNotFoundException(id);
    }
    bloqueFormularioRepository.deleteById(id);
    log.debug("Petición a delete BloqueFormulario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link BloqueFormulario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de BloqueFormulario: {} - start");
    bloqueFormularioRepository.deleteAll();
    log.debug("Petición a deleteAll de BloqueFormulario: {} - end");

  }

  /**
   * Actualiza los datos del {@link BloqueFormulario}.
   * 
   * @param bloqueFormularioActualizar {@link BloqueFormulario} con los datos
   *                                   actualizados.
   * @return El {@link BloqueFormulario} actualizado.
   * @throws BloqueFormularioNotFoundException Si no existe ningún
   *                                           {@link BloqueFormulario} con ese
   *                                           id.
   * @throws IllegalArgumentException          Si el {@link BloqueFormulario} no
   *                                           tiene id.
   */

  @Transactional
  public BloqueFormulario update(final BloqueFormulario bloqueFormularioActualizar) {
    log.debug("update(BloqueFormulario bloqueFormularioActualizar) - start");

    Assert.notNull(bloqueFormularioActualizar.getId(),
        "BloqueFormulario id no puede ser null para actualizar un bloqueFormulario");

    return bloqueFormularioRepository.findById(bloqueFormularioActualizar.getId()).map(bloqueFormulario -> {
      bloqueFormulario.setNombre(bloqueFormularioActualizar.getNombre());
      bloqueFormulario.setOrden(bloqueFormularioActualizar.getOrden());
      bloqueFormulario.setFormulario(bloqueFormularioActualizar.getFormulario());
      bloqueFormulario.setActivo(bloqueFormularioActualizar.getActivo());

      BloqueFormulario returnValue = bloqueFormularioRepository.save(bloqueFormulario);
      log.debug("update(BloqueFormulario bloqueFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new BloqueFormularioNotFoundException(bloqueFormularioActualizar.getId()));
  }

  /**
   * Recupera los bloques formularios de un comité.
   * 
   * @param idComite         Identificador de {@link Comite}.
   * @param idTipoEvaluacion Identificador de {@link TipoEvaluacion}.
   * @param paging           Datos de la paginación.
   * @return lista paginada {@link BloqueFormulario}.
   */
  @Override
  public Page<BloqueFormulario> findByComiteAndTipoEvaluacion(Long idComite, Pageable paging, Long idTipoEvaluacion) {
    log.debug("findByComite(Long idComite) - start");

    Assert.notNull(idComite,
        "El identificador de comité no puede ser null para recuperar los bloques de formulario asociados.");

    Assert.notNull(idTipoEvaluacion,
        "El identificador del tipo de Evaluación no puede ser null para recuperar los bloques de formulario asociados.");

    if (!tipoEvaluacionRepository.existsById(idTipoEvaluacion)) {
      throw new TipoEvaluacionNotFoundException(idTipoEvaluacion);
    }

    List<Long> formulariosIds = new ArrayList<>();

    switch (idTipoEvaluacion.intValue()) {
      case 1: {
        // Tipo Evaluación Retrospectiva

        // Se recuperan los ids de los formularios que se encuentran activos para el
        // comité que se recibe por parámetro (tiene que estar activo) cuyo formulario
        // sea del tipo 6 - > Retrospectiva
        formulariosIds = comiteFormularioRepository
            .findByComiteIdAndComiteActivoTrueAndFormularioActivoTrueAndFormularioIdIn(idComite, Arrays.asList(6L),
                paging)
            .stream().map(comiteFormulario -> comiteFormulario.getFormulario().getId()).collect(Collectors.toList());
        break;
      }
      case 2: {
        // Tipo Evaluación Memoria

        // Se recuperan los ids de los formularios que se encuentran activos para el
        // comité que se recibe por parámetro (tiene que estar activo) cuyo formulario
        // sea del tipo 1 - > M10 , 2 -> M20 y 3 -> M30
        formulariosIds = comiteFormularioRepository
            .findByComiteIdAndComiteActivoTrueAndFormularioActivoTrueAndFormularioIdIn(idComite,
                Arrays.asList(1L, 2L, 3L), paging)
            .stream().map(comiteFormulario -> comiteFormulario.getFormulario().getId()).collect(Collectors.toList());
        break;
      }
      case 3: {
        // Tipo Evaluación Seguimiento Anual

        // Se recuperan los ids de los formularios que se encuentran activos para el
        // comité que se recibe por parámetro (tiene que estar activo) cuyo formulario
        // sea del tipo 4 - > Seguimiento Anual
        formulariosIds = comiteFormularioRepository
            .findByComiteIdAndComiteActivoTrueAndFormularioActivoTrueAndFormularioIdIn(idComite, Arrays.asList(4L),
                paging)
            .stream().map(comiteFormulario -> comiteFormulario.getFormulario().getId()).collect(Collectors.toList());
        break;
      }
      case 4: {
        // Se recuperan los ids de los formularios que se encuentran activos para el
        // comité que se recibe por parámetro (tiene que estar activo) cuyo formulario
        // sea del tipo 5 - > Seguimiento Final
        formulariosIds = comiteFormularioRepository
            .findByComiteIdAndComiteActivoTrueAndFormularioActivoTrueAndFormularioIdIn(idComite, Arrays.asList(5L),
                paging)
            .stream().map(comiteFormulario -> comiteFormulario.getFormulario().getId()).collect(Collectors.toList());
        break;
      }
    }

    if (CollectionUtils.isEmpty(formulariosIds)) {
      throw new ComiteFormularioNotFoundException(idComite);
    }

    Specification<BloqueFormulario> specActivos = BloqueFormularioSpecifications.activos();

    Specification<BloqueFormulario> specFormularioIds = BloqueFormularioSpecifications.formularioIdsIn(formulariosIds);

    Specification<BloqueFormulario> specs = Specification.where(specActivos).and(specFormularioIds);

    Page<BloqueFormulario> returnValue = bloqueFormularioRepository.findAll(specs, paging);

    log.debug("findByComite(Long idComite) - end");

    return returnValue;
  }

}
