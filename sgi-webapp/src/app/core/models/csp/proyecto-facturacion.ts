import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IEstadoValidacionIP } from './estado-validacion-ip';
import { IProyectoProrroga } from './proyecto-prorroga';
import { ITipoFacturacion } from './tipo-facturacion';

export interface IProyectoFacturacion {
  id: number;
  comentario: I18nFieldValue[];
  fechaConformidad: DateTime;
  fechaEmision: DateTime;
  importeBase: number;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  tipoFacturacion: ITipoFacturacion;
  estadoValidacionIP: IEstadoValidacionIP;
  proyectoProrroga: IProyectoProrroga;
  proyectoSgeRef: string
}
