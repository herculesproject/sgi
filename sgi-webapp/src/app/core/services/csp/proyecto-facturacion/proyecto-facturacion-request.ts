import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';

export interface IProyectoFacturacionRequest {
  comentario: I18nFieldValueRequest[];
  fechaConformidad: string;
  fechaEmision: string;
  importeBase: number;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  tipoFacturacionId: number;
  estadoValidacionIP: {
    id: number;
    comentario: string;
    estado: TipoEstadoValidacion;
  }
  proyectoProrrogaId: number;
  proyectoSgeRef: string;
}
