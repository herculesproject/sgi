package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoPeriodoPagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudProyectoPeriodoPagoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoPeriodoPagoServiceTest {

  @Mock
  private SolicitudProyectoPeriodoPagoRepository repository;

  @Mock
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  private SolicitudProyectoPeriodoPagoService service;

  @Mock
  private SolicitudService solicitudService;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoPeriodoPagoServiceImpl(repository, solicitudProyectoSocioRepository,
        solicitudService);
  }

  @Test
  public void update_ReturnsSolicitudProyectoPeriodoPago() {
    // given: una lista con uno de los SolicitudProyectoPeriodoPago actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoSocioId = 1L;

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(2L, 1L));
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(4L, 1L));
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(5L, 1L));

    SolicitudProyectoPeriodoPago newSolicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(null, 1L);
    newSolicitudProyectoPeriodoPago.setMes(5);
    SolicitudProyectoPeriodoPago updatedSolicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(4L, 1L);
    updatedSolicitudProyectoPeriodoPago.setMes(6);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoActualizar = new ArrayList<>();
    solicitudProyectoPeriodoPagoActualizar.add(newSolicitudProyectoPeriodoPago);
    solicitudProyectoPeriodoPagoActualizar.add(updatedSolicitudProyectoPeriodoPago);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newSolicitudProyectoPeriodoPago.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoPagoExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<SolicitudProyectoPeriodoPago>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SolicitudProyectoPeriodoPago>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoPeriodoPago> periodosPagos = invocation.getArgument(0);
          return periodosPagos.stream().map(periodoPago -> {
            if (periodoPago.getId() == null) {
              periodoPago.setId(6L);
            }
            periodoPago.getSolicitudProyectoSocio().setId(solicitudProyectoSocioId);
            return periodoPago;
          }).collect(Collectors.toList());
        });

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: se actualiza solicitudProyectoPeriodoPagoActualizar
    List<SolicitudProyectoPeriodoPago> periodosPagoActualizados = service.update(solicitudProyectoSocioId,
        solicitudProyectoPeriodoPagoActualizar);

    // then: El SolicitudProyectoPeriodoPago se actualiza correctamente.

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosPagoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosPagoActualizados.get(0).getSolicitudProyectoSocio().getId())
        .as("get(0).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(0).getImporte()).as("get(0).getImporte()")
        .isEqualTo(newSolicitudProyectoPeriodoPago.getImporte());
    Assertions.assertThat(periodosPagoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(newSolicitudProyectoPeriodoPago.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(0).getMes()).as("get(0).getMes()")
        .isEqualTo(newSolicitudProyectoPeriodoPago.getMes());

    Assertions.assertThat(periodosPagoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedSolicitudProyectoPeriodoPago.getId());
    Assertions.assertThat(periodosPagoActualizados.get(1).getSolicitudProyectoSocio().getId())
        .as("get(1).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(1).getImporte()).as("get(1).getImporte()")
        .isEqualTo(updatedSolicitudProyectoPeriodoPago.getImporte());
    Assertions.assertThat(periodosPagoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(updatedSolicitudProyectoPeriodoPago.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(1).getMes()).as("get(1).getMes()")
        .isEqualTo(updatedSolicitudProyectoPeriodoPago.getMes());

    Mockito.verify(repository, Mockito.times(1)).deleteAll(ArgumentMatchers.<SolicitudProyectoPeriodoPago>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<SolicitudProyectoPeriodoPago>anyList());

  }

  @Test
  public void update_WithSolicitudProyectoSocioNotExist_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: a SolicitudProyectoPeriodoPago with non existing
    // SolicitudProyectoSocio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(1L, 1L);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoPago)))
        // then: throw exception as SolicitudProyectoSocio is not found
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoPeriodoPagoNotFoundException() {
    // given: Un SolicitudProyectoPeriodoPago actualizado con un id que no existe
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(1L, 1L);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(3L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoPago.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoPagoExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoPago
    // then: Lanza una excepcion porque el SolicitudProyectoPeriodoPago no existe
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoPago)))
        .isInstanceOf(SolicitudProyectoPeriodoPagoNotFoundException.class);
  }

  @Test
  public void update_WithSolicitudProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given:Se actualiza SolicitudProyectoSocio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(1L, 1L);

    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().setId(2L);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(1L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoPago.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoPagoExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio del SolicitudProyectoPeriodoPago
    // then: Lanza una excepcion porque no se puede modificar el campo
    // SolicitudProyectoSocio
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar la solicitud proyecto socio del SolicitudProyectoPeriodoPago");
  }

  @Test
  public void update_WithMesSuperiorDuracion_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoPago cuyo mes es superior a la
    // duración de solicitud proyecto datos
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(1L, 1L);

    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setDuracion(2);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(1L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoPago.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoPagoExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoPago
    // then: Lanza una excepcion porque el mes es superior a la duración de
    // solicitud proyecto datos
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El mes no puede ser superior a la duración en meses indicada en la Convocatoria");
  }

  @Test
  public void update_WithMesSolapamiento_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoPago cuyo mes es superior a la
    // duración de solicitud proyecto datos
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago1 = generarSolicitudProyectoPeriodoPago(1L, 1L);
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago2 = generarSolicitudProyectoPeriodoPago(2L, 1L);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(1L, 1L));
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(2L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoPago1.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoPagoExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoPago
    // then: Lanza una excepcion porque el mes es superior a la duración de
    // solicitud proyecto datos
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoPeriodoPago1, solicitudProyectoPeriodoPago2)))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_WithoutMes_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoPeriodoPago que no tiene mes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(1L, 1L);

    solicitudProyectoPeriodoPago.setMes(null);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoPeriodoPagoExistentes.add(generarSolicitudProyectoPeriodoPago(1L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoPago.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoPagoExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoPago
    // then: Lanza una excepcion porque no tiene mes
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Mes no puede ser null para realizar la acción sobre SolicitudProyectoPeriodoPago");
  }

  @Test
  public void findById_ReturnsSolicitudProyectoPeriodoPago() {
    // given: Un SolicitudProyectoPeriodoPago con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoPeriodoPago(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoPeriodoPago por su id
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = service.findById(idBuscado);

    // then: el SolicitudProyectoPeriodoPago
    Assertions.assertThat(solicitudProyectoPeriodoPago).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoPeriodoPago.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoPeriodoPagoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoPeriodoPago con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoPeriodoPago por su id
    // then: lanza un SolicitudProyectoPeriodoPagoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoPeriodoPagoNotFoundException.class);
  }

  @Test
  public void findAllBySolicitudProyectoSocio_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoPeriodoPago
    Long solicitudId = 1L;
    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPago = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoPeriodoPago.add(generarSolicitudProyectoPeriodoPago(i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoPeriodoPago>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoPeriodoPago>>() {
          @Override
          public Page<SolicitudProyectoPeriodoPago> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoPeriodoPago.size() ? solicitudProyectoPeriodoPago.size() : toIndex;
            List<SolicitudProyectoPeriodoPago> content = solicitudProyectoPeriodoPago.subList(fromIndex, toIndex);
            Page<SolicitudProyectoPeriodoPago> page = new PageImpl<>(content, pageable,
                solicitudProyectoPeriodoPago.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoPeriodoPago> page = service.findAllBySolicitudProyectoSocio(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPagoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoPeriodoPagoRecuperado.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoPago
   * 
   * @param solicitudProyectoPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoPeriodoPago
   */
  private SolicitudProyectoPeriodoPago generarSolicitudProyectoPeriodoPago(Long solicitudProyectoPeriodoPagoId,
      Long solicitudProyectoSocioId) {

    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = SolicitudProyectoPeriodoPago.builder()
        .id(solicitudProyectoPeriodoPagoId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
            .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(1L)
                .solicitud(Solicitud.builder().id(1L).activo(Boolean.TRUE).build()).build())
            .build())
        .numPeriodo(3).importe(new BigDecimal(358)).mes(3).build();

    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoPeriodoPago;
  }

}
