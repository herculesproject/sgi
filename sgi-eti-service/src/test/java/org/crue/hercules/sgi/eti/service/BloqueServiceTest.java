package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.BloqueNotFoundException;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.service.impl.BloqueServiceImpl;
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
 * BloqueServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class BloqueServiceTest extends BaseServiceTest {

  @Mock
  private BloqueRepository bloqueRepository;

  private BloqueService bloqueService;

  @BeforeEach
  public void setUp() throws Exception {
    bloqueService = new BloqueServiceImpl(bloqueRepository);
  }

  @Test
  public void find_WithId_ReturnsBloque() {
    BDDMockito.given(bloqueRepository.findById(1L)).willReturn(Optional.of(generarMockBloque(1L, "Bloque1")));

    Bloque Bloque = bloqueService.findById(1L);

    Assertions.assertThat(Bloque.getId()).isEqualTo(1L);

    Assertions.assertThat(Bloque.getNombre()).isEqualTo("Bloque1");

  }

  @Test
  public void find_NotFound_ThrowsBloqueNotFoundException() throws Exception {
    BDDMockito.given(bloqueRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> bloqueService.findById(1L)).isInstanceOf(BloqueNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullBloqueList() {
    // given: One hundred Bloque
    List<Bloque> bloques = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloques.add(generarMockBloque(Long.valueOf(i), "Bloque" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            bloqueRepository.findAll(ArgumentMatchers.<Specification<Bloque>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(bloques));

    // when: find unlimited
    Page<Bloque> page = bloqueService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Bloques
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Bloques
    List<Bloque> bloques = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloques.add(generarMockBloque(Long.valueOf(i), "Bloque" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            bloqueRepository.findAll(ArgumentMatchers.<Specification<Bloque>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Bloque>>() {
          @Override
          public Page<Bloque> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Bloque> content = bloques.subList(fromIndex, toIndex);
            Page<Bloque> page = new PageImpl<>(content, pageable, bloques.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Bloque> page = bloqueService.findAll(null, paging);

    // then: A Page with ten Bloques are returned containing
    // nombre='Bloque031' to 'Bloque040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Bloque Bloque = page.getContent().get(i);
      Assertions.assertThat(Bloque.getNombre()).isEqualTo("Bloque" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Bloque
   * 
   * @param id     id del Bloque
   * @param nombre el nombre de Bloque
   * @return el objeto Bloque
   */

  private Bloque generarMockBloque(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("Formulario1");
    formulario.setDescripcion("Descripcion formulario 1");

    Bloque Bloque = new Bloque();
    Bloque.setId(id);
    Bloque.setFormulario(formulario);
    Bloque.setNombre(nombre);
    Bloque.setOrden(1);

    return Bloque;
  }
}