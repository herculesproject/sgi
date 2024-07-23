package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.RetrospectivaDto;

import lombok.Getter;

@Getter
public class RetrospectivaObject {
  private Long id;
  private EstadoRetrospectivaObject estadoRetrospectiva;
  private Instant fechaRetrospectiva;

  public RetrospectivaObject(RetrospectivaDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public RetrospectivaObject(RetrospectivaDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      if (dto.getEstadoRetrospectiva() != null) {
        this.estadoRetrospectiva = new EstadoRetrospectivaObject(dto.getEstadoRetrospectiva(), lang);
      }
      this.fechaRetrospectiva = dto.getFechaRetrospectiva();
    }
  }
}
