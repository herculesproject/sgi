import { I18nFieldValue } from '@core/i18n/i18n-field';

/**
 * Textos de ayuda de la solicitud de evaluación. Cada texto es null si la ayuda no se muestra.
 */
export interface IPeticionEvaluacionAyudas {
  disenioMetodologico: I18nFieldValue[];
  fechaInicio: I18nFieldValue[];
  introduccion: I18nFieldValue[];
  resumen: I18nFieldValue[];
  tareas: I18nFieldValue[];
}
