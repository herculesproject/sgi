import { FormacionEspecifica } from '../../../models/eti/formacion-especifica';
import { TipoTarea } from '../../../models/eti/tipo-tarea';
import { IEquipoTrabajoResponse } from '../equipo-trabajo/equipo-trabajo-response';
import { IMemoriaResponse } from '../memoria/memoria-response';

export interface ITareaResponse {
  /** Id */
  id: number;
  /** Equipo de trabajo. */
  equipoTrabajo: IEquipoTrabajoResponse;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** Formación específica */
  formacionEspecifica: FormacionEspecifica;
  /** Tarea */
  tarea: string;
  /** Formación */
  formacion: string;
  /** Organismo */
  organismo: string;
  /** Año */
  anio: number;
  /** Tipo tarea */
  tipoTarea: TipoTarea;
}
