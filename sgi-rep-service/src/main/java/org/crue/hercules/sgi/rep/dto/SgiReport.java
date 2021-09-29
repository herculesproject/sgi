package org.crue.hercules.sgi.rep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instancia que contiene un report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SgiReport {
  private String name;
  private String path;
  private byte[] content;
  private OutputReportType outputReportType;
}