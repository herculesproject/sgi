import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class BreadcrumbService {


  constructor(protected logger: NGXLogger) {
    this.logger.debug(BreadcrumbService.name, 'constructor(protected logger: NGXLogger)', 'start');
    this.logger.debug(BreadcrumbService.name, 'constructor(protected logger: NGXLogger)', 'end');
  }

  /**
   * Crea dinamicamente la url del elemento del breadcrumb
   *
   * @param destino Página de la aplicación
   * @param urlParams Parámetros de la url
   */
  crearUrl(destino: string, urlParams: string[]): string {
    this.logger.debug(BreadcrumbService.name, 'crearUrl(destino: string, urls: string[])', 'start');
    let resultado = '';
    for (const aux of urlParams) {
      resultado += aux + '/';
      if (aux === destino) {
        break;
      }
    }
    this.logger.debug(BreadcrumbService.name, 'crearUrl(destino: string, urls: string[])', 'end');
    return resultado;
  }


}
