package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.PlanNotFoundException;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.repository.PlanRepository;
import org.crue.hercules.sgi.csp.service.impl.PlanServiceImpl;
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
 * PlanServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

  @Mock
  private PlanRepository repository;

  private PlanService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new PlanServiceImpl(repository);
  }

  @Test
  public void create_ReturnsPlan() {
    // given: Un nuevo Plan
    Plan plan = generarMockPlan(null);

    BDDMockito.given(repository.findByNombre(plan.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(repository.save(plan)).will((InvocationOnMock invocation) -> {
      Plan planCreado = invocation.getArgument(0);
      planCreado.setId(1L);
      return planCreado;
    });

    // when: Creamos el Plan
    Plan planCreado = service.create(plan);

    // then: El Plan se crea correctamente
    Assertions.assertThat(planCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(planCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(planCreado.getNombre()).as("getNombre").isEqualTo(plan.getNombre());
    Assertions.assertThat(planCreado.getDescripcion()).as("getDescripcion").isEqualTo(plan.getDescripcion());
    Assertions.assertThat(planCreado.getActivo()).as("getActivo").isEqualTo(plan.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Plan que ya tiene id
    Plan plan = generarMockPlan(1L);

    // when: Creamos el Plan
    // then: Lanza una excepcion porque el Plan ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(plan)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Plan id tiene que ser null para crear un nuevo Plan");
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo Plan con un nombre que ya existe
    Plan planNew = generarMockPlan(null, "nombreRepetido");
    Plan plan = generarMockPlan(1L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(planNew.getNombre())).willReturn(Optional.of(plan));

    // when: Creamos el Plan
    // then: Lanza una excepcion porque hay otro Plan con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(planNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un Plan con el nombre " + planNew.getNombre());
  }

  @Test
  public void update_ReturnsPlan() {
    // given: Un nuevo Plan con el nombre actualizado
    Plan plan = generarMockPlan(1L);
    Plan planNombreActualizado = generarMockPlan(1L, "NombreActualizado");

    BDDMockito.given(repository.findByNombre(planNombreActualizado.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(plan));
    BDDMockito.given(repository.save(ArgumentMatchers.<Plan>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Plan
    Plan planActualizado = service.update(planNombreActualizado);

    // then: El Plan se actualiza correctamente.
    Assertions.assertThat(planActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(planActualizado.getId()).as("getId()").isEqualTo(plan.getId());
    Assertions.assertThat(planActualizado.getNombre()).as("getNombre()").isEqualTo(planNombreActualizado.getNombre());
    Assertions.assertThat(planActualizado.getDescripcion()).as("getDescripcion").isEqualTo(plan.getDescripcion());
    Assertions.assertThat(planActualizado.getActivo()).as("getActivo()").isEqualTo(plan.getActivo());
  }

  @Test
  public void update_WithIdNotExist_ThrowsPlanNotFoundException() {
    // given: Un Plan a actualizar con un id que no existe
    Plan plan = generarMockPlan(1L, "Plan");

    // when: Actualizamos el Plan
    // then: Lanza una excepcion porque el Plan no existe
    Assertions.assertThatThrownBy(() -> service.update(plan)).isInstanceOf(PlanNotFoundException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo Plan con un nombre que ya existe
    Plan planActualizado = generarMockPlan(1L, "nombreRepetido");
    Plan plan = generarMockPlan(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(planActualizado.getNombre())).willReturn(Optional.of(plan));

    // when: Creamos el Plan
    // then: Lanza una excepcion porque hay otro Plan con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(planActualizado)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un Plan con el nombre " + planActualizado.getNombre());
  }

  @Test
  public void disable_ReturnsPlan() {
    // given: Un nuevo Plan activo
    Plan plan = generarMockPlan(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(plan));
    BDDMockito.given(repository.save(ArgumentMatchers.<Plan>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Plan
    Plan planActualizado = service.disable(plan.getId());

    // then: El Plan se desactiva correctamente.
    Assertions.assertThat(planActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(planActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(planActualizado.getNombre()).as("getNombre()").isEqualTo(plan.getNombre());
    Assertions.assertThat(planActualizado.getDescripcion()).as("getDescripcion()").isEqualTo(plan.getDescripcion());
    Assertions.assertThat(planActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsPlanNotFoundException() {
    // given: Un id de un Plan que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Plan
    // then: Lanza una excepcion porque el Plan no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(PlanNotFoundException.class);
  }

  @Test
  public void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo Plan con un nombre que ya existe
    Plan planUpdated = generarMockPlan(1L, "nombreRepetido");
    Plan plan = generarMockPlan(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(planUpdated.getNombre())).willReturn(Optional.of(plan));

    // when: Actualizamos el Plan
    // then: Lanza una excepcion porque ya existe otro Plan con ese
    // nombre
    Assertions.assertThatThrownBy(() -> service.update(planUpdated)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Plan
    List<Plan> planes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      planes.add(generarMockPlan(i, "Plan" + String.format("%03d", i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<Plan>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Plan>>() {
          @Override
          public Page<Plan> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > planes.size() ? planes.size() : toIndex;
            List<Plan> content = planes.subList(fromIndex, toIndex);
            Page<Plan> page = new PageImpl<>(content, pageable, planes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Plan> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los Plan del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Plan plan = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(plan.getNombre()).isEqualTo("Plan" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Plan
    List<Plan> planes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      planes.add(generarMockPlan(i, "Plan" + String.format("%03d", i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<Plan>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Plan>>() {
          @Override
          public Page<Plan> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > planes.size() ? planes.size() : toIndex;
            List<Plan> content = planes.subList(fromIndex, toIndex);
            Page<Plan> page = new PageImpl<>(content, pageable, planes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Plan> page = service.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los Plan del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Plan plan = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(plan.getNombre()).isEqualTo("Plan" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsPlan() {
    // given: Un Plan con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockPlan(idBuscado)));

    // when: Buscamos el Plan por su id
    Plan plan = service.findById(idBuscado);

    // then: el Plan
    Assertions.assertThat(plan).as("isNotNull()").isNotNull();
    Assertions.assertThat(plan.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(plan.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(plan.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsPlanNotFoundException() throws Exception {
    // given: Ningun Plan con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Plan por su id
    // then: lanza un PlanNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(PlanNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id id del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id) {
    return generarMockPlan(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id     id del Plan
   * @param nombre nombre del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id, String nombre) {
    Plan plan = new Plan();
    plan.setId(id);
    plan.setNombre(nombre);
    plan.setDescripcion("descripcion-" + id);
    plan.setActivo(true);

    return plan;
  }

}
