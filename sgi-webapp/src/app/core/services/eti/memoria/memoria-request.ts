import { MemoriaTipo } from '@core/models/eti/memoria';

export interface IMemoriaRequest {
  /** Peticion Evaluacion */
  peticionEvaluacionId: number;
  /** Comité */
  comiteId: number;
  /** Título */
  titulo: string;
  /** Referencia persona responsable */
  responsableRef: string;
  /** Tipo Memoria */
  tipo: MemoriaTipo;
}
