package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoDocumentoServiceImpl;
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
 * ModeloTipoDocumentoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ModeloTipoDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;

  @Mock
  private ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;

  @Mock
  private TipoDocumentoRepository tipoDocumentoRepository;

  private ModeloTipoDocumentoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoDocumentoServiceImpl(modeloEjecucionRepository, modeloTipoDocumentoRepository,
        modeloTipoFaseRepository, tipoDocumentoRepository);
  }

  @Test
  public void create_ReturnsModeloTipoDocumento() {
    // given: Un nuevo ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, null);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    BDDMockito.given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndTipoDocumentoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(new ArrayList<>());

    BDDMockito.given(modeloTipoDocumentoRepository.save(modeloTipoDocumento)).will((InvocationOnMock invocation) -> {
      ModeloTipoDocumento modeloTipoDocumentoCreado = invocation.getArgument(0);
      modeloTipoDocumentoCreado.setId(1L);
      return modeloTipoDocumentoCreado;
    });

    // when: Creamos el ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumentoCreado = service.create(modeloTipoDocumento);

    // then: El ModeloTipoDocumento se crea correctamente
    Assertions.assertThat(modeloTipoDocumentoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoDocumento.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoDocumentoCreado.getTipoDocumento()).as("getTipoDocumento()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(modeloTipoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloTipoFase()).as("getModeloTipoFase()").isNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento que ya tiene id
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(1L);

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque el ModeloTipoDocumento ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear un modeloTipoDocumento");
  }

  @Test
  public void create_WithoutModeloEjecucionId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con un ModeloEjecucion sin id
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null);
    modeloTipoDocumento.getModeloEjecucion().setId(null);

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id ModeloEjecucion no puede ser null para crear un modeloTipoDocumento");
  }

  @Test
  public void create_WithoutTipoDocumentoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con un TipoDocumento sin id
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null);
    modeloTipoDocumento.getTipoDocumento().setId(null);

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id TipoDocumento no puede ser null para crear un modeloTipoDocumento");
  }

  @Test
  public void create_WithNoExistingModeloEjecucion_ThrowsModeloEjecucionNotFoundException() {
    // given: Un nuevo ModeloTipoDocumento con un ModeleoEjecucion que no existe
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, null);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque el ModeleoEjecucion no existe id
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingTipoDocumento_ThrowsTipoDocumentoNotFoundException() {
    // given: Un nuevo ModeloTipoDocumento con un TipoDocumento que no existe
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, null);
    modeloTipoDocumento.getTipoDocumento().setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el modeloTipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe id
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  public void create_WithDisabledTipoDocumento_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con un TipoDocumento que no esta activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, null);
    modeloTipoDocumento.getTipoDocumento().setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no esta activo
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoDocumento debe estar Activo");
  }

  @Test
  public void create_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con un ModeloTipoFase que no esta activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, 1L);
    modeloTipoDocumento.getModeloTipoFase().setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    BDDMockito.given(modeloTipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloTipoFase()));

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque el ModeloTipoFase no esta activo
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El ModeloTipoFase debe estar Activo");
  }

  @Test
  public void create_WithModeloTipoFaseAndExistWithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con una combinacion de ModeloEjecucionId
    // y TipoDocumentoId y ModeloTipoFaseId que ya existe y esta activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    BDDMockito.given(modeloTipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloTipoFase()));

    BDDMockito.given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndTipoDocumentoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Arrays.asList(generarMockModeloTipoDocumento(1L, 1L, null)));

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque ya existe esa relacion activa
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para ese ModeloEjecucion y ese TipoDocumento sin ModeloTipoFase");
  }

  @Test
  public void create_WithoutModeloTipoFaseAndExistWithModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con una combinacion de ModeloEjecucionId
    // y TipoDocumentoId y ModeloTipoFaseId que ya existe y esta activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, null);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    BDDMockito.given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndTipoDocumentoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Arrays.asList(generarMockModeloTipoDocumento(1L, 1L, 1L)));

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque ya existe esa relacion activa
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para ese ModeloEjecucion y ese TipoDocumento con ModeloTipoFase");
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoDocumentoAndModeloTipoFaseIdAndActivo_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoDocumento con una combinacion de ModeloEjecucionId
    // y TipoDocumentoId y ModeloTipoFaseId que ya existe y esta activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    BDDMockito.given(modeloTipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloTipoFase()));

    BDDMockito.given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndTipoDocumentoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Arrays.asList(generarMockModeloTipoDocumento(1L, 1L, 1L)));

    // when: Creamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque ya existe esa relacion activa
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoDocumento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para ese ModeloEjecucion, TipoDocumento y ModeloTipoFase");
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoDocumentoAndModeloTipoFaseIdAndActivoFalse_ReturnEnableModeloTipoDocumento() {
    // given: Un nuevo ModeloTipoDocumento con una combinacion de ModeloEjecucionId
    // y TipoDocumentoId y ModeloTipoFaseId que ya existe y no esta activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, 1L);
    ModeloTipoDocumento modeloTipoDocumentoExiste = generarMockModeloTipoDocumento(1L, 1L, 1L);
    modeloTipoDocumentoExiste.setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloEjecucion()));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getTipoDocumento()));

    BDDMockito.given(modeloTipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento.getModeloTipoFase()));

    BDDMockito.given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndTipoDocumentoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Arrays.asList(modeloTipoDocumentoExiste));

    BDDMockito.given(modeloTipoDocumentoRepository.save(modeloTipoDocumento)).will((InvocationOnMock invocation) -> {
      ModeloTipoDocumento modeloTipoDocumentoCreado = invocation.getArgument(0);
      return modeloTipoDocumentoCreado;
    });

    // when: Creamos el ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumentoCreado = service.create(modeloTipoDocumento);

    // then: El ModeloTipoDocumento se crea correctamente
    Assertions.assertThat(modeloTipoDocumentoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoDocumento.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoDocumentoCreado.getTipoDocumento()).as("getTipoDocumento()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(modeloTipoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloTipoFase()).as("getModeloTipoFase()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoCreado.getModeloTipoFase().getId()).as("getModeloTipoFase().getId()")
        .isEqualTo(modeloTipoDocumento.getModeloTipoFase().getId());
    Assertions.assertThat(modeloTipoDocumentoCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void disable_ReturnsModeloTipoDocumento() {
    // given: Un nuevo ModeloTipoDocumento activo
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(1L);

    BDDMockito.given(modeloTipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));
    BDDMockito.given(modeloTipoDocumentoRepository.save(ArgumentMatchers.<ModeloTipoDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumentoActualizado = service.disable(modeloTipoDocumento.getId());

    // then: El ModeloTipoDocumento se desactiva correctamente.
    Assertions.assertThat(modeloTipoDocumentoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoActualizado.getId()).as("getId()").isEqualTo(modeloTipoDocumento.getId());
    Assertions.assertThat(modeloTipoDocumentoActualizado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoActualizado.getModeloEjecucion().getId())
        .as("getModeloEjecucion().getId()").isEqualTo(modeloTipoDocumento.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoDocumentoActualizado.getTipoDocumento()).as("getTipoDocumento()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoActualizado.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(modeloTipoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(modeloTipoDocumentoActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsModeloTipoDocumentoNotFoundException() {
    // given: Un id de un ModeloTipoDocumento que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloTipoDocumentoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ModeloTipoDocumento
    // then: Lanza una excepcion porque el ModeloTipoDocumento no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste))
        .isInstanceOf(ModeloTipoDocumentoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsModeloTipoDocumento() {
    // given: Un ModeloTipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloTipoDocumentoRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockModeloTipoDocumento(idBuscado)));

    // when: Buscamos el ModeloTipoDocumento por su id
    ModeloTipoDocumento modeloTipoDocumento = service.findById(idBuscado);

    // then: el ModeloTipoDocumento
    Assertions.assertThat(modeloTipoDocumento).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoDocumento.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(modeloTipoDocumento.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoDocumento.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoDocumento.getTipoDocumento()).as("getTipoDocumento()").isNotNull();
    Assertions.assertThat(modeloTipoDocumento.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoDocumento.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsModeloTipoDocumentoNotFoundException() throws Exception {
    // given: Ningun ModeloTipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloTipoDocumentoRepository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ModeloTipoDocumento por su id
    // then: lanza un ModeloTipoDocumentoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ModeloTipoDocumentoNotFoundException.class);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoDocumento para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoDocumento> modeloTipoDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoDocumentos.add(generarMockModeloTipoDocumento(i));
    }

    BDDMockito.given(modeloTipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoDocumento>>() {
          @Override
          public Page<ModeloTipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoDocumentos.size() ? modeloTipoDocumentos.size() : toIndex;
            List<ModeloTipoDocumento> content = modeloTipoDocumentos.subList(fromIndex, toIndex);
            Page<ModeloTipoDocumento> page = new PageImpl<>(content, pageable, modeloTipoDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoDocumento> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoDocumento modeloTipoDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoDocumento.getTipoDocumento().getNombre())
          .isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id id del ModeloTipoDocumento
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(Long id) {
    return generarMockModeloTipoDocumento(id, id, id);
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id               id del ModeloTipoDocumento
   * @param idTipoDocumento  id idTipoDocumento del TipoDocumento
   * @param idModeloTipoFase id del ModeloTipoFase
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(Long id, Long idTipoDocumento, Long idModeloTipoFase) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoDocumento modeloTipoDocumento = new ModeloTipoDocumento();
    modeloTipoDocumento.setId(id);
    modeloTipoDocumento.setModeloEjecucion(modeloEjecucion);
    modeloTipoDocumento.setTipoDocumento(
        generarMockTipoDocumento(idTipoDocumento, "TipoDocumento" + String.format("%03d", idTipoDocumento)));
    if (idModeloTipoFase != null) {
      modeloTipoDocumento.setModeloTipoFase(generarMockModeloTipoFase(idModeloTipoFase, idModeloTipoFase));
    }

    modeloTipoDocumento.setActivo(true);

    return modeloTipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id id del ModeloTipoDocumento
   * @param id idTipoDocumento del TipoDocumento
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, Long idTipoFase) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(idTipoFase);
    tipoFase.setNombre("nombre-" + idTipoFase);
    tipoFase.setDescripcion("descripcion-" + idTipoFase);
    tipoFase.setActivo(Boolean.TRUE);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setActivo(true);
    modeloTipoFase.setTipoFase(tipoFase);

    return modeloTipoFase;
  }

}