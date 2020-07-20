package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apartado_formulario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ApartadoFormulario extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apartado_formulario_seq")
  @SequenceGenerator(name = "apartado_formulario_seq", sequenceName = "apartado_formulario_seq", allocationSize = 1)
  private Long id;

  /** Bloque Formulario. */
  @ManyToOne
  @JoinColumn(name = "bloque_formulario_id", nullable = false)
  private BloqueFormulario bloqueFormulario;

  /** Nombre. */
  @Column(name = "nombre", length = 250, nullable = false)
  private String nombre;

  /** Apartado Formulario Padre. */
  @ManyToOne
  @JoinColumn(name = "apartado_formulario_padre_id", nullable = true)
  private ApartadoFormulario apartadoFormularioPadre;

  /** Orden. */
  @Column(name = "orden", nullable = false)
  private Integer orden;

  /** Componente Formulario. */
  @OneToOne
  @JoinColumn(name = "componente_formulario_id", nullable = false)
  private ComponenteFormulario componenteFormulario;

  /** Control de borrado lógico */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;
}
