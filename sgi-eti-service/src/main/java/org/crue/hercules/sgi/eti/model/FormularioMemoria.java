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
@Table(name = "formulario_memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class FormularioMemoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formulario_memoria_seq")
  @SequenceGenerator(name = "formulario_memoria_seq", sequenceName = "formulario_memoria_seq", allocationSize = 1)
  private Long id;

  /** Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false)
  @NotNull
  private Memoria memoria;

  /** Formulario */
  @ManyToOne
  @JoinColumn(name = "formulario_id", nullable = false)
  @NotNull
  private Formulario formulario;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean activo;

}