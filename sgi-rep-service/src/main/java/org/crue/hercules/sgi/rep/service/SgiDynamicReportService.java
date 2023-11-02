package org.crue.hercules.sgi.rep.service;

import java.io.IOException;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes dinámicos
 */
@Service
@Slf4j
public class SgiDynamicReportService extends SgiReportService {

  public SgiDynamicReportService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfServic,
      SgiReportExcelService sgiExcelService) {
    super(sgiConfigProperties, sgiApiConfServic, sgiExcelService);
  }

  public byte[] exportExcelOrCsv(SgiDynamicReportDto sgiReport) throws IOException {
    return export(sgiReport);
  }

}