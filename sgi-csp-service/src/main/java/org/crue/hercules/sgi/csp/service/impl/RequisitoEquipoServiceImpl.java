package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RequisitoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RequisitoEquipoServiceImpl implements RequisitoEquipoService {

  private final RequisitoEquipoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public RequisitoEquipoServiceImpl(RequisitoEquipoRepository repository,
      ConvocatoriaRepository convocatoriaRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Guarda la entidad {@link RequisitoEquipo}.
   * 
   * @param requisitoEquipo la entidad {@link RequisitoEquipo} a guardar.
   * @return RequisitoEquipo la entidad {@link RequisitoEquipo} persistida.
   */
  @Override
  @Transactional
  public RequisitoEquipo create(RequisitoEquipo requisitoEquipo) {
    log.debug("create(RequisitoEquipo requisitoEquipo) - start");

    Assert.isNull(requisitoEquipo.getId(), "Id tiene que ser null para crear RequisitoEquipo");

    Assert.notNull(requisitoEquipo.getConvocatoria(), "La Convocatoria no puede ser null para crear RequisitoEquipo");

    Assert.notNull(requisitoEquipo.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para crear RequisitoEquipo");

    requisitoEquipo.setConvocatoria(convocatoriaRepository.findById(requisitoEquipo.getConvocatoria().getId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(requisitoEquipo.getConvocatoria().getId())));

    RequisitoEquipo returnValue = repository.save(requisitoEquipo);

    log.debug("create(RequisitoEquipo requisitoEquipo) - end");
    return returnValue;

  }

  /**
   * Actualiza la entidad {@link RequisitoEquipo} por {@link Convocatoria}
   * 
   * @param requisitoEquipoActualizar la entidad {@link RequisitoEquipo} a
   *                                  guardar.
   * @param convocatoriaId            Identificador de la {@link Convocatoria}
   * @return RequisitoEquipo la entidad {@link RequisitoEquipo} persistida.
   */
  @Override
  @Transactional
  public RequisitoEquipo update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId) {
    log.debug("update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId) - start");

    Assert.notNull(convocatoriaId, "La Convocatoria no puede ser null para actualizar RequisitoEquipo");

    requisitoEquipoActualizar.setConvocatoria(convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId)));

    return repository.findByConvocatoriaId(convocatoriaId).map(requisitoEquipo -> {
      requisitoEquipo.setAniosNivelAcademico(requisitoEquipoActualizar.getAniosNivelAcademico());
      requisitoEquipo.setAniosVinculacion(requisitoEquipoActualizar.getAniosVinculacion());
      requisitoEquipo.setEdadMaxima(requisitoEquipoActualizar.getEdadMaxima());
      requisitoEquipo.setRatioMujeres(requisitoEquipoActualizar.getRatioMujeres());
      requisitoEquipo.setModalidadContratoRef(requisitoEquipoActualizar.getModalidadContratoRef());
      requisitoEquipo.setNivelAcademicoRef(requisitoEquipoActualizar.getNivelAcademicoRef());
      requisitoEquipo.setNumMaximoCompetitivosActivos(requisitoEquipoActualizar.getNumMaximoCompetitivosActivos());
      requisitoEquipo.setNumMaximoNoCompetitivosActivos(requisitoEquipoActualizar.getNumMaximoNoCompetitivosActivos());
      requisitoEquipo.setNumMinimoCompetitivos(requisitoEquipoActualizar.getNumMinimoCompetitivos());
      requisitoEquipo.setNumMinimoNoCompetitivos(requisitoEquipoActualizar.getNumMinimoNoCompetitivos());
      requisitoEquipo.setOtrosRequisitos(requisitoEquipoActualizar.getOtrosRequisitos());
      requisitoEquipo.setVinculacionUniversidad(requisitoEquipoActualizar.getVinculacionUniversidad());

      RequisitoEquipo returnValue = repository.save(requisitoEquipo);
      log.debug("update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId) - end");
      return returnValue;
    }).orElseThrow(() -> new RequisitoEquipoNotFoundException(requisitoEquipoActualizar.getId()));

  }

  /**
   * Obtiene el {@link RequisitoEquipo} para una {@link Convocatoria}.
   *
   * @param id el id de la {@link Convocatoria}.
   * @return la entidad {@link RequisitoEquipo} de la {@link Convocatoria}
   */
  @Override
  public RequisitoEquipo findByConvocatoriaId(Long id) {
    log.debug("findByConvocatoriaId(Long id) - start");

    if (convocatoriaRepository.existsById(id)) {
      final Optional<RequisitoEquipo> returnValue = repository.findByConvocatoriaId(id);
      log.debug("findByConvocatoriaId(Long id) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new ConvocatoriaNotFoundException(id);
    }
  }

}
