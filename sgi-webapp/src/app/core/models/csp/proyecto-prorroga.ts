import { IProyecto } from './proyecto';

export enum TipoProrrogaEnum {
  TIEMPO = 'Tiempo',
  IMPORTE = 'Importe',
  TIEMPO_IMPORTE = 'Tiempo e importe'
}

export interface IProyectoProrroga {
  id: number;
  proyecto: IProyecto;
  numProrroga: number;
  fechaConcesion: Date;
  tipoProrroga: TipoProrrogaEnum;
  fechaFin: Date;
  importe: number;
  observaciones: string;
}