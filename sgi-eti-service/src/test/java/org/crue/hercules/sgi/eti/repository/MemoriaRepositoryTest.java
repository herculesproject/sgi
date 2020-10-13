package org.crue.hercules.sgi.eti.repository;

import java.time.LocalDate;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class MemoriaRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private MemoriaRepository repository;

  @Test
  public void findByIdAndActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para la memoria activa

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    // when: Se buscan los datos
    Optional<Memoria> result = repository.findByIdAndActivoTrue(memoria.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.get()).isNotNull();

  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @return el objeto Comite
   */
  private Comite generarMockComite() {
    return new Comite(null, "Comite1", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @return el objeto TipoActividad
   */
  private TipoActividad generarMockTipoActividad() {
    return new TipoActividad(1L, "TipoActividad", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoInvestigacionTutelada
   * 
   * @return el objeto TipoInvestigacionTutelada
   */
  private TipoInvestigacionTutelada generarMockTipoInvestigacionTutelada() {
    return new TipoInvestigacionTutelada(1L, "TipoInvestigacionTutelada", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param tipoActividad             el objeto TipoActividad
   * @param tipoInvestigacionTutelada el objeto TipoInvestigacionTutelada
   * @return PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad,
      TipoInvestigacionTutelada tipoInvestigacionTutelada) {
    return new PeticionEvaluacion(null, "Referencia solicitud convocatoria", "Codigo", "PeticionEvaluacion",
        tipoActividad, tipoInvestigacionTutelada, "Fuente financiación", LocalDate.now(), LocalDate.now(), "Resumen",
        "Valor social", "Objetivos", "DiseñoMetodologico", Boolean.FALSE, Boolean.FALSE, "user-001", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @return el objeto TipoEstadoMemoria
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria() {
    return new TipoEstadoMemoria(1L, "TipoEstadoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoMemoria
   * 
   * @return el objeto TipoMemoria
   */
  private TipoMemoria generarMockTipoMemoria() {
    return new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto EstadoRetrospectiva
   * 
   * @return el objeto EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockEstadoRetrospectiva() {
    return new EstadoRetrospectiva(1L, "EstadoRetrospectiva", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Retrospectiva
   * 
   * @param estadoRetrospectiva el objeto EstadoRetrospectiva
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(EstadoRetrospectiva estadoRetrospectiva) {
    return new Retrospectiva(null, estadoRetrospectiva, LocalDate.now());
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param peticionEvaluacion el objeto PeticionEvaluacion
   * @param comite             el objeto Comite
   * @param tipoMemoria        el objeto TipoMemoria
   * @param tipoEstadoMemoria  el objeto TipoEstadoMemoria
   * @param retrospectiva      el objeto Retrospectiva
   * @return Memoria
   */
  private Memoria generarMockMemoria(PeticionEvaluacion peticionEvaluacion, Comite comite, TipoMemoria tipoMemoria,
      TipoEstadoMemoria tipoEstadoMemoria, Retrospectiva retrospectiva) {
    return new Memoria(null, "numRef-001", peticionEvaluacion, comite, "Memoria", "user-001", tipoMemoria,
        tipoEstadoMemoria, LocalDate.now(), Boolean.TRUE, retrospectiva, 3, Boolean.TRUE);
  }

}