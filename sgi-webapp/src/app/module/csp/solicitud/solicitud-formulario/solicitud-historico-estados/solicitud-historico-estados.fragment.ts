import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { map } from 'rxjs/operators';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';

export class SolicitudHistoricoEstadosFragment extends Fragment {

  historicoEstado$: BehaviorSubject<StatusWrapper<IEstadoSolicitud>[]>;

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService
  ) {
    super(key);
    this.logger.debug(SolicitudHistoricoEstadosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.historicoEstado$ = new BehaviorSubject<StatusWrapper<IEstadoSolicitud>[]>([]);
    this.logger.debug(SolicitudHistoricoEstadosFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudHistoricoEstadosFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.solicitudService.findEstadoSolicitud(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((historicoEstados) => {
        this.historicoEstado$.next(historicoEstados.map(
          historicoEstado => new StatusWrapper<IEstadoSolicitud>(historicoEstado))
        );
        this.logger.debug(SolicitudHistoricoEstadosFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }

}
