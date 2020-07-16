package org.crue.hercules.sgi.eti.model;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comite_formulario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ComiteFormulario extends BaseEntity {

  /**
   * ComiteFormulario
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", length = 28, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comite_formulario_seq")
  @SequenceGenerator(name = "comite_formulario_seq", sequenceName = "comite_formulario_seq", allocationSize = 1)
  private Long id;

  /** Comité */
  @ManyToOne
  @JoinColumn(name = "comite_id", nullable = false)
  @NotNull
  private Comite comite;

  /** Formulario */
  @ManyToOne
  @JoinColumn(name = "formulario_id", nullable = false)
  private Formulario formulario;
}