package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoSocioEquipoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSocioEquipoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoSocioEquipoRepository repository;

  @Mock
  private ProyectoSocioRepository proyectoSocioRepository;

  private ProyectoSocioEquipoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoSocioEquipoServiceImpl(repository, proyectoSocioRepository);
  }

  @Test
  public void update_ReturnsProyectoSocioEquipo() {
    // given: una lista con uno de los ProyectoSocioEquipo actualizado,
    // otro nuevo y sin el otros existente
    Long proyectoSocioId = 1L;

    List<ProyectoSocioEquipo> proyectoSocioEquipoExistentes = new ArrayList<>();
    proyectoSocioEquipoExistentes.add(generarMockProyectoSocioEquipo(2L));
    proyectoSocioEquipoExistentes.add(generarMockProyectoSocioEquipo(4L));
    proyectoSocioEquipoExistentes.add(generarMockProyectoSocioEquipo(5L));

    ProyectoSocioEquipo newProyectoSocioEquipo = generarMockProyectoSocioEquipo(null);
    newProyectoSocioEquipo.setPersonaRef("002");
    ProyectoSocioEquipo updatedProyectoSocioEquipo = generarMockProyectoSocioEquipo(4L);

    List<ProyectoSocioEquipo> proyectoSocioEquipoActualizar = new ArrayList<>();
    proyectoSocioEquipoActualizar.add(newProyectoSocioEquipo);
    proyectoSocioEquipoActualizar.add(updatedProyectoSocioEquipo);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newProyectoSocioEquipo.getProyectoSocio()));

    BDDMockito.given(repository.findAllByProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(proyectoSocioEquipoExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<ProyectoSocioEquipo>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioEquipo>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoSocioEquipo> periodosPagos = invocation.getArgument(0);
          return periodosPagos.stream().map(periodoPago -> {
            if (periodoPago.getId() == null) {
              periodoPago.setId(6L);
            }
            periodoPago.getProyectoSocio().setId(proyectoSocioId);
            return periodoPago;
          }).collect(Collectors.toList());
        });

    // when: se actualiza proyectoSocioEquipoActualizar
    List<ProyectoSocioEquipo> periodosPagoActualizados = service.update(proyectoSocioId, proyectoSocioEquipoActualizar);
    periodosPagoActualizados.sort(Comparator.comparing(ProyectoSocioEquipo::getId));

    // then: Se crea el nuevo ProyectoSocioEquipo, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosPagoActualizados.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedProyectoSocioEquipo.getId());
    Assertions.assertThat(periodosPagoActualizados.get(0).getProyectoSocio().getId())
        .as("get(0).getProyectoSocio().getId()").isEqualTo(proyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo(updatedProyectoSocioEquipo.getPersonaRef());
    Assertions.assertThat(periodosPagoActualizados.get(0).getRolProyecto().getId())
        .as("get(0).getRolProyecto().getId()").isEqualTo(updatedProyectoSocioEquipo.getRolProyecto().getId());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(updatedProyectoSocioEquipo.getFechaInicio());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(updatedProyectoSocioEquipo.getFechaFin());

    Assertions.assertThat(periodosPagoActualizados.get(1).getId()).as("get(1).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosPagoActualizados.get(1).getProyectoSocio().getId())
        .as("get(1).getSolicitudProyectoSocio().getId()").isEqualTo(proyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(1).getPersonaRef()).as("get(1).getPersonaRef()")
        .isEqualTo(newProyectoSocioEquipo.getPersonaRef());
    Assertions.assertThat(periodosPagoActualizados.get(1).getRolProyecto().getId())
        .as("get(1).getRolProyecto().getId()").isEqualTo(newProyectoSocioEquipo.getRolProyecto().getId());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(newProyectoSocioEquipo.getFechaInicio());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(newProyectoSocioEquipo.getFechaFin());

    Mockito.verify(repository, Mockito.times(1)).deleteAll(ArgumentMatchers.<ProyectoSocioEquipo>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<ProyectoSocioEquipo>anyList());

  }

  @Test
  public void update_WithtudProyectoSocioNotExist_ThrowsProyectoSocioNotFoundException() {
    // given: a ProyectoSocioEquipo with non existing
    // ProyectoSocio
    Long proyectoSocioId = 1L;
    ProyectoSocioEquipo proyectoSocioEquipo = generarMockProyectoSocioEquipo(1L);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocioEquipo
        () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioEquipo)))
        // then: throw exception as ProyectoSocio is not found
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProyectoSocioEquipoNotFoundException() {
    // given: Un ProyectoSocioEquipo actualizado con un id que no existe
    Long solicitudProyectoSocioId = 1L;
    ProyectoSocioEquipo proyectoPeriodoPago = generarMockProyectoSocioEquipo(1L);

    List<ProyectoSocioEquipo> proyectoPeriodoPagoExistentes = new ArrayList<>();
    proyectoPeriodoPagoExistentes.add(generarMockProyectoSocioEquipo(3L));

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoPeriodoPago.getProyectoSocio()));

    BDDMockito.given(repository.findAllByProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(proyectoPeriodoPagoExistentes);

    // when: Actualizamos el ProyectoSocioEquipo
    // then: Lanza una excepcion porque el ProyectoSocioEquipo no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, Arrays.asList(proyectoPeriodoPago)))
        .isInstanceOf(ProyectoSocioEquipoNotFoundException.class);
  }

  @Test
  public void update_WithProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given:Se actualiza ProyectoSocio
    Long proyectoSocioId = 1L;
    ProyectoSocioEquipo proyectoPeriodoPago = generarMockProyectoSocioEquipo(1L);

    proyectoPeriodoPago.getProyectoSocio().setId(2L);

    List<ProyectoSocioEquipo> proyectoPeriodoPagoExistentes = new ArrayList<>();
    proyectoPeriodoPagoExistentes.add(generarMockProyectoSocioEquipo(1L));

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoPeriodoPago.getProyectoSocio()));

    BDDMockito.given(repository.findAllByProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(proyectoPeriodoPagoExistentes);

    // when: Actualizamos el ProyectoSocio del ProyectoSocioEquipo
    // then: Lanza una excepcion porque no se puede modificar el campo
    // ProyectoSocio
    Assertions.assertThatThrownBy(() -> service.update(proyectoSocioId, Arrays.asList(proyectoPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar el proyecto socio del ProyectoSocioEquipo");
  }

  @Test
  public void findById_ReturnsProyectoSocioEquipo() {
    // given: Un ProyectoSocioEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockProyectoSocioEquipo(idBuscado)));

    // when: Buscamos el ProyectoSocioEquipo por su id
    ProyectoSocioEquipo proyectoPeriodoPago = service.findById(idBuscado);

    // then: el ProyectoSocioEquipo
    Assertions.assertThat(proyectoPeriodoPago).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoPeriodoPago.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProyectoSocioEquipoNotFoundException() throws Exception {
    // given: Ningun ProyectoSocioEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoSocioEquipo por su id
    // then: lanza un ProyectoSocioEquipoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoSocioEquipoNotFoundException.class);
  }

  @Test
  public void findAllByProyectoSocio_ReturnsPage() {
    // given: Una lista con 37 ProyectoSocioEquipo
    Long solicitudId = 1L;
    List<ProyectoSocioEquipo> proyectoPeriodoPago = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoPeriodoPago.add(generarMockProyectoSocioEquipo(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoSocioEquipo>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoSocioEquipo>>() {
          @Override
          public Page<ProyectoSocioEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoPeriodoPago.size() ? proyectoPeriodoPago.size() : toIndex;
            List<ProyectoSocioEquipo> content = proyectoPeriodoPago.subList(fromIndex, toIndex);
            Page<ProyectoSocioEquipo> page = new PageImpl<>(content, pageable, proyectoPeriodoPago.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSocioEquipo> page = service.findAllByProyectoSocio(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoSocioPeriodoPAgo del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoSocioEquipo proyectoPeriodoPagoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoPeriodoPagoRecuperado.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que genera un ProyectoSocioEquipo
   * 
   * @param id Identificador
   * @return el ProyectoSocioEquipo
   */
  private ProyectoSocioEquipo generarMockProyectoSocioEquipo(Long id) {

    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);

    Proyecto proyecto1 = Proyecto.builder()//
        .id(id).titulo("proyecto 1").acronimo("PR1").fechaInicio(LocalDate.of(2020, 11, 20))
        .fechaFin(LocalDate.of(2021, 11, 20)).unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE).build();

    RolSocio rolSocio = RolSocio.builder()//
        .id(id).abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .coordinador(Boolean.FALSE)//
        .activo(Boolean.TRUE)//
        .build();

    RolProyecto rolProyecto = RolProyecto.builder()//
        .id(id).abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .rolPrincipal(Boolean.FALSE)//
        .equipo(RolProyecto.Equipo.INVESTIGACION).activo(Boolean.TRUE)//
        .build();

    ProyectoSocio proyectoSocio1 = ProyectoSocio.builder()//
        .id(id).proyecto(proyecto1).empresaRef("empresa-0041").rolSocio(rolSocio).build();

    ProyectoSocioEquipo proyectoSocioEquipo = new ProyectoSocioEquipo(id, proyectoSocio1, rolProyecto, "001",
        LocalDate.of(2021, 4, 10), null);

    return proyectoSocioEquipo;
  }

}
