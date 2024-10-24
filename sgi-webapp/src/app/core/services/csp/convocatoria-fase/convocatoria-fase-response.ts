import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';
import { IConvocatoriaFaseAvisoResponse } from './convocatoria-fase-aviso-response';

export interface IConvocatoriaFaseResponse {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFaseResponse;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  aviso1: IConvocatoriaFaseAvisoResponse;
  aviso2: IConvocatoriaFaseAvisoResponse;
}