package org.crue.hercules.sgi.eti.dto;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import org.crue.hercules.sgi.eti.enums.Language;
import org.crue.hercules.sgi.eti.model.Bloque;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartadoOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String nombre;
  private Bloque bloque;
  private ApartadoOutput padre;
  private Integer orden;
  private String esquema;
  private String lang;

  @JsonRawValue
  public String getEsquema() {
    return esquema;
  }

  public void setEsquema(final String esquema) {
    this.esquema = esquema;
  }

  @JsonProperty(value = "esquema")
  public void setEsquemaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setEsquema(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setEsquema(stringWriter.toString());
      }
    }
  }

  public ApartadoOutput(Long id, String nombre, Bloque bloque, Integer orden, String esquema, Language lang) {
    this.id = id;
    this.nombre = nombre;
    this.bloque = bloque;
    this.orden = orden;
    this.esquema = esquema;
    this.lang = lang.getCode();
  }

  public ApartadoOutput(Long id, String nombre, Bloque bloque, Long padreId,
      Integer padreOrden, Integer orden, String esquema,
      Language lang) {
    ApartadoOutput padreOutput = new ApartadoOutput();
    padreOutput.setId(padreId);
    padreOutput.setOrden(padreOrden);
    this.id = id;
    this.nombre = nombre;
    this.bloque = bloque;
    this.padre = padreOutput;
    this.orden = orden;
    this.esquema = esquema;
    this.lang = lang.getCode();
  }

}
