package org.crue.hercules.sgi.rep.service;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SgiReportServiceTest
 */
class SgiReportServiceTest extends BaseReportServiceTest {

  private SgiReportService sgiReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @Mock
  private SgiReportExcelService sgiExcelService;

  @BeforeEach
  public void setUp() throws Exception {
    sgiReportService = new SgiReportService(sgiConfigProperties, sgiApiConfService, sgiExcelService);
  }

}
