import { DateTime } from 'luxon';
import { IProyectoSge } from '../sge/proyecto-sge';
import { IGrupoEspecialInvestigacion } from './grupo-especial-investigacion';
import { IGrupoTipo } from './grupo-tipo';
import { ISolicitud } from './solicitud';

export interface IGrupo {
  id: number;
  nombre: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  proyectoSge: IProyectoSge;
  solicitud: ISolicitud;
  codigo: string;
  tipo: IGrupoTipo;
  especialInvestigacion: IGrupoEspecialInvestigacion;
  activo: boolean;
}
