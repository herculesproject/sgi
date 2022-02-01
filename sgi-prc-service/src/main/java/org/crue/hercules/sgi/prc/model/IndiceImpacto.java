package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;
import java.util.stream.Stream;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.prc.exceptions.TipoFuenteImpactoNotFoundException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = IndiceImpacto.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndiceImpacto extends BaseEntity {

  protected static final String TABLE_NAME = "indice_impacto";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** TipoFuenteImpacto */
  public enum TipoFuenteImpacto {
    /** WOS */
    E000("000"),
    /** JCR */
    E010("JCR"),
    /** INRECS */
    E020("INRECS"),
    /** BCI */
    BCI("BCI"),
    /** ICEE */
    ICEE("ICEE"),
    /** DIALNET */
    DIALNET("DIALNET"),
    /** CitEC */
    CITEC("CITEC"),
    /** SCIMAGO */
    SCIMAGO("SCIMAGO"),
    /** ERIH */
    ERIH("ERIH"),
    /** MIAR */
    MIAR("MIAR"),
    /** FECYT */
    FECYT("FECYT"),
    /** GII-GRIN-SCIE */
    GII_GRIN_SCIE("GII-GRIN-SCIE"),
    /** CORE */
    CORE("CORE"),
    /** Otros */
    OTHERS("OTHERS");

    private String internValue;

    private TipoFuenteImpacto(String internValue) {
      this.internValue = internValue;
    }

    public String getInternValue() {
      return internValue;
    }

    public static TipoFuenteImpacto getByInternValue(String internValue) {
      try {
        return Stream.of(TipoFuenteImpacto.values())
            .filter(internValueValue -> internValueValue.getInternValue().equalsIgnoreCase(internValue))
            .findFirst()
            .orElseThrow(() -> new TipoFuenteImpactoNotFoundException(internValue));

      } catch (Exception e) {
        throw new TipoFuenteImpactoNotFoundException(internValue);
      }
    }
  }

  /** TipoRanking */
  public enum TipoRanking {
    /** Clase1 */
    CLASE1,
    /** Clase2 */
    CLASE2,
    /** Clase3 */
    CLASE3,
    /** A* */
    A_POR,
    /** A */
    A;
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** TipoFuenteImpacto */
  @Column(name = "tipo_fuente_impacto", length = TIPO_FUENTE_IMPACTO_LENGTH, nullable = false)
  private TipoFuenteImpacto fuenteImpacto;

  /** TipoRanking */
  @Column(name = "tipo_ranking", length = TIPO_RANKING_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoRanking ranking;

  /** anio */
  @Column(name = "anio", nullable = false)
  private Integer anio;

  /** anio */
  @Column(name = "otra_fuente_impacto", length = OTRA_FUENTE_IMPACTO_LENGTH, nullable = true)
  private String otraFuenteImpacto;

  /** posicionPublicacion */
  @Column(name = "posicion_publicacion", nullable = true)
  private BigDecimal posicionPublicacion;

  /** numeroRevistas */
  @Column(name = "numero_revistas", nullable = true)
  private BigDecimal numeroRevistas;

  /** revista25 */
  @Column(name = "revista_25", columnDefinition = "boolean default false", nullable = true)
  private Boolean revista25;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INDICEIMPACTO_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

}
