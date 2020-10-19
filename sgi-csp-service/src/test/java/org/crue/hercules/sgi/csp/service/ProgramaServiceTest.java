package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.PlanNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.repository.PlanRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.service.impl.ProgramaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProgramaServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ProgramaServiceTest {

  @Mock
  private PlanRepository planRepository;

  @Mock
  private ProgramaRepository repository;

  private ProgramaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProgramaServiceImpl(repository, planRepository);
  }

  @Test
  public void create_ReturnsPrograma() {
    // given: Un nuevo Programa
    Programa programa = generarMockPrograma(null, "nombre-1", 1L, null);

    BDDMockito.given(repository.findByNombreAndPlanId(programa.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.empty());

    BDDMockito.given(planRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(generarMockPlan(1L)));

    BDDMockito.given(repository.save(programa)).will((InvocationOnMock invocation) -> {
      Programa programaCreado = invocation.getArgument(0);
      programaCreado.setId(1L);
      return programaCreado;
    });

    // when: Creamos el Programa
    Programa programaCreado = service.create(programa);

    // then: El Programa se crea correctamente
    Assertions.assertThat(programaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(programaCreado.getNombre()).as("getNombre").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaCreado.getDescripcion()).as("getDescripcion").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaCreado.getActivo()).as("getActivo").isEqualTo(programa.getActivo());
  }

  @Test
  public void create_WithPadre_ReturnsPrograma() {
    // given: Un nuevo Programa
    Programa programa = generarMockPrograma(null, "nombre-1", 1L, 1L);

    BDDMockito.given(repository.findByNombreAndPlanId(programa.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.empty());

    BDDMockito.given(planRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(generarMockPlan(1L)));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(generarMockPrograma(1L)));

    BDDMockito.given(repository.save(programa)).will((InvocationOnMock invocation) -> {
      Programa programaCreado = invocation.getArgument(0);
      programaCreado.setId(1L);
      return programaCreado;
    });

    // when: Creamos el Programa
    Programa programaCreado = service.create(programa);

    // then: El Programa se crea correctamente
    Assertions.assertThat(programaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(programaCreado.getNombre()).as("getNombre").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaCreado.getDescripcion()).as("getDescripcion").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaCreado.getActivo()).as("getActivo").isEqualTo(programa.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Programa que ya tiene id
    Programa programa = generarMockPrograma(1L);

    // when: Creamos el Programa
    // then: Lanza una excepcion porque el Programa ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(programa)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Programa id tiene que ser null para crear un nuevo Programa");
  }

  @Test
  public void create_WithoutPlanId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Programa
    Programa programa = generarMockPrograma(null, "nombreRepetido", 1L, null);
    programa.getPlan().setId(null);

    // when: Creamos el Programa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(programa)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Plan no puede ser null para crear un Programa");
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo Programa con un nombre que ya existe
    Programa programaNew = generarMockPrograma(null, "nombreRepetido", 1L, null);
    Programa programa = generarMockPrograma(1L, "nombreRepetido", 1L, null);

    BDDMockito.given(repository.findByNombreAndPlanId(programaNew.getNombre(), programaNew.getPlan().getId()))
        .willReturn(Optional.of(programa));

    // when: Creamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(programaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un Programa con el nombre " + programaNew.getNombre() + " en el Plan");
  }

  @Test
  public void create_WithNoExistingPlan_ThrowsPlanNotFoundException() {
    // given: Un nuevo Programa con un plan que no existe
    Programa programa = generarMockPrograma(null, "nombreRepetido", 1L, null);

    BDDMockito.given(repository.findByNombreAndPlanId(programa.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.empty());

    BDDMockito.given(planRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el Programa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(programa)).isInstanceOf(PlanNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingPadre_ThrowsProgramaNotFoundException() {
    // given: Un nuevo Programa con un padre que no existe
    Programa programa = generarMockPrograma(null, "nombreRepetido", 1L, 1L);

    BDDMockito.given(repository.findByNombreAndPlanId(programa.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.empty());

    BDDMockito.given(planRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(generarMockPlan(1L)));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el Programa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(programa)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void update_ReturnsPrograma() {
    // given: Un nuevo Programa con el nombre actualizado
    Programa programa = generarMockPrograma(1L);
    Programa programaNombreActualizado = generarMockPrograma(1L, "NombreActualizado", 1L, null);

    BDDMockito.given(repository.findByNombreAndPlanId(programaNombreActualizado.getNombre(),
        programaNombreActualizado.getPlan().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(programa));
    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Programa
    Programa programaActualizado = service.update(programaNombreActualizado);

    // then: El Programa se actualiza correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()")
        .isEqualTo(programaNombreActualizado.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(programa.getActivo());
  }

  @Test
  public void update_WithPadre_ReturnsPrograma() {
    // given: Un nuevo Programa con el nombre actualizado
    Programa programa = generarMockPrograma(2L);
    Programa programaNombreActualizado = generarMockPrograma(2L, "NombreActualizado", 1L, 1L);

    BDDMockito
        .given(repository.findByNombreAndPlanId(programaNombreActualizado.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(generarMockPrograma(1L)));
    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(programa));

    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Programa
    Programa programaActualizado = service.update(programaNombreActualizado);

    // then: El Programa se actualiza correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()")
        .isEqualTo(programaNombreActualizado.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(programa.getActivo());
  }

  @Test
  public void update_ActivoToFalse_ReturnsPrograma() {
    // given: Un nuevo Programa con activo actualizado a false
    Programa programa = generarMockPrograma(2L);
    Programa programaHijo = generarMockPrograma(3L, "hijo", 1L, 2L);
    Programa programaNieto = generarMockPrograma(4L, "nieto", 1L, 3L);
    Programa programaActivoActualizado = generarMockPrograma(2L, "NombreActualizado", 1L, 1L);
    programaActivoActualizado.setActivo(false);

    BDDMockito
        .given(repository.findByNombreAndPlanId(programaActivoActualizado.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(generarMockPrograma(1L)));
    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(programa));

    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(2L))).willReturn(Arrays.asList(programaHijo));
    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(3L))).willReturn(Arrays.asList(programaNieto));
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<Programa>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Programa
    Programa programaActualizado = service.update(programaActivoActualizado);

    // then: El Programa se actualiza correctamente y sus hijos se desactivan.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()")
        .isEqualTo(programaActivoActualizado.getActivo());

    Mockito.verify(repository, Mockito.times(2)).saveAll(ArgumentMatchers.<Programa>anyList());
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un Programa actualizado con un nombre que ya existe
    Programa programaActualizado = generarMockPrograma(1L, "nombreRepetido", 1L, null);
    Programa programa = generarMockPrograma(2L, "nombreRepetido", 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(programaActualizado));

    BDDMockito.given(repository.findByNombreAndPlanId(programaActualizado.getNombre(), programa.getPlan().getId()))
        .willReturn(Optional.of(programa));

    // when: Actualizamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(programaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un Programa con el nombre " + programaActualizado.getNombre() + " en el Plan");
  }

  @Test
  public void update_WithNoExistingPadre_ThrowsProgramaNotFoundException() {
    // given: Un Programa actualizado con un padre que no existe
    Programa programa = generarMockPrograma(2L, "nombreRepetido", 1L, 1L);

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.empty());

    // when: Actualizamos el Programa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(programa)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProgramaNotFoundException() {
    // given: Un Programa actualizado con un id que no existe
    Programa programa = generarMockPrograma(1L, "Programa", 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el Programa
    // then: Lanza una excepcion porque el Programa no existe
    Assertions.assertThatThrownBy(() -> service.update(programa)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void disable_ReturnsPrograma() {
    // given: Un nuevo Programa activo
    Programa programa = generarMockPrograma(1L);
    Programa programaHijo = generarMockPrograma(2L, "hijo", 1L, 1L);
    Programa programaNieto = generarMockPrograma(3L, "nieto", 1L, 2L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(programa));
    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(1L))).willReturn(Arrays.asList(programaHijo));
    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(2L))).willReturn(Arrays.asList(programaNieto));
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<Programa>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Programa y todos sus hijos en cascada
    Programa programaActualizado = service.disable(programa.getId());

    // then: El Programa y todos sus hijos se desactivan correctamente
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(false);

    Mockito.verify(repository, Mockito.times(2)).saveAll(ArgumentMatchers.<Programa>anyList());

  }

  @Test
  public void disable_WithIdNotExist_ThrowsProgramaNotFoundException() {
    // given: Un id de un Programa que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Programa
    // then: Lanza una excepcion porque el Programa no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void findById_ReturnsPrograma() {
    // given: Un Programa con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockPrograma(idBuscado)));

    // when: Buscamos el Programa por su id
    Programa programa = service.findById(idBuscado);

    // then: el Programa
    Assertions.assertThat(programa).as("isNotNull()").isNotNull();
    Assertions.assertThat(programa.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(programa.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(programa.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProgramaNotFoundException() throws Exception {
    // given: Ningun Programa con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Programa por su id
    // then: lanza un ProgramaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void findAllByPlan_ReturnsPage() {
    // given: Una lista con 37 Programa para el Plan
    Long idPlan = 1L;
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Programa>>() {
          @Override
          public Page<Programa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > programas.size() ? programas.size() : toIndex;
            List<Programa> content = programas.subList(fromIndex, toIndex);
            Page<Programa> page = new PageImpl<>(content, pageable, programas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Programa> page = service.findAllByPlan(idPlan, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Programa programa = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllTodosByPlan_ReturnsPage() {
    // given: Una lista con 37 Programa para el Plan
    Long idPlan = 1L;
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Programa>>() {
          @Override
          public Page<Programa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > programas.size() ? programas.size() : toIndex;
            List<Programa> content = programas.subList(fromIndex, toIndex);
            Page<Programa> page = new PageImpl<>(content, pageable, programas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Programa> page = service.findAllTodosByPlan(idPlan, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Programa programa = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("nombre-" + i);
    }
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

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id id del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id) {
    return generarMockPrograma(id, "nombre-" + id, id, null);
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id              id del Programa
   * @param nombre          nombre del Programa
   * @param idPlan          id del plan
   * @param idProgramaPadre id del Programa padre
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre, Long idPlan, Long idProgramaPadre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + id);
    programa.setPlan(generarMockPlan(idPlan));
    if (idProgramaPadre != null) {
      programa.setPadre(generarMockPrograma(idProgramaPadre));
    }
    programa.setActivo(true);

    return programa;
  }

}
