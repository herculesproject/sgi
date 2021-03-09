import { IConvocatoria } from '../convocatoria';

export interface IConvocatoriaEntidadGestoraBackend {
  id: number;
  convocatoria: IConvocatoria;
  entidadRef: string;
}
