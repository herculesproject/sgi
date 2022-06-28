package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
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

/**
 * ProyectoPeriodoJustificacionServiceTest
 */
@Import({ ProyectoPeriodoJustificacionService.class, ApplicationContextSupport.class })
public class ProyectoPeriodoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private ProyectoPeriodoJustificacionRepository repository;
  @MockBean
  private ProyectoRepository proyectoRepository;
  @MockBean
  private EntityManager entityManager;
  @MockBean
  private EntityManagerFactory entityManagerFactory;
  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ProyectoPeriodoJustificacionService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void findAllByProyectoSgeRef_ReturnsPage() {
    // given: Una lista con 37 ProyectoPeriodoJustificacion
    String proyectosSgeRef = "1";
    List<ProyectoPeriodoJustificacion> periodosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      periodosJustificacion.add(generarMockProyectoPeriodoJustificacion(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPeriodoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoJustificacion>>() {
          @Override
          public Page<ProyectoPeriodoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > periodosJustificacion.size() ? periodosJustificacion.size() : toIndex;
            List<ProyectoPeriodoJustificacion> content = periodosJustificacion.subList(fromIndex, toIndex);
            Page<ProyectoPeriodoJustificacion> page = new PageImpl<>(content, pageable, periodosJustificacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoPeriodoJustificacion> page = service.findAllByProyectoSgeRef(proyectosSgeRef, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().hasSize()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoJustificacion proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  private ProyectoPeriodoJustificacion generarMockProyectoPeriodoJustificacion(Long id) {
    final String observacionesSuffix = id != null ? String.format("%03d", id) : "001";
    return ProyectoPeriodoJustificacion.builder()
        .id(id)
        .observaciones("observaciones-" + observacionesSuffix)
        .build();
  }
}
