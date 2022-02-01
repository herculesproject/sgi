package org.crue.hercules.sgi.prc.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = ConfiguracionBaremo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConfiguracionBaremo extends BaseActivableEntity implements Serializable {

  protected static final String TABLE_NAME = "configuracion_baremo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public enum TipoBaremo {
    AGRUPADOR,
    SEXENIO,
    COSTE_INDIRECTO,
    AUTORIA_BCI_EDITORIAL_EXTRANJERA,
    CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA,
    EDICION_BCI_EDITORIAL_EXTRANJERA,
    COMENTARIO_BCI_EDITORIAL_EXTRANJERA,
    AUTORIA_BCI_EDITORIAL_NACIONAL,
    CAP_LIBRO_BCI_EDITORIAL_NACIONAL,
    EDICION_BCI_EDITORIAL_NACIONAL,
    COMENTARIO_BCI_EDITORIAL_NACIONAL,
    AUTORIA_ICEE_Q1,
    CAP_LIBRO_ICEE_Q1,
    EDICION_ICEE_Q1,
    COMENTARIO_ICEE_Q1,
    AUTORIA_ICEE_RESTO_CUARTILES,
    CAP_LIBRO_ICEE_RESTO_CUARTILES,
    EDICION_ICEE_RESTO_CUARTILES,
    COMENTARIO_ICEE_RESTO_CUARTILES,
    AUTORIA_DIALNET,
    CAP_LIBRO_DIALNET,
    EDICION_DIALNET,
    COMENTARIO_DIALNET,
    AUTORIA_OTRAS,
    CAP_LIBRO_OTRAS,
    EDICION_OTRAS,
    COMENTARIO_OTRAS,
    LIBRO_NUMERO_AUTORES,
    LIBRO_EDITORIAL_PRESTIGIO;
  }

  public enum TipoFuente {
    SGI,
    CVN,
    OTRO_SISTEMA;
  }

  public enum TipoPuntos {
    PRINCIPAL,
    EXTRA,
    MODULADOR;
  }

  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = BaseEntity.NOMBRE_CONF_BAREMOS_LENGTH, nullable = false)
  private String nombre;

  @Column(name = "prioridad", nullable = true)
  private Integer prioridad;

  @Column(name = "tipo_baremo", length = BaseEntity.TIPO_BAREMO_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoBaremo tipoBaremo;

  @Column(name = "tipo_fuente", length = BaseEntity.TIPO_FUENTE_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoFuente tipoFuente;

  @Column(name = "tipo_puntos", length = BaseEntity.TIPO_PUNTOS_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoPuntos tipoPuntos;

  /** EpigrafeCVN */
  @Column(name = "epigrafe_cvn", length = BaseEntity.EPIGRAFE_LENGTH, nullable = false)
  private EpigrafeCVN epigrafeCVN;

  /** ConfiguracionBaremo padre. */
  @ManyToOne
  @JoinColumn(name = "configuracion_baremo_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONFIGURACIONBAREMO_PADRE"))
  private ConfiguracionBaremo padre;

  @OneToMany(mappedBy = "configuracionBaremo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Baremo> baremos = null;

}
