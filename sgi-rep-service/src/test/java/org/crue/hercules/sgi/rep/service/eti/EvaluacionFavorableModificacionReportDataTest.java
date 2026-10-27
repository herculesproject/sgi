package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.dto.eti.ComiteNombreDto;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoActividadDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoInvestigacionTuteladaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.SexoDto;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.enums.TipoActividad;
import org.crue.hercules.sgi.rep.enums.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.EvaluacionFavorableModificacionReportData;
import org.crue.hercules.sgi.rep.report.data.objects.ConvocatoriaReunionObject;
import org.crue.hercules.sgi.rep.report.data.objects.EvaluacionObject;
import org.crue.hercules.sgi.rep.report.data.objects.PersonaObject;
import org.junit.jupiter.api.Test;

/**
 * EvaluacionFavorableModificacionReportDataTest
 */
class EvaluacionFavorableModificacionReportDataTest extends BaseReportEtiServiceTest {

  @Test
  void setEvaluacion_WithEvaluacion_SetsDatosEvaluacion() {
    // given: una evaluación de un comité masculino
    EvaluacionDto evaluacion = generarMockEvaluacion(1L);
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoInvestigacionTutelada(null);
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos de la evaluación
    reportData.setEvaluacion(evaluacion);

    // then: se exponen los objetos anidados y los valores retenidos por
    // compatibilidad
    Assertions.assertThat(reportData.getData().get("evaluacion")).isInstanceOf(EvaluacionObject.class);
    Assertions.assertThat(reportData.getData()).containsKeys("convocatoria", "memoria", "peticionEvaluacion");
    Assertions.assertThat(reportData.getData())
        .containsEntry("fechaDictamen",
            SgiReportHelper.formatDateWithMonthName(evaluacion.getConvocatoriaReunion().getFechaEvaluacion()))
        .containsEntry("numeroActa", "1/2021")
        .containsEntry("referenciaMemoria", "numReferencia")
        .containsEntry("comite", "CEI")
        .containsEntry("nombreInvestigacion", "nombreInvestigacion")
        .containsEntry("del", SgiReportHelper.getMessage("common.del"))
        .containsEntry("el", SgiReportHelper.getMessage("common.el"));
  }

  @Test
  void setEvaluacion_WithComiteFemenino_SetsArticulosFemeninos() {
    // given: una evaluación de un comité femenino
    EvaluacionDto evaluacion = generarMockEvaluacion(1L);
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoInvestigacionTutelada(null);
    evaluacion.getMemoria().getComite().getNombre().get(0).setGenero(ComiteNombreDto.Genero.F);
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos de la evaluación
    reportData.setEvaluacion(evaluacion);

    // then: los artículos del comité van en femenino
    Assertions.assertThat(reportData.getData())
        .containsEntry("del", SgiReportHelper.getMessage("common.dela"))
        .containsEntry("el", SgiReportHelper.getMessage("common.la"));
  }

  @Test
  void setEvaluacion_WithProyectoInvestigacionDocencia_SetsActividad() {
    // given: una evaluación cuya petición es de tipo Proyecto de investigación y
    // docencia
    EvaluacionDto evaluacion = generarMockEvaluacion(1L);
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoActividad(
        TipoActividadDto.builder().id(TipoActividad.PROYECTO_INVESTIGACION_DOCENCIA.getId()).nombre("nombre").build());
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoInvestigacionTutelada(null);
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos de la evaluación
    reportData.setEvaluacion(evaluacion);

    // then: la actividad es el tipo nuevo, en masculino
    Assertions.assertThat(reportData.getData())
        .containsEntry("actividad",
            SgiReportHelper.getMessage("enum.tipo-actividad.PROYECTO_INVESTIGACION_DOCENCIA"))
        .containsEntry("fieldAlActividad", SgiReportHelper.getMessage("common.al"));
  }

