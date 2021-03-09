import { IConvocatoria } from '../convocatoria';
import { IPrograma } from '../programa';

export interface IConvocatoriaEntidadConvocanteBackend {
  id: number;
  convocatoria: IConvocatoria;
  entidadRef: string;
  programa: IPrograma;
}
