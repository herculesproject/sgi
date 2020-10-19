package org.crue.hercules.sgdoc.repository.specification;

import java.util.List;

import org.crue.hercules.sgdoc.model.DocumentoEntity;
import org.crue.hercules.sgdoc.model.DocumentoEntity_;
import org.springframework.data.jpa.domain.Specification;

public class DocumentoSpecifications {

  public static Specification<DocumentoEntity> byDocumentoRefs(List<String> documentoRefs) {
    return (root, query, cb) -> {
      return root.get(DocumentoEntity_.DOCUMENTO_REF).in(documentoRefs);
    };
  }

}