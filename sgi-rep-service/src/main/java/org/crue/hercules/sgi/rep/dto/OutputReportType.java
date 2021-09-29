package org.crue.hercules.sgi.rep.dto;

import com.google.common.net.MediaType;

/**
 * The supported output types for reports
 */
public enum OutputReportType {
  PDF(MediaType.PDF.toString()), EXCEL(MediaType.MICROSOFT_EXCEL.toString()), HTML(MediaType.HTML_UTF_8.toString());

  public String type;

  private OutputReportType(String type) {
    this.type = type;
  }
}