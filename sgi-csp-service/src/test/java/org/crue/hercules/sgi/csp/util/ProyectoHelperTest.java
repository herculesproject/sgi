package org.crue.hercules.sgi.csp.util;

import java.util.Collections;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ProyectoHelperTest
 */
class ProyectoHelperTest extends BaseServiceTest {

  @Mock
  private ProyectoRepository repository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;

  private ProyectoHelper proyectoHelper;

  @BeforeEach
  void setUp() {
    proyectoHelper = new ProyectoHelper(repository, proyectoEquipoRepository, proyectoResponsableEconomicoRepository);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @CsvSource({
      "CSP-PRO-E_2, true", // gestor de la unidad de gestión del proyecto
      "CSP-PRO-V_2, true", // visor de la unidad de gestión del proyecto
      "CSP-PRO-MOD-V, true", // visor de proyectos desde otros módulos
      "CSP-PRO-V_3, false", // visor de una unidad de gestión distinta a la del proyecto
      "CSP-PRO-INV-VR, false" // investigador, sin rol de gestión
  })
  void hasUserAuthorityViewAsGestorOrVisor_DependingOnAuthority_ReturnsExpected(String authority, boolean expected) {
    // given: un usuario autenticado con la autoridad indicada, sobre un proyecto de
    // la unidad "2"
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("user", null,
            Collections.singletonList(new SimpleGrantedAuthority(authority))));
    Proyecto proyecto = Proyecto.builder().unidadGestionRef("2").build();

    // when: se comprueba si accede como gestión o visor
    boolean result = proyectoHelper.hasUserAuthorityViewAsGestorOrVisor(proyecto);

    // then: coincide con lo esperado para esa autoridad
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-V_2" })
  void hasUserAuthorityViewAsGestorOrVisor_ByProyectoId_UserIsVisorOfUO_ReturnsTrue() {
    // given: un proyectoId existente cuyo proyecto pertenece a la UO del visor
    Long proyectoId = 1L;
    Proyecto proyecto = Proyecto.builder().id(proyectoId).unidadGestionRef("2").build();
    BDDMockito.given(repository.findById(proyectoId)).willReturn(Optional.of(proyecto));

    // when: se comprueba si accede como gestión a partir del id
    boolean result = proyectoHelper.hasUserAuthorityViewAsGestorOrVisor(proyectoId);

    // then: se considera acceso de gestión
    Assertions.assertThat(result).isTrue();
  }

  @Test
  void hasUserAuthorityViewAsGestorOrVisor_ByProyectoId_ProyectoNotFound_ThrowsProyectoNotFoundException() {
    // given: un proyectoId que no existe
    Long proyectoId = 1L;
    BDDMockito.given(repository.findById(proyectoId)).willReturn(Optional.empty());

    // when: se comprueba si accede como gestión a partir del id
    // then: se lanza ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> proyectoHelper.hasUserAuthorityViewAsGestorOrVisor(proyectoId))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

}
