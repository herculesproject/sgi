package org.crue.hercules.sgi.csp.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.AreaTematicaArbolNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ListadoAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaArbolRepository;
import org.crue.hercules.sgi.csp.repository.ListadoAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.specification.AreaTematicaArbolSpecifications;
import org.crue.hercules.sgi.csp.service.AreaTematicaArbolService;
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
 * Service Implementation para la gestión de {@link AreaTematicaArbol}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class AreaTematicaArbolServiceImpl implements AreaTematicaArbolService {

  private final AreaTematicaArbolRepository repository;
  private final ListadoAreaTematicaRepository listadoAreaTematicaRepository;

  public AreaTematicaArbolServiceImpl(AreaTematicaArbolRepository areaTematicaArbolRepository,
      ListadoAreaTematicaRepository listadoAreaTematicaRepository) {
    this.repository = areaTematicaArbolRepository;
    this.listadoAreaTematicaRepository = listadoAreaTematicaRepository;
  }

  /**
   * Guardar un nuevo {@link AreaTematicaArbol}.
   *
   * @param areaTematicaArbol la entidad {@link AreaTematicaArbol} a guardar.
   * @return la entidad {@link AreaTematicaArbol} persistida.
   */
  @Override
  @Transactional
  public AreaTematicaArbol create(AreaTematicaArbol areaTematicaArbol) {
    log.debug("create(AreaTematicaArbol areaTematicaArbol) - start");

    Assert.isNull(areaTematicaArbol.getId(),
        "AreaTematicaArbol id tiene que ser null para crear un nuevo AreaTematicaArbol");
    Assert.notNull(areaTematicaArbol.getListadoAreaTematica().getId(),
        "Id ListadoAreaTematica no puede ser null para crear un AreaTematicaArbol");

    Assert.isTrue(
        !(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
            areaTematicaArbol.getListadoAreaTematica().getId()).isPresent()),
        "Ya existe un AreaTematicaArbol con la abreviatura " + areaTematicaArbol.getAbreviatura()
            + " en el ListadoAreaTematica");
    Assert.isTrue(
        !(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbol.getNombre(),
            areaTematicaArbol.getListadoAreaTematica().getId()).isPresent()),
        "Ya existe un AreaTematicaArbol con el nombre " + areaTematicaArbol.getNombre() + " en el ListadoAreaTematica");

    areaTematicaArbol.setListadoAreaTematica(
        listadoAreaTematicaRepository.findById(areaTematicaArbol.getListadoAreaTematica().getId()).orElseThrow(
            () -> new ListadoAreaTematicaNotFoundException(areaTematicaArbol.getListadoAreaTematica().getId())));

    if (areaTematicaArbol.getPadre() != null) {
      if (areaTematicaArbol.getPadre().getId() == null) {
        areaTematicaArbol.setPadre(null);
      } else {
        areaTematicaArbol.setPadre(repository.findById(areaTematicaArbol.getPadre().getId())
            .orElseThrow(() -> new AreaTematicaArbolNotFoundException(areaTematicaArbol.getPadre().getId())));
      }
    }

    areaTematicaArbol.setActivo(true);
    AreaTematicaArbol returnValue = repository.save(areaTematicaArbol);

    log.debug("create(AreaTematicaArbol areaTematicaArbol) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link AreaTematicaArbol} y si se pone activo a false hace lo
   * mismo con todos sus hijos en cascada.
   *
   * @param areaTematicaArbolActualizar la entidad {@link AreaTematicaArbol} a
   *                                    actualizar.
   * @return la entidad {@link AreaTematicaArbol} persistida.
   */
  @Override
  @Transactional
  public AreaTematicaArbol update(AreaTematicaArbol areaTematicaArbolActualizar) {
    log.debug("update(AreaTematicaArbol areaTematicaArbolActualizar) - start");

    Assert.notNull(areaTematicaArbolActualizar.getId(),
        "AreaTematicaArbol id no puede ser null para actualizar un AreaTematicaArbol");

    if (areaTematicaArbolActualizar.getPadre() != null) {
      if (areaTematicaArbolActualizar.getPadre().getId() == null) {
        areaTematicaArbolActualizar.setPadre(null);
      } else {
        areaTematicaArbolActualizar.setPadre(repository.findById(areaTematicaArbolActualizar.getPadre().getId())
            .orElseThrow(() -> new AreaTematicaArbolNotFoundException(areaTematicaArbolActualizar.getPadre().getId())));
      }
    }

    return repository.findById(areaTematicaArbolActualizar.getId()).map(areaTematicaArbol -> {
      repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbolActualizar.getAbreviatura(),
          areaTematicaArbol.getListadoAreaTematica().getId()).ifPresent((areaTematicaArbolExistente) -> {
            Assert.isTrue(areaTematicaArbolActualizar.getId() == areaTematicaArbolExistente.getId(),
                "Ya existe un AreaTematicaArbol con la abreviatura " + areaTematicaArbolExistente.getAbreviatura()
                    + " en el ListadoAreaTematica");
          });

      repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbolActualizar.getNombre(),
          areaTematicaArbol.getListadoAreaTematica().getId()).ifPresent((tipoDocumentoExistente) -> {
            Assert.isTrue(areaTematicaArbolActualizar.getId() == tipoDocumentoExistente.getId(),
                "Ya existe un AreaTematicaArbol con el nombre " + tipoDocumentoExistente.getNombre()
                    + " en el ListadoAreaTematica");
          });

      areaTematicaArbol.setAbreviatura(areaTematicaArbolActualizar.getAbreviatura());
      areaTematicaArbol.setNombre(areaTematicaArbolActualizar.getNombre());
      areaTematicaArbol.setPadre(areaTematicaArbolActualizar.getPadre());
      areaTematicaArbol.setActivo(areaTematicaArbolActualizar.getActivo());

      // Si el areaTematicaArbol se pone activo=false se hace lo mismo con sus hijos
      if (!areaTematicaArbolActualizar.getActivo()) {
        List<AreaTematicaArbol> areaTematicaArbolesHijos = repository
            .findByPadreIdIn(Arrays.asList(areaTematicaArbol.getId()));

        while (!areaTematicaArbolesHijos.isEmpty()) {
          areaTematicaArbolesHijos.stream().map(areaTematicaArbolHijo -> {
            areaTematicaArbolHijo.setActivo(false);
            return areaTematicaArbolHijo;
          }).collect(Collectors.toList());

          repository.saveAll(areaTematicaArbolesHijos);

          areaTematicaArbolesHijos = repository.findByPadreIdIn(
              areaTematicaArbolesHijos.stream().map(AreaTematicaArbol::getId).collect(Collectors.toList()));
        }
      }

      AreaTematicaArbol returnValue = repository.save(areaTematicaArbol);
      log.debug("update(AreaTematicaArbol areaTematicaArbolActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaArbolNotFoundException(areaTematicaArbolActualizar.getId()));
  }

  /**
   * Desactiva el {@link AreaTematicaArbol} y todos sus hijos en cascada.
   *
   * @param id Id del {@link AreaTematicaArbol}.
   * @return la entidad {@link AreaTematicaArbol} persistida.
   */
  @Override
  @Transactional
  public AreaTematicaArbol disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "AreaTematicaArbol id no puede ser null para desactivar un AreaTematicaArbol");

    return repository.findById(id).map(areaTematicaArbol -> {
      areaTematicaArbol.setActivo(false);

      List<AreaTematicaArbol> areaTematicaArbolsHijos = repository
          .findByPadreIdIn(Arrays.asList(areaTematicaArbol.getId()));

      while (!areaTematicaArbolsHijos.isEmpty()) {
        areaTematicaArbolsHijos.stream().map(areaTematicaArbolHijo -> {
          areaTematicaArbolHijo.setActivo(false);
          return areaTematicaArbolHijo;
        }).collect(Collectors.toList());

        repository.saveAll(areaTematicaArbolsHijos);

        areaTematicaArbolsHijos = repository.findByPadreIdIn(
            areaTematicaArbolsHijos.stream().map(AreaTematicaArbol::getId).collect(Collectors.toList()));
      }

      AreaTematicaArbol returnValue = repository.save(areaTematicaArbol);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaArbolNotFoundException(id));
  }

  /**
   * Obtiene {@link AreaTematicaArbol} por su id.
   *
   * @param id el id de la entidad {@link AreaTematicaArbol}.
   * @return la entidad {@link AreaTematicaArbol}.
   */
  @Override
  public AreaTematicaArbol findById(Long id) {
    log.debug("findById(Long id)  - start");
    final AreaTematicaArbol returnValue = repository.findById(id)
        .orElseThrow(() -> new AreaTematicaArbolNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AreaTematicaArbol} activos para un
   * {@link ListadoAreaTematica}.
   *
   * @param idListadoAreaTematica el id de la entidad {@link ListadoAreaTematica}.
   * @param query                 la información del filtro.
   * @param pageable              la información de la paginación.
   * @return la lista de entidades {@link AreaTematicaArbol} del
   *         {@link ListadoAreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematicaArbol> findAllByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug(
        "findAllByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<AreaTematicaArbol> specByQuery = new QuerySpecification<AreaTematicaArbol>(query);
    Specification<AreaTematicaArbol> specByListadoAreaTematicaId = AreaTematicaArbolSpecifications
        .byListadoAreaTematicaId(idListadoAreaTematica);
    Specification<AreaTematicaArbol> specActivos = AreaTematicaArbolSpecifications.activos();

    Specification<AreaTematicaArbol> specs = Specification.where(specByListadoAreaTematicaId).and(specActivos)
        .and(specByQuery);

    Page<AreaTematicaArbol> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AreaTematicaArbol} para un {@link ListadoAreaTematica}.
   *
   * @param idListadoAreaTematica el id de la entidad {@link ListadoAreaTematica}.
   * @param query                 la información del filtro.
   * @param pageable              la información de la paginación.
   * @return la lista de entidades {@link AreaTematicaArbol} del
   *         {@link ListadoAreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematicaArbol> findAllTodosByListadoAreaTematica(Long idListadoAreaTematica,
      List<QueryCriteria> query, Pageable pageable) {
    log.debug(
        "findAllTodosByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<AreaTematicaArbol> specByQuery = new QuerySpecification<AreaTematicaArbol>(query);
    Specification<AreaTematicaArbol> specByListadoAreaTematicaId = AreaTematicaArbolSpecifications
        .byListadoAreaTematicaId(idListadoAreaTematica);

    Specification<AreaTematicaArbol> specs = Specification.where(specByListadoAreaTematicaId).and(specByQuery);

    Page<AreaTematicaArbol> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllTodosByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
