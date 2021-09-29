package org.crue.hercules.sgi.rep.dto;

/**
 * Instancia que contiene un report M10, M20 o M30
 */
public class SgiReportMXX extends SgiReport {
  public SgiReportMXX() {
    this.setPath("report/mxx/mxx.prpt");
    this.setName("mxx");
  }
}