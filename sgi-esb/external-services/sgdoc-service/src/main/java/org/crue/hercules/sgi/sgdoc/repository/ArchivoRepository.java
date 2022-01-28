package org.crue.hercules.sgi.sgdoc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.sgdoc.model.Archivo;
import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchivoRepository extends JpaRepository<Archivo, String>, JpaSpecificationExecutor<Archivo> {

  /**
   * Recupera un archivo por el documentoRef
   * 
   * @param documentoRef Identificador del {@link Documento}
   * @return el {@link Archivo} del {@link Documento}
   */
  Optional<Archivo> findByDocumentoDocumentoRef(String documentoRef);

  /**
   * Elimina un archivo por el documentoRef
   * 
   * @param documentoRef Identificador del {@link Documento}
   */
  void deleteByDocumentoDocumentoRef(String documentoRef);

}
