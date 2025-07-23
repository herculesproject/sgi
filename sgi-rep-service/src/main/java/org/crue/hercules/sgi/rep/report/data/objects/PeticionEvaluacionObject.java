package org.crue.hercules.sgi.rep.report.data.objects;

import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto.EstadoFinanciacion;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto.TipoValorSocial;

import lombok.Getter;

@Getter
public class PeticionEvaluacionObject {
  private Long id;
  private String solicitudConvocatoriaRef;
  private String codigo;
  private String titulo;
  private TipoActividadObject tipoActividad;
  private TipoInvestigacionTuteladaObject tipoInvestigacionTutelada;
  private Boolean existeFinanciacion;
  private String fuenteFinanciacion;
  private EstadoFinanciacion estadoFinanciacion;
  private BigDecimal importeFinanciacion;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String resumen;
  private TipoValorSocial valorSocial;
  private String otroValorSocial;
  private String objetivos;
  private String disMetodologico;
  private Boolean externo;
  private Boolean tieneFondosPropios;
  private String personaRef;
  private Long checklistId;
  private String tutorRef;
  private Boolean activo;

  public PeticionEvaluacionObject(PeticionEvaluacionDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public PeticionEvaluacionObject(PeticionEvaluacionDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.solicitudConvocatoriaRef = dto.getSolicitudConvocatoriaRef();
      this.codigo = dto.getCodigo();
      this.titulo = I18nHelper.getFieldValue(dto.getTitulo(), lang);
      if (dto.getTipoActividad() != null) {
        this.tipoActividad = new TipoActividadObject(dto.getTipoActividad(), lang);
      }
      if (dto.getTipoInvestigacionTutelada() != null) {
        this.tipoInvestigacionTutelada = new TipoInvestigacionTuteladaObject(dto.getTipoInvestigacionTutelada(), lang);
      }
      this.existeFinanciacion = dto.getExisteFinanciacion();
      this.fuenteFinanciacion = dto.getFuenteFinanciacion();
      this.estadoFinanciacion = dto.getEstadoFinanciacion();
      this.importeFinanciacion = dto.getImporteFinanciacion();
      this.fechaInicio = dto.getFechaInicio();
      this.fechaFin = dto.getFechaFin();
      this.resumen = I18nHelper.getFieldValue(dto.getResumen(), lang);
      this.valorSocial = dto.getValorSocial();
      if (dto.getOtroValorSocial() != null) {
        this.otroValorSocial = I18nHelper.getFieldValue(dto.getOtroValorSocial(), lang);
      }
      if (dto.getObjetivos() != null) {
        this.objetivos = I18nHelper.getFieldValue(dto.getObjetivos(), lang);
      }
      if (dto.getDisMetodologico() != null) {
        this.disMetodologico = I18nHelper.getFieldValue(dto.getDisMetodologico(), lang);
      }
      this.externo = dto.getExterno();
      this.tieneFondosPropios = dto.getTieneFondosPropios();
      this.personaRef = dto.getPersonaRef();
      this.checklistId = dto.getChecklistId();
      this.tutorRef = dto.getTutorRef();
      this.activo = dto.getActivo();
    }
  }
}
