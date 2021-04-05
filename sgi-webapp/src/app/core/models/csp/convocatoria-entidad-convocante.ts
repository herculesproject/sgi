import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IPrograma } from './programa';

export interface IConvocatoriaEntidadConvocante {
  id: number;
  convocatoriaId: number;
  entidad: IEmpresaEconomica;
  programa: IPrograma;
}
