import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IRolProyecto } from './rol-proyecto';

export interface IProyectoEquipo {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** personaRef */
  persona: IPersona;
  /** rolProyecto */
  rolProyecto: IRolProyecto;
  /** fechaInicio */
  fechaInicio: DateTime;
  /** fechaFin */
  fechaFin: DateTime;
  /** horas dedicacion */
  horasDedicacion: number;
}
