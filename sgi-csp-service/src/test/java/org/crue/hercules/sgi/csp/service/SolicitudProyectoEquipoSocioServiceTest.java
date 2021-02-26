package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoEquipoSocioServiceImpl;
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
 * SolicitudProyectoEquipoSocioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoEquipoSocioServiceTest {

  @Mock
  private SolicitudProyectoEquipoSocioRepository repository;

  @Mock
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  @Mock
  private SolicitudService solicitudService;

  private SolicitudProyectoEquipoSocioService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoEquipoSocioServiceImpl(repository, solicitudService,
        solicitudProyectoSocioRepository);
  }

  @Test
  public void update_ReturnsSolicitudProyectoEquipoSocio() {
    // given: una lista con uno de los SolicitudProyectoEquipoSocio actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoSocioId = 1L;

    List<SolicitudProyectoEquipoSocio> solicitudProyecotEquipoSocioJustificiacionExistentes = new ArrayList<>();
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoEquipoSocio(2L, 1L));
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoEquipoSocio(4L, 1L));
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoEquipoSocio(5L, 1L));

    SolicitudProyectoEquipoSocio newSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);
    newSolicitudProyectoEquipoSocio.setMesInicio(1);
    newSolicitudProyectoEquipoSocio.setMesFin(3);
    SolicitudProyectoEquipoSocio updatedSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(4L, 1L);
    updatedSolicitudProyectoEquipoSocio.setMesInicio(5);
    updatedSolicitudProyectoEquipoSocio.setMesFin(7);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoEquipoSocio);
    solicitudProyectoEquipoSocioActualizar.add(updatedSolicitudProyectoEquipoSocio);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(updatedSolicitudProyectoEquipoSocio.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyecotEquipoSocioJustificiacionExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<SolicitudProyectoEquipoSocio>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SolicitudProyectoEquipoSocio>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios = invocation.getArgument(0);
          return solicitudProyectoEquipoSocios.stream().map(solicitudProyectoEquipoSocio -> {
            if (solicitudProyectoEquipoSocio.getId() == null) {
              solicitudProyectoEquipoSocio.setId(6L);
            }
            solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().setId(solicitudProyectoSocioId);
            return solicitudProyectoEquipoSocio;
          }).collect(Collectors.toList());
        });

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: updatedSolicitudProyectoEquipoSocio
    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizados = service
        .update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar);

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getSolicitudProyectoSocio().getId())
        .as("get(0).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getMesInicio()).as("get(0).getMesInicio()")
        .isEqualTo(newSolicitudProyectoEquipoSocio.getMesInicio());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getMesFin()).as("get(0).getMesFin()")
        .isEqualTo(newSolicitudProyectoEquipoSocio.getMesFin());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo(newSolicitudProyectoEquipoSocio.getPersonaRef());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getRolProyecto().getId())
        .as("get(0).getRolProyecto().getId()").isEqualTo(newSolicitudProyectoEquipoSocio.getRolProyecto().getId());

    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedSolicitudProyectoEquipoSocio.getId());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getSolicitudProyectoSocio().getId())
        .as("get(0).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getMesInicio()).as("get(1).getMesInicio()")
        .isEqualTo(updatedSolicitudProyectoEquipoSocio.getMesInicio());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getMesFin()).as("get(1).getMesFin()")
        .isEqualTo(updatedSolicitudProyectoEquipoSocio.getMesFin());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getPersonaRef()).as("get(1).getPersonaRef()")
        .isEqualTo(updatedSolicitudProyectoEquipoSocio.getPersonaRef());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getRolProyecto().getId())
        .as("get(0).getRolProyecto().getId()").isEqualTo(updatedSolicitudProyectoEquipoSocio.getRolProyecto().getId());

    Mockito.verify(repository, Mockito.times(1)).deleteAll(ArgumentMatchers.<SolicitudProyectoEquipoSocio>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<SolicitudProyectoEquipoSocio>anyList());

  }

  @Test
  public void update_WithoutSolicitudProyectoSocio_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: Un nuevo SolicitudProyectoEquipoSocio que no tiene
    // SolicitudProyectoSocio

    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoEquipoSocio newSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    newSolicitudProyectoEquipoSocio.setRolProyecto(null);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoEquipoSocio);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    // when: Actualizamos el SolicitudProyectoEquipoSocio
    // then: Lanza una excepcion porque no tiene solicitud proyecto socio
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithoSolicitudProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given: Se actualiza la solicitud proyecto socio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoEquipoSocio updateSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(2L, 1L);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    updateSolicitudProyectoEquipoSocio.setRolProyecto(null);
    solicitudProyectoEquipoSocioActualizar.add(updateSolicitudProyectoEquipoSocio);

    List<SolicitudProyectoEquipoSocio> solicitudProyecotEquipoSocioJustificiacionExistentes = new ArrayList<>();
    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(2L, 1L);
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().setId(3L);
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(solicitudProyectoEquipoSocio);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(updateSolicitudProyectoEquipoSocio.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyecotEquipoSocioJustificiacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoEquipoSocio
    // then: Lanza una excepcion porque se ha modificado la solicitud proyecto socio
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar la solicitud proyecto socio del SolicitudProyectoEquipoSocio");
  }

  @Test
  public void update_WithoutRolProyectoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipoSocio que no tiene rol proyecto
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoEquipoSocio newSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    newSolicitudProyectoEquipoSocio.setRolProyecto(null);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoEquipoSocio);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newSolicitudProyectoEquipoSocio.getSolicitudProyectoSocio()));

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoEquipoSocio
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El rol de participación no puede ser null para realizar la acción sobre SolicitudProyectoEquipoSocio");
  }

  @Test
  public void update_WithoutPersonaRef_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipoSocio que no tiene persona ref

    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoEquipoSocio newSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    newSolicitudProyectoEquipoSocio.setPersonaRef(null);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoEquipoSocio);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newSolicitudProyectoEquipoSocio.getSolicitudProyectoSocio()));

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoEquipoSocio
    // then: Lanza una excepcion porque no tiene persona ref
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La persona ref no puede ser null para realizar la acción sobre SolicitudProyectoEquipoSocio");
  }

  @Test
  public void update_WithRangosMesesSolapados_ThrowsIllegalArgumentException() {
    // given: una lista con uno de los SolicitudProyectoEquipoSocio actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoSocioId = 1L;

    List<SolicitudProyectoEquipoSocio> solicitudProyecotEquipoSocioJustificiacionExistentes = new ArrayList<>();
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoEquipoSocio(4L, 1L));

    SolicitudProyectoEquipoSocio newSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);
    SolicitudProyectoEquipoSocio updatedSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(4L, 1L);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();
    newSolicitudProyectoEquipoSocio.setMesInicio(1);
    newSolicitudProyectoEquipoSocio.setMesFin(2);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoEquipoSocio);
    solicitudProyectoEquipoSocioActualizar.add(updatedSolicitudProyectoEquipoSocio);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(updatedSolicitudProyectoEquipoSocio.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyecotEquipoSocioJustificiacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoEquipoSocio
    // then: Lanza una excepcion porque existen otros solicitudproyectosocioequipo
    // dentro del mismo rango de meses
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");

  }

  @Test
  public void findById_ReturnsSolicitudProyectoEquipoSocio() {
    // given: Un SolicitudProyectoEquipoSocio con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoEquipoSocio(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoEquipoSocio por su id
    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = service.findById(idBuscado);

    // then: el SolicitudProyectoEquipoSocio
    Assertions.assertThat(solicitudProyectoEquipoSocio).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEquipoSocio.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoEquipoSocioNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoEquipoSocio con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoEquipoSocio por su id
    // then: lanza un SolicitudProyectoEquipoSocioNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoEquipoSocioNotFoundException.class);
  }

  @Test
  public void findBySolicitudProyectoSocioId_ReturnsSolicitudProyectoEquipoSocio() {
    // given: Una lista con 37 SolicitudProyectoSocio
    Long solicitudId = 1L;
    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocio = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEquipoSocio.add(generarSolicitudProyectoEquipoSocio(i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipoSocio>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoEquipoSocio>>() {
          @Override
          public Page<SolicitudProyectoEquipoSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoEquipoSocio.size() ? solicitudProyectoEquipoSocio.size() : toIndex;
            List<SolicitudProyectoEquipoSocio> content = solicitudProyectoEquipoSocio.subList(fromIndex, toIndex);
            Page<SolicitudProyectoEquipoSocio> page = new PageImpl<>(content, pageable,
                solicitudProyectoEquipoSocio.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoEquipoSocio> page = service.findAllBySolicitudProyectoSocio(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocioRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoEquipoSocioRecuperado.getId()).isEqualTo(i);
    }

  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEquipoSocio
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoEquipoSocio
   */
  private SolicitudProyectoEquipoSocio generarSolicitudProyectoEquipoSocio(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = SolicitudProyectoEquipoSocio.builder()
        .id(solicitudProyectoEquipoSocioId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(entidadesRelacionadasId)
            .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(1L)
                .solicitud(Solicitud.builder().id(1L).activo(Boolean.TRUE).build()).build())
            .build())
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build()).personaRef("user-001").mesInicio(1)
        .mesFin(3).build();

    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);

    return solicitudProyectoEquipoSocio;
  }

}
