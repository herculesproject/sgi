package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.RetrospectivaDto;

import lombok.Getter;

@Getter
public class MemoriaObject {
  private Long id;
  private String numReferencia;
  private PeticionEvaluacionObject peticionEvaluacion;
  private ComiteObject comite;
  private String titulo;
  private String personaRef;
  private TipoMemoriaObject tipoMemoria;
  private TipoEstadoMemoriaObject estadoActual;
  private Instant fechaEnvioSecretaria;
  private Boolean requiereRetrospectiva;
  private RetrospectivaDto retrospectiva;
  private Integer version;
  private Boolean activo;
  private MemoriaObject memoriaOriginal;

  public MemoriaObject(MemoriaDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public MemoriaObject(MemoriaDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.numReferencia = dto.getNumReferencia();
      if (dto.getPeticionEvaluacion() != null) {
        this.peticionEvaluacion = new PeticionEvaluacionObject(dto.getPeticionEvaluacion(), lang);
      }
      if (dto.getComite() != null) {
        this.comite = new ComiteObject(dto.getComite(), lang);
      }
      this.titulo = dto.getTitulo();
      this.personaRef = dto.getPersonaRef();
      if (dto.getTipoMemoria() != null) {
        this.tipoMemoria = new TipoMemoriaObject(dto.getTipoMemoria(), lang);
      }
      if (dto.getEstadoActual() != null) {
        this.estadoActual = new TipoEstadoMemoriaObject(dto.getEstadoActual(), lang);
      }
      this.fechaEnvioSecretaria = dto.getFechaEnvioSecretaria();
      this.requiereRetrospectiva = dto.getRequiereRetrospectiva();
      this.version = dto.getVersion();
      this.activo = dto.getActivo();
      if (dto.getMemoriaOriginal() != null) {
        this.memoriaOriginal = new MemoriaObject(dto.getMemoriaOriginal(), lang);
      }
    }
  }
}
