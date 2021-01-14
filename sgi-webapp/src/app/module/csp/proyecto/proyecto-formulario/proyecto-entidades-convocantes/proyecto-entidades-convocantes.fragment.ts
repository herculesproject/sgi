import { OnDestroy } from '@angular/core';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { Fragment } from '@core/services/action-service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';
import { ProyectoService } from '@core/services/csp/proyecto.service';

export class ProyectoEntidadesConvocantesFragment extends Fragment implements OnDestroy {
  private deleted: Set<number> = new Set<number>();
  private edited: Set<number> = new Set<number>();
  proyectoEntidadConvocantes$ = new BehaviorSubject<IProyectoEntidadConvocante[]>([]);
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private empresaEconomicaService: EmpresaEconomicaService,
  ) {
    super(key);
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `onInitialize()`, 'start');
    if (this.getKey()) {
      const subscription = this.proyectoService.findAllEntidadConvocantes(this.getKey() as number).pipe(
        map((response) => response.items),
        mergeMap(proyectoEntidadConvocanteData => {
          return from(proyectoEntidadConvocanteData).pipe(
            mergeMap((data) => {
              return this.loadEmpresaEconomica(data);
            })
          );
        }),
      ).subscribe((proyectoEntidadConvocanteData) => {
        const current = this.proyectoEntidadConvocantes$.value;
        current.push(proyectoEntidadConvocanteData);
        this.proyectoEntidadConvocantes$.next(current);
        this.logger.debug(ProyectoEntidadesConvocantesFragment.name, `onInitialize()`, 'end');
      });
      this.subscriptions.push(subscription);
    }
  }

  private loadEmpresaEconomica(data: IProyectoEntidadConvocante): Observable<IProyectoEntidadConvocante> {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, `loadEmpresaEconomica()`, 'start');
    const entidadRef = data.entidad.personaRef;
    return this.empresaEconomicaService.findById(entidadRef).pipe(
      map(empresaEconomica => {
        data.entidad = empresaEconomica;
        return data;
      }),
      catchError(() => of(data)),
      tap(() => this.logger.debug(ProyectoEntidadesConvocantesFragment.name, `loadEmpresaEconomica()`, 'end'))
    );
  }

  private getSecondLevelPrograma(programa: IPrograma): IPrograma {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `getSecondLevelPrograma(programa: ${programa})`, 'start');
    if (programa?.padre?.padre) {
      this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
        `getSecondLevelPrograma(programa: ${programa})`, 'end');
      return this.getSecondLevelPrograma(programa.padre);
    }
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `getSecondLevelPrograma(programa: ${programa})`, 'end');
    return programa;
  }

  public deleteProyectoEntidadConvocante(proyectoEntidadConvocante: IProyectoEntidadConvocante) {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `deleteProyectoEntidadConvocante(data: ${proyectoEntidadConvocante})`, 'start');

    if (proyectoEntidadConvocante.id) {
      this.deleted.add(proyectoEntidadConvocante.id);
    }

    const current = this.proyectoEntidadConvocantes$.value;
    const index = current.findIndex(value => value === proyectoEntidadConvocante);
    current.splice(index, 1);
    this.proyectoEntidadConvocantes$.next(current);

    this.setChanges(true);

    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `deleteProyectoEntidadConvocante(data: ${proyectoEntidadConvocante})`, 'end');
  }

  public updateProyectoEntidadConvocante(data: IProyectoEntidadConvocante) {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `updateProyectoEntidadConvocante(data: ${data})`, 'start');

    let current = this.proyectoEntidadConvocantes$.value;
    current = current.map((proyectoEntidadConvocante) => {
      if (proyectoEntidadConvocante.id === data.id) {
        // Creamos un nuevo ProyectoEntidadConvocante para forzar la actualización de la fila
        // (de otro modo el pipe "proyectoEntidadConvocantePlan" no se re-evalúa)
        const newProyectoEntidadConvocante = { ...data };
        if (data.id) {
          this.edited.add(data.id);
        }
        return newProyectoEntidadConvocante;
      }
      return proyectoEntidadConvocante;
    });

    this.proyectoEntidadConvocantes$.next(current);
    this.setChanges(true);

    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `updateProyectoEntidadConvocante(wrapper: ${data})`, 'end');
  }

  public addProyectoEntidadConvocante(data: IProyectoEntidadConvocante) {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `addProyectoEntidadConvocante(entidadConvocante: ${data})`, 'start');
    const current: IProyectoEntidadConvocante[] = this.proyectoEntidadConvocantes$.value;
    current.push(data);
    this.proyectoEntidadConvocantes$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `addProyectoEntidadConvocante(entidadConvocante: ${data})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `saveOrUpdate()`, 'start');
    return merge(
      this.deleteProyectoEntidadConvocantes(),
      this.updateProyectoEntidadConvocantes(),
      this.createProyectoEntidadConvocantes()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private deleteProyectoEntidadConvocantes(): Observable<void> {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `deleteProyectoEntidadConvocantes()`, 'start');
    if (this.deleted.size === 0) {
      this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
        `deleteProyectoEntidadConvocantes()`, 'end');
      return of(void 0);
    }
    return from(this.deleted).pipe(
      mergeMap((id) => {
        return this.proyectoService.deleteEntidadConvocanteById(this.getKey() as number, id)
          .pipe(
            tap(() => {
              this.deleted.delete(id);
            }),
            tap(() => this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
              `deleteProyectoEntidadConvocantes()`, 'end'))
          );
      })
    );
  }

  private updateProyectoEntidadConvocantes(): Observable<void> {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `updateProyectoEntidadConvocantes()`, 'start');
    const editedEntidades = this.proyectoEntidadConvocantes$.value.filter((value) => value.id && this.edited.has(value.id));
    if (editedEntidades.length === 0) {
      this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
        `updateProyectoEntidadConvocantes()`, 'end');
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((data) => {
        return this.proyectoService.setEntidadConvocantePrograma(this.getKey() as number,
          data.id, data.programa).pipe(
            map((updatedEntidad) => {
              this.edited.delete(updatedEntidad.id);
              let current: IProyectoEntidadConvocante[] = this.proyectoEntidadConvocantes$.value;
              current = current.map((proyectoEntidadConvocante) =>
                proyectoEntidadConvocante.id === updatedEntidad.id ? updatedEntidad : proyectoEntidadConvocante);
              this.proyectoEntidadConvocantes$.next(current);
            }),
            tap(() => this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
              `updateProyectoEntidadConvocantes()`, 'end')
            )
          );
      })
    );
  }

  private createProyectoEntidadConvocantes(): Observable<void> {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
      `createProyectoEntidadConvocantes()`, 'start');
    const createdEntidades = this.proyectoEntidadConvocantes$.value.filter((value) => !value.id);
    if (createdEntidades.length === 0) {
      this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
        `createProyectoEntidadConvocantes()`, 'end');
      return of(void 0);
    }
    return from(createdEntidades).pipe(
      mergeMap((data) => {
        return this.proyectoService.createEntidadConvocante(this.getKey() as number, data).pipe(
          map((createdEntidad) => {
            let current: IProyectoEntidadConvocante[] = this.proyectoEntidadConvocantes$.value;
            current = current.map((proyectoEntidadConvocante) =>
              proyectoEntidadConvocante === data ? createdEntidad : proyectoEntidadConvocante);
            this.proyectoEntidadConvocantes$.next(current);
          }),
          tap(() => this.logger.debug(ProyectoEntidadesConvocantesFragment.name,
            `createProyectoEntidadConvocantes()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const created: boolean = this.proyectoEntidadConvocantes$.value.some((proyectoEntidadConvocate) => !proyectoEntidadConvocate.id);
    this.logger.debug(ProyectoEntidadesConvocantesFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.deleted.size > 0 || this.edited.size > 0 || created);
  }
}
