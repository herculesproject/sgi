package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_fase")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoFase extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_fase_seq")
  @SequenceGenerator(name = "tipo_fase_seq", sequenceName = "tipo_fase_seq", allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = 50, nullable = false)
  @NotNull
  private String nombre;

  @Column(name = "descripcion", length = 250)
  @NotNull
  private String descripcion;

  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}
