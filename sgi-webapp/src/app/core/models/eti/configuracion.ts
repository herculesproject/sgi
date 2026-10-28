import { marker } from '@biesbjerg/ngx-translate-extract-marker';

/**
 * Visibilidad de un campo configurable.
 */
export enum FieldVisibility {
  /** El campo no se muestra */
  HIDDEN = 'HIDDEN',
  /** El campo se muestra y es opcional */
  OPTIONAL = 'OPTIONAL',
  /** El campo se muestra y es obligatorio */
  REQUIRED = 'REQUIRED'
}

export const FIELD_VISIBILITY_MAP: Map<FieldVisibility, string> = new Map([
  [FieldVisibility.HIDDEN, marker(`eti.field-visibility.HIDDEN`)],
  [FieldVisibility.OPTIONAL, marker(`eti.field-visibility.OPTIONAL`)],
  [FieldVisibility.REQUIRED, marker(`eti.field-visibility.REQUIRED`)]
]);

export interface IConfiguracion {
  /** Id. */
  id: number;
  /** diasArchivadaInactivo */
  diasArchivadaInactivo: number;
  /** mesesArchivadaPendienteCorrecciones */
  mesesArchivadaPendienteCorrecciones: number;
  /** diasLimiteEvaluador */
  diasLimiteEvaluador: number;
  /** diasAvisoRetrospectiva */
  diasAvisoRetrospectiva: number;
  /** duracionProyectoEvaluacion */
  duracionProyectoEvaluacion: number;
}
