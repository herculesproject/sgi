package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.mapper.ConvocatoriaConceptoGastoCodigoEcMapper;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaConceptoGastoCodigoEcServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConvocatoriaConceptoGastoCodigoEcServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ConvocatoriaConceptoGastoCodigoEcServiceTest {

  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository repository;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcMapper convocatoriaConceptoGastoCodigoEcMapper;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaConceptoGastoCodigoEcServiceImpl(repository, convocatoriaConceptoGastoRepository,
        convocatoriaConceptoGastoCodigoEcMapper);
  }

  @Test
  public void create_ReturnsConvocatoriaConceptoGastoCodigoEc() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc
    ConvocatoriaConceptoGastoCodigoEc newConvocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        null);

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto()));

    BDDMockito.given(repository.save(newConvocatoriaConceptoGastoCodigoEc)).will((InvocationOnMock invocation) -> {
      ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCreado = invocation.getArgument(0);
      convocatoriaConceptoGastoCreado.setId(1L);
      return convocatoriaConceptoGastoCreado;
    });

    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcCreado = service
        .create(newConvocatoriaConceptoGastoCodigoEc);

    // then: El ConvocatoriaConceptoGastoCodigoEc se crea correctamente
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcCreado.getConvocatoriaConceptoGasto().getObservaciones())
        .as("getObservaciones()")
        .isEqualTo(newConvocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getObservaciones());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc que ya tiene id
    ConvocatoriaConceptoGastoCodigoEc newConvocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);

    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque el ConvocatoriaConceptoGastoCodigoEc ya
    // tiene id
    Assertions.assertThatThrownBy(() -> service.create(newConvocatoriaConceptoGastoCodigoEc))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaConceptoGastoCodigoEc");
  }

  @Test
  public void create_WithoutConvocatoriaConceptoGasto_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc sin convocatoria
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        null);
    convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().setId(null);

    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGastoCodigoEc))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id ConvocatoriaConceptoGasto no puede ser null para crear ConvocatoriaConceptoGastoCodigoEc");
  }

  @Test
  public void update_ReturnsConvocatoriaConceptoGastoCodigoEc() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc con el nombre actualizado
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcCodigoActualizado = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    convocatoriaConceptoGastoCodigoEcCodigoActualizado.setCodigoEconomicoRef("Cod actualizado");

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEcCodigoActualizado.getConvocatoriaConceptoGasto()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEcCodigoActualizado));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaConceptoGastoCodigoEc
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizado = service
        .update(convocatoriaConceptoGastoCodigoEcCodigoActualizado);

    // then: El ConvocatoriaConceptoGastoCodigoEc se actualiza correctamente.
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcActualizado.getId()).as("getId()")
        .isEqualTo(convocatoriaConceptoGastoCodigoEc.getId());
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcActualizado.getCodigoEconomicoRef())
        .as("getCodigoEconomicoRef()")
        .isEqualTo(convocatoriaConceptoGastoCodigoEcCodigoActualizado.getCodigoEconomicoRef());
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEcActualizado.getConvocatoriaConceptoGasto())
        .as("getConvocatoriaConceptoGasto()")
        .isEqualTo(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaConceptoGastoCodigoEcNotFoundException() {
    // given: Un ConvocatoriaConceptoGastoCodigoEc actualizado con un id que no
    // existe
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto()));

    // when: Actualizamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque el ConvocatoriaConceptoGastoCodigoEc no
    // existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoCodigoEc))
        .isInstanceOf(ConvocatoriaConceptoGastoCodigoEcNotFoundException.class);
  }

  @Test
  public void update_WithConvocatoriaConceptoGastoIdNoExists_ThrowsConceptoGastoNotFoundException() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc sin tipo de enlace
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());
    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque el concepto gasto es null
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoCodigoEcActualizar))
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class)
        .hasMessage("ConvocatoriaConceptoGasto 1 does not exist.");
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
        .isInstanceOf(ConvocatoriaConceptoGastoCodigoEcNotFoundException.class);
  }

  @Test
  public void findById_ReturnsConvocatoriaConceptoGastoCodigoEc() {
    // given: Un ConvocatoriaConceptoGastoCodigoEc con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGastoCodigoEc(idBuscado)));

    // when: Buscamos el ConvocatoriaConceptoGastoCodigoEc por su id
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = service.findById(idBuscado);

    // then: el ConvocatoriaConceptoGastoCodigoEc
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef()).as("getCodigoEconomicoRef()")
        .isEqualTo("Cod-1");
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getId())
        .as("getConvocatoriaConceptoGasto()").isEqualTo(1L);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaConceptoGastoCodigoEcNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaConceptoGastoCodigoEc con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaConceptoGastoCodigoEc por su id
    // then: lanza un ConvocatoriaConceptoGastoCodigoEcNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaConceptoGastoCodigoEcNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGastoCodigoEc
   * 
   * @param id     id del ConvocatoriaConceptoGastoCodigoEc
   * @param nombre nombre del ConvocatoriaConceptoGastoCodigoEc
   * @return el objeto ConvocatoriaConceptoGastoCodigoEc
   */
  private ConvocatoriaConceptoGastoCodigoEc generarMockConvocatoriaConceptoGastoCodigoEc(Long id) {

    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);

    conceptoGasto.setActivo(true);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId((id == null ? 1 : id));
    convocatoriaConceptoGasto.setConvocatoria(convocatoria);
    convocatoriaConceptoGasto.setObservaciones("Obs-" + (id == null ? 1 : id));
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setImporteMaximo(400.0);
    convocatoriaConceptoGasto.setNumMeses(4);
    convocatoriaConceptoGasto.setPermitido(true);
    convocatoriaConceptoGasto.setPorcentajeCosteIndirecto(3);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = new ConvocatoriaConceptoGastoCodigoEc();
    convocatoriaConceptoGastoCodigoEc.setId(id);
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("Cod-" + (id == null ? 1 : id));
    convocatoriaConceptoGastoCodigoEc.setConvocatoriaConceptoGasto(convocatoriaConceptoGasto);
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(LocalDate.now());
    convocatoriaConceptoGastoCodigoEc.setFechaFin(LocalDate.now());

    return convocatoriaConceptoGastoCodigoEc;
  }

}
