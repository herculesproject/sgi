import { IEmpresaEconomica } from '../sgp/empresa-economica';

export interface IConvocatoriaEntidadGestora {
  id: number;
  convocatoriaId: number;
  empresaEconomica: IEmpresaEconomica;
}
