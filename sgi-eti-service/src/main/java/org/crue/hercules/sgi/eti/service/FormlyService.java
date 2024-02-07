package org.crue.hercules.sgi.eti.service;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.crue.hercules.sgi.eti.exceptions.FormlyNotFoundException;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.repository.FormlyRepository;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiCnfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Reglas de negocio de la entidad Formly.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class FormlyService {
  FormlyRepository repository;

  SgiApiCnfService configService;

  public FormlyService(FormlyRepository repository, SgiApiCnfService configService) {
    this.repository = repository;
    this.configService = configService;
  }

  /**
   * Recupera un Formly a partir de su identificador.
   * 
   * @param id Identificador del Formly a recuperar
   * @return Formly El Formly con el id solicitado
   */
  public Formly getById(Long id) {
    log.debug("getById(Long id) - start");
    final Formly returnValue = repository.findById(id).orElseThrow(() -> new FormlyNotFoundException(id));
    log.debug("getById(Long id) - end");
    return returnValue;
  }

  /**
   * Recupera la última versión (versión actual) del Formly con el nombre
   * solicitado.
   * 
   * @param nombre Nombre del Formly a recuperar
   * @return Formly La última versión del Formly con el nombre solicitado.
   */
  public Formly findFirstByOrderByVersionDesc(String nombre) {
    log.debug("findFirstOrderByVersionDesc() - start");
    final Formly returnValue = repository.findFirstByOrderByVersionDesc()
        .orElseThrow(() -> new FormlyNotFoundException());
    boolean hasFormlyNombre = false;
    if (!ObjectUtils.isEmpty(returnValue) && CollectionUtils.isNotEmpty(returnValue.getFormlyNombres())) {
      hasFormlyNombre = returnValue.getFormlyNombres().stream().anyMatch(f -> f.getNombre().equalsIgnoreCase(nombre));
    }
    log.debug("findFirstOrderByVersionDesc() - end");
    return hasFormlyNombre ? returnValue : null;
  }

}
