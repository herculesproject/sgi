package org.crue.hercules.sgi.csp.service;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.rel.ProyectoRelacionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionEntidadOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRelService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class ProyectoRelacionServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoHelper proyectoHelper;
  @Mock
  private SgiApiRelService sgiApiRelService;
  @Mock
  private RelacionEntidadResolver relacionEntidadResolver;

  private ProyectoRelacionService proyectoRelacionService;

  @BeforeEach
  void setup() {
    this.proyectoRelacionService = new ProyectoRelacionService(proyectoHelper, sgiApiRelService,
        relacionEntidadResolver);
  }

  @Test
  void findRelacionesProyecto_WhenProyectoIsOrigen_EnrichesEntidadDestino() {
    // given: una relacion en la que el proyecto actual es el origen y el destino es
    // una invencion
    Long proyectoId = 5L;
    RelacionOutput relacion = RelacionOutput.builder()
        .id(1L)
        .tipoEntidadOrigen(TipoEntidad.PROYECTO)
        .entidadOrigenRef(proyectoId.toString())
        .tipoEntidadDestino(TipoEntidad.INVENCION)
        .entidadDestinoRef("10")
        .build();
    BDDMockito.given(sgiApiRelService.findRelacionesProyecto(proyectoId))
        .willReturn(Collections.singletonList(relacion));
    BDDMockito.given(relacionEntidadResolver.resolve(TipoEntidad.INVENCION, 10L))
        .willReturn(RelacionEntidadOutput.builder().id(10L).build());

    // when: se recuperan las relaciones del proyecto
    List<ProyectoRelacionOutput> result = this.proyectoRelacionService.findRelacionesProyecto(proyectoId);

    // then: se comprueba el acceso como miembro y la entidad relacionada es el
    // destino (la invencion), resuelta por el resolver
    BDDMockito.verify(proyectoHelper).checkCanAccessProyecto(proyectoId,
        ProyectoHelper.InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA);
    BDDMockito.verify(relacionEntidadResolver).resolve(TipoEntidad.INVENCION, 10L);
    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0).getTipoEntidadRelacionada()).isEqualTo(TipoEntidad.INVENCION);
    Assertions.assertThat(result.get(0).getEntidadRelacionada().getId()).isEqualTo(10L);
  }

  @Test
  void findRelacionesProyecto_WhenProyectoIsDestino_EnrichesEntidadOrigen() {
    // given: una relacion en la que el proyecto actual es el destino y el origen es
    // un grupo
    Long proyectoId = 5L;
    RelacionOutput relacion = RelacionOutput.builder()
        .id(1L)
        .tipoEntidadOrigen(TipoEntidad.GRUPO)
        .entidadOrigenRef("20")
        .tipoEntidadDestino(TipoEntidad.PROYECTO)
        .entidadDestinoRef(proyectoId.toString())
        .build();
    BDDMockito.given(sgiApiRelService.findRelacionesProyecto(proyectoId))
        .willReturn(Collections.singletonList(relacion));
    BDDMockito.given(relacionEntidadResolver.resolve(TipoEntidad.GRUPO, 20L))
        .willReturn(RelacionEntidadOutput.builder().id(20L).build());

    // when: se recuperan las relaciones del proyecto
    List<ProyectoRelacionOutput> result = this.proyectoRelacionService.findRelacionesProyecto(proyectoId);

    // then: la entidad relacionada es el origen (el grupo)
    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0).getTipoEntidadRelacionada()).isEqualTo(TipoEntidad.GRUPO);
    Assertions.assertThat(result.get(0).getEntidadRelacionada().getId()).isEqualTo(20L);
  }

  @Test
  void findRelacionesProyecto_WhenResolverFails_ReturnIdOnly() {
    // given: el resolver falla al recuperar la entidad relacionada
    Long proyectoId = 5L;
    RelacionOutput relacion = RelacionOutput.builder()
        .id(1L)
        .tipoEntidadOrigen(TipoEntidad.PROYECTO)
        .entidadOrigenRef(proyectoId.toString())
        .tipoEntidadDestino(TipoEntidad.INVENCION)
        .entidadDestinoRef("10")
        .build();
    BDDMockito.given(sgiApiRelService.findRelacionesProyecto(proyectoId))
        .willReturn(Collections.singletonList(relacion));
    BDDMockito.given(relacionEntidadResolver.resolve(TipoEntidad.INVENCION, 10L))
        .willThrow(new RuntimeException("PII no disponible"));

    // when: se recuperan las relaciones del proyecto
    List<ProyectoRelacionOutput> result = this.proyectoRelacionService.findRelacionesProyecto(proyectoId);

    // then: la relacion se devuelve con la entidad reducida a solo su identificador
    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0).getEntidadRelacionada().getId()).isEqualTo(10L);
    Assertions.assertThat(result.get(0).getEntidadRelacionada().getTitulo()).isNull();
  }

  @Test
  void findRelacionesProyecto_WithoutAccess_ThrowsExceptionAndDoesNotCallRel() {
    // given: el usuario no tiene acceso al proyecto
    Long proyectoId = 5L;
    BDDMockito.willThrow(new UserNotAuthorizedToAccessProyectoException())
        .given(proyectoHelper)
        .checkCanAccessProyecto(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

    // when: se recuperan las relaciones del proyecto
    // then: se propaga la excepcion y no se invoca a REL
    Assertions.assertThatThrownBy(() -> this.proyectoRelacionService.findRelacionesProyecto(proyectoId))
        .isInstanceOf(UserNotAuthorizedToAccessProyectoException.class);
    BDDMockito.verify(sgiApiRelService, BDDMockito.never()).findRelacionesProyecto(ArgumentMatchers.anyLong());
  }

}
