package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;

import lombok.Getter;

@Getter
public class MemoriaPeticionEvaluacionObject {
  private Long id;
  private String numReferencia;
  private String titulo;
  private ComiteObject comite;
  private TipoEstadoMemoriaObject estadoActual;
  private boolean requiereRetrospectiva;
  private RetrospectivaObject retrospectiva;
  private Instant fechaEvaluacion;
  private Instant fechaLimite;
  private boolean isResponsable;
  private boolean activo;

  public MemoriaPeticionEvaluacionObject(MemoriaPeticionEvaluacionDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public MemoriaPeticionEvaluacionObject(MemoriaPeticionEvaluacionDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.numReferencia = dto.getNumReferencia();
      this.titulo = I18nHelper.getFieldValue(dto.getTitulo(), lang);
      if (dto.getComite() != null) {
        this.comite = new ComiteObject(dto.getComite(), lang);
      }
      if (dto.getEstadoActual() != null) {
        this.estadoActual = new TipoEstadoMemoriaObject(dto.getEstadoActual(), lang);
      }
      this.requiereRetrospectiva = dto.isRequiereRetrospectiva();
      if (dto.getRetrospectiva() != null) {
        this.retrospectiva = new RetrospectivaObject(dto.getRetrospectiva(), lang);
      }
      this.fechaEvaluacion = dto.getFechaEvaluacion();
      this.fechaLimite = dto.getFechaLimite();
      this.isResponsable = dto.isResponsable();
      this.activo = dto.isActivo();
    }
  }
}
