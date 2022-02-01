package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.model.Acreditacion_;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.crue.hercules.sgi.prc.model.Proyecto_;
import org.crue.hercules.sgi.prc.validation.FirmaOrPersonaRefOrNombreAndApellidosAutor;
import org.crue.hercules.sgi.prc.validation.UniqueElementsByFields;
import org.crue.hercules.sgi.prc.validation.UrlOrDocumentoRefAcreditacion;
import org.crue.hercules.sgi.prc.validation.ValorFormat;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProduccionCientificaApiInput implements Serializable {

  @Valid
  @NotEmpty
  @UniqueElementsByFields(fieldsNames = CampoProduccionCientifica_.CODIGO_CV_N)
  private List<CampoProduccionCientificaInput> campos;

  @Valid
  @NotEmpty
  @UniqueElementsByFields(fieldsNames = Autor_.FIRMA)
  @UniqueElementsByFields(fieldsNames = Autor_.PERSONA_REF)
  @UniqueElementsByFields(fieldsNames = { Autor_.NOMBRE, Autor_.APELLIDOS })
  private List<AutorInput> autores;

  @Valid
  @UniqueElements
  private List<IndiceImpactoInput> indicesImpacto;

  @Valid
  @UniqueElementsByFields(fieldsNames = Acreditacion_.DOCUMENTO_REF)
  @UniqueElementsByFields(fieldsNames = Acreditacion_.URL)
  private List<AcreditacionInput> acreditaciones;

  @Valid
  @UniqueElementsByFields(fieldsNames = Proyecto_.PROYECTO_REF)
  private List<ProyectoInput> proyectos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @ValorFormat
  public static class CampoProduccionCientificaInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @Size(max = BaseEntity.CAMPO_CVN_LENGTH)
    private String codigo;

    @NotEmpty
    @UniqueElements
    private List<String> valores;

    @JsonIgnore
    private CodigoCVN codigoCVN;

    public void setCodigo(String codigo) {
      this.codigo = codigo;
      if (StringUtils.hasText(codigo)) {
        this.codigoCVN = CodigoCVN.getByInternValue(codigo);
      }
    }

    public void setCodigoCVN(CodigoCVN codigoCVN) {
      if (null != codigoCVN) {
        this.codigo = codigoCVN.getInternValue();
      }
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @FirmaOrPersonaRefOrNombreAndApellidosAutor
  public static class AutorInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = BaseEntity.FIRMA_LENGTH)
    private String firma;

    @Size(max = BaseEntity.PERSONA_REF_LENGTH)
    private String personaRef;

    @Size(max = BaseEntity.NOMBRE_LENGTH)
    private String nombre;

    @Size(max = BaseEntity.APELLIDOS_LENGTH)
    private String apellidos;

    @NotNull
    private Integer orden;

    @NotEmpty
    private String orcidId;

    private Boolean ip;

  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  public static class IndiceImpactoInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @Size(max = BaseEntity.TIPO_FUENTE_IMPACTO_LENGTH)
    private String tipoFuenteImpacto;

    @JsonIgnore
    private TipoFuenteImpacto fuenteImpacto;

    private TipoRanking ranking;

    @NotNull
    private Integer anio;

    @Size(max = BaseEntity.OTRA_FUENTE_IMPACTO_LENGTH)
    private String otraFuenteImpacto;

    private BigDecimal posicionPublicacion;

    private BigDecimal numeroRevistas;

    private Boolean revista25;

    public void setTipoFuenteImpacto(String tipoFuenteImpacto) {
      this.tipoFuenteImpacto = tipoFuenteImpacto;
      if (StringUtils.hasText(tipoFuenteImpacto)) {
        this.fuenteImpacto = TipoFuenteImpacto.getByInternValue(tipoFuenteImpacto);
      }
    }

    public void setFuenteImpacto(TipoFuenteImpacto fuenteImpacto) {
      if (null != fuenteImpacto) {
        this.tipoFuenteImpacto = fuenteImpacto.getInternValue();
      }
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @UrlOrDocumentoRefAcreditacion
  public static class AcreditacionInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = BaseEntity.DOCUMENTO_REF_LENGTH)
    private String documentoRef;

    @Size(max = BaseEntity.URL_LENGTH)
    private String url;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProyectoInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long proyectoRef;
  }
}
