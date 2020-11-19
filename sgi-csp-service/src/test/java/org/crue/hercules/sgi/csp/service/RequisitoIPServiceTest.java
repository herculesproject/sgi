package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoIPNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.RequisitoIPServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RequisitoIPServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class RequisitoIPServiceTest extends BaseServiceTest {

  @Mock
  private RequisitoIPRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  private RequisitoIPService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new RequisitoIPServiceImpl(repository, convocatoriaRepository);
  }

  @Test
  public void create_ReturnsRequisitoIP() {
    // given: Un nuevo RequisitoIP
    RequisitoIP requisitoIP = generarMockRequisitoIP(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(requisitoIP.getConvocatoria()));

    BDDMockito.given(repository.save(requisitoIP)).will((InvocationOnMock invocation) -> {
      RequisitoIP requisitoIPCreado = invocation.getArgument(0);
      requisitoIPCreado.setId(1L);
      return requisitoIPCreado;
    });

    // when: Creamos el RequisitoIP
    RequisitoIP requisitoIPCreado = service.create(requisitoIP);

    // then: El RequisitoIP se crea correctamente
    Assertions.assertThat(requisitoIPCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoIPCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(requisitoIPCreado.getSexo()).as("getSexo()").isEqualTo(requisitoIP.getSexo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoIP que ya tiene id
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    // when: Creamos el RequisitoIP
    // then: Lanza una excepcion porque el RequisitoIP ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(requisitoIP)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear RequisitoIP");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoIP sin convocatoria
    RequisitoIP requisitoIP = generarMockRequisitoIP(null);
    requisitoIP.getConvocatoria().setId(null);

    // when: Creamos el RequisitoIP
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(requisitoIP)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear RequisitoIP");
  }

  @Test
  public void update_ReturnsRequisitoIP() {
    // given: Un nuevo RequisitoIP con el sexo actualizado
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);
    RequisitoIP requisitoIPActualizado = generarMockRequisitoIP(1L);
    requisitoIPActualizado.setSexo("Mujer");

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoIP));
    BDDMockito.given(repository.save(ArgumentMatchers.<RequisitoIP>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el RequisitoIP
    requisitoIPActualizado = service.update(requisitoIPActualizado, 1L);

    // then: El RequisitoIP se actualiza correctamente.
    Assertions.assertThat(requisitoIPActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoIPActualizado.getId()).as("getId()").isEqualTo(requisitoIP.getId());
    Assertions.assertThat(requisitoIPActualizado.getSexo()).as("getSexo()").isEqualTo(requisitoIP.getSexo());
  }

  @Test
  public void update_WithIdNotExist_ThrowsRequisitoIPNotFoundException() {
    // given: Un RequisitoIP actualizado con un id que no existe
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(requisitoIP));

    // when: Actualizamos el RequisitoIP
    // then: Lanza una excepcion porque el RequisitoIP no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoIP, 1L))
        .isInstanceOf(RequisitoIPNotFoundException.class);
  }

  @Test
  public void findByConvocatoriaId_ReturnsRequisitoIP() {
    // given: Un RequisitoIP con el id buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findByConvocatoriaId(idBuscado))
        .willReturn(Optional.of(generarMockRequisitoIP(idBuscado)));

    // when: Buscamos el RequisitoIP por su id
    RequisitoIP requisitoIP = service.findByConvocatoria(idBuscado);

    // then: el RequisitoIP
    Assertions.assertThat(requisitoIP).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoIP.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(requisitoIP.getSexo()).as("getSexo()").isEqualTo("Hombre");

  }

  @Test
  public void findByConvocatoriaId_WithIdNotExist_ThrowsConvocatoriaNotFoundExceptionException() throws Exception {
    // given: Ninguna Convocatoria con el id buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Buscamos el RequisitoIP por id Convocatoria
    // then: lanza un ConvocatoriaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findByConvocatoria(idBuscado))
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id id del RequisitoIP
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setId(id);
    Long idConvocatoria = id;
    if (idConvocatoria == null) {
      idConvocatoria = 1L;
    }
    requisitoIP.setConvocatoria(
        Convocatoria.builder().id(idConvocatoria).activo(Boolean.TRUE).codigo("codigo" + idConvocatoria).build());
    requisitoIP.setSexo("Hombre");
    return requisitoIP;
  }

}
