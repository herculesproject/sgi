package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoDescripcion;
import org.crue.hercules.sgi.csp.model.RolProyectoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class RolProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private RolProyectoRepository repository;

  @Test
  void findByAbreviaturaAndActivoIsTrue_ReturnsRolProyecto() throws Exception {
    // given: data RolProyecto with Abreviatura to find
    RolProyecto rolProyecto1 = generarMockRolProyecto("001", Boolean.TRUE);
    generarMockRolProyecto("002", Boolean.TRUE);
    generarMockRolProyecto("001", Boolean.FALSE);

    // when: find given Abreviatura
    String abreviaturaToFind = rolProyecto1.getAbreviatura();
    RolProyecto responseData = repository.findByAbreviaturaAndActivoIsTrue(abreviaturaToFind).get();

    // then: RolProyecto with given Abreviatura is found
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto1.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(abreviaturaToFind);
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolProyecto1.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(rolProyecto1.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto1.getRolPrincipal());
    Assertions.assertThat(responseData.getOrden()).as("getOrden()").isEqualTo(rolProyecto1.getOrden());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto1.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByAbreviaturaAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: Abreviatura to find
    String abreviaturaToFind = "001";

    // when: find given Abreviatura
    Optional<RolProyecto> responseData = repository.findByAbreviaturaAndActivoIsTrue(abreviaturaToFind);

    // then: RolProyecto with given Abreviatura is not found
    Assertions.assertThat(responseData).isEqualTo(Optional.empty());
  }

  @Test
  void findByNombreAndActivoIsTrue_ReturnsRolProyecto() throws Exception {
    // given: data RolProyecto with Nombre to find
    RolProyecto rolProyecto1 = generarMockRolProyecto("001", Boolean.TRUE);
    generarMockRolProyecto("002", Boolean.TRUE);
    generarMockRolProyecto("001", Boolean.FALSE);

    // when: find given Nombre
    Set<RolProyectoNombre> nombreToFind = rolProyecto1.getNombre();
    RolProyecto responseData = repository.findByNombreValueAndActivoIsTrue(I18nHelper.getValueForLanguage(nombreToFind, Language.ES)).get();

    // then: RolProyecto with given Nombre is found
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto1.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(rolProyecto1.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(nombreToFind);
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto1.getRolPrincipal());
    Assertions.assertThat(responseData.getOrden()).as("getOrden()").isEqualTo(rolProyecto1.getOrden());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto1.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: Nombre to find
    String nombreToFind = "001";

    // when: find given Nombre
    Optional<RolProyecto> responseData = repository.findByNombreValueAndActivoIsTrue(nombreToFind);

    // then: RolProyecto with given Nombre is not found
    Assertions.assertThat(responseData).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera RolProyecto
   * 
   * @param suffix
   * @return el objeto RolProyecto
   */
  private RolProyecto generarMockRolProyecto(String suffix, Boolean activo) {
    Set<RolProyectoNombre> nombre = new HashSet<>();
    nombre.add(new RolProyectoNombre(Language.ES, "nombre-" + suffix));
    Set<RolProyectoDescripcion> descricion = new HashSet<>();
    descricion.add(new RolProyectoDescripcion(Language.ES, "descripcion-" + suffix));
    // @formatter:off
    RolProyecto rolProyecto = RolProyecto.builder()
        .abreviatura(suffix)
        .nombre(nombre)
        .descripcion(descricion)
        .rolPrincipal(Boolean.FALSE)
        .orden(null)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(activo)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(rolProyecto);
  }
}