  @Test
  void setEvaluacion_WithPracticaDocente_SetsActividadFemenina() {
    // given: una evaluación cuya petición es de tipo Práctica docente
    EvaluacionDto evaluacion = generarMockEvaluacion(1L);
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoActividad(
        TipoActividadDto.builder().id(TipoActividad.PRACTICA_DOCENTE.getId()).nombre("nombre").build());
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoInvestigacionTutelada(null);
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos de la evaluación
    reportData.setEvaluacion(evaluacion);

    // then: la actividad es Práctica docente, en femenino
    Assertions.assertThat(reportData.getData())
        .containsEntry("actividad", SgiReportHelper.getMessage("enum.tipo-actividad.PRACTICA_DOCENTE"))
        .containsEntry("fieldAlActividad", SgiReportHelper.getMessage("common.a.la"));
  }

  @Test
  void setEvaluacion_WithInvestigacionTutelada_SetsTipoInvestigacionTutelada() {
    // given: una evaluación cuya petición es una investigación tutelada de tipo
    // Tesis doctoral
    EvaluacionDto evaluacion = generarMockEvaluacion(1L);
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoActividad(
        TipoActividadDto.builder().id(TipoActividad.INVESTIGACION_TUTELADA.getId()).nombre("nombre").build());
    evaluacion.getMemoria().getPeticionEvaluacion().setTipoInvestigacionTutelada(
        TipoInvestigacionTuteladaDto.builder().id(TipoInvestigacionTutelada.TESIS_DOCTORAL.getId()).nombre("nombre")
            .build());
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos de la evaluación
    reportData.setEvaluacion(evaluacion);

    // then: la actividad es el tipo de investigación tutelada, en femenino
    Assertions.assertThat(reportData.getData())
        .containsEntry("actividad", SgiReportHelper.getMessage("enum.tipo-investigacion-tutelada.TESIS_DOCTORAL"))
        .containsEntry("fieldAlActividad", SgiReportHelper.getMessage("common.a.la"));
  }

  @Test
  void setInvestigador_WithHombre_SetsCamposMasculinos() {
    // given: un investigador hombre
    PersonaDto investigador = generarMockPersona("12345678A");
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos del investigador
    reportData.setInvestigador(investigador);

    // then: los textos van en masculino
    Assertions.assertThat(reportData.getData().get("investigador")).isInstanceOf(PersonaObject.class);
    Assertions.assertThat(reportData.getData())
        .containsEntry("nombreInvestigador", "nombre apellidos")
        .containsEntry("fieldElInvestigador", SgiReportHelper.getMessage("common.el"))
        .containsEntry("fieldInvestigador", SgiReportHelper.getMessage("field.investigador.masculino"));
  }

  @Test
  void setInvestigador_WithMujer_SetsCamposFemeninos() {
    // given: una investigadora
    PersonaDto investigadora = generarMockPersonaMujer();
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos del investigador
    reportData.setInvestigador(investigadora);

    // then: los textos van en femenino
    Assertions.assertThat(reportData.getData())
        .containsEntry("fieldElInvestigador", SgiReportHelper.getMessage("common.la"))
        .containsEntry("fieldInvestigador", SgiReportHelper.getMessage("field.investigador.femenino"));
  }

  @Test
  void setPresidente_WithMujer_SetsCamposFemeninos() {
    // given: una presidenta
    PersonaDto presidenta = generarMockPersonaMujer();
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos del presidente
    reportData.setPresidente(presidenta);

    // then: los textos van en femenino
    Assertions.assertThat(reportData.getData().get("presidente")).isInstanceOf(PersonaObject.class);
    Assertions.assertThat(reportData.getData())
        .containsEntry("fieldDelPresidente", SgiReportHelper.getMessage("common.dela"))
        .containsEntry("fieldPresidente", SgiReportHelper.getMessage("field.presidente.femenino"));
  }

  @Test
  void setPresidente_WithHombre_SetsCamposMasculinos() {
    // given: un presidente hombre
    PersonaDto presidente = generarMockPersona("12345678A");
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos del presidente
    reportData.setPresidente(presidente);

    // then: los textos van en masculino
    Assertions.assertThat(reportData.getData())
        .containsEntry("fieldDelPresidente", SgiReportHelper.getMessage("common.del"))
        .containsEntry("fieldPresidente", SgiReportHelper.getMessage("field.presidente.masculino"));
  }

