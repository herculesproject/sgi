import { IInformeBackend } from '@core/models/eti/backend/informe-backend';
import { IInforme } from '@core/models/eti/informe';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MEMORIA_CONVERTER } from './memoria.converter';
import { ACTA_CONVERTER } from './acta.converter';

class InformeConverter extends SgiBaseConverter<IInformeBackend, IInforme> {
  toTarget(value: IInformeBackend): IInforme {
    if (!value) {
      return value as unknown as IInforme;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      documentos: value.informeDocumentos,
      version: value.version,
      tipoEvaluacion: value.tipoEvaluacion
    };
  }

  fromTarget(value: IInforme): IInformeBackend {
    if (!value) {
      return value as unknown as IInformeBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      informeDocumentos: value.documentos,
      version: value.version,
      tipoEvaluacion: value.tipoEvaluacion
    };
  }
}


export const INFORME_CONVERTER = new InformeConverter();
