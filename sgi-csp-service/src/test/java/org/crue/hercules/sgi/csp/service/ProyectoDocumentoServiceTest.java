package org.crue.hercules.sgi.csp.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoDocumentoServiceImpl;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoDocumentoServiceTest
 */
class ProyectoDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoDocumentoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;
  @Mock
  private ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  @Mock
  private ProyectoHelper proyectoHelper;

  private ProyectoDocumentoServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new ProyectoDocumentoServiceImpl(repository, proyectoRepository, modeloTipoFaseRepository,
        modeloTipoDocumentoRepository, proyectoHelper);
  }

  @Test
  void findAllByProyectoId_ProyectoNotFound_ThrowsProyectoNotFoundException() {
    // given: un proyectoId que no existe
    Long proyectoId = 1L;
    Pageable pageable = Pageable.unpaged();
    BDDMockito.given(proyectoRepository.findById(proyectoId)).willReturn(Optional.empty());

    // when: se buscan los documentos del proyecto
    // then: se lanza ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findAllByProyectoId(proyectoId, null, pageable))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void findAllByProyectoId_UserWithoutAccess_ThrowsUserNotAuthorizedToAccessProyectoException() {
    // given: un usuario sin acceso al proyecto
    Long proyectoId = 1L;
    Pageable pageable = Pageable.unpaged();
    Proyecto proyecto = Proyecto.builder().id(proyectoId).unidadGestionRef("2").build();
    BDDMockito.given(proyectoRepository.findById(proyectoId)).willReturn(Optional.of(proyecto));
    BDDMockito.willThrow(new UserNotAuthorizedToAccessProyectoException()).given(proyectoHelper)
        .checkCanAccessProyecto(proyecto,
            ProyectoHelper.InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA);

    // when: se buscan los documentos del proyecto
    // then: se lanza UserNotAuthorizedToAccessProyectoException
    Assertions.assertThatThrownBy(() -> service.findAllByProyectoId(proyectoId, null, pageable))
        .isInstanceOf(UserNotAuthorizedToAccessProyectoException.class);
  }

  @Test
  void findAllByProyectoId_UserIsGestor_ReturnsAllProyectoDocumento() {
    // given: un usuario con acceso de gestión (visor/editor) sobre el proyecto
    Long proyectoId = 1L;
    Proyecto proyecto = Proyecto.builder().id(proyectoId).unidadGestionRef("2").build();
    BDDMockito.given(proyectoRepository.findById(proyectoId)).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoHelper.hasUserAuthorityViewAsGestorOrVisor(proyecto)).willReturn(true);

    List<ProyectoDocumento> documentos = Arrays.asList(
        ProyectoDocumento.builder().id(1L).proyectoId(proyectoId).visible(Boolean.TRUE).build(),
        ProyectoDocumento.builder().id(2L).proyectoId(proyectoId).visible(Boolean.FALSE).build());
    Page<ProyectoDocumento> page = new PageImpl<>(documentos);
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(page);

    // when: se buscan los documentos del proyecto
    Page<ProyectoDocumento> result = service.findAllByProyectoId(proyectoId, null, PageRequest.of(0, 10));

    // then: se devuelven todos los documentos, sin filtrar por visible
    Assertions.assertThat(result.getContent()).isEqualTo(documentos);
  }

  @Test
  void findAllByProyectoId_UserIsNotGestor_AppliesOnlyVisiblesFilter() {
    // given: un usuario sin acceso de gestión (investigador) sobre el proyecto
    Long proyectoId = 1L;
    Proyecto proyecto = Proyecto.builder().id(proyectoId).unidadGestionRef("2").build();
    BDDMockito.given(proyectoRepository.findById(proyectoId)).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoHelper.hasUserAuthorityViewAsGestorOrVisor(proyecto)).willReturn(false);

    List<ProyectoDocumento> documentosVisibles = Arrays
        .asList(ProyectoDocumento.builder().id(1L).proyectoId(proyectoId).visible(Boolean.TRUE).build());
    Page<ProyectoDocumento> page = new PageImpl<>(documentosVisibles);
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(page);

    // when: se buscan los documentos del proyecto
    Page<ProyectoDocumento> result = service.findAllByProyectoId(proyectoId, null, PageRequest.of(0, 10));

    // then: se comprueba la condición de gestor y se devuelve la página filtrada
    // por el repositorio
    Assertions.assertThat(result.getContent()).isEqualTo(documentosVisibles);
    BDDMockito.then(proyectoHelper).should().hasUserAuthorityViewAsGestorOrVisor(proyecto);
  }

}
