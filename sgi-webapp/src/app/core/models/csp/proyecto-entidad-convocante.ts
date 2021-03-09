import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IPrograma } from './programa';

export interface IProyectoEntidadConvocante {
  id: number;
  entidad: IEmpresaEconomica;
  programaConvocatoria: IPrograma;
  programa: IPrograma;
}
