import { ITipoPlazosFases } from './tipo-plazos-fases';

export interface IPlazosFases {
  /** Id */
  id: number;

  /** Fecha Inicio */
  fechaInicio: Date;

  /** Plazos */
  tipoPlazosFases: ITipoPlazosFases;

  /** Fecha Fin */
  fechaFin: Date;

  /** Observaciones */
  observaciones: string;

  /** Activo */
  activo: boolean;

}
