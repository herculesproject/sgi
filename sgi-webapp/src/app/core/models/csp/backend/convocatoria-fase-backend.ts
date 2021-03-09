import { IConvocatoria } from '../convocatoria';
import { ITipoFase } from '../tipos-configuracion';

export interface IConvocatoriaFaseBackend {
  id: number;
  convocatoria: IConvocatoria;
  tipoFase: ITipoFase;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
}
