package org.crue.hercules.sgi.rep.service;

import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.eti.ComiteDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.ConfiguracionDto;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.DictamenDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoActividadDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoMemoriaDto;

/**
 * BaseReportServiceTest
 */
class BaseReportServiceTest extends BaseServiceTest {

  protected EvaluacionDto generarMockEvaluacion(Long idEvaluacion) {
    return EvaluacionDto.builder()
        .id(idEvaluacion)
        .memoria(generarMockMemoria(1L, 1L))
        .convocatoriaReunion(generarMockConvocatoriReunion(idEvaluacion))
        .tipoEvaluacion(TipoEvaluacionDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .dictamen(DictamenDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .fechaDictamen(Instant.now())
        .version(1)
        .activo(Boolean.TRUE)
        .build();
  }

  private ConvocatoriaReunionDto generarMockConvocatoriReunion(Long idConvocatoriaReunion) {
    ConvocatoriaReunionDto convocatoriaReunion = ConvocatoriaReunionDto.builder()
        .id(idConvocatoriaReunion)
        .lugar("lugar")
        .comite(generarMockComite(1L, "CEI"))
        .ordenDia("ordenDia")
        .anio(2021)
        .horaInicio(10)
        .minutoInicio(0)
        .horaInicioSegunda(12)
        .minutoInicioSegunda(0)
        .tipoConvocatoriaReunion(
            TipoConvocatoriaReunionDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .fechaEvaluacion(Instant.now())
        .fechaLimite(Instant.now())
        .fechaEnvio(Instant.now())
        .numeroActa(1L)
        .activo(Boolean.TRUE)
        .build();
    return convocatoriaReunion;
  }

  protected ConfiguracionDto generarMockConfiguracion() {
    return ConfiguracionDto.builder()
        .diasArchivadaInactivo(3)
        .mesesArchivadaPendienteCorrecciones(6)
        .diasLimiteEvaluador(31)
        .build();
  }

  protected ComiteDto generarMockComite(Long idComite, String comite) {
    return ComiteDto.builder()
        .id(idComite)
        .comite(comite)
        .nombreDecreto("nombreDecreto")
        .nombreInvestigacion("nombreInvestigacion")
        .nombreSecretario("nombreSecretario")
        .genero(Genero.M)
        .formulario(generarMockFormulario(1L))
        .activo(Boolean.TRUE)
        .build();
  }

  protected FormularioDto generarMockFormulario(Long idFormulario) {
    return FormularioDto.builder().id(idFormulario).build();
  }

  protected PeticionEvaluacionDto generarMockPeticionEvaluacion(Long idPeticionEvaluacion) {
    return PeticionEvaluacionDto.builder().id(
        idPeticionEvaluacion)
        .tipoActividad(TipoActividadDto.builder().id(1L).nombre("nombreTipoActividad").build())
        .build();
  }

  protected MemoriaDto generarMockMemoria(Long idMemoria, Long idFormulario) {

    return MemoriaDto.builder()
        .id(idMemoria)
        .numReferencia("numReferencia")
        .peticionEvaluacion(generarMockPeticionEvaluacion(1L))
        .comite(generarMockComite(1L, "CEI"))
        .titulo("titulo")
        .tipoMemoria(TipoMemoriaDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .fechaEnvioSecretaria(Instant.now())
        .version(1)
        .activo(Boolean.TRUE)
        .build();
  }

}
