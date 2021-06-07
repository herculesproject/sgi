import { IComite } from '@core/models/eti/comite';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { RespuestaService } from '@core/services/eti/respuesta.service';
import { NGXLogger } from 'ngx-logger';
import { MemoriaFormlyFormFragment } from '../../memoria-formly-form.fragment';

export class MemoriaSeguimientoAnualFragment extends MemoriaFormlyFormFragment {

  constructor(
    logger: NGXLogger,
    readonly: boolean,
    key: number,
    comite: IComite,
    formularioService: FormularioService,
    bloqueService: BloqueService,
    apartadoService: ApartadoService,
    respuestaService: RespuestaService,
    memoriaService: MemoriaService,
    evaluacionService: EvaluacionService
  ) {
    super(
      logger,
      key,
      comite,
      readonly,
      TIPO_EVALUACION.SEGUIMIENTO_ANUAL,
      formularioService,
      memoriaService,
      evaluacionService,
      bloqueService,
      respuestaService,
      apartadoService
    );
  }

  protected isEditable(): boolean {
    if (!this.memoria?.estadoActual?.id) {
      return false;
    }
    const estado = this.memoria.estadoActual.id as ESTADO_MEMORIA;
    switch (+estado) {
      case ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL:
      case ESTADO_MEMORIA.FIN_EVALUACION:
        // Un año o más de diferencia entre la fecha actual y la fecha de inicio.
        return this.memoria.peticionEvaluacion.fechaInicio.diffNow('years').years <= -1;
      default:
        return false;
    }
  }

}
