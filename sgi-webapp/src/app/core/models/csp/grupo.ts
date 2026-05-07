import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IProyectoSge } from '../sge/proyecto-sge';
import { ISolicitud } from './solicitud';
import { ITipoGrupo } from './tipo-grupo';

export interface IGrupo {
  id: number;
  acronimo: string;
  nombre: I18nFieldValue[];
  fechaInicio: DateTime;
  fechaFin: DateTime;
  email: string;
  proyectoSge: IProyectoSge;
  solicitud: ISolicitud;
  codigo: string;
  tipoGrupo: ITipoGrupo;
  especialInvestigacion: boolean;
  departamentoOrigenRef: string;
  resumen: I18nFieldValue[];
  activo: boolean;
}
