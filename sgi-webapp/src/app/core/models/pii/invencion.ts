import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IProyecto } from '../csp/proyecto';
import { ITipoProteccion } from './tipo-proteccion';

export interface IInvencion {
  id: number;
  titulo: I18nFieldValue[];
  fechaComunicacion: DateTime;
  descripcion: string;
  comentarios: string;
  proyecto: IProyecto;
  tipoProteccion: ITipoProteccion;
  activo: boolean;
}
