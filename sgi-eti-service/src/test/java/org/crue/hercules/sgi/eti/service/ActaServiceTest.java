package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.service.impl.ActaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ActaServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ActaServiceTest {

  @Mock
  private ActaRepository actaRepository;

  private ActaService actaService;

  @BeforeEach
  public void setUp() throws Exception {
    actaService = new ActaServiceImpl(actaRepository);
  }

  @Test
  public void find_WithId_ReturnsActa() {
    BDDMockito.given(actaRepository.findById(1L)).willReturn(Optional.of(generarMockActa(1L, 123)));

    Acta acta = actaService.findById(1L);

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void find_NotFound_ThrowsActaNotFoundException() throws Exception {
    BDDMockito.given(actaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> actaService.findById(1L)).isInstanceOf(ActaNotFoundException.class);
  }

  @Test
  public void create_ReturnsActa() {
    // given: Un nuevo Acta
    Acta actaNew = generarMockActa(null, 123);

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaRepository.save(actaNew)).willReturn(acta);

    // when: Creamos el acta
    Acta actaCreado = actaService.create(actaNew);

    // then: el acta se crea correctamente
    Assertions.assertThat(actaCreado).isNotNull();
    Assertions.assertThat(actaCreado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(actaCreado.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(actaCreado.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(actaCreado.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(actaCreado.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(actaCreado.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(actaCreado.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(actaCreado.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(actaCreado.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1);
    Assertions.assertThat(actaCreado.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(actaCreado.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void create_ActaWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo acta que ya tiene id
    Acta actaNew = generarMockActa(1L, 123);
    // when: Creamos el acta
    // then: Lanza una excepcion porque el acta ya tiene id
    Assertions.assertThatThrownBy(() -> actaService.create(actaNew)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsActa() {

    // given: Un nuevo acta con el resumen actualizado
    Acta actaResumenActualizado = generarMockActa(1L, 123);
    actaResumenActualizado.setResumen("Resumen actualizado");

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaRepository.findById(1L)).willReturn(Optional.of(acta));
    BDDMockito.given(actaRepository.save(actaResumenActualizado)).willReturn(actaResumenActualizado);

    // when: Actualizamos el acta
    Acta actaActualizado = actaService.update(actaResumenActualizado);

    // then: El acta se actualiza correctamente.
    Assertions.assertThat(actaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(actaActualizado.getResumen()).isEqualTo("Resumen actualizado");

  }

  @Test
  public void update_ThrowsActaNotFoundException() {
    // given: Un acta a actualizar
    Acta actaResumenActualizado = generarMockActa(1L, 123);

    // then: Lanza una excepcion porque el acta no existe
    Assertions.assertThatThrownBy(() -> actaService.update(actaResumenActualizado))
        .isInstanceOf(ActaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un acta que venga sin id
    Acta actaTipoActualizado = generarMockActa(null, 123);

    Assertions.assertThatThrownBy(
        // when: update acta
        () -> actaService.update(actaTipoActualizado))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> actaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsActaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(actaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> actaService.delete(1L))
        // then: Lanza ActaNotFoundException
        .isInstanceOf(ActaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesActa() {
    // given: Id existente
    BDDMockito.given(actaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(actaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> actaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullActaList() {
    // given: One hundred actas
    List<Acta> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActa(Long.valueOf(i), i));
    }

    BDDMockito
        .given(actaRepository.findAll(ArgumentMatchers.<Specification<Acta>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(actas));

    // when: find unlimited
    Page<Acta> page = actaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred actas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred actas
    List<Acta> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActa(Long.valueOf(i), i));
    }

    BDDMockito
        .given(actaRepository.findAll(ArgumentMatchers.<Specification<Acta>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Acta>>() {
          @Override
          public Page<Acta> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Acta> content = actas.subList(fromIndex, toIndex);
            Page<Acta> page = new PageImpl<>(content, pageable, actas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Acta> page = actaService.findAll(null, paging);

    // then: A Page with ten actas are returned containing id='31' to '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Acta acta = page.getContent().get(i);
      Assertions.assertThat(acta.getId()).isEqualTo(j);
    }
  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(1L);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

}
