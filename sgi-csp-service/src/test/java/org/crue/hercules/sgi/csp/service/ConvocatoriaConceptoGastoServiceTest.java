package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.mapper.ConvocatoriaConceptoGastoMapper;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaConceptoGastoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConvocatoriaConceptoGastoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ConvocatoriaConceptoGastoServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaConceptoGastoRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConceptoGastoRepository conceptoGastoRepository;
  @Mock
  private ConvocatoriaConceptoGastoService service;
  @Mock
  private ConvocatoriaConceptoGastoMapper convocatoriaConceptoGastoMapper;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaConceptoGastoServiceImpl(repository, convocatoriaRepository, conceptoGastoRepository,
        convocatoriaConceptoGastoMapper, convocatoriaConceptoGastoCodigoEcRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaConceptoGasto() {
    // given: Un nuevo ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaConceptoGasto.getConvocatoria()));
    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaConceptoGasto.getConceptoGasto()));
    BDDMockito
        .given(repository.findByConvocatoriaIdAndConceptoGastoActivoTrueAndConceptoGastoIdAndPermitidoIs(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.save(newConvocatoriaConceptoGasto)).will((InvocationOnMock invocation) -> {
      ConvocatoriaConceptoGasto convocatoriaConceptoGastoCreado = invocation.getArgument(0);
      convocatoriaConceptoGastoCreado.setId(1L);
      return convocatoriaConceptoGastoCreado;
    });

    // when: Creamos el ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoCreado = service.create(newConvocatoriaConceptoGasto);

    // then: El ConvocatoriaConceptoGasto se crea correctamente
    Assertions.assertThat(convocatoriaConceptoGastoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaConceptoGastoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(newConvocatoriaConceptoGasto.getObservaciones());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGasto que ya tiene id
    ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);

    // when: Creamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque el ConvocatoriaConceptoGasto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(newConvocatoriaConceptoGasto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaConceptoGasto");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGasto sin convocatoria
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null);
    convocatoriaConceptoGasto.getConvocatoria().setId(null);

    // when: Creamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGasto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaConceptoGasto");
  }

  @Test
  public void create_WithConceptoGastoInactivo_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGasto con el ConceptoGasto inactivo
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null);
    convocatoriaConceptoGasto.getConceptoGasto().setActivo(false);

    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGasto.getConceptoGasto()));

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el enlace está inactivo
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGasto))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El ConceptoGasto debe estar activo");
  }

  @Test
  public void update_ReturnsConvocatoriaConceptoGasto() {
    // given: Un nuevo ConvocatoriaConceptoGasto con el nombre actualizado
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoDescripcionActualizada = generarMockConvocatoriaConceptoGasto(
        1L);
    convocatoriaConceptoGastoDescripcionActualizada.setObservaciones("Observaciones actualizadas");

    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGasto.getConceptoGasto()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaConceptoGasto));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaConceptoGasto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizado = service
        .update(convocatoriaConceptoGastoDescripcionActualizada);

    // then: El ConvocatoriaConceptoGasto se actualiza correctamente.
    Assertions.assertThat(convocatoriaConceptoGastoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getId()).as("getId()")
        .isEqualTo(convocatoriaConceptoGasto.getId());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaConceptoGastoDescripcionActualizada.getObservaciones());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConceptoGasto()).as("getConceptoGasto()")
        .isEqualTo(convocatoriaConceptoGasto.getConceptoGasto());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConvocatoria()).as("getConvocatoria()")
        .isEqualTo(convocatoriaConceptoGasto.getConvocatoria());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaConceptoGastoNotFoundException() {
    // given: Un ConvocatoriaConceptoGasto actualizado con un id que no existe
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);

    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGasto.getConceptoGasto()));

    // when: Actualizamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque el ConvocatoriaConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGasto))
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  @Test
  public void update_WithConceptoGastoIdNoExists_ThrowsConceptoGastoNotFoundException() {
    // given: Un nuevo ConvocatoriaConceptoGasto sin tipo de enlace
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar = generarMockConvocatoriaConceptoGasto(1L);

    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());
    // when: Creamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque el concepto gasto es null
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoActualizar))
        .isInstanceOf(ConceptoGastoNotFoundException.class).hasMessage("ConceptoGasto 1 does not exist.");
  }

  @Test
  public void update_WithoutConceptoGasto_ReturnsConvocatoriaConceptoGasto() {
    // given: Un nuevo ConvocatoriaConceptoGasto con el nombre actualizado
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoDescripcionActualizada = generarMockConvocatoriaConceptoGasto(
        1L);
    convocatoriaConceptoGastoDescripcionActualizada.setObservaciones("Observaciones actualizadas");
    convocatoriaConceptoGastoDescripcionActualizada.setConceptoGasto(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaConceptoGasto));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaConceptoGasto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizado = service
        .update(convocatoriaConceptoGastoDescripcionActualizada);

    // then: El ConvocatoriaConceptoGasto se actualiza correctamente.
    Assertions.assertThat(convocatoriaConceptoGastoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getId()).as("getId()")
        .isEqualTo(convocatoriaConceptoGasto.getId());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaConceptoGastoDescripcionActualizada.getObservaciones());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConceptoGasto()).as("getConceptoGasto()").isNull();
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConvocatoria()).as("getConvocatoria()")
        .isEqualTo(convocatoriaConceptoGasto.getConvocatoria());
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing convocatoriaConceptoGasto
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
  public void delete_WithoutId_ThrowsIllegalArgumentException() throws Exception {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class);
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
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsConvocatoriaConceptoGasto() {
    // given: Un ConvocatoriaConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGasto(idBuscado)));

    // when: Buscamos el ConvocatoriaConceptoGasto por su id
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = service.findById(idBuscado);

    // then: el ConvocatoriaConceptoGasto
    Assertions.assertThat(convocatoriaConceptoGasto).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGasto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaConceptoGasto.getObservaciones()).as("getObservaciones()").isEqualTo("Obs-1");
    Assertions.assertThat(convocatoriaConceptoGasto.getConvocatoria().getId()).as("getConvocatoria()").isEqualTo(1L);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaConceptoGastoNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaConceptoGasto por su id
    // then: lanza un ConvocatoriaConceptoGastoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id     id del ConvocatoriaConceptoGasto
   * @param nombre nombre del ConvocatoriaConceptoGasto
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id) {

    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);

    conceptoGasto.setActivo(true);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId(id);
    convocatoriaConceptoGasto.setConvocatoria(convocatoria);
    convocatoriaConceptoGasto.setObservaciones("Obs-" + id);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setImporteMaximo(400.0);
    convocatoriaConceptoGasto.setNumMeses(4);
    convocatoriaConceptoGasto.setPermitido(true);
    convocatoriaConceptoGasto.setPorcentajeCosteIndirecto(3);

    return convocatoriaConceptoGasto;
  }

}
