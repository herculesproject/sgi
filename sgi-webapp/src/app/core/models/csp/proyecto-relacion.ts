import { I18nFieldValue } from '@core/i18n/i18n-field';
import { TipoEntidad } from '@core/models/rel/relacion';

/**
 * Entidad relacionada (convocatoria, proyecto, grupo o invención) resuelta
 * con los datos mínimos necesarios para el listado de relaciones.
 */
export interface IRelacionEntidad {
  id: number;
  titulo: I18nFieldValue[];
  /** Código externo del proyecto relacionado (vacío para otros tipos). */
  codigoExterno?: string;
  /** Códigos SGE del proyecto o grupo relacionado (vacío para otros tipos). */
  codigosSge?: string;
}

/**
 * Relación de un proyecto enriquecida en servidor: incluye la entidad
 * relacionada ya resuelta y orientada respecto al proyecto actual.
 */
export interface IProyectoRelacion {
  id: number;
  tipoEntidadRelacionada: TipoEntidad;
  entidadRelacionada: IRelacionEntidad;
  observaciones: I18nFieldValue[];
}
