package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
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
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaConceptoGastoCodigoEcServiceTest
 */
public class ConvocatoriaConceptoGastoCodigoEcServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository repository;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;

  @Mock
  private ConvocatoriaService convocatoriaService;

  private ConvocatoriaConceptoGastoCodigoEcService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaConceptoGastoCodigoEcServiceImpl(repository, convocatoriaConceptoGastoRepository,
        convocatoriaService);
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
        .hasMessage("ConvocatoriaConceptoGasto es un campo obligatorio en ConvocatoriaConceptoGastoCodigoEc");
  }

  @Test
  public void create_WithoutCodigoEconomicoRef_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc sin codigoEconomicoRef
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        null);
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef(null);

    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque CodigoEconomicoRef es requerido
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGastoCodigoEc))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CodigoEconomicoRef es un campo obligatorio en ConvocatoriaConceptoGastoCodigoEc");
  }

  @Test
  public void create_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc Fecha Inicio > fechaFin
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        null);
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(convocatoriaConceptoGastoCodigoEc.getFechaFin().plusDays(1));

    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque fecha fin debe ser posterior a fecha inicio
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGastoCodigoEc))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");
  }

  @Test
  public void create_DuplicatedCodigoOverlapsDates_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaConceptoGastoCodigoEc duplicate codigo Overlaps Dates
    ConvocatoriaConceptoGastoCodigoEc newConvocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    newConvocatoriaConceptoGastoCodigoEc.setId(null);
    newConvocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("codigoEconomicoRef");

    ConvocatoriaConceptoGastoCodigoEc existingConvocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        2L);
    existingConvocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("codigoEconomicoRef");

    List<ConvocatoriaConceptoGastoCodigoEc> solapadas = new ArrayList<ConvocatoriaConceptoGastoCodigoEc>();
    solapadas.add(existingConvocatoriaConceptoGastoCodigoEc);

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaConceptoGastoCodigoEc>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(solapadas));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaConceptoGastoCodigoEc
        () -> service.create(newConvocatoriaConceptoGastoCodigoEc))
        // then: throw exception as Codigo exists and dates overlaps
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El código económico '%s' ya está presente y tiene un periodo de vigencia que se solapa con el indicado",
            newConvocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef());
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
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc sin concepto gasto
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());
    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque el concepto gasto es null
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoCodigoEcActualizar))
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  @Test
  public void update_WithoutCodigoEconomico_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGastoCodigoEc sin código económico
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    convocatoriaConceptoGastoCodigoEcActualizar.setCodigoEconomicoRef(null);

    // when: Creamos el ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque no tiene código económico
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoCodigoEcActualizar))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CodigoEconomicoRef es un campo obligatorio en ConvocatoriaConceptoGastoCodigoEc");
  }

  @Test
  public void update_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaConceptoGastoCodigoEc Fecha Inicio > fechaFin
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(convocatoriaConceptoGastoCodigoEc.getFechaFin().plusDays(1));

    // when: Modificamos ConvocatoriaConceptoGastoCodigoEc
    // then: Lanza una excepcion porque fecha fin debe ser posterior a fecha inicio
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoCodigoEc))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");
  }

  @Test
  public void update_AnyButDatesWhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaConceptoGastoCodigoEc update Any data but dates when
    // modificable return false
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizado = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    convocatoriaConceptoGastoCodigoEcActualizado.setObservaciones("observaciones-modificadas");

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaConceptoGastoCodigoEc
        () -> service.update(convocatoriaConceptoGastoCodigoEcActualizado))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar ConvocatoriaConceptoGastoCodigoEc. Solo está permitido modificar las fechas de inicio y fin. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o convocatoriaConceptoGastos asociados");
  }

  @Test
  public void update_DuplicatedCodigoOverlapsDates_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaConceptoGastoCodigoEc duplicate codigo Overlaps Dates
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizado = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    List<ConvocatoriaConceptoGastoCodigoEc> solapadas = new ArrayList<ConvocatoriaConceptoGastoCodigoEc>();
    solapadas.add(generarMockConvocatoriaConceptoGastoCodigoEc(2L));

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaConceptoGastoCodigoEc>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(solapadas));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaConceptoGastoCodigoEc
        () -> service.update(convocatoriaConceptoGastoCodigoEcActualizado))
        // then: throw exception as Codigo exists and dates overlaps
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El código económico '%s' ya está presente y tiene un periodo de vigencia que se solapa con el indicado",
            convocatoriaConceptoGastoCodigoEcActualizado.getCodigoEconomicoRef());
  }

  @Test
  public void update_DuplicatedCodigoNotOverlapsDates_NotThrowAnyException() {
    // given: a ConvocatoriaConceptoGastoCodigoEc duplicate codigo Dates OK
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizado = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    convocatoriaConceptoGastoCodigoEcActualizado.setFechaFin(LocalDate.of(2050, 10, 10));
    List<ConvocatoriaConceptoGastoCodigoEc> solapadas = new ArrayList<ConvocatoriaConceptoGastoCodigoEc>();

    BDDMockito.given(convocatoriaConceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGastoCodigoEc));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaConceptoGastoCodigoEc>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(solapadas));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    Assertions.assertThatCode(
        // when: update ConvocatoriaConceptoGastoCodigoEc
        () -> service.update(convocatoriaConceptoGastoCodigoEcActualizado))
        // then: doesNotThrowAnyException
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing convocatoriaConceptoGasto
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGastoCodigoEc(id)));
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
        .isInstanceOf(ConvocatoriaConceptoGastoCodigoEcNotFoundException.class);
  }

  @Test
  public void delete_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: existing ConvocatoriaConceptoGastoCodigoEc when modificable returns
    // false
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGastoCodigoEc(id)));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class);
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
    convocatoriaConceptoGasto.setMesInicial(1);
    convocatoriaConceptoGasto.setMesFinal(4);
    convocatoriaConceptoGasto.setPermitido(true);
    convocatoriaConceptoGasto.setPorcentajeCosteIndirecto(3);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = new ConvocatoriaConceptoGastoCodigoEc();
    convocatoriaConceptoGastoCodigoEc.setId(id);
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("Cod-" + (id == null ? 1 : id));
    convocatoriaConceptoGastoCodigoEc.setConvocatoriaConceptoGasto(convocatoriaConceptoGasto);
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(LocalDate.now().minusDays(1));
    convocatoriaConceptoGastoCodigoEc.setFechaFin(LocalDate.now());

    return convocatoriaConceptoGastoCodigoEc;
  }

}
