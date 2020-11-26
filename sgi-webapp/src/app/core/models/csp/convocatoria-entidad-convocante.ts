import { IConvocatoria } from './convocatoria';
import { IPrograma } from './programa';
import { IEmpresaEconomica } from '../sgp/empresa-economica';

export interface IConvocatoriaEntidadConvocante {
  id: number;
  convocatoria: IConvocatoria;
  entidadRef: string; // TODO: entidad: IEmpresaEconomica;
  entidad: IEmpresaEconomica;
  programa: IPrograma;
}
