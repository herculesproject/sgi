package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SocioPeriodoJustificacionDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.SocioPeriodoJustificacionDocumentoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SocioPeriodoJustificacionDocumentoServiceTest
 */
public class SocioPeriodoJustificacionDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private SocioPeriodoJustificacionDocumentoRepository repository;
  @Mock
  private ProyectoSocioPeriodoJustificacionRepository proyectoSocioRepository;

  private SocioPeriodoJustificacionDocumentoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SocioPeriodoJustificacionDocumentoServiceImpl(repository, proyectoSocioRepository);
  }

  @Test
  public void update_ReturnsSocioPeriodoJustificacionDocumentoList() {
    // given: una lista con uno de los SocioPeriodoJustificacionDocumento
    // actualizado,
    // otro nuevo y sin el otros existente
    Long proyectoSocioId = 1L;

    List<SocioPeriodoJustificacionDocumento> peridosJustificiacionExistentes = new ArrayList<>();
    peridosJustificiacionExistentes.add(generarMockSocioPeriodoJustificacionDocumento(2L));

    SocioPeriodoJustificacionDocumento updateSocioPeriodoJustificacionDocumento1 = generarMockSocioPeriodoJustificacionDocumento(
        4L);

    peridosJustificiacionExistentes.add(updateSocioPeriodoJustificacionDocumento1);

    SocioPeriodoJustificacionDocumento updateSocioPeriodoJustificacionDocumento2 = generarMockSocioPeriodoJustificacionDocumento(
        5L);

    peridosJustificiacionExistentes.add(updateSocioPeriodoJustificacionDocumento2);
    peridosJustificiacionExistentes.add(updateSocioPeriodoJustificacionDocumento2);

    SocioPeriodoJustificacionDocumento newSocioPeriodoJustificacionDocumento = generarMockSocioPeriodoJustificacionDocumento(
        null);

    SocioPeriodoJustificacionDocumento updatedSocioPeriodoJustificacionDocumento = generarMockSocioPeriodoJustificacionDocumento(
        4L);

    List<SocioPeriodoJustificacionDocumento> peridosJustificiacionActualizar = new ArrayList<>();
    peridosJustificiacionActualizar.add(newSocioPeriodoJustificacionDocumento);
    peridosJustificiacionActualizar.add(updatedSocioPeriodoJustificacionDocumento);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(updatedSocioPeriodoJustificacionDocumento.getProyectoSocioPeriodoJustificacion()));

    BDDMockito.given(repository.findAllByProyectoSocioPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(peridosJustificiacionExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<SocioPeriodoJustificacionDocumento>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SocioPeriodoJustificacionDocumento>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SocioPeriodoJustificacionDocumento> periodoJustificaciones = invocation.getArgument(0);
          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(6L);
            }
            periodoJustificacion.getProyectoSocioPeriodoJustificacion().setId(proyectoSocioId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: update
    List<SocioPeriodoJustificacionDocumento> periodosJustificacionActualizados = service.update(proyectoSocioId,
        peridosJustificiacionActualizar);

    // then: Se crea el nuevo SocioPeriodoJustificacionDocumento, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getProyectoSocioPeriodoJustificacion().getId())
        .as("get(0).getProyectoSocioPeriodoJustificacion().getId()").isEqualTo(proyectoSocioId);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getComentario());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getDocumentoRef()).as("get(0).getDocumentoRef()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getDocumentoRef());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getTipoDocumento().getId())
        .as("get(0).getTipoDocumento().getId()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getTipoDocumento().getId());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getNombre()).as("get(0).getNombre()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getNombre());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getVisible()).as("get(0).getVisible()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getVisible());

    Assertions.assertThat(periodosJustificacionActualizados.get(1).getId()).as("get(1).getId()").isEqualTo(4L);
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getProyectoSocioPeriodoJustificacion().getId())
        .as("get(1).getProyectoSocioPeriodoJustificacion().getId()").isEqualTo(proyectoSocioId);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getComentario()).as("get(1).getComentario()")
        .isEqualTo(updatedSocioPeriodoJustificacionDocumento.getComentario());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getDocumentoRef()).as("get(1).getDocumentoRef()")
        .isEqualTo(updatedSocioPeriodoJustificacionDocumento.getDocumentoRef());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getTipoDocumento().getId())
        .as("get(1).getTipoDocumento().getId()")
        .isEqualTo(updatedSocioPeriodoJustificacionDocumento.getTipoDocumento().getId());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getNombre()).as("get(1).getNombre()")
        .isEqualTo(updatedSocioPeriodoJustificacionDocumento.getNombre());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getVisible()).as("get(1).getVisible()")
        .isEqualTo(updatedSocioPeriodoJustificacionDocumento.getVisible());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<SocioPeriodoJustificacionDocumento>anyList());
    Mockito.verify(repository, Mockito.times(1))
        .saveAll(ArgumentMatchers.<SocioPeriodoJustificacionDocumento>anyList());
  }

  @Test
  public void update_WithNoExistingProyectoSocioPeriodoJustificacion_ThrowsProyectoSocioPeriodoJustificacionNotFoundException() {
    // given: a ProyectoSocioPeriodoJustificacionEntidadGestora with non existing
    // ProyectoSocioPeriodoJustificacion
    Long proyectoSocioId = 1L;
    SocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = generarMockSocioPeriodoJustificacionDocumento(
        1L);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocioPeriodoJustificacionEntidadGestora
        () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoJustificacion)))
        // then: throw exception as ProyectoSocioPeriodoJustificacion is not found
        .isInstanceOf(ProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSocioPeriodoJustificacionDocumentoNotFoundException() {
    // given: Un SocioPeriodoJustificacionDocumento a actualizar con un id que no
    // existe
    Long proyectoSocioId = 1L;
    SocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = generarMockSocioPeriodoJustificacionDocumento(
        1L);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion.getProyectoSocioPeriodoJustificacion()));

    BDDMockito.given(repository.findAllByProyectoSocioPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(new ArrayList<>());

    // when:update
    // then: Lanza una excepcion porque el SocioPeriodoJustificacionDocumento no
    // existe
    Assertions
        .assertThatThrownBy(() -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoJustificacion)))
        .isInstanceOf(SocioPeriodoJustificacionDocumentoNotFoundException.class);
  }

  @Test
  public void update_WithProyectoSocioPeriodoJustificacionChange_ThrowsIllegalArgumentException() {
    // given: a SocioPeriodoJustificacionDocumento with proyecto socio modificado
    Long proyectoSocioId = 1L;
    SocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = generarMockSocioPeriodoJustificacionDocumento(
        1L);

    proyectoSocioPeriodoJustificacion.getProyectoSocioPeriodoJustificacion().setId(3L);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion.getProyectoSocioPeriodoJustificacion()));

    BDDMockito.given(repository.findAllByProyectoSocioPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(generarMockSocioPeriodoJustificacionDocumento(1L)));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoJustificacion)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar el proyecto socio del SocioPeriodoJustificacionDocumento");
  }

  @Test
  public void findAllByProyectoSocioPeriodoJustificacion_ReturnsPage() {
    // given: Una lista con 37 ProyectoSocioPeriodoJustificacionEntidadGestora para
    // la ProyectoSocioPeriodoJustificacion
    Long proyectoSocioId = 1L;
    List<SocioPeriodoJustificacionDocumento> proyectoSociosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSociosEntidadesConvocantes.add(generarMockSocioPeriodoJustificacionDocumento(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SocioPeriodoJustificacionDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSociosEntidadesConvocantes.size() ? proyectoSociosEntidadesConvocantes.size()
              : toIndex;
          List<SocioPeriodoJustificacionDocumento> content = proyectoSociosEntidadesConvocantes.subList(fromIndex,
              toIndex);
          Page<SocioPeriodoJustificacionDocumento> pageResponse = new PageImpl<>(content, pageable,
              proyectoSociosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SocioPeriodoJustificacionDocumento> page = service.findAllByProyectoSocioPeriodoJustificacion(proyectoSocioId,
        null, paging);

    // then: Devuelve la pagina 3 con los SocioPeriodoJustificacionDocumento del 31
    // al
    // 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findById_ReturnsSocioPeriodoJustificacionDocumento() {
    // given: Un SocioPeriodoJustificacionDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockSocioPeriodoJustificacionDocumento(idBuscado)));

    // when: Buscamos el SocioPeriodoJustificacionDocumento por su id
    SocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = service.findById(idBuscado);

    // then: el SocioPeriodoJustificacionDocumento
    Assertions.assertThat(proyectoSocioPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getComentario()).as("getComentario()")
        .isEqualTo("comentario");
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getVisible()).as("getVisible()").isTrue();

  }

  @Test
  public void findById_WithIdNotExist_ThrowsSocioPeriodoJustificacionDocumentoNotFoundException() throws Exception {
    // given: Ningun SocioPeriodoJustificacionDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SocioPeriodoJustificacionDocumento por su id
    // then: lanza un SocioPeriodoJustificacionDocumentoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SocioPeriodoJustificacionDocumentoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto SocioPeriodoJustificacionDocumento.
   * 
   * @param id identificador
   * @return el objeto SocioPeriodoJustificacionDocumento
   */
  private SocioPeriodoJustificacionDocumento generarMockSocioPeriodoJustificacionDocumento(Long id) {
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = ProyectoSocioPeriodoJustificacion.builder()
        .id(1L).build();

    TipoDocumento tipoDocumento = TipoDocumento.builder().nombre("tipo1").activo(Boolean.TRUE).build();
    SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumento = SocioPeriodoJustificacionDocumento.builder()
        .id(id).nombre("nombre-" + id).comentario("comentario").documentoRef("001")
        .proyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion).tipoDocumento(tipoDocumento)
        .visible(Boolean.TRUE).build();

    return socioPeriodoJustificacionDocumento;
  }

}
