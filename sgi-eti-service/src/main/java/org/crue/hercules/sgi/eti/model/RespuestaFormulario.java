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

/**
 * RespuestaFormulario
 */

@Entity
@Table(name = "respuesta_formulario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaFormulario extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "respuesta_formulario_seq")
  @SequenceGenerator(name = "respuesta_formulario_seq", sequenceName = "respuesta_formulario_seq", allocationSize = 1)
  private Long id;

  /** Formulario Memoria */
  @ManyToOne
  @JoinColumn(name = "formulario_memoria_id", nullable = false)
  @NotNull
  private FormularioMemoria formularioMemoria;

  /** Componente Formulario */
  @ManyToOne
  @JoinColumn(name = "componente_formulario_id", nullable = false)
  private ComponenteFormulario componenteFormulario;

  /** Valor */
  @Column(name = "valor", length = 50, nullable = false)
  private String valor;

}