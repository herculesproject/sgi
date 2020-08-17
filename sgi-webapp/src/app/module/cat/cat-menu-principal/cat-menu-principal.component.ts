import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { CAT_ROUTE_NAMES } from '../cat-route-names';
import { LayoutService } from '@core/services/layout.service';

@Component({
  selector: 'app-cat-menu-principal',
  templateUrl: './cat-menu-principal.component.html',
  styleUrls: ['./cat-menu-principal.component.scss'],
})
export class CatMenuPrincipalComponent implements OnInit {
  CAT_ROUTE_NAMES = CAT_ROUTE_NAMES;

  element: HTMLElement;

  mostrarNombreMenu: boolean = true;
  panelDesplegado: boolean;

  constructor(private logger: NGXLogger, private layoutService: LayoutService) {
  }

  ngOnInit(): void {
    this.logger.debug(CatMenuPrincipalComponent.name, 'ngOnInit()', 'start');

    this.layoutService.getSeleccionarSidenavAbierto().subscribe(
      (indiceTabSeleccionado: number) => {
        this.panelDesplegado = true;
        if (indiceTabSeleccionado === 1) {
          this.mostrarNombreMenu = true;
          this.panelDesplegado = false;
        } else {
          this.mostrarNombreMenu = false;
          this.panelDesplegado = false;
        }
      });

    this.logger.debug(CatMenuPrincipalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Activamos el acordeon del elemento para poder hacerle
   * un stopProgation para que el servicio al encoger menu lo cierre
   * @param $event
   */
  activarAcordeon($event) {
    this.panelDesplegado = !this.panelDesplegado;
    $event.stopPropagation();
  }


}
