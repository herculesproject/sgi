import { IProyectoDocumentoBackend } from './proyecto-documento-backend';
import { IProyectoPeriodoSeguimientoDocumentoBackend } from './proyecto-periodo-seguimiento-documento-backend';
import { IProyectoProrrogaDocumentoBackend } from './proyecto-prorroga-documento-backend';
import { ISocioPeriodoJustificacionDocumentoBackend } from './socio-periodo-justificacion-documento-backend';

export interface IDocumentosProyectoBackend {
  proyectoDocumentos: IProyectoDocumentoBackend[];
  socioPeriodoJustificacionDocumentos: ISocioPeriodoJustificacionDocumentoBackend[];
  proyectoPeriodoSeguimientoDocumentos: IProyectoPeriodoSeguimientoDocumentoBackend[];
  prorrogaDocumentos: IProyectoProrrogaDocumentoBackend[];
}
