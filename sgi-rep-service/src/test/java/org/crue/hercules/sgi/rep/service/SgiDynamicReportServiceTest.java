package org.crue.hercules.sgi.rep.service;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SgiDynamicReportServiceTest
 */
class SgiDynamicReportServiceTest extends BaseReportServiceTest {

  private SgiDynamicReportService sgiDynamicReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @Mock
  private SgiReportExcelService sgiExcelService;

  @BeforeEach
  public void setUp() throws Exception {
    sgiDynamicReportService = new SgiDynamicReportService(sgiConfigProperties, sgiApiConfService, sgiExcelService);
  }

}
