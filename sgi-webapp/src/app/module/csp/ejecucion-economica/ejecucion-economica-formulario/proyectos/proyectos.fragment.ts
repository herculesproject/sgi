import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export class ProyectosFragment extends Fragment {
  proyectosSge$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);

  constructor(
    key: number,
    private proyectoSgeRef: string,
    private service: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const options: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, this.proyectoSgeRef)
      };
      const subscription = this.service.findAll(options).pipe(
        map(response => response.items),
        switchMap(response => {
          const requestsProyecto: Observable<IProyectoProyectoSge>[] = [];
          response.forEach(ejecucionEconomicaListado => {
            requestsProyecto.push(this.proyectoService.findById(ejecucionEconomicaListado.proyecto.id).pipe(
              map(proyecto => {
                ejecucionEconomicaListado.proyecto = proyecto;
                return ejecucionEconomicaListado;
              })
            ));
          });
          return of(response).pipe(
            tap(() => merge(...requestsProyecto).subscribe())
          );
        }),
        switchMap(response => {
          const requestsProyectoSge: Observable<IProyectoProyectoSge>[] = [];
          response.forEach(ejecucionEconomicaListado => {
            requestsProyectoSge.push(this.proyectoSgeService.findById(ejecucionEconomicaListado.proyectoSge.id).pipe(
              map(proyectoSge => {
                ejecucionEconomicaListado.proyectoSge = proyectoSge;
                return ejecucionEconomicaListado;
              })
            ));
          });
          return of(response).pipe(
            tap(() => merge(...requestsProyectoSge).subscribe())
          );
        })
      ).subscribe((proyectoProyectoSge) => {
        this.proyectosSge$.next(proyectoProyectoSge);
      });

      this.subscriptions.push(subscription);
    }
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
