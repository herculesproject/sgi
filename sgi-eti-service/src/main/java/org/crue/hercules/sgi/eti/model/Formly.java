package org.crue.hercules.sgi.eti.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * La entidad Formly representa un formulario configurable.
 */
@Entity
@Table(name = "formly")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formly extends BaseEntity {
  public static final int NOMBRE_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formly_seq")
  @SequenceGenerator(name = "formly_seq", sequenceName = "formly_seq", allocationSize = 1)
  private Long id;

  /** Version */
  @Column(name = "version", nullable = false)
  private Integer version;

  @OneToMany(mappedBy = "formly")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<FormlyNombre> formlyNombres = null;

}