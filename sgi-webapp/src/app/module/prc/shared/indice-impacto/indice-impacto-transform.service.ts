import { Injectable } from '@angular/core';
import { Cuartil, IIndiceImpacto, MSG_TIPO_FUENTE_IMPACTO_OTHERS, TipoFuenteImpacto } from '@core/models/prc/indice-impacto';
import { TranslateService } from '@ngx-translate/core';

const Q1_CUARTIL_UPPER_LIMIT = 25;
const Q2_CUARTIL_UPPER_LIMIT = 50;
const Q3_CUARTIL_UPPER_LIMIT = 75;

@Injectable({
  providedIn: 'root'
})
export class IndiceImpactoTransformService {

  constructor(private translateService: TranslateService) { }

  transformCuartil(indiceImpacto: IIndiceImpacto): string {
    if (!indiceImpacto) {
      return '';
    }
    if (indiceImpacto.revista25) {
      return Cuartil.Q1;
    }
    if (indiceImpacto.posicionPublicacion && indiceImpacto.numeroRevistas) {
      const caltulatedCuartil = (indiceImpacto.posicionPublicacion * 100) / indiceImpacto.numeroRevistas;

      if (caltulatedCuartil <= Q1_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q1;
      }
      if (caltulatedCuartil > Q1_CUARTIL_UPPER_LIMIT && caltulatedCuartil <= Q2_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q2;
      }
      if (caltulatedCuartil > Q2_CUARTIL_UPPER_LIMIT && caltulatedCuartil <= Q3_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q3;
      }
      if (caltulatedCuartil > Q3_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q4;
      }
    }
  }

  transformFuenteImpacto(indiceImpacto): string {
    if (!indiceImpacto) {
      return '';
    }
    if (indiceImpacto.tipoFuenteImpacto === TipoFuenteImpacto.OTHERS) {
      const othersMsg = this.translateService.instant(MSG_TIPO_FUENTE_IMPACTO_OTHERS);
      return typeof indiceImpacto.otraFuenteImpacto === 'string' ?
        othersMsg + ' - ' + indiceImpacto.otraFuenteImpacto : othersMsg;
    }
    return indiceImpacto.tipoFuenteImpacto;
  }
}
