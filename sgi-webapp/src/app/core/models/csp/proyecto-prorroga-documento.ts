import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento } from './tipos-configuracion';

export interface IProyectoProrrogaDocumento {
  id: number;
  proyectoProrrogaId: number;
  nombre: I18nFieldValue[];
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  visible: boolean;
  comentario: string;
}
