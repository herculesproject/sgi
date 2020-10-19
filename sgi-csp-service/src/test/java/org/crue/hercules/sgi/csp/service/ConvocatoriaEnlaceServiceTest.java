package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEnlaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConvocatoriaEnlaceServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ConvocatoriaEnlaceServiceTest {

  @Mock
  private ConvocatoriaEnlaceRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private TipoEnlaceRepository tipoEnlaceRepository;
  @Mock
  private ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;

  private ConvocatoriaEnlaceService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaEnlaceServiceImpl(repository, convocatoriaRepository, tipoEnlaceRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlace.getConvocatoria()));
    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlace.getTipoEnlace()));

    BDDMockito.given(repository.save(convocatoriaEnlace)).will((InvocationOnMock invocation) -> {
      ConvocatoriaEnlace convocatoriaEnlaceCreado = invocation.getArgument(0);
      convocatoriaEnlaceCreado.setId(1L);
      return convocatoriaEnlaceCreado;
    });

    // when: Creamos el ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlaceCreado = service.create(convocatoriaEnlace);

    // then: El ConvocatoriaEnlace se crea correctamente
    Assertions.assertThat(convocatoriaEnlaceCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlaceCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEnlaceCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlace.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceCreado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlace.getTipoEnlace()).as("getTipoEnlace()")
        .isEqualTo(convocatoriaEnlace.getTipoEnlace());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace que ya tiene id
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaEnlace id tiene que ser null para crear un nuevo ConvocatoriaEnlace");
  }

  @Test
  public void create_WithoutUrl_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace sin url
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null);
    convocatoriaEnlace.setUrl(null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la url es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaEnlace url no puede ser null para crear una nueva ConvocatoriaEnlace");
  }

  @Test
  public void create_WithouConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace sin convocatoria
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null);
    convocatoriaEnlace.getConvocatoria().setId(null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaEnlace");
  }

  @Test
  public void create_WithTipoEnlaceInactivo_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace con el enlace inactivo
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null);
    convocatoriaEnlace.getTipoEnlace().setActivo(false);

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlace.getTipoEnlace()));

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el enlace está inactivo
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El TipoEnlace debe estar activo");
  }

  @Test
  public void create_WithoutTipoEnlace_ThrowsNotFoundException() {
    // given: Un nuevo ConvocatoriaEnlace sin tipo de enlace
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el tipo de enlace es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace))
        .isInstanceOf(TipoEnlaceNotFoundException.class).hasMessage("TipoEnlace 1 does not exist.");
  }

  @Test
  public void update_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace con el nombre actualizado
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);
    ConvocatoriaEnlace convocatoriaEnlaceDescripcionActualizada = generarMockConvocatoriaEnlace(1L);
    convocatoriaEnlaceDescripcionActualizada.setDescripcion("nuevaDescripcion");

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlace.getTipoEnlace()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = service.update(convocatoriaEnlaceDescripcionActualizada);

    // then: El ConvocatoriaEnlace se actualiza correctamente.
    Assertions.assertThat(convocatoriaEnlaceActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getId()).as("getId()").isEqualTo(convocatoriaEnlace.getId());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlaceDescripcionActualizada.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()")
        .isEqualTo(convocatoriaEnlace.getTipoEnlace());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoria()).as("getConvocatoria()")
        .isEqualTo(convocatoriaEnlace.getConvocatoria());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaEnlaceNotFoundException() {
    // given: Un ConvocatoriaEnlace actualizado con un id que no existe
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);
    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlace.getTipoEnlace()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));

    // when: Actualizamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlace))
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
  }

  @Test
  public void update_Withouturl_ThrowsIllegalArgumentException() {
    // given: Un ConvocatoriaEnlace actualizado con sin url
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);
    convocatoriaEnlace.setUrl(null);

    // when: Actualizamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaEnlace url no puede ser null para actualizar un nuevo ConvocatoriaEnlace");
  }

  @Test
  public void update_WithTipoEnlaceInactivo_ThrowsIllegalArgumentException() {
    // given: Una nueva ConvocatoriaActualizada sin url
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);
  
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = generarMockConvocatoriaEnlace(1L);
    convocatoriaEnlaceActualizado.getTipoEnlace().setActivo(false);
    convocatoriaEnlaceActualizado.getTipoEnlace().setId(2L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlace));

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEnlaceActualizado.getTipoEnlace()));

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la url es null
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlaceActualizado)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El TipoEnlace debe estar activo");
  }

  @Test
  public void update_WithTipoEnlaceIdNoExists_ThrowsTipoEnlaceNotFoundException() {
    // given: Un nuevo ConvocatoriaEnlace sin tipo de enlace
    ConvocatoriaEnlace convocatoriaEnlaceActualizar = generarMockConvocatoriaEnlace(1L);

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());
    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el tipo de enlace es null
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlaceActualizar))
        .isInstanceOf(TipoEnlaceNotFoundException.class).hasMessage("TipoEnlace 1 does not exist.");
  }
  
  @Test
  public void update_WithoutTipoEnlace_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace con el nombre actualizado
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);
    ConvocatoriaEnlace convocatoriaEnlaceDescripcionActualizada = generarMockConvocatoriaEnlace(1L);
    convocatoriaEnlaceDescripcionActualizada.setDescripcion("nuevaDescripcion");
    convocatoriaEnlaceDescripcionActualizada.setTipoEnlace(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = service.update(convocatoriaEnlaceDescripcionActualizada);

    // then: El ConvocatoriaEnlace se actualiza correctamente.
    Assertions.assertThat(convocatoriaEnlaceActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getId()).as("getId()").isEqualTo(convocatoriaEnlace.getId());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlaceDescripcionActualizada.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()").isNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoria()).as("getConvocatoria()")
        .isEqualTo(convocatoriaEnlace.getConvocatoria());
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing convocatoriaEnlace
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
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
  }

  @Test
  public void findById_ReturnsConvocatoriaEnlace() {
    // given: Un ConvocatoriaEnlace con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConvocatoriaEnlace(idBuscado)));

    // when: Buscamos el ConvocatoriaEnlace por su id
    ConvocatoriaEnlace convocatoriaEnlace = service.findById(idBuscado);

    // then: el ConvocatoriaEnlace
    Assertions.assertThat(convocatoriaEnlace).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlace.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaEnlace.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(convocatoriaEnlace.getConvocatoria().getId()).as("getConvocatoria()").isEqualTo(1L);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaEnlaceNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaEnlace con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaEnlace por su id
    // then: lanza un ConvocatoriaEnlaceNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id     id del ConvocatoriaEnlace
   * @param nombre nombre del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id) {

    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    TipoEnlace tipoEnlace = new TipoEnlace();
    tipoEnlace.setId(id == null ? 1 : id);

    tipoEnlace.setActivo(true);

    ConvocatoriaEnlace convocatoriaEnlace = new ConvocatoriaEnlace();
    convocatoriaEnlace.setId(id);
    convocatoriaEnlace.setConvocatoria(convocatoria);
    convocatoriaEnlace.setDescripcion("descripcion-" + id);
    convocatoriaEnlace.setUrl("www.url" + id + ".es");
    convocatoriaEnlace.setTipoEnlace(tipoEnlace);

    return convocatoriaEnlace;
  }

}
