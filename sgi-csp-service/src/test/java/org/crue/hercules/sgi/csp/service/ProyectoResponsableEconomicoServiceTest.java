package org.crue.hercules.sgi.csp.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoResponsableEconomicoProjectRangeException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

import javax.validation.Validator;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class ProyectoResponsableEconomicoServiceTest extends BaseServiceTest {

  @Mock
  private Validator validator;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private ProyectoRepository proyectoRepository;

  ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;

  @BeforeEach
  public void setup() {
    this.proyectoResponsableEconomicoService = new ProyectoResponsableEconomicoService(validator, proyectoResponsableEconomicoRepository, proyectoRepository);
  }

  @Test
  void updateProyectoResponsableEconomicos_WithResponsableEconomicoProyectoIdNull_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomico> responsablesEconomicos = Arrays.asList(buildMockProyectoResponsableEconomico(1L, null, "556699", Instant.now().plusSeconds(3600000), Instant.now()));
    List<ProyectoResponsableEconomico> responsablesEconomicosBD =Arrays.asList(buildMockProyectoResponsableEconomico(1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));

    BDDMockito.given(this.proyectoRepository.findById(proyectoId)).willReturn(Optional.of(Proyecto.builder()
    .id(proyectoId)
    .fechaInicio(Instant.now().minusSeconds(3600))
    .fechaFin(Instant.now().minusSeconds(3600))
    .build()));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId)).willReturn(responsablesEconomicosBD);

    Assertions.assertThatThrownBy(() -> this.proyectoResponsableEconomicoService.updateProyectoResponsableEconomicos(proyectoId, responsablesEconomicos))
    .isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  void updateProyectoResponsableEconomicos_WithResponsableEconomicoPersonaRefNull_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomico> responsablesEconomicos = Arrays.asList(buildMockProyectoResponsableEconomico(1L, proyectoId, null, Instant.now().plusSeconds(3600000), Instant.now()));
    List<ProyectoResponsableEconomico> responsablesEconomicosBD =Arrays.asList(buildMockProyectoResponsableEconomico(1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));

    BDDMockito.given(this.proyectoRepository.findById(proyectoId)).willReturn(Optional.of(Proyecto.builder()
    .id(proyectoId)
    .fechaInicio(Instant.now().minusSeconds(3600))
    .fechaFin(Instant.now().minusSeconds(3600))
    .build()));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId)).willReturn(responsablesEconomicosBD);

    Assertions.assertThatThrownBy(() -> this.proyectoResponsableEconomicoService.updateProyectoResponsableEconomicos(proyectoId, responsablesEconomicos))
    .isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  void updateProyectoResponsableEconomicos_WithFechaFinBeforeFechaInicio_ThrowsProyectoResponsableEconomicoProjectRangeException() {
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomico> responsablesEconomicos = Arrays.asList(buildMockProyectoResponsableEconomico(1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));
    List<ProyectoResponsableEconomico> responsablesEconomicosBD =Arrays.asList(buildMockProyectoResponsableEconomico(1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));
    
    BDDMockito.given(this.proyectoRepository.findById(proyectoId)).willReturn(Optional.of(Proyecto.builder()
    .id(proyectoId)
    .fechaInicio(Instant.now().minusSeconds(3600))
    .fechaFin(Instant.now().minusSeconds(3600))
    .build()));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId)).willReturn(responsablesEconomicosBD);

    Assertions.assertThatThrownBy(() -> this.proyectoResponsableEconomicoService.updateProyectoResponsableEconomicos(proyectoId, responsablesEconomicos))
    .isInstanceOf(ProyectoResponsableEconomicoProjectRangeException.class);

  }

  private ProyectoResponsableEconomico buildMockProyectoResponsableEconomico(Long id, Long proyectoId, String personaRef, Instant fechaFin, Instant fechaInicio) {
    return ProyectoResponsableEconomico.builder()
    .fechaFin(fechaFin)
    .fechaInicio(fechaInicio)
    .id(id)
    .proyectoId(proyectoId)
    .personaRef(personaRef)
    .build();
  }
}