import { DateTime } from 'luxon';
import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IRolSocio } from './rol-socio';

export interface IProyectoSocio {
  id: number;
  proyectoId: number;
  empresa: IEmpresaEconomica;
  rolSocio: IRolSocio;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  numInvestigadores: number;
  importeConcedido: number;
}
