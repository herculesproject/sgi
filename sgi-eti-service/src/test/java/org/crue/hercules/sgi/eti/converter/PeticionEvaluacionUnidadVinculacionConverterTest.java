package org.crue.hercules.sgi.eti.converter;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionUnidadVinculacionInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionUnidadVinculacionOutput;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * PeticionEvaluacionUnidadVinculacionConverterTest
 */
class PeticionEvaluacionUnidadVinculacionConverterTest {

  private PeticionEvaluacionUnidadVinculacionConverter converter;

  @BeforeEach
  void setUp() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    converter = new PeticionEvaluacionUnidadVinculacionConverter(modelMapper);
  }

  @Test
  void convertInputs_ReturnsEntitiesWithoutId() {
    // given: una lista con dos inputs
    List<PeticionEvaluacionUnidadVinculacionInput> inputs = Arrays.asList(
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-1").build(),
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-2").build());

    // when: se convierten a entidades
    List<PeticionEvaluacionUnidadVinculacion> result = converter.convertInputs(inputs);

    // then: se obtienen las entidades con la referencia y sin id ni petición
    Assertions.assertThat(result).hasSize(2);
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("UV-1", "UV-2");
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getId).containsOnlyNulls();
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getPeticionEvaluacionId)
        .containsOnlyNulls();
  }

  @Test
  void convert_WithEntity_ReturnsOutput() {
    // given: una entidad
    PeticionEvaluacionUnidadVinculacion entity = generarMock(1L, 2L, "UV-1");

    // when: se convierte a output
    PeticionEvaluacionUnidadVinculacionOutput output = converter.convert(entity);

    // then: el output tiene los mismos datos
    Assertions.assertThat(output.getId()).isEqualTo(1L);
    Assertions.assertThat(output.getPeticionEvaluacionId()).isEqualTo(2L);
    Assertions.assertThat(output.getUnidadVinculacionRef()).isEqualTo("UV-1");
  }

  @Test
  void convert_WithPage_ReturnsOutputPage() {
    // given: una página con una entidad
    Page<PeticionEvaluacionUnidadVinculacion> page = new PageImpl<>(Arrays.asList(generarMock(1L, 2L, "UV-1")));

    // when: se convierte la página
    Page<PeticionEvaluacionUnidadVinculacionOutput> result = converter.convert(page);

    // then: la página convertida tiene el output equivalente
    Assertions.assertThat(result.getContent()).hasSize(1);
    Assertions.assertThat(result.getContent().get(0).getUnidadVinculacionRef()).isEqualTo("UV-1");
  }

  @Test
  void convert_WithList_ReturnsOutputList() {
    // given: una lista con dos entidades
    List<PeticionEvaluacionUnidadVinculacion> entities = Arrays.asList(generarMock(1L, 2L, "UV-1"),
        generarMock(3L, 2L, "UV-2"));

    // when: se convierte la lista
    List<PeticionEvaluacionUnidadVinculacionOutput> result = converter.convert(entities);

    // then: la lista convertida conserva ids y referencias
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacionOutput::getId)
        .containsExactly(1L, 3L);
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacionOutput::getUnidadVinculacionRef)
        .containsExactly("UV-1", "UV-2");
  }

  private PeticionEvaluacionUnidadVinculacion generarMock(Long id, Long peticionEvaluacionId, String ref) {
    return PeticionEvaluacionUnidadVinculacion.builder()
        .id(id)
        .peticionEvaluacionId(peticionEvaluacionId)
        .unidadVinculacionRef(ref)
        .build();
  }

}
