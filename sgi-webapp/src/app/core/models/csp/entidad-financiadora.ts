import { IEmpresa } from '../sgemp/empresa';
import { IFuenteFinanciacion } from './fuente-financiacion';
import { ITipoFinanciacion } from './tipos-configuracion';

export interface IEntidadFinanciadora {
  /**
   * ID
   */
  id: number;
  /**
   * Empresa
   */
  empresa: IEmpresa;
  /**
   * Fuente de financiación
   */
  fuenteFinanciacion: IFuenteFinanciacion;
  /**
   * Tipo de financiación
   */
  tipoFinanciacion: ITipoFinanciacion;
  /**
   * Porcentaje de financiación
   */
  porcentajeFinanciacion: number;
  /**
   * Importe de financiación
   */
  importeFinanciacion: number;
}
