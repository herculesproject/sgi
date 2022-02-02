import { IEstadoProduccionCientifica } from './estado-produccion-cientifica';

export interface IProduccionCientifica {
  id: number;
  produccionCientificaRef: string;
  epigrafe: string;
  estado: IEstadoProduccionCientifica;
}
