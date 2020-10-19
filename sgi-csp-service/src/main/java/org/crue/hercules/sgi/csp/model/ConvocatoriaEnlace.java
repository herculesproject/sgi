package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatoria_enlace")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConvocatoriaEnlace extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_enlace_seq")
  @SequenceGenerator(name = "convocatoria_enlace_seq", sequenceName = "convocatoria_enlace_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false)
  @NotNull
  private Convocatoria convocatoria;

  /** Url */
  @Column(name = "url", length = 250, nullable = false)
  @Size(max = 250)
  private String url;

  /** Descripcion */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Tipo enlace. */
  @ManyToOne
  @JoinColumn(name = "tipo_enlace_id")
  private TipoEnlace tipoEnlace;

}