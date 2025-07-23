import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { ITipoAmbitoGeograficoResponse } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico-response';
import { ITipoFinalidadResponse } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response';
import { IEstadoProyectoResponse } from '../../../services/csp/estado-proyecto/estado-proyecto-response';
import { CausaExencion } from '../proyecto';
import { IProyectoIVABackend } from './proyecto-iva-backend';

export interface IProyectoBackend {
  /** Id */
  id: number;
  /** EstadoProyecto */
  estado: IEstadoProyectoResponse;
  /** Titulo */
  titulo: string;
  /** Acronimo */
  acronimo: string;
  /** codigoInterno */
  codigoInterno: string;
  /** codigoExterno */
  codigoExterno: string;
  /** Fecha Inicio */
  fechaInicio: string;
  /** Fecha inicio informada en algun momento */
  fechaInicioStarted: boolean;
  /** Fecha Fin */
  fechaFin: string;
  /** Fecha Fin Definitiva */
  fechaFinDefinitiva: string;
  /** modelo ejecucion */
  modeloEjecucion: IModeloEjecucionResponse;
  /** finalidad */
  finalidad: ITipoFinalidadResponse;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** ambitoGeografico */
  ambitoGeografico: ITipoAmbitoGeograficoResponse;
  /** confidencial */
  confidencial: boolean;
  /** clasificacionCVN */
  clasificacionCVN: ClasificacionCVN;
  /** convocatoriaExterna */
  convocatoriaExterna: string;
  /** coordinado */
  coordinado: boolean;
  /** colaborativo */
  colaborativo: boolean;
  /** excelencia */
  excelencia: boolean;
  /** Id de RolSocio de la Universidad */
  rolUniversidadId: number;
  /** permitePaquetesTrabajo */
  permitePaquetesTrabajo: boolean;
  /** iva */
  iva: IProyectoIVABackend;
  /** IVA deducible */
  ivaDeducible: boolean;
  /** causaExencion */
  causaExencion: CausaExencion;
  /** observaciones */
  observaciones: string;
  /** unidadGestionRef */
  unidadGestionRef: string;
  /** anualidades */
  anualidades: boolean;
  /** activo  */
  activo: boolean;
  /** Tipo de seguimiento */
  tipoSeguimiento: TipoSeguimiento;
  /** Importe presupuesto */
  importePresupuesto: number;
  /** Importe presupuesto Costes Indirectos */
  importePresupuestoCostesIndirectos: number;
  /** Importe concedido */
  importeConcedido: number;
  /** Importe concedido Costes Indirectos */
  importeConcedidoCostesIndirectos: number;
  /** Importe presupuesto socios */
  importePresupuestoSocios: number;
  /** Importe concedido socios */
  importeConcedidoSocios: number;
  /** total Importe presupuesto */
  totalImportePresupuesto: number;
  /** total Importe concedido */
  totalImporteConcedido: number;
  /** Año */
  anio: number;
}
