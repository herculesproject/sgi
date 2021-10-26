package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformeActaReportInput implements Serializable {

  @NotNull
  private Long idActa;

  @NotNull
  private Instant fecha;
}
