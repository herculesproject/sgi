package org.crue.hercules.sgi.csp.util;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test unitario de {@link EjecucionEconomicaInvestigadorAuthorityHelper}.
 */
@ExtendWith(MockitoExtension.class)
class EjecucionEconomicaInvestigadorAuthorityHelperTest {

  private static final String PROYECTO_SGE_REF = "proyecto-sge-ref-001";
  private static final String PERSONA_REF = "user-001";

  @Mock
  private ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  @Mock
  private GrupoRepository grupoRepository;
  @Mock
  private ProyectoHelper proyectoHelper;

  private EjecucionEconomicaInvestigadorAuthorityHelper helper;

  @BeforeEach
  void setUp() {
    helper = new EjecucionEconomicaInvestigadorAuthorityHelper(proyectoProyectoSgeRepository, grupoRepository,
        proyectoHelper);
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(PERSONA_REF, null));
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void isAccesibleByInvestigador_whenIsInvestigadorPrincipalDeProyecto_returnsTrue() {
    // given: el usuario es investigador principal de un proyecto asociado al SGE
    Mockito.when(proyectoProyectoSgeRepository.count(ArgumentMatchers.<Specification<ProyectoProyectoSge>>any()))
        .thenReturn(1L);

    // when: se comprueba si el proyecto SGE es accesible por el investigador
    boolean accesible = helper.isAccesibleByInvestigador(PROYECTO_SGE_REF);

    // then: es accesible y no se evalua el grupo si ya lo es como IP de proyecto
    Assertions.assertThat(accesible).isTrue();
    Mockito.verify(grupoRepository, Mockito.never()).count(ArgumentMatchers.<Specification<Grupo>>any());
  }

  @Test
  void isAccesibleByInvestigador_whenIsResponsableDeGrupo_returnsTrue() {
    // given: el usuario no es IP de ningun proyecto pero si responsable de un grupo
    Mockito.when(proyectoProyectoSgeRepository.count(ArgumentMatchers.<Specification<ProyectoProyectoSge>>any()))
        .thenReturn(0L);
    Mockito.when(grupoRepository.count(ArgumentMatchers.<Specification<Grupo>>any())).thenReturn(1L);

    // when: se comprueba si el proyecto SGE es accesible por el investigador
    boolean accesible = helper.isAccesibleByInvestigador(PROYECTO_SGE_REF);

    // then: es accesible al ser responsable de grupo
    Assertions.assertThat(accesible).isTrue();
  }

  @Test
  void isAccesibleByInvestigador_whenNiIpNiResponsable_returnsFalse() {
    // given: el usuario no es ni IP de proyecto ni responsable de grupo
    Mockito.when(proyectoProyectoSgeRepository.count(ArgumentMatchers.<Specification<ProyectoProyectoSge>>any()))
        .thenReturn(0L);
    Mockito.when(grupoRepository.count(ArgumentMatchers.<Specification<Grupo>>any())).thenReturn(0L);

    // when: se comprueba si el proyecto SGE es accesible por el investigador
    boolean accesible = helper.isAccesibleByInvestigador(PROYECTO_SGE_REF);

    // then: no es accesible
    Assertions.assertThat(accesible).isFalse();
  }

}
