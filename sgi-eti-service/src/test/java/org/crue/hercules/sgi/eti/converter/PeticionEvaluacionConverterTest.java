package org.crue.hercules.sgi.eti.converter;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EstadoFinanciacionDto;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionSolicitudCspInput;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.EstadoFinanciacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionFuenteFinanciacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.modelmapper.ModelMapper;

/**
 * PeticionEvaluacionConverterTest
 */
class PeticionEvaluacionConverterTest {

  private PeticionEvaluacionConverter converter;

  @BeforeEach
  void setUp() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    converter = new PeticionEvaluacionConverter(modelMapper);
  }

  @Test
  void convert_WithSolicitudCspInput_ReturnsPeticionEvaluacion() {
    // given: un input con todos los campos que envía CSP
    PeticionEvaluacionSolicitudCspInput input = PeticionEvaluacionSolicitudCspInput.builder()
        .titulo(List.of(new I18nFieldValueDto(Language.ES, "Titulo"), new I18nFieldValueDto(Language.EN, "Title")))
        .resumen(List.of(new I18nFieldValueDto(Language.ES, "Resumen")))
        .objetivos(List.of(new I18nFieldValueDto(Language.ES, "Objetivos")))
        .fuenteFinanciacion(List.of(new I18nFieldValueDto(Language.ES, "Fuente")))
        .personaRef("user-001")
        .existeFinanciacion(Boolean.TRUE)
        .estadoFinanciacion(EstadoFinanciacionDto.SOLICITADO)
        .importeFinanciacion(new BigDecimal("1000.50"))
        .solicitudConvocatoriaRef("10")
        .checklistId(5L)
        .build();

    // when: se convierte a entidad
    PeticionEvaluacion entity = converter.convert(input);

    // then: la entidad tiene los mismos datos y sin id ni código
    Assertions.assertThat(entity.getTitulo()).containsExactlyInAnyOrder(
        new PeticionEvaluacionTitulo(Language.ES, "Titulo"), new PeticionEvaluacionTitulo(Language.EN, "Title"));
    Assertions.assertThat(entity.getResumen())
        .containsExactly(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Assertions.assertThat(entity.getObjetivos())
        .containsExactly(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos"));
    Assertions.assertThat(entity.getFuenteFinanciacion())
        .containsExactly(new PeticionEvaluacionFuenteFinanciacion(Language.ES, "Fuente"));
    Assertions.assertThat(entity.getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(entity.getExisteFinanciacion()).isTrue();
    Assertions.assertThat(entity.getEstadoFinanciacion()).isEqualTo(EstadoFinanciacion.SOLICITADO);
    Assertions.assertThat(entity.getImporteFinanciacion()).isEqualByComparingTo("1000.50");
    Assertions.assertThat(entity.getSolicitudConvocatoriaRef()).isEqualTo("10");
    Assertions.assertThat(entity.getChecklistId()).isEqualTo(5L);
    Assertions.assertThat(entity.getId()).isNull();
    Assertions.assertThat(entity.getCodigo()).isNull();
  }

  @ParameterizedTest
  @EnumSource(EstadoFinanciacionDto.class)
  void convert_WithEstadoFinanciacionDto_ReturnsEstadoFinanciacion(EstadoFinanciacionDto estadoFinanciacionDto) {
    // given: un estado de financiación del DTO

    // when: se convierte al estado de la entidad
    EstadoFinanciacion estadoFinanciacion = converter.convert(estadoFinanciacionDto);

    // then: es el estado de la entidad con el mismo nombre
    Assertions.assertThat(estadoFinanciacion.name()).isEqualTo(estadoFinanciacionDto.name());
  }

}
