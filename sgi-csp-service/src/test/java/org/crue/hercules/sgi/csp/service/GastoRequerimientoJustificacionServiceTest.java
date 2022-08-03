package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.GastoRequerimientoJustificacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
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
 * GastoRequerimientoJustificacionServiceTest
 */
@Import({ GastoRequerimientoJustificacionService.class, ApplicationContextSupport.class })
public class GastoRequerimientoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private GastoRequerimientoJustificacionRepository gastoRequerimientoJustificacionRepository;

  @Autowired
  private GastoRequerimientoJustificacionService gastoRequerimientoJustificacionService;

  @Test
  void findAllByRequerimientoJustificacionId_WithPaging_ReturnsPage() {
    // given: One hundred GastoRequerimientoJustificacion
    Long requerimientoJustificacionId = 1234L;
    List<GastoRequerimientoJustificacion> gastoRequerimientoJustificacionList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      gastoRequerimientoJustificacionList
          .add(generarMockGastoRequerimientoJustificacion(i, requerimientoJustificacionId));
    }

    BDDMockito.given(
        gastoRequerimientoJustificacionRepository.findAll(
            ArgumentMatchers.<Specification<GastoRequerimientoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacion>>() {
          @Override
          public Page<GastoRequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<GastoRequerimientoJustificacion> content = gastoRequerimientoJustificacionList
                .subList(fromIndex, toIndex);
            Page<GastoRequerimientoJustificacion> page = new PageImpl<>(content, pageable,
                gastoRequerimientoJustificacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<GastoRequerimientoJustificacion> page = gastoRequerimientoJustificacionService
        .findAllByRequerimientoJustificacionId(
            requerimientoJustificacionId,
            null, paging);

    // then: A Page with ten GastoRequerimientoJustificacion are returned
    // containing incidencia='Incidencia-031' to
    // 'Incidencia-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      GastoRequerimientoJustificacion gastoRequerimientoJustificacion = page.getContent().get(i);
      Assertions.assertThat(gastoRequerimientoJustificacion.getIncidencia())
          .isEqualTo("Incidencia-" + String.format("%03d", j));
    }
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockGastoRequerimientoJustificacion(id, Boolean.TRUE, "Alegacion-" + suffix,
        "gasto-ref-" + suffix, "11/1111",
        null, null, null,
        "Incidencia-" + suffix, requerimientoJustificacionId);
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id, Boolean aceptado,
      String alegacion, String gastoRef, String identificadorJustificacion, BigDecimal importeAceptado,
      BigDecimal importeAlegado, BigDecimal importeRechazado,
      String incidencia, Long requerimientoJustificacionId) {
    return GastoRequerimientoJustificacion.builder()
        .id(id)
        .aceptado(aceptado)
        .alegacion(alegacion)
        .gastoRef(gastoRef)
        .identificadorJustificacion(identificadorJustificacion)
        .importeAceptado(importeAceptado)
        .importeAlegado(importeAlegado)
        .importeRechazado(importeRechazado)
        .incidencia(incidencia)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}
