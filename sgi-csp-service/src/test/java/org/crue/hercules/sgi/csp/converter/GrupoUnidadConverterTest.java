package org.crue.hercules.sgi.csp.converter;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.GrupoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.GrupoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class GrupoUnidadConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private GrupoUnidadVinculacionConverter grupoUnidadConverter;

  @BeforeEach
  void setup() {
    this.grupoUnidadConverter = new GrupoUnidadVinculacionConverter(this.modelMapper);
  }

  @Test
  void convertGrupoUnidadesInput_ReturnsGrupoUnidadList() {
    // given: una lista con un GrupoUnidadVinculacionInput
    final String unidadRef = "testing-ref";
    final GrupoUnidadVinculacionInput input = this.buildMockGrupoUnidadInput(unidadRef);
    final GrupoUnidadVinculacion entity = this.buildMockGrupoUnidad(1L, 1L, unidadRef);

    final List<GrupoUnidadVinculacionInput> inputList = Arrays.asList(input);

    BDDMockito.given(this.modelMapper.map(input, GrupoUnidadVinculacion.class)).willReturn(entity);

    // when: se convierte la lista a entidades
    List<GrupoUnidadVinculacion> result = this.grupoUnidadConverter.convertGrupoUnidadesInput(inputList);

    // then: se obtiene una lista con un GrupoUnidadVinculacion sin id
    Assertions.assertThat(result)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1);
    Assertions.assertThat(result.get(0).getId()).isNull();
  }

  @Test
  void convert_ReturnsGrupoUnidadOutputPage() {
    // given: una Page con una entidad GrupoUnidadVinculacion
    final String unidadRef = "testing-ref";
    final Long id = 1L;
    final GrupoUnidadVinculacion entity = this.buildMockGrupoUnidad(id, 1L, unidadRef);
    final GrupoUnidadVinculacionOutput output = this.buildMockGrupoUnidadOutput(id, unidadRef);

    final List<GrupoUnidadVinculacion> entities = Arrays.asList(entity);
    final Page<GrupoUnidadVinculacion> page = new PageImpl<>(entities);

    BDDMockito.given(modelMapper.map(entity, GrupoUnidadVinculacionOutput.class)).willReturn(output);

    // when: se convierte la Page a Page de GrupoUnidadVinculacionOutput
    Page<GrupoUnidadVinculacionOutput> result = this.grupoUnidadConverter.convert(page);

    // then: se obtiene una Page con un GrupoUnidadVinculacionOutput equivalente
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).isNotEmpty();
    Assertions.assertThat(result.getContent().get(0).getId()).isEqualTo(id);
    Assertions.assertThat(result.getContent().get(0).getUnidadVinculacionRef()).isEqualTo(unidadRef);
  }

  private GrupoUnidadVinculacionInput buildMockGrupoUnidadInput(String unidadVinculacionRef) {
    return GrupoUnidadVinculacionInput.builder()
        .unidadVinculacionRef(unidadVinculacionRef)
        .build();
  }

  private GrupoUnidadVinculacion buildMockGrupoUnidad(Long id, Long grupoId, String unidadVinculacionRef) {
    return GrupoUnidadVinculacion.builder()
        .id(id)
        .unidadVinculacionRef(unidadVinculacionRef)
        .grupoId(grupoId)
        .build();
  }

  private GrupoUnidadVinculacionOutput buildMockGrupoUnidadOutput(Long id, String unidadVinculacionRef) {
    return GrupoUnidadVinculacionOutput.builder()
        .id(id)
        .unidadVinculacionRef(unidadVinculacionRef)
        .build();
  }

}
