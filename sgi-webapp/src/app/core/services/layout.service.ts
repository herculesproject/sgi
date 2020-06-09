import { Injectable } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BehaviorSubject, Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class LayoutService {

  // Toogle para abrir o cerrar acordeones
  private isMenuAbierto$ = new BehaviorSubject<boolean>(null);

  constructor(private logger: NGXLogger) { }

  /**
   * Modifica el estado del indicador de panel desplegado y emite el cambio.
   *
   * @param value identificador del toggle
   */
  setToogleSidenav(value: boolean): void {
    this.logger.debug(LayoutService.name, 'setToogleSidenav(value: boolean)', 'start');
    this.isMenuAbierto$.next(value);
    this.logger.debug(LayoutService.name, 'setToogleSidenav(value: boolean)', 'start');
  }

  /**
   * Recupera el valor del indicador del toogle menu.
   * @return indicador del toogle menu.
   */
  getToogleSidenav(): Observable<boolean> {
    this.logger.debug(LayoutService.name, 'getToogleSidenav()', 'start');
    this.logger.debug(LayoutService.name, 'getToogleSidenav()', 'start');
    return this.isMenuAbierto$;
  }

}
