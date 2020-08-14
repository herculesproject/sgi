import { Component, OnDestroy, OnInit } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  anchoPantalla: number;
  // toogle
  toggleActive: boolean;

  nombreModulo: string;
  private subscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly sidenav: LayoutService,
    private readonly router: Router,
  ) {
    this.anchoPantalla = window.innerWidth;
    this.crearSubscripcionUrl();
  }

  ngOnInit(): void {
    this.logger.debug(HeaderComponent.name, 'ngOnInit()', 'start');
    this.toggleActive = false;
    this.crearSubscripcionUrl();
    this.logger.debug(HeaderComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(HeaderComponent.name, 'ngOnDestroy()', 'start');
    this.subscription?.unsubscribe();
    this.logger.debug(HeaderComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Cambiar el estado del menú acordeón
   */
  toggleSidenav(): void {
    this.logger.debug(HeaderComponent.name, 'toggleSidenav()', 'start');
    this.toggleActive = !this.toggleActive;
    this.sidenav.setToogleSidenav(this.toggleActive);
    this.logger.debug(HeaderComponent.name, 'toggleSidenav()', 'end');
  }

  /**
   * Crea una subscripción para saber cuando cambia la url
   * y mostrar el módulo actual seleccionado
   */
  private crearSubscripcionUrl() {
    this.logger.debug(HeaderComponent.name, 'crearSubscripcionUrl()', 'start');
    this.subscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const identificador = event.url.split('/')[1];
      if (identificador && identificador !== '') {
        this.nombreModulo = 'cabecera.modulo.' + identificador;
      }
    });
    this.logger.debug(HeaderComponent.name, 'crearSubscripcionUrl()', 'end');
  }
}
