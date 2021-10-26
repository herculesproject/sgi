package org.crue.hercules.sgi.rep.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.service.SgiDynamicReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SgiDynamicReportServiceIT extends BaseIT {

  @Autowired
  SgiDynamicReportService service;

  @Test
  public void testInformeDinamico() throws Exception {
    // given: data for report

    // @formatter:off
    List<SgiDynamicReportDto.SgiFilterReportDto> filters = new ArrayList<>();
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro1").filter("Valor de filtro 1").build());
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro2").filter("Valor de filtro 2").build());

    List<SgiDynamicReportDto.SgiColumReportDto> columns = new ArrayList<>();
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().name("Nombre").type(SgiDynamicReportDto.TypeColumnReportEnum.STRING).build());
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().name("Fecha").type(SgiDynamicReportDto.TypeColumnReportEnum.STRING).build());

    List<SgiDynamicReportDto.SgiRowReportDto> rows = new ArrayList<>();
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre1", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre2", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre3", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre4", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre5", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre6", "22/10/2021")).build());

    SgiDynamicReportDto report = SgiDynamicReportDto.builder()
      .name("informeDinamico")
      .outputReportType(OutputReportType.PDF)
      .title("Listado de prueba de informe dinámico")
      .filters(filters)
      .columns(columns)
      .rows(rows)
      .build();
    // @formatter:on

    // when: generate report
    service.generateDynamicReport(report);

    // given: report generated
    assertNotNull(report);

    File file = File.createTempFile(report.getName(), ".pdf");
    FileUtils.writeByteArrayToFile(file, report.getContent());
  }

}