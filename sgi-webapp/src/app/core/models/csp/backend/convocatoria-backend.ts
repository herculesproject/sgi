import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { ITipoFinalidadResponse } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response';
import { Estado } from '../convocatoria';
import { IModeloEjecucion, ITipoAmbitoGeografico, ITipoRegimenConcurrencia } from '../tipos-configuracion';

export interface IConvocatoriaBackend {
  id: number;
  unidadGestionRef: string;
  modeloEjecucion: IModeloEjecucion;
  codigo: string;
  fechaPublicacion: string;
  fechaProvisional: string;
  fechaConcesion: string;
  titulo: string;
  objeto: string;
  observaciones: string;
  finalidad: ITipoFinalidadResponse;
  regimenConcurrencia: ITipoRegimenConcurrencia;
  formularioSolicitud: FormularioSolicitud;
  estado: Estado;
  duracion: number;
  abiertoPlazoPresentacionSolicitud: boolean;
  ambitoGeografico: ITipoAmbitoGeografico;
  clasificacionCVN: ClasificacionCVN;
  activo: boolean;
  excelencia: boolean;
  codigoInterno: string;
  anio: number;
}
