package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.service.impl.ComentarioServiceImpl;
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
 * ComentarioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ComentarioServiceTest {

  @Mock
  private ComentarioRepository comentarioRepository;

  @Mock
  private EvaluacionRepository evaluacionRepository;

  private ComentarioService comentarioService;

  @BeforeEach
  public void setUp() throws Exception {
    comentarioService = new ComentarioServiceImpl(comentarioRepository, evaluacionRepository);
  }

  @Test
  public void find_WithId_ReturnsComentario() {
    BDDMockito.given(comentarioRepository.findById(1L))
        .willReturn(Optional.of(generarMockComentario(1L, "Comentario1")));

    Comentario comentario = comentarioService.findById(1L);

    Assertions.assertThat(comentario.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentario.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentario.getApartadoFormulario().getId()).as("apartadoFormulario.id").isEqualTo(100L);
    Assertions.assertThat(comentario.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentario.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentario.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(300L);
    Assertions.assertThat(comentario.getTexto()).as("texto").isEqualTo("Comentario1");
  }

  @Test
  public void find_NotFound_ThrowsComentarioNotFoundException() throws Exception {
    BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> comentarioService.findById(1L)).isInstanceOf(ComentarioNotFoundException.class);
  }

  @Test
  public void create_ReturnsComentario() {
    // given: Un nuevo Comentario
    Comentario comentarioNew = generarMockComentario(null, "ComentarioNew");

    Comentario comentario = generarMockComentario(1L, "ComentarioNew");

    BDDMockito.given(comentarioRepository.save(comentarioNew)).willReturn(comentario);

    // when: Creamos el comentario
    Comentario comentarioCreado = comentarioService.create(comentarioNew);

    // then: el comentario se crea correctamente
    Assertions.assertThat(comentarioCreado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentarioCreado.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentarioCreado.getApartadoFormulario().getId()).as("apartadoFormulario.id").isEqualTo(100L);
    Assertions.assertThat(comentarioCreado.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentarioCreado.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentarioCreado.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentarioCreado.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(300L);
    Assertions.assertThat(comentarioCreado.getTexto()).as("texto").isEqualTo("ComentarioNew");
  }

  @Test
  public void create_ComentarioWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo comentario que ya tiene id
    Comentario comentarioNew = generarMockComentario(1L, "ComentarioNew");
    // when: Creamos el comentario
    // then: Lanza una excepcion porque el comentario ya tiene id
    Assertions.assertThatThrownBy(() -> comentarioService.create(comentarioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsComentario() {
    // given: Un nuevo comentario con el texto actualizado
    Comentario comentarioTextoActualizado = generarMockComentario(1L, "Comentario1 actualizado");

    Comentario comentario = generarMockComentario(1L, "Comentario1");

    BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.of(comentario));
    BDDMockito.given(comentarioRepository.save(comentarioTextoActualizado)).willReturn(comentarioTextoActualizado);

    // when: Actualizamos el comentario
    Comentario comentarioActualizado = comentarioService.update(comentarioTextoActualizado);

    // then: El comentario se actualiza correctamente.
    Assertions.assertThat(comentarioActualizado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentarioActualizado.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentarioActualizado.getApartadoFormulario().getId()).as("apartadoFormulario.id")
        .isEqualTo(100L);
    Assertions.assertThat(comentarioActualizado.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentarioActualizado.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentarioActualizado.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentarioActualizado.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(300L);
    Assertions.assertThat(comentarioActualizado.getTexto()).as("texto").isEqualTo("Comentario1 actualizado");
  }

  @Test
  public void update_ThrowsComentarionNotFoundException() {
    // given: Un nuevo comentario a actualizar
    Comentario comentarioTextoActualizado = generarMockComentario(1L, "Comentario1 actualizado");

    // then: Lanza una excepcion porque el comentario no existe
    Assertions.assertThatThrownBy(() -> comentarioService.update(comentarioTextoActualizado))
        .isInstanceOf(ComentarioNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un comentario que venga sin id
    Comentario comentarioTextoActualizado = generarMockComentario(null, "Comentario1 actualizado");

    Assertions.assertThatThrownBy(
        // when: update comentario
        () -> comentarioService.update(comentarioTextoActualizado))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> comentarioService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsComentarioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(comentarioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> comentarioService.delete(1L))
        // then: Lanza ComentarioNotFoundException
        .isInstanceOf(ComentarioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesComentario() {
    // given: Id existente
    BDDMockito.given(comentarioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(comentarioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> comentarioService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullComentarioList() {
    // given: One hundred comentarios
    List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i)));
    }

    BDDMockito.given(comentarioRepository.findAll(ArgumentMatchers.<Specification<Comentario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(comentarios));

    // when: find unlimited
    Page<Comentario> page = comentarioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred comentarios
    List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i)));
    }

    BDDMockito.given(comentarioRepository.findAll(ArgumentMatchers.<Specification<Comentario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Comentario>>() {
          @Override
          public Page<Comentario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Comentario> content = comentarios.subList(fromIndex, toIndex);
            Page<Comentario> page = new PageImpl<>(content, pageable, comentarios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Comentario> page = comentarioService.findAll(null, paging);

    // then: A Page with ten comentarios are returned containing
    // descripcion='Comentario031' to 'Comentario040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", j));
    }
  }

  @Test
  public void findByEvaluacionWithoutPagingValidId() {
    // given: EL id de la evaluación sea valido
    Long evaluacionId = 1L;
    int numeroComentario = 6;
    List<Comentario> comentarios = new ArrayList<>();
    for (int i = 0; i < numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i)));
    }
    BDDMockito.given(comentarioRepository.findByEvaluacionId(evaluacionId, Pageable.unpaged()))
        .willReturn(new PageImpl<>(comentarios));
    // when: se listen sus comentarios
    Page<Comentario> page = comentarioService.findByEvaluacionId(evaluacionId, Pageable.unpaged());

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(numeroComentario);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(numeroComentario);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(numeroComentario);
    for (int i = 0; i < numeroComentario; i++) {
      Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", i));
    }
  }

  @Test
  public void findByEvaluacionWithPagingValidId() {
    // given: EL id de la evaluación sea valido
    Long evaluacionId = 1L;
    int numeroComentario = 100;
    List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i)));
    }

    int numPagina = 7;
    int numElementos = 10;
    Pageable paging = PageRequest.of(numPagina, numElementos);
    BDDMockito
        .given(comentarioRepository.findByEvaluacionId(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comentario>>() {
          @Override
          public Page<Comentario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Comentario> content = comentarios.subList(fromIndex, toIndex);
            Page<Comentario> page = new PageImpl<>(content, pageable, comentarios.size());
            return page;
          }
        });
    // when: se listen sus comentarios
    Page<Comentario> page = comentarioService.findByEvaluacionId(evaluacionId, paging);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(numElementos);
    Assertions.assertThat(page.getNumber()).isEqualTo(numPagina);
    Assertions.assertThat(page.getSize()).isEqualTo(numElementos);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(numeroComentario);
    for (int i = 0, j = (numPagina * 10) + 1; i < 10; i++, j++) {
      Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", j));
    }
  }

  @Test
  public void findByEvaluacionNullId() {
    // given: EL id de la evaluación sea null
    Long evaluacionId = null;
    try {
      // when: se quiera listar sus comentarios
      comentarioService.findByEvaluacionId(evaluacionId, Pageable.unpaged());
      Assertions.fail("El id no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la evaluación no puede ser nulo para listar sus comentarios");
    }
  }

  @Test
  public void createAllEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    Long evaluacionId = null;
    try {
      // when: se quiera añadir comentarios
      comentarioService.createAll(evaluacionId, new ArrayList<>());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Evaluacion id no tiene que ser null para crear un listado de comentario");
    }
  }

  @Test
  public void createAllEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    Long evaluacionId = 12L;
    try {
      // when: se quiera añadir comentarios
      comentarioService.createAll(evaluacionId, new ArrayList<>());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluacion " + evaluacionId + " does not exist.");
    }
  }

  @Test
  public void createAllNotComentarios() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    // when: se quiera una lista vacia de comentarios
    List<Comentario> comentarios = comentarioService.createAll(evaluacionId, new ArrayList<>());
    // then: obtenemos una lista vacia de comentarios
    Assertions.assertThat(comentarios != null);
    Assertions.assertThat(comentarios.isEmpty());
  }

  @Test
  public void createAllComentariosNotValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    List<Comentario> comentarios = new ArrayList<>();
    comentarios.add(generarMockComentario(null, "texto"));
    // when: se quiera una lista de comentarios cuyo evaluacion no coincide con el
    // id indicado
    try {
      comentarioService.createAll(evaluacionId, comentarios);
      Assertions.fail("EvaluacionId debe coincidir con el id de la evaluacion del comentario para crearlo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("EvaluacionId debe coincidir con el id de la evaluacion del comentario para crearlo");
    }
  }

  @Test
  public void createAllValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    Comentario comentario = generarMockComentario(null, "texto");
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    Comentario comentarioNew = generarMockComentario(12L, "texto");
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);
    List<Comentario> comentarios = new ArrayList<>();
    comentarios.add(comentario);

    // when: se quiera una lista vacia de comentarios
    List<Comentario> resultados = comentarioService.createAll(evaluacionId, comentarios);

    // then: obtenemos una lista vacia de comentarios
    Assertions.assertThat(resultados != null);
    Assertions.assertThat(resultados.size() == 1);
    Assertions.assertThat(resultados.size() == comentarios.size());
  }

  @Test
  public void updateAllEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    Long evaluacionId = null;
    try {
      // when: se quiera añadir comentarios
      comentarioService.updateAll(evaluacionId, new ArrayList<>());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Evaluacion id no tiene que ser null para actualizar un listado de comentario");
    }
  }

  @Test
  public void updateAllEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    Long evaluacionId = 12L;
    try {
      // when: se quiera actualizar comentarios
      comentarioService.updateAll(evaluacionId, new ArrayList<>());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluacion " + evaluacionId + " does not exist.");
    }
  }

  @Test
  public void updateAllNotComentarios() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    // when: se quiera actualizar una lista vacia de comentarios
    List<Comentario> comentarios = comentarioService.updateAll(evaluacionId, new ArrayList<>());
    // then: obtenemos una lista vacia de comentarios
    Assertions.assertThat(comentarios != null);
    Assertions.assertThat(comentarios.isEmpty());
  }

  @Test
  public void updateAllComentariosNotValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    List<Comentario> comentarios = new ArrayList<>();
    comentarios.add(generarMockComentario(null, "texto"));
    // when: se quiera actualizar una lista de comentarios cuyo evaluacion no
    // coincide con el id indicado
    try {
      comentarioService.updateAll(evaluacionId, comentarios);
      Assertions.fail("EvaluacionId debe coincidir con el id de la evaluacion del comentario para actualizarlo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("EvaluacionId debe coincidir con el id de la evaluacion del comentario para actualizarlo");
    }
  }

  @Test
  public void updateAllValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    Comentario comentario = generarMockComentario(12L, "texto");
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);
    List<Comentario> comentarios = new ArrayList<>();
    comentarios.add(comentario);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se quiera actualizar una lista de comentarios
    List<Comentario> resultados = comentarioService.updateAll(evaluacionId, comentarios);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
    Assertions.assertThat(resultados.size() == 1);
    Assertions.assertThat(resultados.size() == comentarios.size());
  }

  @Test
  public void deleteAllEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    Long evaluacionId = null;
    try {
      // when: se quiera eliminar comentarios
      comentarioService.deleteAll(evaluacionId, new ArrayList<>());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Evaluacion id no tiene que ser null para eliminar un listado de comentario");
    }
  }

  @Test
  public void deleteAllEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    Long evaluacionId = 12L;
    try {
      // when: se quiera eliminar comentarios
      comentarioService.deleteAll(evaluacionId, new ArrayList<>());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluacion " + evaluacionId + " does not exist.");
    }
  }

  @Test
  public void deleteAllComentariosNotValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    BDDMockito.given(comentarioRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockComentario(evaluacionId, "")));
    List<Long> ids = new ArrayList<>();
    ids.add(evaluacionId);
    // when: se quiera eliminar una lista de comentarios cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteAll(evaluacionId, ids);
      Assertions.fail("EvaluacionId debe coincidir con el id de la evaluacion del comentario para eliminarlo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("EvaluacionId debe coincidir con el id de la evaluacion del comentario para eliminarlo");
    }
  }

  @Test
  public void deleteAllValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);
    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));
    Comentario comentario = generarMockComentario(evaluacionId, "texto");
    comentario.setEvaluacion(evaluacion);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));
    BDDMockito.given(comentarioRepository.existsById(evaluacionId)).willReturn(true);
    List<Long> ids = new ArrayList<>();
    ids.add(evaluacionId);

    // when: se quiera eliminar una lista de comentarios
    Exception exception = null;
    try {
      comentarioService.deleteAll(evaluacionId, ids);
    } catch (Exception e) {
      exception = e;
    }
    // then: no debe producirse ninguna excepción
    Assertions.assertThat(exception == null);
  }

  @Test
  public void countByEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    Long evaluacionId = null;
    try {
      // when: se quiera enumerar sus comentarios
      comentarioService.countByEvaluacionId(evaluacionId);
      Assertions.fail("El id de la evaluación no puede ser nulo para mostrar el número total de sus comentarios");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la evaluación no puede ser nulo para mostrar el número total de sus comentarios");
    }
  }

  @Test
  public void countByEvaluacionIdValid() {
    // given: EL id de la evaluación es válido
    Long evaluacionId = 12L;
    int numeroComentario = 123;
    BDDMockito.given(comentarioRepository.countByEvaluacionId(evaluacionId)).willReturn(numeroComentario);
    // when: se quiera enumerar sus comentarios
    int count = comentarioService.countByEvaluacionId(evaluacionId);
    // then: debe funcionar correctamente
    Assertions.assertThat(count == numeroComentario);
  }

  /**
   * Función que devuelve un objeto Comentario
   * 
   * @param id    id de la comentario
   * @param texto texto del comentario
   * @return el objeto Comentario
   */
  public Comentario generarMockComentario(Long id, String texto) {
    ApartadoFormulario apartadoFormulario = new ApartadoFormulario();
    apartadoFormulario.setId(100L);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(200L);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(300L);

    Comentario comentario = new Comentario();
    comentario.setId(id);
    comentario.setApartadoFormulario(apartadoFormulario);
    comentario.setEvaluacion(evaluacion);
    comentario.setTipoComentario(tipoComentario);
    comentario.setTexto(texto);

    return comentario;
  }

  private Evaluacion generarMockEvaluacion(Long id) {
    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    return evaluacion;
  }

}
