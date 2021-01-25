import { IProyecto } from "@core/models/csp/proyecto";
import { IProyectoPlazos } from "@core/models/csp/proyecto-plazo";
import { Fragment } from "@core/services/action-service";
import { ProyectoPlazoService } from "@core/services/csp/proyecto-plazo.service";
import { ProyectoService } from "@core/services/csp/proyecto.service";
import { StatusWrapper } from "@core/utils/status-wrapper";
import { NGXLogger } from "ngx-logger";
import { BehaviorSubject, from, merge, Observable, of } from "rxjs";
import { map, mergeMap, takeLast, tap } from "rxjs/operators";

export class ProyectoPlazosFragment extends Fragment {
  plazos$ = new BehaviorSubject<StatusWrapper<IProyectoPlazos>[]>([]);
  private plazosEliminados: StatusWrapper<IProyectoPlazos>[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoPlazoService: ProyectoPlazoService,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(ProyectoPlazosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoPlazosFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoPlazosFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.proyectoService.findPlazosProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((plazos) => {
        this.plazos$.next(plazos.map(
          plazo => new StatusWrapper<IProyectoPlazos>(plazo))
        );
        this.logger.debug(ProyectoPlazosFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  /**
   * Insertamos plazos
   *
   * @param plazo plazo
   */
  public addPlazos(plazo: IProyectoPlazos) {
    this.logger.debug(ProyectoPlazosFragment.name,
      `addPlazos(plazo: ${plazo})`, 'start');
    const wrapped = new StatusWrapper<IProyectoPlazos>(plazo);
    wrapped.setCreated();
    const current = this.plazos$.value;
    current.push(wrapped);
    this.plazos$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ProyectoPlazosFragment.name,
      `addPlazos(plazo: ${plazo})`, 'end');
  }

  public deletePlazo(wrapper: StatusWrapper<IProyectoPlazos>) {
    this.logger.debug(ProyectoPlazosFragment.name,
      `${this.deletePlazo.name}(wrapper: ${wrapper})`, 'start');
    const current = this.plazos$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IProyectoPlazos>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.plazosEliminados.push(current[index]);
      }
      wrapper.setDeleted();
      current.splice(index, 1);
      this.plazos$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoPlazosFragment.name,
      `${this.deletePlazo.name}(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoPlazosFragment.name, 'saveOrUpdate()', 'start');
    return merge(
      this.deletePlazos(),
      this.updatePlazos(),
      this.createPlazos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoPlazosFragment.name, `${this.saveOrUpdate.name}()`, 'end'))
    );
  }

  private deletePlazos(): Observable<void> {
    this.logger.debug(ProyectoPlazosFragment.name, `${this.deletePlazos.name}()`, 'start');
    if (this.plazosEliminados.length === 0) {
      this.logger.debug(ProyectoPlazosFragment.name, `${this.deletePlazos.name}()`, 'end');
      return of(void 0);
    }
    return from(this.plazosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPlazoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.plazosEliminados = this.plazosEliminados.filter(deletedPlazo =>
                deletedPlazo.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ProyectoPlazosFragment.name,
              `${this.deletePlazo.name}()`, 'end'))
          );
      })
    );
  }

  private createPlazos(): Observable<void> {
    this.logger.debug(ProyectoPlazosFragment.name, `${this.createPlazos.name}()`, 'start');
    const createdPlazos = this.plazos$.value.filter((proyectoPlazo) => proyectoPlazo.created);
    if (createdPlazos.length === 0) {
      this.logger.debug(ProyectoPlazosFragment.name, `${this.createPlazos.name}()`, 'end');
      return of(void 0);
    }
    createdPlazos.forEach(
      (wrapper: StatusWrapper<IProyectoPlazos>) => wrapper.value.proyecto = {
        id: this.getKey(),
        activo: true
      } as IProyecto
    );
    return from(createdPlazos).pipe(
      mergeMap((wrappedPlazos) => {
        return this.proyectoPlazoService.create(wrappedPlazos.value).pipe(
          map((result) => {
            const index = this.plazos$.value.findIndex((currentPlazos) => currentPlazos === wrappedPlazos);
            this.plazos$.value[index] = new StatusWrapper<IProyectoPlazos>(result);
          }),
          tap(() => this.logger.debug(ProyectoPlazosFragment.name,
            `${this.createPlazos.name}()`, 'end'))
        );
      })
    );
  }

  private updatePlazos(): Observable<void> {
    this.logger.debug(ProyectoPlazosFragment.name, `${this.updatePlazos.name}()`, 'start');
    const updatePlazos = this.plazos$.value.filter((proyectoPlazo) => proyectoPlazo.edited);
    if (updatePlazos.length === 0) {
      this.logger.debug(ProyectoPlazosFragment.name, `${this.updatePlazos.name}()`, 'end');
      return of(void 0);
    }
    return from(updatePlazos).pipe(
      mergeMap((wrappedPlazos) => {
        return this.proyectoPlazoService.update(wrappedPlazos.value.id, wrappedPlazos.value).pipe(
          map((updatedPlazos) => {
            const index = this.plazos$.value.findIndex((currentPlazos) => currentPlazos === wrappedPlazos);
            this.plazos$.value[index] = new StatusWrapper<IProyectoPlazos>(updatedPlazos);
          }),
          tap(() => this.logger.debug(ProyectoPlazosFragment.name,
            `${this.updatePlazos.name}()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoPlazosFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'start');
    const touched: boolean = this.plazos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoPlazosFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'end');
    return (this.plazosEliminados.length > 0 || touched);
  }
}