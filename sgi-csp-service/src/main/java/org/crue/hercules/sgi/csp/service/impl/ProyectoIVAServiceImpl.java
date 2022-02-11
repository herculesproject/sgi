package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoIVARepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoIVAService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoIVA}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoIVAServiceImpl implements ProyectoIVAService {

  private final ProyectoIVARepository repository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;

  public ProyectoIVAServiceImpl(ProyectoIVARepository repository, ProyectoEquipoRepository proyectoEquipoRepository) {
    this.repository = repository;
    this.proyectoEquipoRepository = proyectoEquipoRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoIVA}.
   * 
   * @param proyectoIVA la entidad {@link ProyectoIVA} a guardar.
   * @return ProyectoIVA la entidad {@link ProyectoIVA} persistida.
   */
  @Override
  @Transactional
  public ProyectoIVA create(ProyectoIVA proyectoIVA) {
    log.debug("create(ProyectoIVA proyectoIVA) - start");

    ProyectoIVA returnValue = repository.save(proyectoIVA);

    log.debug("create(ProyectoIVA proyectoIVA) - end");
    return returnValue;

  }

  @Override
  public Page<ProyectoIVA> findAllByProyectoIdOrderByIdDesc(Long proyectoId, Pageable pageable) {
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - start");

    if (ProyectoHelper.hasUserAuthorityInvestigador() && !checkUserPresentInEquipos(proyectoId)) {
      throw new UserNotAuthorizedToAccessProyectoException();
    }

    Page<ProyectoIVA> returnValue = repository.findAllByProyectoIdAndIvaIsNotNullOrderByIdDesc(proyectoId, pageable);

    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si el usuario actual está presente en el equipo
   * 
   * @throws {@link UserNotAuthorizedToAccessProyectoException}
   */
  private boolean checkUserPresentInEquipos(Long proyectoId) {
    Long numeroProyectoEquipo = this.proyectoEquipoRepository
        .count(ProyectoEquipoSpecifications.byProyectoId(proyectoId)
            .and(ProyectoEquipoSpecifications.byPersonaRef(ProyectoHelper.getUserPersonaRef())));
    return numeroProyectoEquipo > 0;
  }
}
