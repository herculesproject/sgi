package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.enums.Language;
import org.crue.hercules.sgi.eti.model.Formulario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Formulario formulario;
  private String nombre;
  private Integer orden;
  private String lang;

  public BloqueOutput(Long id, Formulario formulario, String nombre, Integer orden, Language language) {
    this.id = id;
    this.formulario = formulario;
    this.nombre = nombre;
    this.orden = orden;
    this.lang = language.getCode();
  }

  public BloqueOutput(Long id, String nombre, Integer orden, Language language) {
    this.id = id;
    this.nombre = nombre;
    this.orden = orden;
    this.lang = language.getCode();
  }

}
