import { IEmpresaEconomica } from '../sgp/empresa-economica';

export interface IProyectoEntidadGestora {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** entidadRef */
  empresaEconomica: IEmpresaEconomica;
}