  @Test
  void setSecretario_WithHombre_SetsCamposMasculinos() {
    // given: un secretario hombre
    PersonaDto secretario = generarMockPersona("12345678A");
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos del secretario
    reportData.setSecretario(secretario);

    // then: el nombre se compone y el texto va en masculino
    Assertions.assertThat(reportData.getData().get("secretario")).isInstanceOf(PersonaObject.class);
    Assertions.assertThat(reportData.getData())
        .containsEntry("nombreSecretario", "nombre apellidos")
        .containsEntry("fieldSecretario", SgiReportHelper.getMessage("field.secretario.masculino"));
  }

  @Test
  void setSecretario_WithMujer_SetsCamposFemeninos() {
    // given: una secretaria
    PersonaDto secretaria = generarMockPersonaMujer();
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos del secretario
    reportData.setSecretario(secretaria);

    // then: el texto va en femenino
    Assertions.assertThat(reportData.getData())
        .containsEntry("fieldSecretario", SgiReportHelper.getMessage("field.secretario.femenino"));
  }

  @Test
  void setConvocatoriaUltimaEvaluacion_WithConvocatoria_SetsDatosMemoriaOriginal() {
    // given: la convocatoria de la última evaluación de la memoria original
    ConvocatoriaReunionDto convocatoria = generarMockConvocatoriaReunion(2L);
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se cargan los datos de la convocatoria
    reportData.setConvocatoriaUltimaEvaluacion(convocatoria);

    // then: se exponen la convocatoria, la fecha de dictamen y el acta de la
    // memoria original
    Assertions.assertThat(reportData.getData().get("convocatoriaUltimaEvaluacion"))
        .isInstanceOf(ConvocatoriaReunionObject.class);
    Assertions.assertThat(reportData.getData())
        .containsEntry("fechaDictamenMemOriginal",
            SgiReportHelper.formatDateWithMonthName(convocatoria.getFechaEvaluacion()))
        .containsEntry("numeroActaMemOriginal", "1/2021");
  }

  @Test
  void setFechaPrimerEnvioSecretaria_WithFecha_SetsFechas() {
    // given: la fecha del primer envío a secretaría
    Instant fecha = Instant.parse("2026-03-05T10:00:00Z");
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se carga la fecha
    reportData.setFechaPrimerEnvioSecretaria(fecha);

    // then: se expone la fecha original y la formateada
    Assertions.assertThat(reportData.getData())
        .containsEntry("fechaPrimerEnvioSecretaria", fecha)
        .containsEntry("fechaEnvioSecretaria", SgiReportHelper.formatDateWithMonthName(fecha));
  }

  @Test
  void setFechaPrimerEnvioSecretaria_WithNull_SetsFechaVacia() {
    // given: sin fecha de primer envío a secretaría
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se carga la fecha
    reportData.setFechaPrimerEnvioSecretaria(null);

    // then: la fecha formateada queda vacía
    Assertions.assertThat(reportData.getData()).containsEntry("fechaEnvioSecretaria", "");
  }

  @Test
  void getInforme_ReturnsEvaluacionFavorableModificacion() {
    // given: los datos del informe
    EvaluacionFavorableModificacionReportData reportData = new EvaluacionFavorableModificacionReportData();

    // when: se obtiene el informe
    Informe informe = reportData.getInforme();

    // then: es el informe de evaluación favorable de memoria de modificación
    Assertions.assertThat(informe).isEqualTo(Informe.ETI_EVALUACION_FAVORABLE_MODIFICACION);
  }

  private PersonaDto generarMockPersonaMujer() {
    PersonaDto persona = generarMockPersona("12345678B");
    persona.setSexo(SexoDto.builder().id("M").nombre("Mujer").build());
    return persona;
  }

}
