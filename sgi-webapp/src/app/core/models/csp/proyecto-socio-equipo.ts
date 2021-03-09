import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IProyectoSocio } from './proyecto-socio';
import { IRolProyecto } from './rol-proyecto';

export interface IProyectoSocioEquipo {
  id: number;
  proyectoSocio: IProyectoSocio;
  rolProyecto: IRolProyecto;
  persona: IPersona;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
