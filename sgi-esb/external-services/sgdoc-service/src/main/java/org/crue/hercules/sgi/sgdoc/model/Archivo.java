package org.crue.hercules.sgi.sgdoc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = Archivo.TABLE_NAME)
public class Archivo implements Serializable {

  private static final long serialVersionUID = 1L;

  protected static final String TABLE_NAME = "archivo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Archivo.SEQUENCE_NAME)
  @SequenceGenerator(name = Archivo.SEQUENCE_NAME, sequenceName = Archivo.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Lob
  @Column(name = "archivo", columnDefinition = "blob")
  private byte[] archivo;

  @OneToOne
  @NotNull
  @JoinColumn(name = "documento_ref", nullable = false, foreignKey = @ForeignKey(name = "FK_ARCHIVO_DOCUMENTO"))
  private Documento documento;

}