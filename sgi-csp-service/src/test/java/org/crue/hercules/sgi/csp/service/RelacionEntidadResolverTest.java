package org.crue.hercules.sgi.csp.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.pii.InvencionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionEntidadOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiPiiService;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;

class RelacionEntidadResolverTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private GrupoRepository grupoRepository;
  @Mock
  private ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  @Mock
  private SgiApiPiiService sgiApiPiiService;

  private RelacionEntidadResolver resolver;

  @BeforeEach
  void setup() {
    this.resolver = new RelacionEntidadResolver(convocatoriaRepository, proyectoRepository, grupoRepository,
        proyectoProyectoSgeRepository, sgiApiPiiService);
  }

  @Test
  void resolve_NullArguments_ReturnsNull() {
    // given: argumentos nulos (tipo de entidad o identificador)
    // when: se resuelve la entidad relacionada
    // then: se devuelve null en ambos casos
    Assertions.assertThat(resolver.resolve(null, 1L)).isNull();
    Assertions.assertThat(resolver.resolve(TipoEntidad.INVENCION, null)).isNull();
  }

  @Test
  void resolve_Invencion_DelegatesToPii() {
    // given: PII devuelve la invencion
    InvencionOutput invencion = InvencionOutput.builder()
        .id(10L)
        .titulo(Collections.singletonList(new I18nFieldValueDto(Language.ES, "Invencion 10")))
        .build();
    BDDMockito.given(sgiApiPiiService.findInvencionById(10L)).willReturn(invencion);

    // when: se resuelve la entidad relacionada de tipo INVENCION
    RelacionEntidadOutput result = resolver.resolve(TipoEntidad.INVENCION, 10L);

    // then: se devuelve la invencion con su identificador y titulo
    Assertions.assertThat(result.getId()).isEqualTo(10L);
    Assertions.assertThat(result.getTitulo()).hasSize(1);
  }

  @Test
  void resolve_Convocatoria_ReturnsTitulo() {
    // given: el repositorio devuelve la convocatoria con su titulo
    Convocatoria convocatoria = Convocatoria.builder()
        .id(8L)
        .titulo(Set.of(new ConvocatoriaTitulo(Language.ES, "Convocatoria 8")))
        .build();
    BDDMockito.given(convocatoriaRepository.findById(8L)).willReturn(Optional.of(convocatoria));

    // when: se resuelve la entidad relacionada de tipo CONVOCATORIA
    RelacionEntidadOutput result = resolver.resolve(TipoEntidad.CONVOCATORIA, 8L);

    // then: se devuelve la convocatoria con su identificador y titulo
    Assertions.assertThat(result.getId()).isEqualTo(8L);
    Assertions.assertThat(result.getTitulo()).hasSize(1);
  }

  @Test
  void resolve_Proyecto_ReturnsTituloCodigoExternoYCodigosSge() {
    // given: el proyecto existe y tiene varios codigos SGE asociados
    Proyecto proyecto = Proyecto.builder()
        .id(7L)
        .codigoExterno("EXT-7")
        .titulo(Set.of(new ProyectoTitulo(Language.ES, "Proyecto 7")))
        .build();
    BDDMockito.given(proyectoRepository.findById(7L)).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoProyectoSgeRepository.findAll(ArgumentMatchers.<Specification<ProyectoProyectoSge>>any()))
        .willReturn(List.of(
            ProyectoProyectoSge.builder().proyectoSgeRef("SGE-1").build(),
            ProyectoProyectoSge.builder().proyectoSgeRef("SGE-2").build()));

    // when: se resuelve la entidad relacionada de tipo PROYECTO
    RelacionEntidadOutput result = resolver.resolve(TipoEntidad.PROYECTO, 7L);

    // then: se devuelve el proyecto con titulo, codigo externo y codigos SGE concatenados
    Assertions.assertThat(result.getId()).isEqualTo(7L);
    Assertions.assertThat(result.getCodigoExterno()).isEqualTo("EXT-7");
    Assertions.assertThat(result.getCodigosSge()).isEqualTo("SGE-1, SGE-2");
  }

  @Test
  void resolve_Grupo_ReturnsNombreYCodigoSge() {
    // given: el grupo existe con su nombre y codigo SGE
    Grupo grupo = new Grupo();
    grupo.setId(9L);
    grupo.setNombre(Set.of(new GrupoNombre(Language.ES, "Grupo 9")));
    grupo.setProyectoSgeRef("SGE-G");
    BDDMockito.given(grupoRepository.findById(9L)).willReturn(Optional.of(grupo));

    // when: se resuelve la entidad relacionada de tipo GRUPO
    RelacionEntidadOutput result = resolver.resolve(TipoEntidad.GRUPO, 9L);

    // then: se devuelve el grupo con su identificador, nombre (titulo) y codigo SGE
    Assertions.assertThat(result.getId()).isEqualTo(9L);
    Assertions.assertThat(result.getTitulo()).hasSize(1);
    Assertions.assertThat(result.getCodigosSge()).isEqualTo("SGE-G");
  }

  @Test
  void resolve_EntityNotFound_ReturnsIdOnly() {
    // given: el proyecto no existe
    BDDMockito.given(proyectoRepository.findById(7L)).willReturn(Optional.empty());

    // when: se resuelve la entidad relacionada de tipo PROYECTO
    RelacionEntidadOutput result = resolver.resolve(TipoEntidad.PROYECTO, 7L);

    // then: se devuelve solo el identificador, sin titulo
    Assertions.assertThat(result.getId()).isEqualTo(7L);
    Assertions.assertThat(result.getTitulo()).isNull();
  }

}
