package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaHitoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaHitoServiceTest
 */
@ExtendWith(MockitoExtension.class)

public class ConvocatoriaHitoServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaHitoRepository repository;

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  @Mock
  private ModeloTipoHitoRepository modeloTipoHitoRepository;

  private ConvocatoriaHitoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaHitoServiceImpl(repository, convocatoriaRepository, modeloTipoHitoRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaHito() {
    // given: Un nuevo ConvocatoriaHito
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHito, Boolean.TRUE)));

    BDDMockito.given(repository.save(convocatoriaHito)).will((InvocationOnMock invocation) -> {
      ConvocatoriaHito convocatoriaHitoCreado = invocation.getArgument(0);
      convocatoriaHitoCreado.setId(1L);
      return convocatoriaHitoCreado;
    });

    // when: Creamos el ConvocatoriaHito
    ConvocatoriaHito convocatoriaHitoCreado = service.create(convocatoriaHito);

    // then: El ConvocatoriaHito se crea correctamente
    Assertions.assertThat(convocatoriaHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getFecha()).as("getFechaInicio()")
        .isEqualTo(convocatoriaHito.getFecha());
    Assertions.assertThat(convocatoriaHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHito.getGeneraAviso());
  }

  @Test
  public void create_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ConvocatoriaHito con fecha pasada
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.setFecha(LocalDate.now().minusDays(2));
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHito, Boolean.TRUE)));

    BDDMockito.given(repository.save(convocatoriaHito)).will((InvocationOnMock invocation) -> {
      ConvocatoriaHito convocatoriaHitoCreado = invocation.getArgument(0);
      convocatoriaHitoCreado.setId(1L);
      return convocatoriaHitoCreado;
    });

    // when: Creamos el ConvocatoriaHito
    ConvocatoriaHito convocatoriaHitoCreado = service.create(convocatoriaHito);

    // then: El ConvocatoriaHito se crea correctamente con GenerarAviso como FALSE
    Assertions.assertThat(convocatoriaHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getFecha()).as("getFechaInicio()")
        .isEqualTo(convocatoriaHito.getFecha());
    Assertions.assertThat(convocatoriaHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHito.getGeneraAviso());
    Assertions.assertThat(convocatoriaHitoCreado.getGeneraAviso()).as("getGeneraAviso()").isEqualTo(Boolean.FALSE);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaHito que ya tiene id
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    // when: Creamos el ConvocatoriaHito
    // then: Lanza una excepcion porque el ConvocatoriaHito ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaHito)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaHito id tiene que ser null para crear un nuevo ConvocatoriaHito");
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito without ConvocatoriaId
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getConvocatoria().setId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaHito");
  }

  @Test
  public void create_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito without fecha
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));

    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHito, Boolean.TRUE)));

    BDDMockito.given(repository.findByFechaAndTipoHitoId(ArgumentMatchers.any(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as fecha is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaHito with non existing Convocatoria
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito without tipoHitoId
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getTipoHito().setId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as tipoHitoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Hito no puede ser null para crear ConvocatoriaHito");
  }

  @Test
  public void create_WithoutModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaHito con TipoHito no asignado al Modelo de Ejecucion de la
    // convocatoria
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as ModeloTipoHito not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "TipoHito '%s' no disponible para el ModeloEjecucion '%s'", convocatoriaHito.getTipoHito().getNombre(),
            convocatoriaHito.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaHito con la asignación de TipoHito al Modelo de Ejecucion
    // de la convocatoria inactiva
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHito, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoHito '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaHito.getTipoHito().getNombre(),
            convocatoriaHito.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaHito TipoHito disabled
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getTipoHito().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHito, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as TipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no está activo", convocatoriaHito.getTipoHito().getNombre());
  }

  @Test
  public void update_ReturnsConvocatoriaHito() {
    // given: Un nuevo ConvocatoriaHito con el tipoHito actualizado
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoActualizado.setTipoHito(generarMockTipoHito(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHitoActualizado, Boolean.TRUE)));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaHito
    ConvocatoriaHito updated = service.update(convocatoriaHitoActualizado);

    // then: El ConvocatoriaHito se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(convocatoriaHito.getId());
    Assertions.assertThat(updated.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(updated.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHitoActualizado.getComentario());
    Assertions.assertThat(updated.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHitoActualizado.getTipoHito().getId());
    Assertions.assertThat(updated.getFecha()).as("getFecha()").isEqualTo(convocatoriaHitoActualizado.getFecha());
    Assertions.assertThat(updated.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHitoActualizado.getGeneraAviso());
  }

  @Test
  public void update_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ConvocatoriaHito con el la fecha anterior
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoActualizado.setTipoHito(generarMockTipoHito(2L, Boolean.TRUE));
    convocatoriaHitoActualizado.setFecha(LocalDate.now().minusDays(2));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHitoActualizado, Boolean.TRUE)));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaHito
    ConvocatoriaHito updated = service.update(convocatoriaHitoActualizado);

    // then: El ConvocatoriaHito se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(convocatoriaHito.getId());
    Assertions.assertThat(updated.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(updated.getComentario()).as("getComentario()").isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(updated.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(updated.getFecha()).as("getFecha()").isEqualTo(convocatoriaHitoActualizado.getFecha());
    Assertions.assertThat(updated.getGeneraAviso()).as("getGeneraAviso()").isEqualTo(Boolean.FALSE);
  }

  @Test
  public void update_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: Un ConvocatoriaHito a actualizar sin fecha
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, convocatoriaHitoActualizado, Boolean.TRUE)));

    BDDMockito.given(repository.findByFechaAndTipoHitoId(ArgumentMatchers.any(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaHito(2L)));

    // when: Actualizamos el ConvocatoriaHito
    // then: Lanza una excepcion porque la fecha no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaHitoActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaHitoNotFoundException() {
    // given: Un ConvocatoriaHito a actualizar con un id que no existe
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaHito
    // then: Lanza una excepcion porque el ConvocatoriaHito no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaHito))
        .isInstanceOf(ConvocatoriaHitoNotFoundException.class);
  }

  @Test
  public void update_WithoutModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaHito con TipoHito no asignado al Modelo de Ejecucion de la
    // convocatoria
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoActualizado.setTipoHito(generarMockTipoHito(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaHito
        () -> service.update(convocatoriaHitoActualizado))
        // then: throw exception as ModeloTipoHito not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaHitoActualizado.getTipoHito().getNombre(),
            convocatoriaHitoActualizado.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaHito con la asignación de TipoHito al Modelo de Ejecucion
    // de la convocatoria inactiva
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoActualizado.setTipoHito(generarMockTipoHito(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(2L, convocatoriaHitoActualizado, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaHito
        () -> service.update(convocatoriaHitoActualizado))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoHito '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaHitoActualizado.getTipoHito().getNombre(),
            convocatoriaHitoActualizado.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaHito TipoHito disabled
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoActualizado.setTipoHito(generarMockTipoHito(2L, Boolean.FALSE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoHito(2L, convocatoriaHitoActualizado, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaHito
        () -> service.update(convocatoriaHitoActualizado))
        // then: throw exception as TipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no está activo", convocatoriaHitoActualizado.getTipoHito().getNombre());
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ConvocatoriaHito
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaHitoNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaHito para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaHito> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaHito(Long.valueOf(i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaHito>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaHito> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaHito> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaHito> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaHito convocatoriaHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaHito.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findById_ReturnsConvocatoriaHito() {
    // given: Un ConvocatoriaHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConvocatoriaHito(idBuscado)));

    // when: Buscamos el ConvocatoriaHito por su id
    ConvocatoriaHito convocatoriaHito = service.findById(idBuscado);

    // then: el ConvocatoriaHito
    Assertions.assertThat(convocatoriaHito).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHito.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaHitoNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaHito por su id
    // then: lanza un ConvocatoriaHitoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaHitoNotFoundException.class);
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()//
            .id(modeloEjecucionId)//
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))//
            .activo(Boolean.TRUE)//
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .modeloEjecucion(modeloEjecucion)//
            .tipoFinalidad(tipoFinalidad)//
            .activo(Boolean.TRUE)//
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()//
            .id(tipoRegimenConcurrenciaId)//
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()//
            .id(tipoAmbitoGeogragicoId)//
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))//
            .activo(Boolean.TRUE)//
            .build();

    Convocatoria convocatoria = Convocatoria.builder()//
        .id(convocatoriaId)//
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo-" + String.format("%03d", convocatoriaId))//
        .anio(2020)//
        .titulo("titulo-" + String.format("%03d", convocatoriaId))//
        .objeto("objeto-" + String.format("%03d", convocatoriaId))//
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))//
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(TipoDestinatarioEnum.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estadoActual(TipoEstadoConvocatoriaEnum.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVNEnum.AYUDAS)//
        .activo(activo)//
        .build();

    return convocatoria;
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id     id del TipoHito
   * @param activo
   * @return el objeto TipoHito
   */
  private TipoHito generarMockTipoHito(Long id, Boolean activo) {

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre("nombre-" + id);
    tipoHito.setDescripcion("descripcion-" + id);
    tipoHito.setActivo(activo);

    return tipoHito;
  }

  /**
   * Función que genera ModeloTipoHito a partir de un objeto ConvocatoriaHito
   * 
   * @param id
   * @param convocatoriaHito
   * @param activo
   * @return
   */
  private ModeloTipoHito generarMockModeloTipoHito(Long id, ConvocatoriaHito convocatoriaHito, Boolean activo) {

    return ModeloTipoHito.builder()//
        .id(id)//
        .modeloEjecucion(convocatoriaHito.getConvocatoria().getModeloEjecucion())//
        .tipoHito(convocatoriaHito.getTipoHito())//
        .activo(activo)//
        .build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaHito
   * 
   * @param id id del ConvocatoriaHito
   * @return el objeto ConvocatoriaHito
   */
  private ConvocatoriaHito generarMockConvocatoriaHito(Long id) {

    return ConvocatoriaHito.builder()//
        .id(id)//
        .convocatoria(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE))//
        .fecha(LocalDate.of(2020, 10, 19))//
        .comentario("comentario" + id)//
        .generaAviso(Boolean.TRUE)//
        .tipoHito(generarMockTipoHito(1L, Boolean.TRUE))//
        .build();
  }
}
