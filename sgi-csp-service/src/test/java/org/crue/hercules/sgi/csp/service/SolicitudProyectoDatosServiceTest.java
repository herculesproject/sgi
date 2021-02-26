package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoDatosNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoDatosRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoDatosServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SolicitudProyectoDatosServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoDatosServiceTest {

  @Mock
  private SolicitudProyectoDatosRepository repository;

  @Mock
  private SolicitudRepository solicitudRepository;
  @Mock
  private SolicitudService solicitudService;

  private SolicitudProyectoDatosService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoDatosServiceImpl(repository, solicitudRepository, solicitudService);
  }

  @Test
  public void create__ReturnsSolicitudProyectoDatos() {
    // given: Un nuevo SolicitudProyectoDatos
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoDatos>any()))
        .will((InvocationOnMock invocation) -> {
          SolicitudProyectoDatos solicitudProyectoDatosCreado = invocation.getArgument(0);
          if (solicitudProyectoDatosCreado.getId() == null) {
            solicitudProyectoDatosCreado.setId(1L);
          }

          return solicitudProyectoDatosCreado;
        });

    // when: Creamos el SolicitudProyectoDatos
    SolicitudProyectoDatos solicitudProyectoDatosCreado = service.create(solicitudProyectoDatos);

    // then: El SolicitudProyectoDatos se crea correctamente
    Assertions.assertThat(solicitudProyectoDatosCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoDatosCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoDatosCreado.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyectoDatos.getTitulo());
    Assertions.assertThat(solicitudProyectoDatosCreado.getColaborativo()).as("getColaborativo()")
        .isEqualTo(solicitudProyectoDatos.getColaborativo());
    Assertions.assertThat(solicitudProyectoDatosCreado.getPresupuestoPorEntidades()).as("getPresupuestoPorEntidades()")
        .isEqualTo(solicitudProyectoDatos.getPresupuestoPorEntidades());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que ya tiene id
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    // when: Creamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque el SolicitudProyectoDatos ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear la SolicitudProyectoDatos");
  }

  @Test
  public void create_WithoutSolicitudId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene solicitud
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    solicitudProyectoDatos.setSolicitud(null);

    // when: Creamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La solicitud no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void create_WithoutTitulo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene titulo
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    solicitudProyectoDatos.setTitulo(null);

    // when: Creamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene titulo
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El título no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void create_WithoutColaborativo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene colaborativo
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    solicitudProyectoDatos.setColaborativo(null);

    // when: Creamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene colaborativo
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Colaborativo no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void create_WithoutPresupuestoPorEntidades_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene presupuesto por entidades
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    solicitudProyectoDatos.setPresupuestoPorEntidades(null);

    // when: Creamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene presupuesto por entidades
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Presupuesto por entidades no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void create_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudProyectoDatos que tiene una solicitud que no existe
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el SolicitudProyectoDatos
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoDatos))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void update_ReturnsSolicitudProyectoDatos() {
    // given: Un nuevo SolicitudProyectoDatos con el titulo actualizado
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(3L, 1L);

    SolicitudProyectoDatos solicitudProyectoDatosActualizado = generarSolicitudProyectoDatos(3L, 1L);

    solicitudProyectoDatosActualizado.setTitulo("titulo-actualizado");

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudProyectoDatos));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoDatos>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoDatos
    SolicitudProyectoDatos solicitudProyectoDatosActualizada = service.update(solicitudProyectoDatosActualizado);

    // then: El SolicitudProyectoDatos se actualiza correctamente.
    Assertions.assertThat(solicitudProyectoDatosActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoDatosActualizada.getId()).as("getId()")
        .isEqualTo(solicitudProyectoDatos.getId());
    Assertions.assertThat(solicitudProyectoDatosActualizada.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyectoDatos.getTitulo());

  }

  @Test
  public void update_WithSolicitudNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un SolicitudProyectoDatos actualizado con un programa que no existe
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque la solicitud asociada no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoDatos))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoDatosNotFoundException() {
    // given: Un SolicitudProyectoDatos actualizado con un id que no existe
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque el SolicitudProyectoDatos no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoDatos))
        .isInstanceOf(SolicitudProyectoDatosNotFoundException.class);
  }

  @Test
  public void update_WithoutSolicitudId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene solicitud
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    solicitudProyectoDatos.setSolicitud(null);

    // when: Actualizamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La solicitud no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void update_WithoutTitulo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene titulo
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    solicitudProyectoDatos.setTitulo(null);

    // when: Actualizamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene titulo
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El título no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void update_WithoutColaborativo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene colaborativo
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    solicitudProyectoDatos.setColaborativo(null);

    // when: Actualizamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene colaborativo
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Colaborativo no puede ser null para realizar la acción sobre SolicitudProyectoDatos");
  }

  @Test
  public void update_WithoutPresupuestoPorEntidades_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoDatos que no tiene presupuesto por entidades
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    solicitudProyectoDatos.setPresupuestoPorEntidades(null);

    // when: Actualizamos el SolicitudProyectoDatos
    // then: Lanza una excepcion porque no tiene presupuesto por entidades
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoDatos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id no puede ser null para actualizar SolicitudProyectoDatos");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyectoDatos
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
        .isInstanceOf(SolicitudProyectoDatosNotFoundException.class);
  }

  @Test
  public void findById_ReturnsSolicitudProyectoDatos() {
    // given: Un SolicitudProyectoDatos con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoDatos(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoDatos por su id
    SolicitudProyectoDatos solicitudProyectoDatos = service.findById(idBuscado);

    // then: el SolicitudProyectoDatos
    Assertions.assertThat(solicitudProyectoDatos).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoDatos.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoDatosNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoDatos con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoDatos por su id
    // then: lanza un SolicitudProyectoDatosNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoDatosNotFoundException.class);
  }

  @Test
  public void findBySolicitudId_ReturnsSolicitudProyectoDatos() {
    // given: Un Solicitud con el id buscado
    Long idSolicitud = 1L;

    BDDMockito.given(solicitudRepository.existsById(idSolicitud)).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findBySolicitudId(idSolicitud))
        .willReturn(Optional.of(generarSolicitudProyectoDatos(idSolicitud, 1L)));

    // when: Buscamos el SolicitudProyectoDatos por solicitud id
    SolicitudProyectoDatos solicitudProyectoDatos = service.findBySolicitud(idSolicitud);

    // then: el SolicitudProyectoDatos
    Assertions.assertThat(solicitudProyectoDatos).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoDatos.getSolicitud().getId()).as("getSolicitud().getId()")
        .isEqualTo(idSolicitud);
  }

  @Test
  public void findBySolicitudId_WithSolicitudIdNotExist_ThrowsSolicitudProyectoDatosNotFoundException()
      throws Exception {
    // given: Ningun SolicitudProyectoDatos con el solicitud id buscado
    Long idSolicitud = 1L;

    // when: Buscamos el SolicitudProyectoDatos por solicitud id
    // then: lanza un SolicitudProyectoDatosNotFoundException
    Assertions.assertThatThrownBy(() -> service.findBySolicitud(idSolicitud))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoDatos
   * 
   * @param solicitudProyectoDatosId
   * @param solicitudId
   * @return el objeto SolicitudProyectoDatos
   */
  private SolicitudProyectoDatos generarSolicitudProyectoDatos(Long solicitudProyectoDatosId, Long solicitudId) {

    SolicitudProyectoDatos solicitudProyectoDatos = SolicitudProyectoDatos.builder().id(solicitudProyectoDatosId)
        .solicitud(Solicitud.builder().id(solicitudId).build()).titulo("titulo-" + solicitudProyectoDatosId)
        .acronimo("acronimo-" + solicitudProyectoDatosId).colaborativo(Boolean.TRUE)
        .presupuestoPorEntidades(Boolean.TRUE).build();

    solicitudProyectoDatos.getSolicitud().setEstado(new EstadoSolicitud());
    solicitudProyectoDatos.getSolicitud().getEstado().setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoDatos;
  }

}
