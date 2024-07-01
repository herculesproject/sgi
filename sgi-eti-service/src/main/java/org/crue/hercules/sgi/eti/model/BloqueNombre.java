package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloque Nombre
 */

@Entity
@IdClass(BloqueNombreKey.class)
@Table(name = "bloque_nombre")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BloqueNombre implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Bloque */
  @Id
  @NotNull
  private Long bloqueId;

  /** Language. */
  @Id
  @NotNull
  private Language lang;

  /** Nombre */
  @Column(name = "nombre", length = 2000, nullable = false)
  private String nombre;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "bloque_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_BLOQUENOMBRE_BLOQUE"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Bloque bloque = null;

}