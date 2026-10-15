package org.crue.hercules.sgi.csp.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test unitario de {@link ConvocatoriaAuthorityHelper}.
 */
class ConvocatoriaAuthorityHelperTest extends BaseServiceTest {

  private static final Long CONVOCATORIA_ID = 1L;
  private static final String UNIDAD_GESTION_REF = "unidad-001";

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  private ConvocatoriaAuthorityHelper helper;

  @BeforeEach
  void setUp() {
    helper = new ConvocatoriaAuthorityHelper(convocatoriaRepository, configuracionSolicitudRepository);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_ConvocatoriaPublica_NoThrows() {
    // given: un usuario sin autoridades y una convocatoria pública con su configuración
    autenticarConAutoridades();
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.RRHH, Convocatoria.Estado.REGISTRADA);
    ConfiguracionSolicitud configuracionSolicitud = generarConfiguracionSolicitud(Boolean.TRUE);

    // when: se comprueba la visibilidad con la configuración recibida
    // then: no lanza excepción y no consulta ningún repositorio
    Assertions.assertThatCode(
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, configuracionSolicitud))
        .doesNotThrowAnyException();
    Mockito.verifyNoInteractions(convocatoriaRepository, configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_ConvocatoriaRrhhSinConfiguracion_ThrowsUserNotAuthorizedToAccessConvocatoriaException() {
    // given: un usuario sin autoridades y una convocatoria RRHH registrada sin configuración
    autenticarConAutoridades();
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.RRHH, Convocatoria.Estado.REGISTRADA);

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad sin configuración
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, null))
        // then: no es pública y lanza UserNotAuthorizedToAccessConvocatoriaException
        .isInstanceOf(UserNotAuthorizedToAccessConvocatoriaException.class);
    Mockito.verifyNoInteractions(convocatoriaRepository, configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_UnidadGestion_NoThrows() {
    // given: un gestor con CSP-CON-V en la unidad de gestión de una convocatoria sin configuración
    autenticarConAutoridades("CSP-CON-V_" + UNIDAD_GESTION_REF);
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.BORRADOR);

    // when: se comprueba la visibilidad sin configuración
    // then: no lanza excepción
    Assertions.assertThatCode(() -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, null))
        .doesNotThrowAnyException();
    Mockito.verifyNoInteractions(convocatoriaRepository, configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_InvestigadorConConfiguracion_NoThrows() {
    // given: un investigador y una convocatoria registrada con tramitación SGI
    autenticarConAutoridades("CSP-CON-INV-V");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);
    ConfiguracionSolicitud configuracionSolicitud = generarConfiguracionSolicitud(Boolean.TRUE);

    // when: se comprueba la visibilidad con la configuración recibida
    // then: no lanza excepción y no consulta ningún repositorio
    Assertions.assertThatCode(
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, configuracionSolicitud))
        .doesNotThrowAnyException();
    Mockito.verifyNoInteractions(convocatoriaRepository, configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_InvestigadorSinTramitacionSgi_ThrowsUserNotAuthorizedToAccessConvocatoriaException() {
    // given: un investigador y una convocatoria registrada sin tramitación SGI
    autenticarConAutoridades("CSP-CON-INV-V");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);
    ConfiguracionSolicitud configuracionSolicitud = generarConfiguracionSolicitud(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad con la configuración recibida
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, configuracionSolicitud))
        // then: lanza UserNotAuthorizedToAccessConvocatoriaException
        .isInstanceOf(UserNotAuthorizedToAccessConvocatoriaException.class);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_InvestigadorSinConfiguracion_ThrowsConfiguracionSolicitudNotFoundException() {
    // given: un investigador y una convocatoria sin configuración
    autenticarConAutoridades("CSP-CON-INV-V");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad sin configuración
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, null))
        // then: lanza ConfiguracionSolicitudNotFoundException
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoriaWithConfiguracion_SinPermiso_ThrowsUserNotAuthorizedToAccessConvocatoriaException() {
    // given: un usuario sin permisos de convocatoria y una convocatoria no pública con configuración
    autenticarConAutoridades("CSP-SOL-C");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);
    ConfiguracionSolicitud configuracionSolicitud = generarConfiguracionSolicitud(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad con la configuración recibida
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria, configuracionSolicitud))
        // then: lanza UserNotAuthorizedToAccessConvocatoriaException
        .isInstanceOf(UserNotAuthorizedToAccessConvocatoriaException.class);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_UnidadGestion_NoConsultaConfiguracion() {
    // given: un gestor con CSP-CON-E en la unidad de gestión de una convocatoria RRHH
    autenticarConAutoridades("CSP-CON-E_" + UNIDAD_GESTION_REF);
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.RRHH, Convocatoria.Estado.REGISTRADA);

    // when: se comprueba la visibilidad cargando la configuración si hace falta
    // then: no lanza excepción y no consulta la configuración
    Assertions.assertThatCode(() -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        .doesNotThrowAnyException();
    Mockito.verifyNoInteractions(configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_ConvocatoriaPublica_ConsultaConfiguracionYNoThrows() {
    // given: un usuario sin autoridades y una convocatoria pública
    autenticarConAutoridades();
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.RRHH, Convocatoria.Estado.REGISTRADA);
    Mockito.when(configuracionSolicitudRepository.findByConvocatoriaId(CONVOCATORIA_ID))
        .thenReturn(Optional.of(generarConfiguracionSolicitud(Boolean.TRUE)));

    // when: se comprueba la visibilidad cargando la configuración
    // then: no lanza excepción y consulta la configuración una vez
    Assertions.assertThatCode(() -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        .doesNotThrowAnyException();
    Mockito.verify(configuracionSolicitudRepository, Mockito.times(1)).findByConvocatoriaId(CONVOCATORIA_ID);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_InvestigadorConConfiguracion_NoThrows() {
    // given: un investigador y una convocatoria registrada con tramitación SGI
    autenticarConAutoridades("CSP-CON-INV-V");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);
    Mockito.when(configuracionSolicitudRepository.findByConvocatoriaId(CONVOCATORIA_ID))
        .thenReturn(Optional.of(generarConfiguracionSolicitud(Boolean.TRUE)));

    // when: se comprueba la visibilidad cargando la configuración
    // then: no lanza excepción
    Assertions.assertThatCode(() -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        .doesNotThrowAnyException();
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_InvestigadorSinConfiguracion_ThrowsConfiguracionSolicitudNotFoundException() {
    // given: un investigador y una convocatoria sin configuración
    autenticarConAutoridades("CSP-CON-INV-V");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);
    Mockito.when(configuracionSolicitudRepository.findByConvocatoriaId(CONVOCATORIA_ID))
        .thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad cargando la configuración
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        // then: lanza ConfiguracionSolicitudNotFoundException
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_SinPermiso_ThrowsUserNotAuthorizedToAccessConvocatoriaException() {
    // given: un usuario sin permisos de convocatoria y una convocatoria no RRHH
    autenticarConAutoridades("CSP-SOL-C");
    Convocatoria convocatoria = generarConvocatoria(FormularioSolicitud.PROYECTO, Convocatoria.Estado.REGISTRADA);

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad cargando la configuración si hace falta
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        // then: lanza UserNotAuthorizedToAccessConvocatoriaException sin consultar la configuración
        .isInstanceOf(UserNotAuthorizedToAccessConvocatoriaException.class);
    Mockito.verifyNoInteractions(configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_SinFormularioSolicitudSinPermiso_ThrowsUserNotAuthorizedToAccessConvocatoriaException() {
    // given: un usuario sin permisos de UG ni de investigador y una convocatoria activa sin formulario de solicitud
    autenticarConAutoridades("CSP-SOL-C");
    Convocatoria convocatoria = generarConvocatoria(null, Convocatoria.Estado.REGISTRADA);

    Assertions.assertThatThrownBy(
        // when: se comprueba la visibilidad de la convocatoria
        () -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        // then: no es pública y lanza UserNotAuthorizedToAccessConvocatoriaException en lugar de NPE
        .isInstanceOf(UserNotAuthorizedToAccessConvocatoriaException.class);
    Mockito.verifyNoInteractions(configuracionSolicitudRepository);
  }

  @Test
  void checkUserHasAuthorityViewConvocatoria_SinFormularioSolicitudGestorUnidadGestion_NoThrows() {
    // given: un gestor con CSP-CON-V en la unidad de gestión de una convocatoria activa sin formulario de solicitud
    autenticarConAutoridades("CSP-CON-V_" + UNIDAD_GESTION_REF);
    Convocatoria convocatoria = generarConvocatoria(null, Convocatoria.Estado.REGISTRADA);

    // when: se comprueba la visibilidad de la convocatoria
    // then: no lanza excepción
    Assertions.assertThatCode(() -> helper.checkUserHasAuthorityViewConvocatoria(convocatoria))
        .doesNotThrowAnyException();
  }

  private void autenticarConAutoridades(String... authorities) {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null,
        Arrays.stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
  }

  private Convocatoria generarConvocatoria(FormularioSolicitud formularioSolicitud, Convocatoria.Estado estado) {
    return Convocatoria.builder()
        .id(CONVOCATORIA_ID)
        .unidadGestionRef(UNIDAD_GESTION_REF)
        .formularioSolicitud(formularioSolicitud)
        .estado(estado)
        .activo(Boolean.TRUE)
        .build();
  }

  private ConfiguracionSolicitud generarConfiguracionSolicitud(Boolean tramitacionSGI) {
    return ConfiguracionSolicitud.builder()
        .id(1L)
        .convocatoriaId(CONVOCATORIA_ID)
        .tramitacionSGI(tramitacionSGI)
        .build();
  }
}
