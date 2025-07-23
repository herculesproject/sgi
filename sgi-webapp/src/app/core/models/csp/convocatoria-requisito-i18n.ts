import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { ISexo } from '../sgp/sexo';

export interface IConvocatoriaRequisitoI18n {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Fecha máxima nivel académico */
  fechaMaximaNivelAcademico: DateTime;
  /** Fecha mínima nivel académico */
  fechaMinimaNivelAcademico: DateTime;
  /** Edad máxima */
  edadMaxima: number;
  /** Sexo */
  sexo: ISexo;
  /** Vinculación universidad */
  vinculacionUniversidad: boolean;
  /** Fecha máxima categoria profesional */
  fechaMaximaCategoriaProfesional: DateTime;
  /** Fecha mínima categoria profesional */
  fechaMinimaCategoriaProfesional: DateTime;
  /** Número mínimo proyectos competitivos */
  numMinimoCompetitivos: number;
  /** Número mínimo proyectos NO competitivos */
  numMinimoNoCompetitivos: number;
  /** Número máximo proyectos competitivos activos */
  numMaximoCompetitivosActivos: number;
  /** Número máximo proyectos NO competitivos activos */
  numMaximoNoCompetitivosActivos: number;
  /** Otros requisitos */
  otrosRequisitos: I18nFieldValue[];
}
