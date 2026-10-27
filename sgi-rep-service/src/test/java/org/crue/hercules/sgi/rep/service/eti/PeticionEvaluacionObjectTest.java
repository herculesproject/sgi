package org.crue.hercules.sgi.rep.service.eti;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto.EstadoFinanciacion;
import org.crue.hercules.sgi.rep.dto.eti.TipoActividadDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoInvestigacionTuteladaDto;
import org.crue.hercules.sgi.rep.enums.GenderType;
import org.crue.hercules.sgi.rep.enums.Genero;
import org.crue.hercules.sgi.rep.enums.TipoActividad;
import org.crue.hercules.sgi.rep.enums.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.PeticionEvaluacionObject;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;
import org.junit.jupiter.api.Test;

/**
 * PeticionEvaluacionObjectTest
 */
class PeticionEvaluacionObjectTest extends BaseReportEtiServiceTest {

  @Test
  void peticionEvaluacionObject_WithDto_MapsFields() {
    // given: una petición con datos generales, tipo de actividad y tipo de
    // investigación tutelada
    PeticionEvaluacionDto dto = generarMockPeticionEvaluacion(TipoActividad.INVESTIGACION_TUTELADA.getId(),
        TipoInvestigacionTutelada.TESIS_DOCTORAL.getId());
    dto.setCodigo("codigo");
    dto.setTitulo(Collections.singletonList(new I18nFieldValueDto(SgiReportContextHolder.getLanguage(), "titulo")));
    dto.setTutorRef("tutorRef");
    dto.setEstadoFinanciacion(EstadoFinanciacion.CONCEDIDO);

    // when: se construye el objeto del informe
    PeticionEvaluacionObject peticionEvaluacion = new PeticionEvaluacionObject(dto);

    // then: se copian los datos y los tipos se resuelven contra sus enumerados
    Assertions.assertThat(peticionEvaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(peticionEvaluacion.getCodigo()).isEqualTo("codigo");
    Assertions.assertThat(peticionEvaluacion.getTitulo()).isEqualTo("titulo");
    Assertions.assertThat(peticionEvaluacion.getTutorRef()).isEqualTo("tutorRef");
    Assertions.assertThat(peticionEvaluacion.getEstadoFinanciacion()).isEqualTo(EstadoFinanciacion.CONCEDIDO);
    Assertions.assertThat(peticionEvaluacion.getTipoActividad().getTipo())
        .isEqualTo(TipoActividad.INVESTIGACION_TUTELADA);
    Assertions.assertThat(peticionEvaluacion.getTipoInvestigacionTutelada().getTipo())
        .isEqualTo(TipoInvestigacionTutelada.TESIS_DOCTORAL);
  }

  @Test
  void peticionEvaluacionObject_WithNullDto_LeavesFieldsEmpty() {
    // given: sin petición

    // when: se construye el objeto del informe
    PeticionEvaluacionObject peticionEvaluacion = new PeticionEvaluacionObject(null);

    // then: no se rellena ningún dato
    Assertions.assertThat(peticionEvaluacion.getId()).isNull();
    Assertions.assertThat(peticionEvaluacion.getTipoActividad()).isNull();
    Assertions.assertThat(peticionEvaluacion.getTipoInvestigacionTutelada()).isNull();
  }

  @Test
  void getActividad_WithProyectoInvestigacionDocencia_ReturnsTipoActividad() {
    // given: una petición con el tipo de actividad Proyecto de investigación y
    // docencia
    PeticionEvaluacionObject peticionEvaluacion = new PeticionEvaluacionObject(
        generarMockPeticionEvaluacion(TipoActividad.PROYECTO_INVESTIGACION_DOCENCIA.getId(), null));

    // when: se obtiene la actividad
    GenderType actividad = peticionEvaluacion.getActividad();

    // then: es el tipo nuevo, masculino y con texto traducido
    Assertions.assertThat(actividad).isEqualTo(TipoActividad.PROYECTO_INVESTIGACION_DOCENCIA);
    Assertions.assertThat(actividad.getGenero()).isEqualTo(Genero.M);
    Assertions.assertThat(actividad).hasToString(
        SgiReportHelper.getMessage("enum.tipo-actividad.PROYECTO_INVESTIGACION_DOCENCIA"));
    Assertions.assertThat(peticionEvaluacion.getTipoActividad().getNombre()).isEqualTo(actividad.toString());
  }

  @Test
  void getActividad_WithPracticaDocente_ReturnsTipoActividad() {
    // given: una petición con el tipo de actividad Práctica docente
    PeticionEvaluacionObject peticionEvaluacion = new PeticionEvaluacionObject(
        generarMockPeticionEvaluacion(TipoActividad.PRACTICA_DOCENTE.getId(), null));

    // when: se obtiene la actividad
    GenderType actividad = peticionEvaluacion.getActividad();

    // then: es el tipo de actividad, femenino
    Assertions.assertThat(actividad).isEqualTo(TipoActividad.PRACTICA_DOCENTE);
    Assertions.assertThat(actividad.getGenero()).isEqualTo(Genero.F);
  }

  @Test
  void getActividad_WithInvestigacionTutelada_ReturnsTipoInvestigacionTutelada() {
    // given: una petición de investigación tutelada de tipo Trabajo fin de máster
    PeticionEvaluacionObject peticionEvaluacion = new PeticionEvaluacionObject(
        generarMockPeticionEvaluacion(TipoActividad.INVESTIGACION_TUTELADA.getId(),
            TipoInvestigacionTutelada.TRABAJO_FIN_MASTER.getId()));

    // when: se obtiene la actividad
    GenderType actividad = peticionEvaluacion.getActividad();

    // then: es el tipo de investigación tutelada en lugar del tipo de actividad
    Assertions.assertThat(actividad).isEqualTo(TipoInvestigacionTutelada.TRABAJO_FIN_MASTER);
    Assertions.assertThat(actividad.getGenero()).isEqualTo(Genero.M);
  }

  private PeticionEvaluacionDto generarMockPeticionEvaluacion(Long idTipoActividad,
      Long idTipoInvestigacionTutelada) {
    return PeticionEvaluacionDto.builder()
        .id(1L)
        .tipoActividad(TipoActividadDto.builder().id(idTipoActividad).nombre("nombre").build())
        .tipoInvestigacionTutelada(idTipoInvestigacionTutelada != null
            ? TipoInvestigacionTuteladaDto.builder().id(idTipoInvestigacionTutelada).nombre("nombre").build()
            : null)
        .build();
  }

}
