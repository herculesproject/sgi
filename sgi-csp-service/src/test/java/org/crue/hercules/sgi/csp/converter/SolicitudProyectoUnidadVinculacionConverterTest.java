package org.crue.hercules.sgi.csp.converter;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class SolicitudProyectoUnidadVinculacionConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private SolicitudProyectoUnidadVinculacionConverter solicitudProyectoUnidadVinculacionConverter;

  @BeforeEach
  void setup() {
    this.solicitudProyectoUnidadVinculacionConverter = new SolicitudProyectoUnidadVinculacionConverter(this.modelMapper);
  }

  @Test
  void convertSolicitudProyectoUnidadVinculacionesInput_ReturnsSolicitudProyectoUnidadVinculacionList() {
    // given: una lista con un SolicitudProyectoUnidadVinculacionInput
    final String unidadRef = "testing-ref";
    final SolicitudProyectoUnidadVinculacionInput input = this.buildMockSolicitudProyectoUnidadVinculacionInput(unidadRef);
    final SolicitudProyectoUnidadVinculacion entity = this.buildMockSolicitudProyectoUnidadVinculacion(1L, 1L, unidadRef);

    final List<SolicitudProyectoUnidadVinculacionInput> inputList = Arrays.asList(input);

    BDDMockito.given(this.modelMapper.map(input, SolicitudProyectoUnidadVinculacion.class)).willReturn(entity);

    // when: se convierte la lista a entidades
    List<SolicitudProyectoUnidadVinculacion> result = this.solicitudProyectoUnidadVinculacionConverter
        .convertSolicitudProyectoUnidadesInput(inputList);

    // then: se obtiene una lista con un SolicitudProyectoUnidadVinculacion sin id
    Assertions.assertThat(result)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1);
    Assertions.assertThat(result.get(0).getId()).isNull();
  }

  @Test
  void convert_ReturnsSolicitudProyectoUnidadVinculacionOutputPage() {
    // given: una Page con una entidad SolicitudProyectoUnidadVinculacion
    final String unidadRef = "testing-ref";
    final Long id = 1L;
    final SolicitudProyectoUnidadVinculacion entity = this.buildMockSolicitudProyectoUnidadVinculacion(id, 1L, unidadRef);
    final SolicitudProyectoUnidadVinculacionOutput output = this.buildMockSolicitudProyectoUnidadVinculacionOutput(id, unidadRef);

    final List<SolicitudProyectoUnidadVinculacion> entities = Arrays.asList(entity);
    final Page<SolicitudProyectoUnidadVinculacion> page = new PageImpl<>(entities);

    BDDMockito.given(modelMapper.map(entity, SolicitudProyectoUnidadVinculacionOutput.class)).willReturn(output);

    // when: se convierte la Page a Page de SolicitudProyectoUnidadVinculacionOutput
    Page<SolicitudProyectoUnidadVinculacionOutput> result = this.solicitudProyectoUnidadVinculacionConverter.convert(page);

    // then: se obtiene una Page con un SolicitudProyectoUnidadVinculacionOutput
    // equivalente
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).isNotEmpty();
    Assertions.assertThat(result.getContent().get(0).getId()).isEqualTo(id);
    Assertions.assertThat(result.getContent().get(0).getUnidadVinculacionRef()).isEqualTo(unidadRef);
  }

  private SolicitudProyectoUnidadVinculacionInput buildMockSolicitudProyectoUnidadVinculacionInput(String unidadVinculacionRef) {
    return SolicitudProyectoUnidadVinculacionInput.builder()
        .unidadVinculacionRef(unidadVinculacionRef)
        .build();
  }

  private SolicitudProyectoUnidadVinculacion buildMockSolicitudProyectoUnidadVinculacion(Long id, Long solicitudProyectoId,
      String unidadVinculacionRef) {
    return SolicitudProyectoUnidadVinculacion.builder()
        .id(id)
        .unidadVinculacionRef(unidadVinculacionRef)
        .solicitudProyectoId(solicitudProyectoId)
        .build();
  }

  private SolicitudProyectoUnidadVinculacionOutput buildMockSolicitudProyectoUnidadVinculacionOutput(Long id,
      String unidadVinculacionRef) {
    return SolicitudProyectoUnidadVinculacionOutput.builder()
        .id(id)
        .unidadVinculacionRef(unidadVinculacionRef)
        .build();
  }

}
