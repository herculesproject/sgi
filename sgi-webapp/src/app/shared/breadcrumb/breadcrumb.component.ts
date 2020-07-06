import {Component, OnDestroy, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {NavigationEnd, Router} from '@angular/router';
import {TraductorService} from '@core/services/traductor.service';
import {Subscription} from 'rxjs';
import {filter} from 'rxjs/operators';

interface UrlElement {
  nombre: string;
  url: string;
}

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {
  private subscription: Subscription;
  urls: UrlElement[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly router: Router,
    private readonly traductor: TraductorService
  ) {
    this.crearSubscripcionUrl();
  }

  ngOnInit(): void {
    this.logger.debug(BreadcrumbComponent.name, 'ngOnInit()', 'start');
    this.crearSubscripcionUrl();
    this.logger.debug(BreadcrumbComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(BreadcrumbComponent.name, 'ngOnDestroy()', 'start');
    this.subscription?.unsubscribe();
    this.logger.debug(BreadcrumbComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Se subscribe a los cambios en la url para obtener las partes de esta y crear
   * el menú de navegación
   */
  private crearSubscripcionUrl() {
    this.logger.debug(BreadcrumbComponent.name, 'crearSubscripcionUrl()', 'start');
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
            url: this.crearUrl(url, urls)
          });
        }
      });
    });
    this.logger.debug(BreadcrumbComponent.name, 'crearSubscripcionUrl()', 'end');
  }

  /**
   * Crea dinamicamente la url del elemento del breadcrumb
   *
   * @param destino Página de la aplicación
   * @param urlParams Parámetros de la url
   */
  private crearUrl(destino: string, urlParams: string[]): string {
    this.logger.debug(BreadcrumbComponent.name, 'crearUrl(destino: string, urls: string[])', 'start');
    let resultado = '';
    for (const aux of urlParams) {
      resultado += aux + '/';
      if (aux === destino) {
        break;
      }
    }
    this.logger.debug(BreadcrumbComponent.name, 'crearUrl(destino: string, urls: string[])', 'end');
    return resultado;
  }
}
