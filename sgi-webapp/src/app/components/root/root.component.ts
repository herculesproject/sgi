import { Component, OnDestroy, OnInit } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

import { trigger, state, style, transition, animate } from '@angular/animations';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { UrlElement } from '@core/models/shared/breadcrumb';
import { BreadcrumbService } from '@core/services/breadcrumb.service';

@Component({
  selector: 'app-root',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.scss'],
  animations: [
    trigger('sidenavMenu', [
      state('close',
        style({
          'min-width': '50px'
        })
      ),
      state('open',
        style({
          'min-width': '200px'
        })
      ),
      transition('close => open', animate('250ms ease-in')),
      transition('open => close', animate('250ms ease-in')),
    ]),
    trigger('contenidoAnimacion', [
      state('close',
        style({
          'margin-left': '65px'
        })
      ),
      state('open',
        style({
          'margin-left': '208px'
        })
      ),
      transition('close => open', animate('250ms ease-in')),
      transition('open => close', animate('250ms ease-in')),
    ])
  ]
})
export class RootComponent implements OnInit, OnDestroy {
  // width pantalla resoluciones pequenas
  screenWidth: number;
  menuToogle: boolean;
  sidenavAbierto = false;
  sidenavWidth = 5;

  // Saber URL actual
  urls: UrlElement[];

  // Dejar fijo o movil mat-sidenav mouseenter mouseleave
  condicionmouseenter: boolean;
  condicionmmouseleave: boolean;

  // Subscription
  subscriptionMenuToogle: Subscription;
  subscription: Subscription;

  constructor(private logger: NGXLogger, private layoutService: LayoutService, private router: Router,
    private breadcrumbService: BreadcrumbService) {
    this.screenWidth = window.innerWidth;
    this.crearSubscripcionUrl();
  }

  ngOnInit(): void {
    this.logger.info('SGI WEBAPP - HERCULES');
    this.logger.debug(RootComponent.name, 'ngOnInit()', 'start');

    this.subscriptionMenuToogle = this.layoutService
      .getToogleSidenav()
      .subscribe((menuToogle) => {
        this.menuToogle = menuToogle;
      });

    this.crearSubscripcionUrl();

    this.logger.debug(RootComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Se subscribe a los cambios en la url para obtener las partes de esta y crear
   * el menú de navegación
   */
  private crearSubscripcionUrl() {
    this.logger.debug(RootComponent.name, 'crearSubscripcionUrl()', 'start');
    this.subscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.urls = [];
      const urls = event.url.split('/');
      urls.forEach(url => {
        // Comprobación para no añadir los id
        if (url === '' || isNaN(Number(url))) {
          this.urls.push({
            nombre: url,
            url: this.breadcrumbService.crearUrl(url, urls)
          });
        }
      });
      // Dejamos fijo (modulo inicial) y para el resto
      // movil el menu mat-sidenav
      if (this.urls.length <= 2) {
        this.menuAbierto();
        this.condicionmouseenter = true;
        this.condicionmmouseleave = false;
      } else {
        this.menuCerrado();
        this.condicionmouseenter = true;
        this.condicionmmouseleave = true;
      }
    });
    this.logger.debug(RootComponent.name, 'crearSubscripcionUrl()', 'end');
  }

  /**
   * Mat-sidenav abierto
   */
  menuAbierto() {
    this.logger.debug(RootComponent.name, 'menuAbierto()', 'start');
    this.sidenavWidth = 16;
    this.sidenavAbierto = true;
    this.layoutService.seleccionarSidenavAbierto(1);
    this.logger.debug(RootComponent.name, 'menuAbierto()', 'end');
  }

  /**
   * Mat-sidenav cerrado
   */
  menuCerrado() {
    this.logger.debug(RootComponent.name, 'menuCerrado()', 'start');
    this.sidenavWidth = 5;
    this.sidenavAbierto = false;
    this.layoutService.seleccionarSidenavAbierto(2);
    this.logger.debug(RootComponent.name, 'menuCerrado()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(RootComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptionMenuToogle?.unsubscribe();
    this.subscription?.unsubscribe();
    this.logger.debug(RootComponent.name, 'ngOnDestroy()', 'end');
  }

}
