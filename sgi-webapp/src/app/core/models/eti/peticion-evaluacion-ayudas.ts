import { I18nFieldValue } from '@core/i18n/i18n-field';

/**
 * Textos de ayuda de la solicitud de evaluación. Cada texto es null si la ayuda no se muestra.
 */
export interface IPeticionEvaluacionAyudas {
  fechaInicio: I18nFieldValue[];
  resumen: I18nFieldValue[];
  disenioMetodologico: I18nFieldValue[];
  tareas: I18nFieldValue[];
}
