package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.model.Modulador_;
import org.crue.hercules.sgi.prc.validation.UniqueFieldsValues;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueFieldsValues(groups = { Modulador.Create.class,
    Modulador.Update.class }, entityClass = ModuladorInput.class, fieldsNames = {
        Modulador_.TIPO, Modulador_.CONVOCATORIA_BAREMACION_ID })
public class ModuladorOutput implements Serializable {
  private Long id;

  @NotBlank
  @Size(max = BaseEntity.TIPO_MODULADOR_LENGTH)
  private String areaRef;

  @NotNull
  private TipoModulador tipo;

  @NotNull
  private BigDecimal valor1;

  private BigDecimal valor2;

  private BigDecimal valor3;

  private BigDecimal valor4;

  private BigDecimal valor5;

  @NotNull
  private Long convocatoriaBaremacionId;
}
