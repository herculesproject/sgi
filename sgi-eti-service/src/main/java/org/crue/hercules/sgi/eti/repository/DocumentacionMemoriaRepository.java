package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link DocumentacionMemoria}.
 */

@Repository
public interface DocumentacionMemoriaRepository
    extends JpaRepository<DocumentacionMemoria, Long>, JpaSpecificationExecutor<DocumentacionMemoria> {

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria} y un tipo {@link TipoDocumento}
   *
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaId(Long id, Pageable pageable);

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria} y un tipo {@link TipoDocumento}
   *
   * @param id              Id de {@link Memoria}.
   * @param idTipoDocumento Id de {@link TipoDocumento}
   * @param pageable        la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaIdAndTipoDocumentoId(Long id, Long idTipoDocumento, Pageable pageable);

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria} y que no sean de tipo {@link TipoDocumento}
   * Retrospectiva, Seguimiento Anual o Seguimiento Final
   *
   * @param id               Id de {@link Memoria}.
   * @param idTipoDocumentos Lista de {@link TipoDocumento}
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaIdAndTipoDocumentoIdNotIn(Long id, List<Long> idTipoDocumentos,
      Pageable pageable);

}
