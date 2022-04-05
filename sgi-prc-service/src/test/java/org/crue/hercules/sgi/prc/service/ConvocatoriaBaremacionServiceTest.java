package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
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
 * ConvocatoriaBaremacionServiceTest
 */
@Import({ ConvocatoriaBaremacionService.class, ApplicationContextSupport.class })
public class ConvocatoriaBaremacionServiceTest extends BaseServiceTest {

  @MockBean
  private ConvocatoriaBaremacionRepository repository;

  @MockBean
  private ModuladorRepository moduladorRepository;

  @MockBean
  private RangoRepository rangoRepository;

  @MockBean
  private BaremoRepository baremoRepository;

  @MockBean
  private ConfiguracionRepository configuracionRepository;

  @MockBean
  private PuntuacionGrupoRepository puntuacionGrupoRepository;

  @MockBean
  private PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;

  @MockBean
  private ProduccionCientificaRepository produccionCientificaRepository;

  @MockBean
  private ProduccionCientificaBuilderService produccionCientificaBuilderService;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ConvocatoriaBaremacionService service;

  private static final String NOMBRE_PREFIX = "Convocatoria baremación ";

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaBaremacion
    List<ConvocatoriaBaremacion> convocatoriasBaremacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasBaremacion
          .add(generarMockConvocatoriaBaremacion(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaBaremacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaBaremacion>>() {
          @Override
          public Page<ConvocatoriaBaremacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasBaremacion.size() ? convocatoriasBaremacion.size() : toIndex;
            List<ConvocatoriaBaremacion> content = convocatoriasBaremacion.subList(fromIndex, toIndex);
            Page<ConvocatoriaBaremacion> page = new PageImpl<>(content, pageable, convocatoriasBaremacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaBaremacion> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaBaremacion del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaBaremacion convocatoriaBaremacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaBaremacion.getNombre())
          .isEqualTo(NOMBRE_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsConvocatoriaBaremacion() {
    // given: Un ConvocatoriaBaremacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockConvocatoriaBaremacion(idBuscado)));

    // when: Buscamos el ConvocatoriaBaremacion por su id
    ConvocatoriaBaremacion convocatoriaBaremacion = service.findById(idBuscado);

    // then: el ConvocatoriaBaremacion
    Assertions.assertThat(convocatoriaBaremacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaBaremacion.getNombre()).as("getNombre()")
        .isEqualTo(NOMBRE_PREFIX + String.format("%03d", idBuscado));
  }

  @Test
  void activar_WithIdIsNull_ThrowsIllegalArgumentException() {
    // given: Un id buscado null
    Long idBuscado = null;
    // when: Activamos el ConvocatoriaBaremacion por su id
    // then: Throws IllegalArgumentException
    Assertions.assertThatThrownBy(() -> this.service.activar(idBuscado))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void activar_WithNotExistingId_ThrowsConvocatoriaBaremacionNotFoundException() {
    // given: Un ConvocatoriaBaremacion que no existe con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());
    // when: ConvocatoriaBaremacion con ese id no existe
    // then: Throws ConvocatoriaBaremacionNotFoundException
    Assertions.assertThatThrownBy(() -> this.service.activar(idBuscado))
        .isInstanceOf(ConvocatoriaBaremacionNotFoundException.class);
  }

  @Test
  void activar_WithExistingIdAlreadyActivated_ReturnsConvocatoriaBaremacionActivated() {
    // given: Un ConvocatoriaBaremacion ya activado con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockConvocatoriaBaremacion(idBuscado, Boolean.TRUE)));
    // when: Activamos el ConvocatoriaBaremacion encontrado por su id
    ConvocatoriaBaremacion convocatoriaBaremacion = this.service.activar(idBuscado);
    // then: el ConvocatoriaBaremacion
    Assertions.assertThat(convocatoriaBaremacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaBaremacion.getNombre()).as("getNombre()")
        .isEqualTo(NOMBRE_PREFIX + String.format("%03d", idBuscado));
    Assertions.assertThat(convocatoriaBaremacion.getActivo()).as("getActivo()")
        .isEqualTo(Boolean.TRUE);
  }

  @Test
  void activar_WithExistingIdNotActivated_ReturnsConvocatoriaBaremacionActivated() {
    // given: Un ConvocatoriaBaremacion no activado con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockConvocatoriaBaremacion(idBuscado, Boolean.FALSE)));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaBaremacion>any()))
        .willReturn(generarMockConvocatoriaBaremacion(idBuscado, Boolean.TRUE));
    // when: Activamos el ConvocatoriaBaremacion encontrado por su id
    ConvocatoriaBaremacion convocatoriaBaremacion = this.service.activar(idBuscado);
    // then: el ConvocatoriaBaremacion
    Assertions.assertThat(convocatoriaBaremacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaBaremacion.getNombre()).as("getNombre()")
        .isEqualTo(NOMBRE_PREFIX + String.format("%03d", idBuscado));
    Assertions.assertThat(convocatoriaBaremacion.getActivo()).as("getActivo()")
        .isEqualTo(Boolean.TRUE);
  }

  @Test
  void desactivar_WithIdIsNull_ThrowsIllegalArgumentException() {
    // given: Un id buscado null
    Long idBuscado = null;
    // when: Activamos el ConvocatoriaBaremacion por su id
    // then: Throws IllegalArgumentException
    Assertions.assertThatThrownBy(() -> this.service.desactivar(idBuscado))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void desactivar_WithFechaFinEjecucionNotNull_ThrowsIllegalArgumentException() {
    // given: Un id buscado
    Long idBuscado = 1L;
    // when: ConvocatoriaBaremacion con ese id tiene fechaFinEjecucion not
    // null
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockConvocatoriaBaremacion(idBuscado, Instant.now())));
    // then: Throws IllegalArgumentException
    Assertions.assertThatThrownBy(() -> this.service.desactivar(idBuscado))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void desactivar_WithNotExistingId_ThrowsConvocatoriaBaremacionNotFoundException() {
    // given: Un id buscado
    Long idBuscado = 1L;
    // when: ConvocatoriaBaremacion con ese id no existe
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());
    // then: Throws ConvocatoriaBaremacionNotFoundException
    Assertions.assertThatThrownBy(() -> this.service.desactivar(idBuscado))
        .isInstanceOf(ConvocatoriaBaremacionNotFoundException.class);
  }

  @Test
  void desactivar_WithExistingIdAlreadyDeactivated_ReturnsConvocatoriaBaremacionDeactivated() {
    // given: Un ConvocatoriaBaremacion ya desactivado con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockConvocatoriaBaremacion(idBuscado, Boolean.FALSE)));
    // when: Desactivamos el ConvocatoriaBaremacion encontrado por su id
    ConvocatoriaBaremacion convocatoriaBaremacion = this.service.desactivar(idBuscado);
    // then: el ConvocatoriaBaremacion
    Assertions.assertThat(convocatoriaBaremacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaBaremacion.getNombre()).as("getNombre()")
        .isEqualTo(NOMBRE_PREFIX + String.format("%03d", idBuscado));
    Assertions.assertThat(convocatoriaBaremacion.getActivo()).as("getActivo()")
        .isEqualTo(Boolean.FALSE);
  }

  @Test
  void desactivar_WithExistingIdActivated_ReturnsConvocatoriaBaremacionDeactivated() {
    // given: Un ConvocatoriaBaremacion activado con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockConvocatoriaBaremacion(idBuscado, Boolean.TRUE)));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaBaremacion>any()))
        .willReturn(generarMockConvocatoriaBaremacion(idBuscado, Boolean.FALSE));
    // when: Activamos el ConvocatoriaBaremacion encontrado por su id
    ConvocatoriaBaremacion convocatoriaBaremacion = this.service.desactivar(idBuscado);
    // then: el ConvocatoriaBaremacion
    Assertions.assertThat(convocatoriaBaremacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaBaremacion.getNombre()).as("getNombre()")
        .isEqualTo(NOMBRE_PREFIX + String.format("%03d", idBuscado));
    Assertions.assertThat(convocatoriaBaremacion.getActivo()).as("getActivo()")
        .isEqualTo(Boolean.FALSE);
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id, String idRef, Boolean activo,
      Instant fechaInicioEjecucion) {
    ConvocatoriaBaremacion convocatoriaBaremacion = new ConvocatoriaBaremacion();
    convocatoriaBaremacion.setId(id);
    convocatoriaBaremacion.setNombre(NOMBRE_PREFIX + idRef);
    convocatoriaBaremacion.setAnio(2022);
    convocatoriaBaremacion.setUltimoAnio(2023);
    convocatoriaBaremacion.setImporteTotal(new BigDecimal(50000));
    convocatoriaBaremacion.setFechaInicioEjecucion(fechaInicioEjecucion);
    convocatoriaBaremacion.setActivo(activo);

    return convocatoriaBaremacion;
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id) {
    return generarMockConvocatoriaBaremacion(id, String.format("%03d", id), Boolean.TRUE, null);
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id, Boolean activo) {
    return generarMockConvocatoriaBaremacion(id, String.format("%03d", id), activo, null);
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id, Instant fechaInicioEjecucion) {
    return generarMockConvocatoriaBaremacion(id, String.format("%03d", id), Boolean.TRUE, fechaInicioEjecucion);
  }
}
