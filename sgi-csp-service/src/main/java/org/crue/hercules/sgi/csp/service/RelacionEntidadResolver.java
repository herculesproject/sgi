package org.crue.hercules.sgi.csp.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.pii.InvencionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionEntidadOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoProyectoSgeSpecifications;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiPiiService;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Resuelve la representación mínima ({@link RelacionEntidadOutput}) de una
 * entidad relacionada a partir de su tipo e identificador.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RelacionEntidadResolver {

  private final ConvocatoriaRepository convocatoriaRepository;
  private final ProyectoRepository proyectoRepository;
  private final GrupoRepository grupoRepository;
  private final ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  private final SgiApiPiiService sgiApiPiiService;

  /**
   * Resuelve la entidad relacionada del tipo e identificador indicados.
   *
   * @param tipoEntidad tipo de la entidad relacionada
   * @param entidadId   identificador de la entidad relacionada
   * @return la representación mínima de la entidad, o {@code null} si los
   *         parámetros son nulos
   */
  public RelacionEntidadOutput resolve(TipoEntidad tipoEntidad, Long entidadId) {
    log.debug("resolve - tipoEntidad {}, entidadId - {}", tipoEntidad, entidadId);
    if (tipoEntidad == null || entidadId == null) {
      return null;
    }

    switch (tipoEntidad) {
      case CONVOCATORIA:
        return resolveConvocatoria(entidadId);
      case PROYECTO:
        return resolveProyecto(entidadId);
      case GRUPO:
        return resolveGrupo(entidadId);
      case INVENCION:
        return resolveInvencion(entidadId);
      default:
        return RelacionEntidadOutput.builder()
            .id(entidadId)
            .build();
    }
  }

  private RelacionEntidadOutput resolveConvocatoria(Long id) {
    Convocatoria convocatoria = convocatoriaRepository.findById(id).orElse(null);
    if (convocatoria == null) {
      return RelacionEntidadOutput.builder()
          .id(id)
          .build();
    }

    return RelacionEntidadOutput.builder()
        .id(convocatoria.getId())
        .titulo(toI18nDto(convocatoria.getTitulo()))
        .build();
  }

  private RelacionEntidadOutput resolveProyecto(Long id) {
    Proyecto proyecto = proyectoRepository.findById(id).orElse(null);
    if (proyecto == null) {
      return RelacionEntidadOutput.builder()
          .id(id)
          .build();
    }

    String codigosSge = proyectoProyectoSgeRepository
        .findAll(ProyectoProyectoSgeSpecifications.byProyectoId(id)).stream()
        .map(ProyectoProyectoSge::getProyectoSgeRef)
        .collect(Collectors.joining(", "));

    return RelacionEntidadOutput.builder()
        .id(proyecto.getId())
        .titulo(toI18nDto(proyecto.getTitulo()))
        .codigoExterno(proyecto.getCodigoExterno())
        .codigosSge(codigosSge)
        .build();
  }

  private RelacionEntidadOutput resolveGrupo(Long id) {
    Grupo grupo = grupoRepository.findById(id).orElse(null);
    if (grupo == null) {
      return RelacionEntidadOutput.builder()
          .id(id)
          .build();
    }

    return RelacionEntidadOutput.builder()
        .id(grupo.getId())
        .titulo(toI18nDto(grupo.getNombre()))
        .codigosSge(grupo.getProyectoSgeRef())
        .build();
  }

  private RelacionEntidadOutput resolveInvencion(Long id) {
    InvencionOutput invencion = sgiApiPiiService.findInvencionById(id);
    if (invencion == null) {
      return RelacionEntidadOutput.builder()
          .id(id)
          .build();
    }

    return RelacionEntidadOutput.builder()
        .id(invencion.getId())
        .titulo(invencion.getTitulo())
        .build();
  }

  private List<I18nFieldValueDto> toI18nDto(Collection<? extends I18nFieldValue> values) {
    if (values == null) {
      return List.of();
    }

    return values.stream()
        .map(value -> new I18nFieldValueDto(value.getLang(), value.getValue()))
        .toList();
  }

}
