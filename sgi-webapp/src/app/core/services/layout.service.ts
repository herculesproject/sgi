import { Injectable } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class LayoutService {

  // Toogle para abrir o cerrar acordeones
  private isMenuAbierto$ = new BehaviorSubject<boolean>(null);

  // Menu abierto
  private sidenavActivo$ = new Subject<number>();

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

  /**
   * Marca la tab del panel lateral correspondiente al indice como seleccionado y emite el cambio de tab seleccionada.
   *
   * @param indice indice del tab seleccionado.
   */
  seleccionarSidenavAbierto(indice: number): void {
    this.logger.debug(LayoutService.name, 'seleccionarTab(indice: number)', 'start');
    this.sidenavActivo$.next(indice);
    this.logger.debug(LayoutService.name, 'seleccionarTab(indice: number)', 'end');
  }

  /**
   * Devuelve el observable que notifica de los cambios de tab seleccionda en el panel lateral.
   */
  getSeleccionarSidenavAbierto(): Observable<number> {
    this.logger.debug(LayoutService.name, 'getTabPanelLateralSeleccionada()', 'start');
    this.logger.debug(LayoutService.name, 'getTabPanelLateralSeleccionada()', 'end');
    return this.sidenavActivo$;
  }


}
