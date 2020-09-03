package org.crue.hercules.sgi.eti.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class EvaluacionRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private EvaluacionRepository repository;

  @Test
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsPage() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion convocatoriaReunion1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion convocatoriaReunion2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    PeticionEvaluacion peticionEvaluacion = entityManager.persistAndFlush(generarMockPeticionEvaluacion(tipoActividad));
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE)));
    response.add(entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE)));
    response.add(entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE)));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.TRUE));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = repository.findAllByActivoTrueAndConvocatoriaReunionId(convocatoriaReunion1.getId(),
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEqualTo(pageResponse.getContent());
  }

  @Test
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsEmptyPage() throws Exception {

    // given: Sin datos existentes con convocatoriaReunionId = 2

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion convocatoriaReunion1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion convocatoriaReunion2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    PeticionEvaluacion peticionEvaluacion = entityManager.persistAndFlush(generarMockPeticionEvaluacion(tipoActividad));
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = repository.findAllByActivoTrueAndConvocatoriaReunionId(convocatoriaReunion2.getId(),
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEmpty();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaReunion
   * 
   * @param comite
   * @param tipoConvocatoriaReunion
   * @return ConvocatoriaReunion
   */
  public ConvocatoriaReunion generarMockConvocatoriaReunion(Comite comite,
      TipoConvocatoriaReunion tipoConvocatoriaReunion) {

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.now());
    convocatoriaReunion.setFechaLimite(LocalDate.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    return convocatoriaReunion;

  }

  @Test
  public void findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId_ReturnsList() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1, tipoEvaluacionId= 1,
    // esRevMinima = true y activa.

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion convocatoriaReunion1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion convocatoriaReunion2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    PeticionEvaluacion peticionEvaluacion = entityManager.persistAndFlush(generarMockPeticionEvaluacion(tipoActividad));
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.FALSE)));
    response.add(entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.FALSE)));
    response.add(entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.FALSE)));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.FALSE));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.FALSE));

    // when: Se buscan los datos
    List<Evaluacion> result = repository.findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
        tipoEvaluacion.getId(), Boolean.FALSE, convocatoriaReunion1.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.size()).isEqualTo(response.size());

  }

  @Test
  public void findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId_ReturnsEmptyList()
      throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1, tipoEvaluacionId= 1,
    // esRevMinima = true y activa.

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion convocatoriaReunion1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion convocatoriaReunion2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    PeticionEvaluacion peticionEvaluacion = entityManager.persistAndFlush(generarMockPeticionEvaluacion(tipoActividad));
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.TRUE));
    entityManager.persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion1, tipoEvaluacion, Boolean.TRUE));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.FALSE));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, convocatoriaReunion2, tipoEvaluacion, Boolean.FALSE));

    // when: Se buscan los datos
    List<Evaluacion> result = repository.findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
        tipoEvaluacion.getId(), Boolean.FALSE, convocatoriaReunion1.getId());

    // then: Se comprueba que no se recupera ningún registro
    Assertions.assertThat(result.isEmpty()).isTrue();

  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @return el objeto Comite
   */
  public Comite generarMockComite() {
    return new Comite(null, "Comite1", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunionluacion
   * 
   * @return el objeto TipoConvocatoriaReunion
   */
  public TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion() {
    return new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param tipoEvaluacion
   * @return Dictamen
   */
  public Dictamen generarMockDictamen(TipoEvaluacion tipoEvaluacion) {
    return new Dictamen(1L, "Dictamen", tipoEvaluacion, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @return el objeto TipoEvaluacion
   */
  public TipoEvaluacion generarMockTipoEvaluacion() {
    return new TipoEvaluacion(1L, "TipoEvaluacion", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @return el objeto TipoActividad
   */
  public TipoActividad generarMockTipoActividad() {
    return new TipoActividad(1L, "TipoActividad", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param tipoActividad
   * @return PeticionEvaluacion
   */
  public PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad) {
    return new PeticionEvaluacion(null, "Referencia solicitud convocatoria", "Codigo", "PeticionEvaluacion",
        tipoActividad, "Fuente financiación", LocalDate.now(), LocalDate.now(), "Resumen", 3, "Otro valor social",
        "Objetivos", "DiseñoMetodologico", Boolean.FALSE, Boolean.FALSE, "user-001", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @return el objeto TipoEstadoMemoria
   */
  public TipoEstadoMemoria generarMockTipoEstadoMemoria() {
    return new TipoEstadoMemoria(1L, "TipoEstadoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoMemoria
   * 
   * @return el objeto TipoMemoria
   */
  public TipoMemoria generarMockTipoMemoria() {
    return new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto EstadoRetrospectiva
   * 
   * @return el objeto EstadoRetrospectiva
   */
  public EstadoRetrospectiva generarMockEstadoRetrospectiva() {
    return new EstadoRetrospectiva(1L, "EstadoRetrospectiva", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Retrospectiva
   * 
   * @param estadoRetrospectiva
   * @return Retrospectiva
   */
  public Retrospectiva generarMockRetrospectiva(EstadoRetrospectiva estadoRetrospectiva) {
    return new Retrospectiva(null, estadoRetrospectiva, LocalDate.now());
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param peticionEvaluacion
   * @param comite
   * @param tipoMemoria
   * @param tipoEstadoMemoria
   * @param retrospectiva
   * @return Memoria
   */
  public Memoria generarMockMemoria(PeticionEvaluacion peticionEvaluacion, Comite comite, TipoMemoria tipoMemoria,
      TipoEstadoMemoria tipoEstadoMemoria, Retrospectiva retrospectiva) {
    return new Memoria(null, "numRef-001", peticionEvaluacion, comite, "Memoria", "user-001", tipoMemoria,
        tipoEstadoMemoria, LocalDate.now(), Boolean.TRUE, retrospectiva, 3, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param dictamen
   * @param memoria
   * @param convocatoriaReunion
   * @param tipoEvaluacion
   * @return el objeto Evaluacion
   */
  public Evaluacion generarMockEvaluacion(Dictamen dictamen, Memoria memoria, ConvocatoriaReunion convocatoriaReunion,
      TipoEvaluacion tipoEvaluacion, Boolean esRevMinima) {

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(esRevMinima);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }
}