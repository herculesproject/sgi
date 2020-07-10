package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_memoria_comite_seq")
  @SequenceGenerator(name = "tipo_memoria_comite_seq", sequenceName = "tipo_memoria_comite_seq", allocationSize = 1)
  private Long id;

  /** Comite. */
  @OneToOne
  @JoinColumn(name = "comite", nullable = false)
  private Comite comite;

  /** Tipo Memoria. */
  @OneToOne
  @JoinColumn(name = "tipo_memoria", nullable = false)
  private TipoMemoria tipoMemoria;

}