package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableMemoria;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.eti.InformeFavorableMemoriaReportService;
import org.crue.hercules.sgi.rep.service.eti.PeticionEvaluacionService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeFavorableMemoriaReportServiceTest
 */
class InformeFavorableMemoriaReportServiceTest extends BaseReportServiceTest {

  private InformeFavorableMemoriaReportService informeFavorableMemoriaReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @MockBean
  private EvaluacionService evaluacionService;

  @Mock
  private PersonaService personaService;

  @Mock
  private PeticionEvaluacionService peticionEvaluacionService;

  @BeforeEach
  public void setUp() throws Exception {
    informeFavorableMemoriaReportService = new InformeFavorableMemoriaReportService(sgiConfigProperties,
        personaService, evaluacionService, peticionEvaluacionService);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  public void getInformeEvaluacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(
        idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));

    ReportInformeFavorableMemoria report = new ReportInformeFavorableMemoria();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeFavorableMemoriaReportService.getReportInformeFavorableMemoria(report, idEvaluacion);
    assertNotNull(reportContent);

  }

}
