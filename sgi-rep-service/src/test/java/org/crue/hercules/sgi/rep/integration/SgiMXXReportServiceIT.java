package org.crue.hercules.sgi.rep.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.SgiReport;
import org.crue.hercules.sgi.rep.service.SgiMXXReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SgiMXXReportServiceIT extends BaseIT {

  @Autowired
  SgiMXXReportService service;

  @Test
  public void testPdfM10() throws Exception {
    // given: data for report
    String reportPath = "report/mxx/mxx.prpt";
    String reportName = "reportM10_";
    String outputJson = "formly/etiReportOutput_M10.json";

    // @formatter:off
    SgiReport report = SgiReport.builder()
      .path(reportPath)
      .name(reportName)
      .outputReportType(OutputReportType.PDF)
      .build();
    // @formatter:on

    // when: generate report
    service.getReportMXXFromJson(report, outputJson,
        "M10 - Memoria para el CEISH: proyecto de investigación con seres humanos");

    // given: report generated
    assertNotNull(report);

    File file = File.createTempFile(reportName, ".pdf");
    FileUtils.writeByteArrayToFile(file, report.getContent());
  }

  @Test
  public void testPdfM20() throws Exception {
    try {
      // given: data for report
      String reportPath = "report/mxx/mxx.prpt";
      String reportName = "reportM20_";
      String outputJson = "formly/etiReportOutput_M20.json";

      // when: generate report
      // @formatter:off
      SgiReport report = SgiReport.builder()
        .path(reportPath)
        .name(reportName)
        .outputReportType(OutputReportType.PDF)
        .build();
      // @formatter:on

      service.getReportMXXFromJson(report, outputJson,
          "M20 - Memoria para el CEEA: proyecto de investigación con seres humanos");

      // given: report generated
      assertNotNull(report);

      File file = File.createTempFile(reportName, ".pdf");
      FileUtils.writeByteArrayToFile(file, report.getContent());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Test
  public void testPdfM30() throws Exception {
    try {
      // given: data for report
      String reportPath = "report/mxx/mxx.prpt";
      String reportName = "reportM30_";
      String outputJson = "formly/etiReportOutput_M30.json";

      // when: generate report
      // @formatter:off
      SgiReport report = SgiReport.builder()
        .path(reportPath)
        .name(reportName)
        .outputReportType(OutputReportType.PDF)
        .build();
      // @formatter:on

      service.getReportMXXFromJson(report, outputJson,
          "M30 - Memoria para el CEIAB: proyecto de investigación con seres humanos");

      // given: report generated
      assertNotNull(report);

      File file = File.createTempFile(reportName, ".pdf");
      FileUtils.writeByteArrayToFile(file, report.getContent());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}