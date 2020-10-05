package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.custom.CustomMemoriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Memoria}.
 */
@Repository
public interface MemoriaRepository
    extends JpaRepository<Memoria, Long>, JpaSpecificationExecutor<Memoria>, CustomMemoriaRepository {

  /**
   * Recupera la memoria activa con el id recibido por parámetro.
   * 
   * @param idMemoria Id {@link Memoria}
   * @return memoria
   */
  Optional<Memoria> findByIdAndActivoTrue(Long idMemoria);

}