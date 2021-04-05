package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoEquipoServiceImpl;
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
 * SolicitudProyectoEquipoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoEquipoServiceTest {

  @Mock
  private SolicitudProyectoEquipoRepository repository;

  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  @Mock
  private RolProyectoRepository rolProyectoRepository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  private SolicitudRepository solicitudRepository;

  private SolicitudProyectoEquipoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoEquipoServiceImpl(repository, solicitudProyectoRepository, rolProyectoRepository,
        solicitudService, solicitudRepository);
  }

  @Test
  public void create_ReturnsSolicitudProyectoEquipo() {
    // given: Un nuevo SolicitudProyectoEquipo
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    List<SolicitudProyectoEquipo> listSolicitudProyectoEquipo = new ArrayList<>();
    listSolicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(2L, 1L, 1L));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>()).willReturn(listSolicitudProyectoEquipo);

    BDDMockito.given(rolProyectoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoEquipo>any()))
        .will((InvocationOnMock invocation) -> {
          SolicitudProyectoEquipo solicitudProyectoEquipoCreado = invocation.getArgument(0);
          if (solicitudProyectoEquipoCreado.getId() == null) {
            solicitudProyectoEquipoCreado.setId(1L);
          }

          return solicitudProyectoEquipoCreado;
        });

    // when: Creamos el SolicitudProyectoEquipo
    SolicitudProyectoEquipo solicitudProyectoEquipoCreado = service.create(solicitudProyectoEquipo);

    // then: El SolicitudProyectoEquipo se crea correctamente
    Assertions.assertThat(solicitudProyectoEquipoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEquipoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoEquipoCreado.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(solicitudProyectoEquipo.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getRolProyecto().getId()).as("getRolProyecto().getId()")
        .isEqualTo(solicitudProyectoEquipo.getRolProyecto().getId());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getPersonaRef()).as("getPersonaRef()")
        .isEqualTo(solicitudProyectoEquipo.getPersonaRef());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getMesInicio()).as("getMesInicio()")
        .isEqualTo(solicitudProyectoEquipo.getMesInicio());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoEquipo.getMesFin());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipo que ya tiene id
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque el SolicitudProyectoEquipo ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para realizar la acción sobre el SolicitudProyectoEquipo");
  }

  @Test
  public void create_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipo que no tiene solicitud de proyecto
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);

    solicitudProyectoEquipo.setSolicitudProyectoId(null);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque no tiene solicitud de proyecto
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Los datos de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
  }

  @Test
  public void create_WithoutRolProyecto_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipo que no tiene rol proyecto
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);

    solicitudProyectoEquipo.setRolProyecto(null);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque no tiene rol proyecto
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El rol de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
  }

  @Test
  public void create_WithoutPersonaRef_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipo que no tiene persona ref
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);

    solicitudProyectoEquipo.setPersonaRef(null);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque no tiene persona ref
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La persona ref no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
  }

  @Test
  public void create_WithNoExistingRolProyecto_ThrowsRolProyectoNotFoundException() {
    // given: Un nuevo SolicitudProyectoEquipo que tiene un rol proyecto
    // que no existe
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);

    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    List<SolicitudProyectoEquipo> listSolicitudProyectoEquipo = new ArrayList<>();
    listSolicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(2L, 1L, 1L));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>()).willReturn(listSolicitudProyectoEquipo);

    BDDMockito.given(rolProyectoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(RolProyectoNotFoundException.class);
  }

  @Test
  public void create_WithOutSolicitante_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipo cuyo solicitante no se encuentra en
    // el equipo
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);
    solicitudProyectoEquipo.setPersonaRef("personaRef-005");

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>());

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>());

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque solicitante no se encuentra en el equipo
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El solicitante de la solicitud debe ser miembro del equipo");
  }

  @Test
  public void create_ErrorMes_ThrowsIllegalArgumentException() {
    // given: Se crea SolicitudProyectoEquipo cque se encuentra en mismo rango
    // de meses que otro
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    List<SolicitudProyectoEquipo> listSolicitudProyectoEquipo = new ArrayList<>();
    listSolicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(2L, 1L, 1L));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(listSolicitudProyectoEquipo);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque solicitante no se encuentra en el equipo
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El miembro del equipo ya existe en el mismo rango de fechas");
  }

  @Test
  public void update_ReturnsSolicitudProyectoEquipo() {
    // given: Un nuevo SolicitudProyectoEquipo con mes fin actualizado
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(3L, 1L, 1L);

    SolicitudProyectoEquipo solicitudProyectoEquipoMesFinActualizado = generarSolicitudProyectoEquipo(3L, 1L, 1L);

    solicitudProyectoEquipoMesFinActualizado.setMesFin(10);

    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    List<SolicitudProyectoEquipo> listSolicitudProyectoEquipo = new ArrayList<>();
    listSolicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(2L, 1L, 1L));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>()).willReturn(listSolicitudProyectoEquipo);

    BDDMockito.given(rolProyectoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarSolicitudProyectoEquipo(3L, 1L, 1L)));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoEquipo>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoEquipo
    SolicitudProyectoEquipo solicitudProyectoEquipoActualizada = service
        .update(solicitudProyectoEquipoMesFinActualizado);

    // then: El SolicitudProyectoEquipo se actualiza correctamente.
    Assertions.assertThat(solicitudProyectoEquipoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEquipoActualizada.getId()).as("getId()")
        .isEqualTo(solicitudProyectoEquipo.getId());
    Assertions.assertThat(solicitudProyectoEquipoActualizada.getMesFin()).as("getMesFin()").isEqualTo(10);

  }

  @Test
  public void update_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEquipo que no tiene solicitud de proyecto
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(2L, 1L, 1L);

    solicitudProyectoEquipo.setSolicitudProyectoId(null);

    // when: Creamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque no tiene solicitud de proyecto
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Los datos de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
  }

  @Test
  public void update_WithoutRolProyecto_ThrowsIllegalArgumentException() {
    // given: Actualizamos SolicitudProyectoEquipo que no tiene rol proyecto
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(2L, 1L, 1L);

    solicitudProyectoEquipo.setRolProyecto(null);

    // when: Actualizamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque no tiene rol proyecto
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El rol de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
  }

  @Test
  public void update_WithoutPersonaRef_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoEquipo que no tiene persona ref
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);

    solicitudProyectoEquipo.setPersonaRef(null);

    // when: Actualizamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque no tiene persona ref

    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La persona ref no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");

  }

  @Test
  public void update_WithNoExistingRolProyecto_ThrowsRolProyectoNotFoundException() {
    // given: Se actualiza SolicitudProyectoEquipo que tiene un rol proyecto
    // que no existe
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(2L, 1L, 1L);
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    List<SolicitudProyectoEquipo> listSolicitudProyectoEquipo = new ArrayList<>();
    listSolicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(2L, 1L, 1L));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>()).willReturn(listSolicitudProyectoEquipo);

    BDDMockito.given(rolProyectoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEquipo))
        .isInstanceOf(RolProyectoNotFoundException.class);
  }

  @Test
  public void update_WithOutSolicitante_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoEquipo cuyo solicitante no se encuentra
    // en el equipo
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>());
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(new ArrayList<>());

    // when: Actualizamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque solicitante no se encuentra en el equipo
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El solicitante de la solicitud debe ser miembro del equipo");
  }

  @Test
  public void update_ErrorMes_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoEquipo cque se encuentra en mismo rango
    // de meses que otro
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));

    List<SolicitudProyectoEquipo> listSolicitudProyectoEquipo = new ArrayList<>();
    listSolicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(2L, 1L, 1L));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any()))
        .willReturn(listSolicitudProyectoEquipo);

    // when: Actualizamos el SolicitudProyectoEquipo
    // then: Lanza una excepcion porque solicitante no se encuentra en el equipo
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEquipo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El miembro del equipo ya existe en el mismo rango de fechas");
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
        .isInstanceOf(SolicitudProyectoEquipoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsSolicitudProyectoEquipo() {
    // given: Un SolicitudProyectoEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoEquipo(idBuscado, 1L, 1L)));

    // when: Buscamos el SolicitudProyectoEquipo por su id
    SolicitudProyectoEquipo solicitudProyectoEquipo = service.findById(idBuscado);

    // then: el SolicitudProyectoEquipo
    Assertions.assertThat(solicitudProyectoEquipo).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEquipo.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoEquipoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoEquipo por su id
    // then: lanza un SolicitudProyectoEquipoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoEquipoNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoEquipo
    Long solicitudId = 1L;
    List<SolicitudProyectoEquipo> solicitudProyectoEquipo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(i, i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoEquipo>>() {
          @Override
          public Page<SolicitudProyectoEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoEquipo.size() ? solicitudProyectoEquipo.size() : toIndex;
            List<SolicitudProyectoEquipo> content = solicitudProyectoEquipo.subList(fromIndex, toIndex);
            Page<SolicitudProyectoEquipo> page = new PageImpl<>(content, pageable, solicitudProyectoEquipo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoEquipo> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoEquipo solicitudProyectoEquipoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoEquipoRecuperado.getId()).isEqualTo(i);
    }
  }

  private SolicitudProyecto generarMockSolicitudProyecto(Long solicitudProyectoId, Long solicitudId) {
    return SolicitudProyecto.builder().id(solicitudProyectoId).build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEquipo
   * 
   * @param solicitudProyectoEquipoId
   * @param solicitudProyectoId
   * @param tipoDocumentoId
   * @return el objeto SolicitudProyectoEquipo
   */
  private SolicitudProyectoEquipo generarSolicitudProyectoEquipo(Long solicitudProyectoEquipoId,
      Long solicitudProyectoId, Long rolProyectoId) {

    SolicitudProyectoEquipo solicitudProyectoEquipo = SolicitudProyectoEquipo.builder().id(solicitudProyectoEquipoId)
        .solicitudProyectoId(1L).personaRef("personaRef-" + solicitudProyectoEquipoId)
        .rolProyecto(RolProyecto.builder().id(rolProyectoId).build()).mesInicio(1).mesFin(5).build();

    return solicitudProyectoEquipo;
  }

  private Solicitud generarMockSolicitud(Long solicitudId) {
    return Solicitud.builder().id(1L).activo(Boolean.TRUE).build();
  }
}
