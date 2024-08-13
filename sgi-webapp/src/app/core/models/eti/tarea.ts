import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IEquipoTrabajo } from './equipo-trabajo';
import { FormacionEspecifica } from './formacion-especifica';
import { IMemoria } from './memoria';
import { TipoTarea } from './tipo-tarea';

export interface ITarea {
  /** Id */
  id: number;
  /** Equipo de trabajo. */
  equipoTrabajo: IEquipoTrabajo;
  /** Memoria */
  memoria: IMemoria;
  /** Formación específica */
  formacionEspecifica: FormacionEspecifica;
  /** Nombre */
  nombre: I18nFieldValue[];
  /** Formación */
  formacion: I18nFieldValue[];
  /** Organismo */
  organismo: string;
  /** Año */
  anio: number;
  /** Tipo tarea */
  tipoTarea: TipoTarea;
}
