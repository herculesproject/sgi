package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomInformeFormularioRepository {
  Page<InformeFormulario> findByMemoria(Long idPeticionEvaluacion, Pageable pageable);
}
