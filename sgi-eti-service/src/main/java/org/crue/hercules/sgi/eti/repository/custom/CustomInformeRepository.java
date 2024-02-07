package org.crue.hercules.sgi.eti.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.eti.dto.InformeOutput;
import org.crue.hercules.sgi.eti.enums.Language;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Informe}.
 */
@Component
public interface CustomInformeRepository {

  /**
   * Devuelve el último informe asociado a la memoria ordenado por la versión desc
   * 
   * @param idMemoria identificador de la {@link Memoria}
   * @param lang      code language
   * @return el {@link Informe}
   */
  InformeOutput findFirstByMemoriaIdAndLangOrderByVersionDesc(Long idMemoria, Language lang);

  Page<InformeOutput> findByMemoriaIdAndLang(Long idMemoria, Language lang, Pageable pageable);

  /**
   * Devuelve el {@link Informe} filtrado por la {@link Memoria} y su tipo de
   * evaluación
   * 
   * @param idMemoria        identificador de la {@link Memoria}
   * @param idTipoEvaluacion identificador del {@link TipoEvaluacion}
   * @param lang             code language
   * @return el {@link Informe}
   */
  InformeOutput findFirstByMemoriaIdAndTipoEvaluacionIdAndLangOrderByVersionDesc(Long idMemoria,
      Long idTipoEvaluacion, Language lang);

}
