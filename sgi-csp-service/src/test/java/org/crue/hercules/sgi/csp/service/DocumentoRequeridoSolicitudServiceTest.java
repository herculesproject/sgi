package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.DocumentoRequeridoSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.DocumentoRequeridoSolicitudServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class DocumentoRequeridoSolicitudServiceTest extends BaseServiceTest {

  @Mock
  private DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;
  @Mock
  private ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  private DocumentoRequeridoSolicitudService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new DocumentoRequeridoSolicitudServiceImpl(documentoRequeridoSolicitudRepository,
        configuracionSolicitudRepository, modeloTipoFaseRepository, modeloTipoDocumentoRepository, convocatoriaService);
  }

  @Test
  public void create_ReturnsDocumentoRequeridoSolicitud() {
    // given: new DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(
            newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes())));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoDocumento(newDocumentoRequeridoSolicitud)));

    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willAnswer(new Answer<DocumentoRequeridoSolicitud>() {
          @Override
          public DocumentoRequeridoSolicitud answer(InvocationOnMock invocation) throws Throwable {
            DocumentoRequeridoSolicitud givenData = invocation.getArgument(0, DocumentoRequeridoSolicitud.class);
            DocumentoRequeridoSolicitud newData = new DocumentoRequeridoSolicitud();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud createdDocumentoRequeridoSolicitud = service.create(newDocumentoRequeridoSolicitud);

    // then: new DocumentoRequeridoSolicitud is created
    Assertions.assertThat(createdDocumentoRequeridoSolicitud).isNotNull();
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getId()).isNotNull();
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getId())
        .isEqualTo(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getId());
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getTipoDocumento().getId())
        .isEqualTo(newDocumentoRequeridoSolicitud.getTipoDocumento().getId());
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getObservaciones())
        .isEqualTo(newDocumentoRequeridoSolicitud.getObservaciones());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with id filled
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithoutConfiguracionId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without ConfigracionId
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    newDocumentoRequeridoSolicitud.setConfiguracionSolicitud(null);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ConfigracionId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without ConvocatoriaId
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().setConvocatoria(null);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithoutTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without TipoDocumento
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    newDocumentoRequeridoSolicitud.setTipoDocumento(null);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithNoExistingConfiguracionSolicitud_ThrowsNotFoundException() {
    // given: a DocumentoRequeridoSolicitud without No existing
    // ConfiguracionSolicitud
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ConfiguracionSolicitud is not found
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  public void create_WithoutFasePresentacionSolicitudes_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without FasePresentacionSolicitudes
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().setFasePresentacionSolicitudes(null);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as FasePresentacionSolicitudes is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Solo se pueden añadir documentos asociados a la Fase del plazo de presentación de solicitudes en la configuración de la convocatoria");
  }

  @Test
  public void create_WithNoExistingModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase not assigned to ModeloEjecucion Convocatoria
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes().getTipoFase()
                .getNombre(),
            newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion()
                .getNombre());
  }

  @Test
  public void create_WithModeloTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase assigned to disabled ModeloEjecucion
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    modeloTipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoFase.getTipoFase().getNombre(), newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()
                .getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase disabled assigned to ModeloEjecucion Convocatoria
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithNoExistingModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento nos assigned to
    // ModeloEjecucion or ModeloTipoFase
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no disponible para el ModeloEjecucion '%s' y TipoFase '%s'",
            newDocumentoRequeridoSolicitud.getTipoDocumento().getNombre(), newDocumentoRequeridoSolicitud
                .getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion().getNombre(),
            modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento assigned to disabled
    // ModeloEjecucion
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newDocumentoRequeridoSolicitud);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoDocumento '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoDocumento.getTipoDocumento().getNombre(), newDocumentoRequeridoSolicitud
                .getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion().getNombre());

  }

  @Test
  public void create_WithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento disabled assigned to
    // ModeloEjecucion
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        newDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newDocumentoRequeridoSolicitud);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no está activo", modeloTipoDocumento.getTipoDocumento().getNombre());
  }

  @Test
  public void create_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud when modificable returns False
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede crear DocumentoRequeridoSolicitud. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void update_ReturnsDocumentoRequeridoSolicitud() {
    // given: update DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(generarMockTipoDocumento(2L));

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(
            originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes())));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud)));

    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willReturn(updatedDocumentoRequeridoSolicitud);

    // when: update DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud updated = service.update(updatedDocumentoRequeridoSolicitud);

    // then: DocumentoRequeridoSolicitud is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getConfiguracionSolicitud().getId())
        .isEqualTo(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getId());
    Assertions.assertThat(updated.getTipoDocumento().getId())
        .isEqualTo(updatedDocumentoRequeridoSolicitud.getTipoDocumento().getId());
    Assertions.assertThat(updated.getObservaciones()).isEqualTo(updatedDocumentoRequeridoSolicitud.getObservaciones());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a updated DocumentoRequeridoSolicitud with id filled
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setId(null);

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("DocumentoRequeridoSolicitud id no puede ser null para actualizar un DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithNoExistingDocumentoRequeridoSolicitud_ThrowsNotFoundException() {
    // given: a DocumentoRequeridoSolicitud with no existing Id
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as id is not found
        .isInstanceOf(DocumentoRequeridoSolicitudNotFoundException.class);
  }

  @Test
  public void update_WithoutConfiguracionId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without ConfigracionId
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setConfiguracionSolicitud(null);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ConfigracionId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without ConvocatoriaId
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.getConfiguracionSolicitud().setConvocatoria(null);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithoutTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without TipoDocumento
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(null);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithNoExistingConfiguracionSolicitud_ThrowsNotFoundException() {
    // given: a DocumentoRequeridoSolicitud with No existing ConfiguracionSolicitud
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ConfiguracionSolicitud is not found
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  public void update_WithoutFasePresentacionSolicitudes_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without FasePresentacionSolicitudes
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().setFasePresentacionSolicitudes(null);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as FasePresentacionSolicitudes is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Solo se pueden añadir documentos asociados a la Fase del plazo de presentación de solicitudes en la configuración de la convocatoria");
  }

  @Test
  public void update_WithNoExistingModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase not assigned to ModeloEjecucion Convocatoria
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            updatedDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes()
                .getTipoFase().getNombre(),
            updatedDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion()
                .getNombre());
  }

  @Test
  public void update_WithModeloTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase assigned to disabled ModeloEjecucion
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    modeloTipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoFase.getTipoFase().getNombre(), originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()
                .getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase disabled assigned to ModeloEjecucion Convocatoria
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_WithNoExistingModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento nos assigned to
    // ModeloEjecucion or ModeloTipoFase
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no disponible para el ModeloEjecucion '%s' y TipoFase '%s'",
            updatedDocumentoRequeridoSolicitud.getTipoDocumento().getNombre(), originalDocumentoRequeridoSolicitud
                .getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion().getNombre(),
            modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_TipoDocumentoWithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento assigned to disabled
    // ModeloEjecucion
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(generarMockTipoDocumento(2L));
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoDocumento '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoDocumento.getTipoDocumento().getNombre(), originalDocumentoRequeridoSolicitud
                .getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithModeloTipoDocumentoDisabledAndSameTipoDocumentoId_DoesNotThrowAnyException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento assigned to disabled
    // ModeloEjecucion without updating TipoDocumento
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willReturn(updatedDocumentoRequeridoSolicitud);

    Assertions.assertThatCode(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void update_TipoDocumentoWithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento disabled assigned
    // ModeloEjecucion
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(generarMockTipoDocumento(2L));
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatCode(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no está activo", modeloTipoDocumento.getTipoDocumento().getNombre());
  }

  @Test
  public void update_WithTipoDocumentoDisabledAndSameTipoDocumentoId_DoesNotThrowAnyException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento disabled assigned to
    // ModeloEjecucion without updating TipoDocumento
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(
        originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willReturn(updatedDocumentoRequeridoSolicitud);

    Assertions.assertThatCode(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void update_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud when modificable return false
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(documentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(documentoRequeridoSolicitud.getConfiguracionSolicitud()));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(documentoRequeridoSolicitud))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar DocumentoRequeridoSolicitud. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void delete_WithExistingId_DoesNotThrowAnyException() {
    // given: existing DocumentoRequeridoSolicitud
    Long id = 1L;

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockDocumentoRequeridoSolicitud(id)));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(documentoRequeridoSolicitudRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() throws Exception {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("DocumentoRequeridoSolicitud id no puede ser null para eliminar un DocumentoRequeridoSolicitud");
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(DocumentoRequeridoSolicitudNotFoundException.class);
  }

  @Test
  public void findByIdConvocatoria_WithExistingId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: existing ConfiguracionSolicitud
    DocumentoRequeridoSolicitud documentoRequeridoSolicitudExistente = generarMockDocumentoRequeridoSolicitud(1L);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(documentoRequeridoSolicitudExistente));

    // when: find ConfiguracionSolicitud Convocatoria
    DocumentoRequeridoSolicitud found = service.findById(documentoRequeridoSolicitudExistente.getId());

    // then: ConfiguracionSolicitud is updated
    Assertions.assertThat(found).isNotNull();
    Assertions.assertThat(found.getId()).isNotNull();
    Assertions.assertThat(found.getId()).as("getId()").isEqualTo(documentoRequeridoSolicitudExistente.getId());
    Assertions.assertThat(found.getConfiguracionSolicitud().getId()).as("getConfiguracionSolicitud().getId()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getConfiguracionSolicitud().getId());
    Assertions.assertThat(found.getConfiguracionSolicitud().getConvocatoria().getId())
        .as("getConfiguracionSolicitud().getConvocatoria().getId()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getConfiguracionSolicitud().getConvocatoria().getId());
    Assertions.assertThat(found.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getTipoDocumento().getId());
    Assertions.assertThat(found.getObservaciones()).as("getObservaciones()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getObservaciones());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing convocatoria
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing convocatoria
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(DocumentoRequeridoSolicitudNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 DocumentoRequeridoSolicitud para la Convocatoria
    Long convocatoriaId = 1L;
    List<DocumentoRequeridoSolicitud> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarMockDocumentoRequeridoSolicitud(i));
    }

    BDDMockito
        .given(documentoRequeridoSolicitudRepository.findAll(
            ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<DocumentoRequeridoSolicitud>>() {
          @Override
          public Page<DocumentoRequeridoSolicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<DocumentoRequeridoSolicitud> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<DocumentoRequeridoSolicitud> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<DocumentoRequeridoSolicitud> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los DocumentoRequeridoSolicitud del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      DocumentoRequeridoSolicitud DocumentoRequeridoSolicitud = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(DocumentoRequeridoSolicitud.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(DocumentoRequeridoSolicitud.getObservaciones())
          .isEqualTo("observacionesDocumentoRequeridoSolicitud-" + i);
    }
  }

  /**
   * Función que devuelve un objeto DocumentoRequeridoSolicitud
   * 
   * @param id id del DocumentoRequeridoSolicitud
   * @return el objeto DocumentoRequeridoSolicitud
   */
  private DocumentoRequeridoSolicitud generarMockDocumentoRequeridoSolicitud(Long id) {

    return DocumentoRequeridoSolicitud.builder()//
        .id(id)//
        .configuracionSolicitud(generarMockConfiguracionSolicitud(id, id, id))//
        .tipoDocumento(generarMockTipoDocumento(id))//
        .observaciones("observacionesDocumentoRequeridoSolicitud-" + id)//
        .build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase a partir de un objeto
   * ConvocatoriaFase
   * 
   * @param convocatoriaTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarMockModeloTipoFase(ConvocatoriaFase convocatoriaTipoFase) {

    return ModeloTipoFase.builder()//
        .id(convocatoriaTipoFase.getId() == null ? 1L : convocatoriaTipoFase.getId())//
        .modeloEjecucion(convocatoriaTipoFase.getConvocatoria().getModeloEjecucion())//
        .tipoFase(convocatoriaTipoFase.getTipoFase())//
        .solicitud(Boolean.TRUE)//
        .convocatoria(Boolean.TRUE)//
        .proyecto(Boolean.TRUE)//
        .activo(Boolean.TRUE)//
        .build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento a partir de un objeto
   * DocumentoRequeridoSolicitud
   * 
   * @param documentoRequeridoSolicitud
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) {

    return ModeloTipoDocumento.builder()//
        .id(documentoRequeridoSolicitud.getId() == null ? 1L : documentoRequeridoSolicitud.getId())//
        .modeloEjecucion(documentoRequeridoSolicitud.getConfiguracionSolicitud().getConvocatoria().getModeloEjecucion())//
        .modeloTipoFase(generarMockModeloTipoFase(
            documentoRequeridoSolicitud.getConfiguracionSolicitud().getFasePresentacionSolicitudes()))//
        .tipoDocumento(documentoRequeridoSolicitud.getTipoDocumento())//
        .activo(Boolean.TRUE)//
        .build();
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {

    return TipoDocumento.builder()//
        .id(id)//
        .nombre("nombreTipoDocumento-" + id)//
        .descripcion("descripcionTipoDocumento-" + id)//
        .activo(Boolean.TRUE)//
        .build();
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  private TipoFase generarMockTipoFase(Long id) {

    return TipoFase.builder()//
        .id(id)//
        .nombre("nombreTipoFase-" + id)//
        .descripcion("descripcionTipoFase-" + id)//
        .activo(Boolean.TRUE)//
        .build();
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId,
      Long convocatoriaFaseId) {

    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    TipoFase tipoFase = generarMockTipoFase(1L);

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()//
        .id(convocatoriaFaseId)//
        .convocatoria(convocatoria)//
        .tipoFase(tipoFase)//
        .fechaInicio(LocalDateTime.of(2020, 10, 1, 17, 18, 19))//
        .fechaFin(LocalDateTime.of(2020, 10, 15, 17, 18, 19))//
        .observaciones("observaciones")//
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()//
        .id(configuracionSolicitudId)//
        .convocatoria(convocatoria)//
        .tramitacionSGI(Boolean.TRUE)//
        .fasePresentacionSolicitudes(convocatoriaFase)//
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))//
        .formularioSolicitud(FormularioSolicitud.ESTANDAR)//
        .build();

    return configuracionSolicitud;
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()//
            .id(modeloEjecucionId)//
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))//
            .activo(Boolean.TRUE)//
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .modeloEjecucion(modeloEjecucion)//
            .tipoFinalidad(tipoFinalidad)//
            .activo(Boolean.TRUE)//
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()//
            .id(tipoRegimenConcurrenciaId)//
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()//
            .id(tipoAmbitoGeogragicoId)//
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))//
            .activo(Boolean.TRUE)//
            .build();

    Convocatoria convocatoria = Convocatoria.builder()//
        .id(convocatoriaId)//
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo-" + String.format("%03d", convocatoriaId))//
        .anio(2020)//
        .titulo("titulo-" + String.format("%03d", convocatoriaId))//
        .objeto("objeto-" + String.format("%03d", convocatoriaId))//
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))//
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(Convocatoria.Destinatarios.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estado(Convocatoria.Estado.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVN.AYUDAS)//
        .activo(activo)//
        .build();

    return convocatoria;
  }
}
