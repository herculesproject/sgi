package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.RequisitoEquipoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * RequisitoEquipoServiceTest
 */

public class RequisitoEquipoServiceTest extends BaseServiceTest {

  @Mock
  private RequisitoEquipoRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  private RequisitoEquipoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new RequisitoEquipoServiceImpl(repository, convocatoriaRepository, convocatoriaService);
  }

  @Test
  public void create_ReturnsRequisitoEquipo() {
    // given: Un nuevo RequisitoEquipo
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(requisitoEquipo.getConvocatoria()));

    BDDMockito.given(repository.save(requisitoEquipo)).will((InvocationOnMock invocation) -> {
      RequisitoEquipo requisitoEquipoCreado = invocation.getArgument(0);
      requisitoEquipoCreado.setId(1L);
      return requisitoEquipoCreado;
    });

    // when: Creamos el RequisitoEquipo
    RequisitoEquipo requisitoEquipoCreado = service.create(requisitoEquipo);

    // then: El RequisitoEquipo se crea correctamente
    Assertions.assertThat(requisitoEquipoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoEquipoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(requisitoEquipoCreado.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoEquipo que ya tiene id
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    // when: Creamos el RequisitoEquipo
    // then: Lanza una excepcion porque el RequisitoEquipo ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(requisitoEquipo)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear RequisitoEquipo");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoEquipo sin convocatoria
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(null);
    requisitoEquipo.getConvocatoria().setId(null);

    // when: Creamos el RequisitoEquipo
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(requisitoEquipo)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria no puede ser null para crear RequisitoEquipo");
  }

  @Test
  public void create_WithDuplicatedConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoEquipo con convocatoria ya asignada
    RequisitoEquipo requisitoEquipoExistente = generarMockRequisitoEquipo(1L);
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(null);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoEquipoExistente));

    // when: Creamos el RequisitoEquipo
    // then: Lanza una excepcion porque la convocatoria ya tiene un RequisitoEquipo
    Assertions.assertThatThrownBy(() -> service.create(requisitoEquipo)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe RequisitoEquipo para la convocatoria %s", requisitoEquipo.getConvocatoria().getCodigo());
  }

  @Test
  public void update_ReturnsRequisitoEquipo() {
    // given: Un nuevo RequisitoEquipo con el sexo actualizado
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);
    RequisitoEquipo requisitoEquipoActualizado = generarMockRequisitoEquipo(1L);
    requisitoEquipoActualizado.setEdadMaxima(45);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoEquipo.getConvocatoria()));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);
    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoEquipo));
    BDDMockito.given(repository.save(ArgumentMatchers.<RequisitoEquipo>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el RequisitoEquipo
    requisitoEquipoActualizado = service.update(requisitoEquipoActualizado, requisitoEquipo.getConvocatoria().getId());

    // then: El RequisitoEquipo se actualiza correctamente.
    Assertions.assertThat(requisitoEquipoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoEquipoActualizado.getId()).as("getId()").isEqualTo(requisitoEquipo.getId());
    Assertions.assertThat(requisitoEquipoActualizado.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
  }

  @Test
  public void update_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un RequisitoEquipo actualizado con una convocatoria que no existe
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);
    requisitoEquipo.setConvocatoria(null);

    // when: Actualizamos el RequisitoEquipo
    // then: Lanza una excepcion porque el RequisitoEquipo no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoEquipo, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La Convocatoria no puede ser null para actualizar RequisitoEquipo");
  }

  @Test
  public void update_WithConvocatoriaNotExist_ThrowsNotFoundException() {
    // given: Un RequisitoEquipo actualizado con una convocatoria que no existe
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el RequisitoEquipo
    // then: Lanza una excepcion porque la Convocatoria no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoEquipo, requisitoEquipo.getConvocatoria().getId()))
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void update_NotExist_ThrowsNotFoundException() {
    // given: Un RequisitoEquipo actualizado con una convocatoria que no existe
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoEquipo.getConvocatoria()));
    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el RequisitoEquipo
    // then: Lanza una excepcion porque la Convocatoria no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoEquipo, requisitoEquipo.getConvocatoria().getId()))
        .isInstanceOf(RequisitoEquipoNotFoundException.class);
  }

  @Test
  public void update_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a RequisitoEquipo when modificable return false
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);
    requisitoEquipo.getConvocatoria().setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoEquipo.getConvocatoria()));
    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoEquipo));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update RequisitoEquipo
        () -> service.update(requisitoEquipo, requisitoEquipo.getConvocatoria().getId()))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar RequisitoEquipo. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void findByConvocatoriaId_ReturnsRequisitoEquipo() {
    // given: Un RequisitoEquipo con el id convocatoria buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findByConvocatoriaId(idBuscado))
        .willReturn(Optional.of(generarMockRequisitoEquipo(idBuscado)));

    // when: Buscamos el RequisitoEquipo por id Convocatoria
    RequisitoEquipo requisitoEquipo = service.findByConvocatoriaId(idBuscado);

    // then: el RequisitoEquipo
    Assertions.assertThat(requisitoEquipo).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoEquipo.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(requisitoEquipo.getEdadMaxima()).as("getEdadMaxima()").isEqualTo(50);

  }

  @Test
  public void findByConvocatoriaId_WithIdNotExist_ThrowsConvocatoriaNotFoundException() throws Exception {
    // given: Ninguna convocatoria con el id Convocatoria buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Buscamos id de Convocatoria
    // then: lanza un ConvocatoriaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findByConvocatoriaId(idBuscado))
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto RequisitoEquipo
   * 
   * @param id id del RequisitoEquipo
   * @return el objeto RequisitoEquipo
   */
  private RequisitoEquipo generarMockRequisitoEquipo(Long id) {
    RequisitoEquipo requisitoEquipo = new RequisitoEquipo();
    requisitoEquipo.setId(id);
    Long idConvocatoria = id;
    if (idConvocatoria == null) {
      idConvocatoria = 1L;
    }
    requisitoEquipo.setConvocatoria(
        Convocatoria.builder().id(idConvocatoria).activo(Boolean.TRUE).codigo("codigo" + idConvocatoria).build());
    requisitoEquipo.setEdadMaxima(50);
    return requisitoEquipo;
  }

}
