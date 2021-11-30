package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportMXX;
import org.crue.hercules.sgi.rep.service.eti.ApartadoService;
import org.crue.hercules.sgi.rep.service.eti.BloqueService;
import org.crue.hercules.sgi.rep.service.eti.MXXReportService;
import org.crue.hercules.sgi.rep.service.eti.MemoriaService;
import org.crue.hercules.sgi.rep.service.eti.PeticionEvaluacionService;
import org.crue.hercules.sgi.rep.service.eti.RespuestaService;
import org.crue.hercules.sgi.rep.service.eti.SgiFormlyService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * MXXReportServiceTest
 */
class MXXReportServiceTest extends BaseReportServiceTest {

  private MXXReportService mxxReportService;

  @MockBean
  private SgiConfigProperties sgiConfigProperties;

  @MockBean
  private MemoriaService memoriaService;

  @MockBean
  private PeticionEvaluacionService peticionEvaluacionService;

  @Mock
  private PersonaService personaService;

  @Mock
  private BloqueService bloqueService;

  @Mock
  private ApartadoService apartadoService;

  @Mock
  private SgiFormlyService sgiFormlyService;

  @Mock
  private RespuestaService respuestaService;

  @BeforeEach
  public void setUp() throws Exception {
    mxxReportService = new MXXReportService(sgiConfigProperties, memoriaService,
        peticionEvaluacionService, personaService, bloqueService,
        apartadoService, sgiFormlyService, respuestaService);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ESCR", "ETI-MEM-INV-ERTR" })
  public void getMXX_ReturnsResource() throws Exception {
    Long idMemoria = 26L;
    Long idFormulario = 3L;

    BDDMockito.given(memoriaService.findById(idMemoria)).willReturn((generarMockMemoria(idMemoria, idFormulario)));

    ReportMXX report = new ReportMXX();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = mxxReportService.getReportMXX(report, idMemoria, idFormulario);
    assertNotNull(reportContent);

  }

}
