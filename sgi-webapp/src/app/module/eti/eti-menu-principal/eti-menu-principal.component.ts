import { Component, OnInit, ViewChild } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { LayoutService } from '@core/services/layout.service';
import { MatAccordion } from '@angular/material/expansion';
import { ETI_ROUTE_NAMES } from '../eti-route-names';

@Component({
  selector: 'app-eti-menu-principal',
  templateUrl: './eti-menu-principal.component.html',
  styleUrls: ['./eti-menu-principal.component.scss']
})
export class EtiMenuPrincipalComponent implements OnInit {
  ETI_ROUTE_NAMES = ETI_ROUTE_NAMES;
  @ViewChild('accordion', { static: true }) Accordion: MatAccordion;

  element: HTMLElement;

  mostrarNombreMenu: boolean = true;
  panelDesplegado: boolean;

  constructor(private logger: NGXLogger, private layoutService: LayoutService) {
  }

  ngOnInit(): void {
    this.logger.debug(EtiMenuPrincipalComponent.name, 'ngOnInit()', 'start');

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

    this.logger.debug(EtiMenuPrincipalComponent.name, 'ngOnInit()', 'end');
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
