import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { ICodigoEconomicoGasto } from '../sge/codigo-economico-gasto';

export interface IConvocatoriaConceptoGastoCodigoEc {
  id: number;
  convocatoriaConceptoGastoId: number;
  codigoEconomico: ICodigoEconomicoGasto;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: I18nFieldValue[];
}
