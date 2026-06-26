import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { ConfigService } from '@core/services/cnf/config.service';
import { ConfigService as ConfigServiceCSP } from '@core/services/csp/configuracion/config.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { LanguageService } from '@core/services/language.service';
import { forkJoin, Subscription } from 'rxjs';
import { INV_ROUTE_NAMES } from '../inv-route-names';

@Component({
  selector: 'sgi-inv-root',
  templateUrl: './inv-root.component.html',
  styleUrls: ['./inv-root.component.scss']
})
export class InvRootComponent implements OnDestroy {

  get INV_ROUTE_NAMES() {
    return INV_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  // tslint:disable-next-line: variable-name
  _urlSistemaGestionExterno: string;
  get urlSistemaGestionExterno(): string {
    return this.parseI18nValue(this._urlSistemaGestionExterno);
  }

  // tslint:disable-next-line: variable-name
  _nombreSistemaGestionExterno: string;
  get nombreSistemaGestionExterno(): string {
    return this.parseI18nValue(this._nombreSistemaGestionExterno);
  }

  showEvaluaciones = false;
  showSeguimientos = false;
  showActas = false;
  showEjecucionEconomica = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly evaluadorService: EvaluadorService,
    private readonly configService: ConfigService,
    private readonly configServiceCSP: ConfigServiceCSP,
    private readonly languageService: LanguageService
  ) {
    this.subscriptions.push(
      forkJoin(
        {
          hasAssignedActas: this.evaluadorService.hasAssignedActas(),
          hasAssignedEvaluaciones: this.evaluadorService.hasAssignedEvaluaciones(),
          hasAssignedEvaluacionesSeguimiento: this.evaluadorService.hasAssignedEvaluacionesSeguimiento(),
          isInvEjecucionEconomicaVistaIpEnabled: this.configServiceCSP.isInvEjecucionEconomicaVistaIpEnabled(),
          nombre: this.configService.getNombreSistemaGestionExterno(),
          url: this.configService.getUrlSistemaGestionExterno()
        }
      ).subscribe(({
        hasAssignedActas,
        hasAssignedEvaluaciones,
        hasAssignedEvaluacionesSeguimiento,
        isInvEjecucionEconomicaVistaIpEnabled,
        nombre,
        url
      }) => {
        this._nombreSistemaGestionExterno = nombre;
        this._urlSistemaGestionExterno = url;
        this.showActas = hasAssignedActas;
        this.showEjecucionEconomica = isInvEjecucionEconomicaVistaIpEnabled;
        this.showEvaluaciones = hasAssignedEvaluaciones;
        this.showSeguimientos = hasAssignedEvaluacionesSeguimiento;
      })
    );
  }

  private parseI18nValue(value: string): string {
    try {
      const parsed = JSON.parse(value);
      if (Array.isArray(parsed)) {
        return this.languageService.getFieldValue(parsed);
      }
      return value;
    } catch {
      return value;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
