import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';

/**
 * Servicio encargado de la internacionalización de los
 * textos mostrados en los HTMLs.
 *
 * Los ficheros con los textos se encuentran en la carpeta
 * "src/assets/i18n"
 */
@Injectable({
  providedIn: 'root'
})
export class TraductorService {
  /**
   * Constructor del servicio
   *
   * @param logger Log del servicio
   * @param translateService Servicio encargado de traducir
   */
  constructor(
    private logger: NGXLogger,
    public translateService: TranslateService
  ) {
    this.logger.debug(TraductorService.name, 'constructor', 'start');
    this.translateService.setDefaultLang('es');
    this.logger.debug(TraductorService.name, 'constructor', 'end');
  }

  /**
   * Cambia el idioma de la aplicación.
   * Para que funcione debe existir un fichero "idioma.json"
   *
   * @param idioma Idioma seleccionado
   */
  cambiarLenguaje(idioma: string): void {
    this.logger.debug(
      TraductorService.name,
      'cambiarLenguaje(idioma: string)',
      'start'
    );
    this.translateService.use(idioma);
    this.logger.debug(
      TraductorService.name,
      'cambiarLenguaje(idioma: string)',
      'end'
    );
  }

  /**
   * Devuelve una cadena traducida al idioma seleccionado en la aplicación.
   * Este método se usa en las clases TS unicamente.
   *
   * @param identificador Identificador del contenido
   */
  getTexto(identificador: string): string {
    this.logger.debug(
      TraductorService.name,
      'getTexto(identificador: string)',
      'start'
    );
    const palabra = this.translateService.instant(identificador);
    this.logger.debug(
      TraductorService.name,
      'getTexto(identificador: string)',
      'start'
    );
    return palabra;
  }
}
