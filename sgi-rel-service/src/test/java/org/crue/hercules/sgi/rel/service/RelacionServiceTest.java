package org.crue.hercules.sgi.rel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rel.exceptions.RelacionNotFoundException;
import org.crue.hercules.sgi.rel.model.Relacion;
import org.crue.hercules.sgi.rel.model.Relacion.TipoEntidad;
import org.crue.hercules.sgi.rel.repository.RelacionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Import({ RelacionService.class, ApplicationContextSupport.class })
class RelacionServiceTest extends BaseServiceTest {

  @MockBean
  private RelacionRepository relacionRepository;

  @Autowired
  private RelacionService relacionService;

  @Test
  void findById_WithId_ReturnsRelacion() {
    // given: una Relacion existente
    BDDMockito.given(relacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockRelacion(1L)));

    // when: buscamos por id
    Relacion relacion = relacionService.findById(1L);

    // then: se devuelve la Relacion
    Assertions.assertThat(relacion).isNotNull();
    Assertions.assertThat(relacion.getId()).isEqualTo(1L);
    Assertions.assertThat(relacion.getTipoEntidadOrigen()).isEqualTo(TipoEntidad.PROYECTO);
    Assertions.assertThat(relacion.getTipoEntidadDestino()).isEqualTo(TipoEntidad.GRUPO);
  }

  @Test
  void findById_NotFound_ThrowsRelacionNotFoundException() {
    // given: una Relacion que no existe
    BDDMockito.given(relacionRepository.findById(1L)).willReturn(Optional.empty());

    // when: buscamos por id
    // then: lanza RelacionNotFoundException
    Assertions.assertThatThrownBy(() -> relacionService.findById(1L))
        .isInstanceOf(RelacionNotFoundException.class);
  }

  @Test
  void create_ReturnsRelacion() {
    // given: una nueva Relacion sin id
    Relacion relacion = generarMockRelacion(null);

    BDDMockito.given(relacionRepository.save(relacion)).will((InvocationOnMock invocation) -> {
      Relacion creada = invocation.getArgument(0);
      creada.setId(1L);
      return creada;
    });

    // when: creamos la Relacion
    Relacion creada = relacionService.create(relacion);

    // then: la Relacion se crea correctamente
    Assertions.assertThat(creada).isNotNull();
    Assertions.assertThat(creada.getId()).isEqualTo(1L);
    Assertions.assertThat(creada.getTipoEntidadOrigen()).isEqualTo(relacion.getTipoEntidadOrigen());
    Assertions.assertThat(creada.getTipoEntidadDestino()).isEqualTo(relacion.getTipoEntidadDestino());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: una Relacion con id no nulo
    Relacion relacion = generarMockRelacion(1L);

    // when: la creamos
    // then: lanza IllegalArgumentException
    Assertions.assertThatThrownBy(() -> relacionService.create(relacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsRelacion() {
    // given: una Relacion existente con cambios
    Relacion relacion = generarMockRelacion(1L);
    Relacion actualizada = generarMockRelacion(1L);
    actualizada.setEntidadDestinoRef("ref-destino-actualizada");

    BDDMockito.given(relacionRepository.findById(1L)).willReturn(Optional.of(relacion));
    BDDMockito.given(relacionRepository.save(ArgumentMatchers.<Relacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: actualizamos la Relacion
    Relacion result = relacionService.update(actualizada);

    // then: se devuelven los datos actualizados
    Assertions.assertThat(result.getId()).isEqualTo(1L);
    Assertions.assertThat(result.getEntidadDestinoRef()).isEqualTo("ref-destino-actualizada");
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: una Relacion sin id
    Relacion relacion = generarMockRelacion(null);

    // when: la actualizamos
    // then: lanza IllegalArgumentException
    Assertions.assertThatThrownBy(() -> relacionService.update(relacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_NotFound_ThrowsRelacionNotFoundException() {
    // given: una Relacion cuyo id no existe
    Relacion relacion = generarMockRelacion(1L);
    BDDMockito.given(relacionRepository.findById(1L)).willReturn(Optional.empty());

    // when: la actualizamos
    // then: lanza RelacionNotFoundException
    Assertions.assertThatThrownBy(() -> relacionService.update(relacion))
        .isInstanceOf(RelacionNotFoundException.class);
  }

  @Test
  void delete_RemovesRelacion() {
    // given: una Relacion existente
    BDDMockito.given(relacionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    // when: la borramos
    relacionService.delete(1L);

    // then: el repositorio recibe la orden de borrado
    BDDMockito.then(relacionRepository).should().deleteById(1L);
  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: un id nulo
    // when: borramos
    // then: lanza IllegalArgumentException
    Assertions.assertThatThrownBy(() -> relacionService.delete(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_NotFound_ThrowsRelacionNotFoundException() {
    // given: un id que no existe
    BDDMockito.given(relacionRepository.existsById(1L)).willReturn(Boolean.FALSE);

    // when: borramos
    // then: lanza RelacionNotFoundException
    Assertions.assertThatThrownBy(() -> relacionService.delete(1L))
        .isInstanceOf(RelacionNotFoundException.class);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: Cien relaciones
    List<Relacion> relaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      relaciones.add(generarMockRelacion(Long.valueOf(i)));
    }

    BDDMockito.given(
        relacionRepository.findAll(ArgumentMatchers.<Specification<Relacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Relacion>>() {
          @Override
          public Page<Relacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Relacion> content = relaciones.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, relaciones.size());
          }
        });

    // when: pedimos la página 3 con tamaño 10
    Pageable paging = PageRequest.of(3, 10);
    Page<Relacion> page = relacionService.findAll(null, paging);

    // then: se devuelve la página esperada
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  private Relacion generarMockRelacion(Long id) {
    Relacion relacion = new Relacion();
    relacion.setId(id);
    relacion.setTipoEntidadOrigen(TipoEntidad.PROYECTO);
    relacion.setTipoEntidadDestino(TipoEntidad.GRUPO);
    relacion.setEntidadOrigenRef("ref-origen-" + id);
    relacion.setEntidadDestinoRef("ref-destino-" + id);
    return relacion;
  }

}
