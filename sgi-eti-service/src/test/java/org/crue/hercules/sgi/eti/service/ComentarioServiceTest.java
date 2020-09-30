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
        .willReturn(Optional.of(generarMockComentario(1L, "Comentario1", 1L)));

    final Comentario comentario = comentarioService.findById(1L);

    Assertions.assertThat(comentario.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentario.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentario.getApartadoFormulario().getId()).as("apartadoFormulario.id").isEqualTo(100L);
    Assertions.assertThat(comentario.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentario.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentario.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(1L);
    Assertions.assertThat(comentario.getTexto()).as("texto").isEqualTo("Comentario1");
  }

  @Test
  public void find_NotFound_ThrowsComentarioNotFoundException() throws Exception {
    BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> comentarioService.findById(1L)).isInstanceOf(ComentarioNotFoundException.class);
  }

  @Test
  public void findByEvaluacionGestorWithoutPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final Long tipoComentarioId = 1L;
    final int numeroComentario = 6;
    final List<Comentario> comentarios = new ArrayList<>();
    for (int i = 0; i < numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }
    BDDMockito.given(
        comentarioRepository.findByEvaluacionIdAndTipoComentarioId(evaluacionId, tipoComentarioId, Pageable.unpaged()))
        .willReturn(new PageImpl<>(comentarios));
    // when: se listen sus comentarios
    final Page<Comentario> page = comentarioService.findByEvaluacionIdGestor(evaluacionId, Pageable.unpaged());

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(numeroComentario);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(numeroComentario);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(numeroComentario);
    for (int i = 0; i < numeroComentario; i++) {
      final Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", i));
    }
  }

  @Test
  public void findByEvaluacionGestorWithPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final int numeroComentario = 100;
    final List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }

    final int numPagina = 3;
    final int numElementos = 10;
    BDDMockito.given(comentarioRepository.findByEvaluacionIdAndTipoComentarioId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Comentario>>() {
          @Override
          public Page<Comentario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
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
    Pageable paging = PageRequest.of(3, 10);
    final Page<Comentario> page = comentarioService.findByEvaluacionIdGestor(evaluacionId, paging);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(numElementos);
    Assertions.assertThat(page.getNumber()).isEqualTo(numPagina);
    Assertions.assertThat(page.getSize()).isEqualTo(numElementos);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(numeroComentario);
    for (int i = 0, j = (numPagina * 10) + 1; i < 10; i++, j++) {
      final Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", j));
    }
  }

  @Test
  public void findByEvaluacionGestorNullId() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera listar sus comentarios
      comentarioService.findByEvaluacionIdGestor(evaluacionId, Pageable.unpaged());
      Assertions.fail("El id no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la evaluación no puede ser nulo para listar sus comentarios");
    }
  }

  @Test
  public void findByEvaluacionEvaluadorWithoutPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final Long tipoComentarioId = 2L;
    final int numeroComentario = 6;
    final List<Comentario> comentarios = new ArrayList<>();
    for (int i = 0; i < numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 2L));
    }
    BDDMockito.given(
        comentarioRepository.findByEvaluacionIdAndTipoComentarioId(evaluacionId, tipoComentarioId, Pageable.unpaged()))
        .willReturn(new PageImpl<>(comentarios));
    // when: se listen sus comentarios
    final Page<Comentario> page = comentarioService.findByEvaluacionIdEvaluador(evaluacionId, Pageable.unpaged());

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(numeroComentario);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(numeroComentario);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(numeroComentario);
    for (int i = 0; i < numeroComentario; i++) {
      final Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", i));
    }
  }

  @Test
  public void findByEvaluacionEvaluadorWithPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final int numeroComentario = 100;
    final List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }

    final int numPagina = 7;
    final int numElementos = 10;
    final Pageable paging = PageRequest.of(numPagina, numElementos);
    BDDMockito.given(comentarioRepository.findByEvaluacionIdAndTipoComentarioId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Comentario>>() {
          @Override
          public Page<Comentario> answer(final InvocationOnMock invocation) throws Throwable {
            final Pageable pageable = invocation.getArgument(2, Pageable.class);
            final int size = pageable.getPageSize();
            final int index = pageable.getPageNumber();
            final int fromIndex = size * index;
            final int toIndex = fromIndex + size;
            final List<Comentario> content = comentarios.subList(fromIndex, toIndex);
            final Page<Comentario> page = new PageImpl<>(content, pageable, comentarios.size());
            return page;
          }
        });
    // when: se listen sus comentarios
    final Page<Comentario> page = comentarioService.findByEvaluacionIdEvaluador(evaluacionId, paging);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(numElementos);
    Assertions.assertThat(page.getNumber()).isEqualTo(numPagina);
    Assertions.assertThat(page.getSize()).isEqualTo(numElementos);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(numeroComentario);
    for (int i = 0, j = (numPagina * 10) + 1; i < 10; i++, j++) {
      final Comentario comentario = page.getContent().get(i);
      Assertions.assertThat(comentario.getTexto()).isEqualTo("Comentario" + String.format("%03d", j));
    }
  }

  @Test
  public void findByEvaluacionEvaluadorNullId() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera listar sus comentario
      comentarioService.findByEvaluacionIdEvaluador(evaluacionId, Pageable.unpaged());
      Assertions.fail("El id no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la evaluación no puede ser nulo para listar sus comentarios");
    }
  }

  @Test
  public void createComentarioGestorEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera añadir comentario
      comentarioService.createComentarioGestor(evaluacionId, new Comentario());
      Assertions.fail("Evaluación id no puede ser null para crear un nuevo comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación id no puede ser null para crear un nuevo comentario");
    }
  }

  @Test
  public void createComentarioGestorEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    try {
      // when: se quiera añadir comentarios
      comentarioService.createComentarioGestor(evaluacionId, new Comentario());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluacion " + evaluacionId + " does not exist.");
    }
  }

  @Test
  public void createComentarioGestorComentariosNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    // when: se quiere insertar un comentario cuya evaluacion no coincide con el
    // id indicado
    try {
      comentarioService.createComentarioGestor(evaluacionId, comentario);
      Assertions.fail("La evaluación no debe estar rellena para crear un nuevo comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La evaluación no debe estar rellena para crear un nuevo comentario");
    }
  }

  @Test
  public void createComentarioEvaluadorValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    final Comentario comentario = generarMockComentario(null, "texto", 2L);
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 2L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioEvaluador(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
    Assertions.assertThat(comentarioCreado.getId()).isNotNull();

  }

  @Test
  public void createComentarioEvaluadorEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera añadir comentario
      comentarioService.createComentarioEvaluador(evaluacionId, new Comentario());
      Assertions.fail("Evaluación id no puede ser null para crear un nuevo comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación id no puede ser null para crear un nuevo comentario");
    }
  }

  @Test
  public void createComentarioEvaluadorEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    try {
      // when: se quiere añadir comentario
      comentarioService.createComentarioEvaluador(evaluacionId, new Comentario());
      Assertions.fail("El id de la evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluacion " + evaluacionId + " does not exist.");
    }
  }

  @Test
  public void createComentariosEvaluadorComentariosNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(null, "texto", 2L);
    // when: se quiere insertar un comentario cuya evaluacion no coincide con el
    // id indicado
    try {
      comentarioService.createComentarioEvaluador(evaluacionId, comentario);
      Assertions.fail("La evaluación no debe estar rellena para crear un nuevo comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La evaluación no debe estar rellena para crear un nuevo comentario");
    }
  }

  @Test
  public void createComentarioGestor_Success() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
    Assertions.assertThat(comentarioCreado.getId()).isNotNull();

  }

  @Test
  public void updateComentarioGestor_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera añadir comentarios
      comentarioService.updateComentarioGestor(evaluacionId, generarMockComentario(1L, "comentario1", 1L));
      Assertions.fail("Evaluación id no puede ser null  para actualizar un comentario.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Evaluación id no puede ser null  para actualizar un comentario.");
    }
  }

  @Test
  public void updateComentarioGestor_ThrowNotFoundException() {
    // given: EL comentario a actualizar no existe
    final Long evaluacionId = 12L;
    Assertions
        .assertThatThrownBy(
            () -> comentarioService.updateComentarioGestor(evaluacionId, generarMockComentario(1L, "comentario1", 1L)))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void updateComentarioGestor_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    try {

      Comentario comentario = generarMockComentario(1L, "comentario1", 1L);
      Comentario comentarioActualizado = generarMockComentario(1L, "comentario1 actualizado", 1L);

      BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.of(comentario));
      // when: se quiera actualizar un comentario cuya evaluación no es la misma que
      // la recibida
      comentarioService.updateComentarioGestor(evaluacionId, comentarioActualizado);

      Assertions.fail("El comentario no pertenece a la evaluación recibida.");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("El comentario no pertenece a la evaluación recibida.");
    }
  }

  @Test
  public void updateComentarioGestor_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    final Comentario comentario = generarMockComentario(comentarioId, "texto", 2L);

    // when: se quiera actualizar un comentario cuya evaluacion no
    // coincide con el id indicado
    try {
      comentarioService.updateComentarioGestor(evaluacionId, comentario);
      Assertions.fail("No se puede actualizar un tipo de comentario que no sea del tipo Gestor.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("No se puede actualizar un tipo de comentario que no sea del tipo Gestor.");
    }
  }

  @Test
  public void updateComentarioGestor_Success() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(12L, "texto", 1L);
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);

    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se quiera actualizar una lista de comentarios
    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioEvaluador_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera añadir comentarios
      comentarioService.updateComentarioEvaluador(evaluacionId, generarMockComentario(1L, "comentario1", 2L));
      Assertions.fail("Evaluación id no puede ser null  para actualizar un comentario.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Evaluación id no puede ser null  para actualizar un comentario.");
    }
  }

  @Test
  public void updateComentarioEvaluador_ThrowNotFoundException() {
    // given: EL comentario a actualizar no existe
    final Long evaluacionId = 12L;
    Assertions.assertThatThrownBy(
        () -> comentarioService.updateComentarioEvaluador(evaluacionId, generarMockComentario(1L, "comentario1", 2L)))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void updateComentarioEvaluador_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    try {

      Comentario comentario = generarMockComentario(1L, "comentario1", 2L);
      Comentario comentarioActualizado = generarMockComentario(1L, "comentario1 actualizado", 2L);

      BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.of(comentario));
      // when: se quiera actualizar un comentario cuya evaluación no es la misma que
      // la recibida
      comentarioService.updateComentarioEvaluador(evaluacionId, comentarioActualizado);

      Assertions.fail("El comentario no pertenece a la evaluación recibida.");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("El comentario no pertenece a la evaluación recibida.");
    }
  }

  @Test
  public void updateComentarioEvaluador_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    final Comentario comentario = generarMockComentario(comentarioId, "texto", 1L);

    // when: se quiera actualizar un comentario cuya evaluacion no
    // coincide con el id indicado
    try {
      comentarioService.updateComentarioEvaluador(evaluacionId, comentario);
      Assertions.fail("No se puede actualizar un tipo de comentario que no sea del tipo Evaluador.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("No se puede actualizar un tipo de comentario que no sea del tipo Evaluador.");
    }
  }

  @Test
  public void updateComentarioEvaluador_Success() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(12L, "texto", 2L);
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);

    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se quiera actualizar una lista de comentarios
    final Comentario resultados = comentarioService.updateComentarioEvaluador(evaluacionId, comentario);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
  }

  @Test
  public void deleteComentarioGestor_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final Long comentarioId = 1L;
    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Evaluación id no puede ser null para eliminar un comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación id no puede ser null para eliminar un comentario");
    }
  }

  @Test
  public void deleteComentarioGestorEvaluacion_ComentarioIdNull() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioGestor(evaluacionId, null);
      Assertions.fail("Comentario id no puede ser null para eliminar un comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario id no puede ser null para eliminar un comentario");
    }
  }

  @Test
  public void deleteComentarioGestorComentarios_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("El comentario no pertenece a la evaluación recibida");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("El comentario no pertenece a la evaluación recibida");
    }
  }

  @Test
  public void deleteComentarioGestorComentarios_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("El comentario no pertenece a la evaluación recibida");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("El comentario no pertenece a la evaluación recibida");
    }
  }

  @Test
  public void deleteComentarioGestorComentarios_throwNotFoundException() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    // when: se quiera eliminar un comentario que no existe

    Assertions.assertThatThrownBy(() -> comentarioService.deleteComentarioGestor(evaluacionId, comentarioId))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void deleteComentarioEvaluador_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final Long comentarioId = 1L;
    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId);
      Assertions.fail("Evaluación id no puede ser null para eliminar un comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación id no puede ser null para eliminar un comentario");
    }
  }

  @Test
  public void deleteComentarioEvaluadorEvaluacion_ComentarioIdNull() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioEvaluador(evaluacionId, null);
      Assertions.fail("Comentario id no puede ser null para eliminar un comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario id no puede ser null para eliminar un comentario");
    }
  }

  @Test
  public void deleteComentarioEvaluadorComentarios_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId);
      Assertions.fail("El comentario no pertenece a la evaluación recibida");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("El comentario no pertenece a la evaluación recibida");
    }
  }

  @Test
  public void deleteComentarioEvaluadorComentarios_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 200L;
    final Long comentarioId = 1L;

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId);
      Assertions.fail("No se puede eliminar el comentario debido a su tipo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("No se puede eliminar el comentario debido a su tipo");
    }
  }

  @Test
  public void deleteComentarioEvaluadprComentarios_throwNotFoundException() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    // when: se quiera eliminar un comentario que no existe

    Assertions.assertThatThrownBy(() -> comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  /**
   * Función que devuelve un objeto Comentario
   * 
   * @param id    id de la comentario
   * @param texto texto del comentario
   * @return el objeto Comentario
   */
  public Comentario generarMockComentario(final Long id, final String texto, Long tipoComentarioId) {
    final ApartadoFormulario apartadoFormulario = new ApartadoFormulario();
    apartadoFormulario.setId(100L);

    final Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(200L);

    final TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(tipoComentarioId);

    final Comentario comentario = new Comentario();
    comentario.setId(id);
    comentario.setApartadoFormulario(apartadoFormulario);
    comentario.setEvaluacion(evaluacion);
    comentario.setTipoComentario(tipoComentario);
    comentario.setTexto(texto);

    return comentario;
  }

  private Evaluacion generarMockEvaluacion(final Long id) {
    final Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    return evaluacion;
  }

}
