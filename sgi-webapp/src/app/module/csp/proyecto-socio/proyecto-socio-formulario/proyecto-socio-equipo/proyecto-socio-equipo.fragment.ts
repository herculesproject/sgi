import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { Fragment } from '@core/services/action-service';
import { ProyectoSocioEquipoService } from '@core/services/csp/proyecto-socio-equipo.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoSocioEquipoFragment extends Fragment {
  proyectoSocioEquipos$ = new BehaviorSubject<StatusWrapper<IProyectoSocioEquipo>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoSocioService: ProyectoSocioService,
    private proyectoSocioEquipoService: ProyectoSocioEquipoService,
    private personaFisicaService: PersonaFisicaService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoSocioService.findAllProyectoEquipoSocio(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                const personaRef = element.persona.personaRef;
                return this.personaFisicaService.getInformacionBasica(personaRef).pipe(
                  map(persona => {
                    element.persona = persona;
                    return element;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(response => response.items)
        ).subscribe(
          result => {
            this.proyectoSocioEquipos$.next(
              result.map(proyectoSocioEquipo =>
                new StatusWrapper<IProyectoSocioEquipo>(proyectoSocioEquipo)
              )
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addProyectoSocioEquipo(element: IProyectoSocioEquipo) {
    const wrapped = new StatusWrapper<IProyectoSocioEquipo>(element);
    wrapped.setCreated();
    const current = this.proyectoSocioEquipos$.value;
    current.push(wrapped);
    this.proyectoSocioEquipos$.next(current);
    this.setChanges(true);
  }

  deleteProyectoSocioEquipo(wrapper: StatusWrapper<IProyectoSocioEquipo>) {
    const current = this.proyectoSocioEquipos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.proyectoSocioEquipos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.proyectoSocioEquipos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.proyectoSocioEquipoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.proyectoSocioEquipos$.next(
            results.map(value => new StatusWrapper<IProyectoSocioEquipo>(value)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.proyectoSocioEquipos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }
}
