import { ConvocatoriaReunion } from './convocatoria-reunion';
import { TipoEstadoActa } from './tipo-estado-acta';
import { Comite } from './comite';
import { Acta } from './acta';

export class ActaListado {
  /** Id */
  id: number;

  /** Fecha evaluación */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Numero */
  numero: number;

  /** Estado Actual */
  estadoActual: TipoEstadoActa;

  /** Número de memorias iniciales */
  numeroMemoriasIniciales: number;

  /** Número de memorias revisiones */
  numeroMemoriasRevisiones: number;

  /** Número de memorias totales */
  numeroMemoriasTotales: number;

  constructor(acta: Acta) {
    this.id = acta.id;
    this.convocatoriaReunion = acta.convocatoriaReunion;
    this.numero = acta.numero;
    this.estadoActual = acta.estadoActual;

  }
}