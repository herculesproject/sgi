package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link InformeFormulario}.
 */
@Repository
public interface InformeFormularioRepository
    extends JpaRepository<InformeFormulario, Long>, JpaSpecificationExecutor<InformeFormulario> {

  /**
   * Devuelve el último informe asociado a la memoria ordenado por la versión desc
   * 
   * @param idMemoria identificador de la {@link Memoria}
   * @return el {@link InformeFormulario}
   */
  InformeFormulario findFirstByFormularioMemoriaMemoriaIdOrderByVersionDesc(Long idMemoria);
}