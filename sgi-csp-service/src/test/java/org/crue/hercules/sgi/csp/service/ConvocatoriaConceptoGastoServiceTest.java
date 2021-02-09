package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
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
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * ConvocatoriaConceptoGastoServiceTest
 */
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
  private ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaConceptoGastoServiceImpl(repository, convocatoriaRepository, conceptoGastoRepository,
        convocatoriaConceptoGastoCodigoEcRepository, convocatoriaService);
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
  public void create_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaConceptoGasto when modificable returns False
    ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);
    newConvocatoriaConceptoGasto.setId(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaConceptoGasto.getConvocatoria()));
    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaConceptoGasto.getConceptoGasto()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaConceptoGasto
        () -> service.create(newConvocatoriaConceptoGasto))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede crear ConvocatoriaConceptoGasto. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
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
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing convocatoriaConceptoGasto
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGasto(id)));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);
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

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  @Test
  public void delete_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: existing ConvocatoriaConceptoGasto when modificable returns false
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGasto(id)));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede eliminar ConvocatoriaConceptoGasto. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
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
    convocatoriaConceptoGasto.setMesInicial(4);
    convocatoriaConceptoGasto.setMesFinal(5);
    convocatoriaConceptoGasto.setPermitido(true);
    convocatoriaConceptoGasto.setPorcentajeCosteIndirecto(3);

    return convocatoriaConceptoGasto;
  }

}
