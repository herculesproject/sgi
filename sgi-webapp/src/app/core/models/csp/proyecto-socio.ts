import { DateTime } from 'luxon';
import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IProyecto } from './proyecto';
import { IRolSocio } from './rol-socio';

export interface IProyectoSocio {
  id: number;
  proyecto: IProyecto;
  empresa: IEmpresaEconomica;
  rolSocio: IRolSocio;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  numInvestigadores: number;
  importeConcedido: number;
}
