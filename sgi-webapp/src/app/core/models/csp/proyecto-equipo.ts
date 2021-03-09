import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IProyecto } from './proyecto';
import { IRolProyecto } from './rol-proyecto';

export interface IProyectoEquipo {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyecto;
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
