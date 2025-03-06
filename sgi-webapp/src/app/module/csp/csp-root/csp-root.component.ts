import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { ConfigService } from '@core/services/cnf/config.service';
import { ConfigService as ConfigServiceCSP } from '@core/services/csp/configuracion/config.service';
import { forkJoin, Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../csp-route-names';

@Component({
  selector: 'sgi-csp-root',
  templateUrl: './csp-root.component.html',
  styleUrls: ['./csp-root.component.scss']
})
export class CspRootComponent implements OnDestroy {

  private subscriptions: Subscription[] = [];

  get CSP_ROUTE_NAMES() {
    return CSP_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  // tslint:disable-next-line: variable-name
  _urlSistemaGestionExterno: string;
  get urlSistemaGestionExterno(): string {
    return this._urlSistemaGestionExterno;
  }

  // tslint:disable-next-line: variable-name
  _nombreSistemaGestionExterno: string;
  get nombreSistemaGestionExterno(): string {
    return this._nombreSistemaGestionExterno;
  }

  _isNotificacionPresupuestoSgeEnabled: boolean;
  get isNotificacionPresupuestoSgeEnabled(): boolean {
    return this._isNotificacionPresupuestoSgeEnabled;
  }

  constructor(
    private configService: ConfigService,
    private configServiceCSP: ConfigServiceCSP
  ) {
    this.subscriptions.push(
      forkJoin(
        {
          nombre: this.configService.getNombreSistemaGestionExterno(),
          url: this.configService.getUrlSistemaGestionExterno(),
          notificacionPresupuestoSge: this.configServiceCSP.isNotificacionPresupuestosSgeEnabled()
        }
      ).subscribe(({ nombre, url, notificacionPresupuestoSge }) => {
        this._nombreSistemaGestionExterno = nombre;
        this._urlSistemaGestionExterno = url;
        this._isNotificacionPresupuestoSgeEnabled = notificacionPresupuestoSge;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
