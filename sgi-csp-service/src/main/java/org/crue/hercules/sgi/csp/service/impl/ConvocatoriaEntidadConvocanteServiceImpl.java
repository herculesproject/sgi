package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadConvocanteSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
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
 * Service Implementation para la gestión de
 * {@link ConvocatoriaEntidadConvocante}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaEntidadConvocanteServiceImpl implements ConvocatoriaEntidadConvocanteService {

  private final ConvocatoriaEntidadConvocanteRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ProgramaRepository programaRepository;

  public ConvocatoriaEntidadConvocanteServiceImpl(
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      ConvocatoriaRepository convocatoriaRepository, ProgramaRepository programaRepository) {
    this.repository = convocatoriaEntidadConvocanteRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.programaRepository = programaRepository;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocante la entidad
   *                                      {@link ConvocatoriaEntidadConvocante} a
   *                                      guardar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadConvocante create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) {
    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - start");

    Assert.isNull(convocatoriaEntidadConvocante.getId(),
        "ConvocatoriaEntidadConvocante id tiene que ser null para crear un nuevo ConvocatoriaEntidadConvocante");

    Assert.notNull(convocatoriaEntidadConvocante.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaEntidadGestora");

    convocatoriaEntidadConvocante.setConvocatoria(
        convocatoriaRepository.findById(convocatoriaEntidadConvocante.getConvocatoria().getId()).orElseThrow(
            () -> new ConvocatoriaNotFoundException(convocatoriaEntidadConvocante.getConvocatoria().getId())));

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadConvocante.getConvocatoria().getId(),
            convocatoriaEntidadConvocante.getEntidadRef()).isPresent(),
        "Ya existe una asociación activa para esa Convocatoria y Entidad");

    if (convocatoriaEntidadConvocante.getPrograma() != null) {
      if (convocatoriaEntidadConvocante.getPrograma().getId() == null) {
        convocatoriaEntidadConvocante.setPrograma(null);
      } else {
        convocatoriaEntidadConvocante
            .setPrograma(programaRepository.findById(convocatoriaEntidadConvocante.getPrograma().getId())
                .orElseThrow(() -> new ProgramaNotFoundException(convocatoriaEntidadConvocante.getPrograma().getId())));
        Assert.isTrue(convocatoriaEntidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
      }
    }

    ConvocatoriaEntidadConvocante returnValue = repository.save(convocatoriaEntidadConvocante);

    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocanteActualizar la entidad
   *                                                {@link ConvocatoriaEntidadConvocante}
   *                                                a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadConvocante update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) {
    log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) - start");

    Assert.notNull(convocatoriaEntidadConvocanteActualizar.getId(),
        "ConvocatoriaEntidadConvocante id no puede ser null para actualizar un ConvocatoriaEntidadConvocante");

    return repository.findById(convocatoriaEntidadConvocanteActualizar.getId()).map(convocatoriaEntidadConvocante -> {
      repository.findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadConvocanteActualizar.getConvocatoria().getId(),
          convocatoriaEntidadConvocanteActualizar.getEntidadRef()).ifPresent(convocatoriaR -> {
            Assert.isTrue(convocatoriaEntidadConvocante.getId() == convocatoriaR.getId(),
                "Ya existe una asociación activa para esa Convocatoria y Entidad");
          });

      if (convocatoriaEntidadConvocanteActualizar.getPrograma() != null) {
        if (convocatoriaEntidadConvocanteActualizar.getPrograma().getId() == null) {
          convocatoriaEntidadConvocanteActualizar.setPrograma(null);
        } else {
          convocatoriaEntidadConvocanteActualizar.setPrograma(
              programaRepository.findById(convocatoriaEntidadConvocanteActualizar.getPrograma().getId()).orElseThrow(
                  () -> new ProgramaNotFoundException(convocatoriaEntidadConvocanteActualizar.getPrograma().getId())));
          Assert.isTrue(convocatoriaEntidadConvocanteActualizar.getPrograma().getActivo(),
              "El Programa debe estar Activo");
        }
      }

      convocatoriaEntidadConvocante.setPrograma(convocatoriaEntidadConvocanteActualizar.getPrograma());

      ConvocatoriaEntidadConvocante returnValue = repository.save(convocatoriaEntidadConvocante);
      log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) - end");
      return returnValue;
    }).orElseThrow(
        () -> new ConvocatoriaEntidadConvocanteNotFoundException(convocatoriaEntidadConvocanteActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaEntidadConvocante}.
   *
   * @param id Id del {@link ConvocatoriaEntidadConvocante}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaEntidadConvocante id no puede ser null para desactivar un ConvocatoriaEntidadConvocante");

    if (!repository.existsById(id)) {
      throw new ConvocatoriaEntidadConvocanteNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaEntidadConvocante} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadConvocante}.
   * @return la entidad {@link ConvocatoriaEntidadConvocante}.
   */
  @Override
  public ConvocatoriaEntidadConvocante findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaEntidadConvocante returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaEntidadConvocanteNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaEntidadConvocante> findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConvocatoriaEntidadConvocante> specByQuery = new QuerySpecification<ConvocatoriaEntidadConvocante>(
        query);
    Specification<ConvocatoriaEntidadConvocante> specByConvocatoria = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(idConvocatoria);

    Specification<ConvocatoriaEntidadConvocante> specs = Specification.where(specByConvocatoria).and(specByQuery);

    Page<ConvocatoriaEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
