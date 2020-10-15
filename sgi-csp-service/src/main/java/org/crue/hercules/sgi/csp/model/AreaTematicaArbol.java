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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "area_tematica_arbol")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AreaTematicaArbol extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "area_tematica_arbol_seq")
  @SequenceGenerator(name = "area_tematica_arbol_seq", sequenceName = "area_tematica_arbol_seq", allocationSize = 1)
  private Long id;

  /** Abreviatura. */
  @Column(name = "abreviatura", length = 5, nullable = false)
  @NotEmpty
  @Size(max = 5)
  private String abreviatura;

  /** Nombre. */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

  /** Listado area tematica. */
  @ManyToOne
  @JoinColumn(name = "listado_area_tematica_id", nullable = false)
  @NotNull
  private ListadoAreaTematica listadoAreaTematica;

  /** Programa padre. */
  @ManyToOne
  @JoinColumn(name = "area_tematica_arbol_padre_id", nullable = true)
  private AreaTematicaArbol padre;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}
