package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_memoria_comite")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoMemoriaComite extends BaseEntity {

  /**
   * Serial Version.
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Comite. */
  @ManyToOne
  @JoinColumn(name = "comite_id", nullable = false)
  @NotNull
  private Comite comite;

  /** Tipo Memoria. */
  @ManyToOne
  @JoinColumn(name = "tipo_memoria_id", nullable = false)
  @NotNull
  private TipoMemoria tipoMemoria;

}