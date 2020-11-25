import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { tap, map, takeLast, mergeMap } from 'rxjs/operators';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { ConvocatoriaEnlaceService } from '@core/services/csp/convocatoria-enlace.service';
import { IConvocatoria } from '@core/models/csp/convocatoria';

export class ConvocatoriaEnlaceFragment extends Fragment {
  enlace$ = new BehaviorSubject<StatusWrapper<IConvocatoriaEnlace>[]>([]);
  private enlaceEliminados: StatusWrapper<IConvocatoriaEnlace>[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaEnlaceService: ConvocatoriaEnlaceService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getEnlaces(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((enlace) => {
        this.enlace$.next(enlace.map(
          enlaces => new StatusWrapper<IConvocatoriaEnlace>(enlaces))
        );
        this.logger.debug(ConvocatoriaEnlaceFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addEnlace(enlace: IConvocatoriaEnlace) {
    this.logger.debug(ConvocatoriaEnlaceFragment.name,
      `addEnlace(addEnlace: ${enlace})`, 'start');
    const wrapped = new StatusWrapper<IConvocatoriaEnlace>(enlace);
    wrapped.setCreated();
    const current = this.enlace$.value;
    current.push(wrapped);
    this.enlace$.next(current);
    this.setChanges(true);
    this.logger.debug(ConvocatoriaEnlaceFragment.name,
      `addEnlace(addEnlace: ${enlace})`, 'end');
  }

  public deleteEnlace(wrapper: StatusWrapper<IConvocatoriaEnlace>) {
    this.logger.debug(ConvocatoriaEnlaceFragment.name,
      `deleteEnlace(wrapper: ${wrapper})`, 'start');
    const current = this.enlace$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IConvocatoriaEnlace>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.enlaceEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.enlace$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ConvocatoriaEnlaceFragment.name,
      `deleteEnlace(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteEnlaces(),
      this.updateEnlaces(),
      this.createEnlaces()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ConvocatoriaEnlaceFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteEnlaces(): Observable<void> {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `deleteEnlaces()`, 'start');
    if (this.enlaceEliminados.length === 0) {
      this.logger.debug(ConvocatoriaEnlaceFragment.name, `deleteEnlaces()`, 'end');
      return of(void 0);
    }
    return from(this.enlaceEliminados).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaEnlaceService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.enlaceEliminados = this.enlaceEliminados.filter(deletedEnlace =>
                deletedEnlace.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ConvocatoriaEnlaceFragment.name,
              `deleteEnlaces()`, 'end'))
          );
      }));
  }

  private createEnlaces(): Observable<void> {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `createEnlaces()`, 'start');
    const createdEnlaces = this.enlace$.value.filter((convocatoriaEnlace) => convocatoriaEnlace.created);
    if (createdEnlaces.length === 0) {
      this.logger.debug(ConvocatoriaEnlaceFragment.name, `createEnlaces()`, 'end');
      return of(void 0);
    }
    createdEnlaces.forEach(
      (wrapper: StatusWrapper<IConvocatoriaEnlace>) => wrapper.value.convocatoria = {
        id: this.getKey(),
        activo: true
      } as IConvocatoria
    );
    return from(createdEnlaces).pipe(
      mergeMap((wrappedEnlaces) => {
        return this.convocatoriaEnlaceService.create(wrappedEnlaces.value).pipe(
          map((updatedEnlaces) => {
            const index = this.enlace$.value.findIndex((currentEnlaces) => currentEnlaces === wrappedEnlaces);
            this.enlace$.value[index] = new StatusWrapper<IConvocatoriaEnlace>(updatedEnlaces);
          }),
          tap(() => this.logger.debug(ConvocatoriaEnlaceFragment.name,
            `createEnlaces()`, 'end'))
        );
      }));
  }

  private updateEnlaces(): Observable<void> {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `updateEnlaces()`, 'start');
    const updateEnlaces = this.enlace$.value.filter((convocatoriaEnlace) => convocatoriaEnlace.edited);
    if (updateEnlaces.length === 0) {
      this.logger.debug(ConvocatoriaEnlaceFragment.name, `updateEnlaces()`, 'end');
      return of(void 0);
    }
    return from(updateEnlaces).pipe(
      mergeMap((wrappedEnlaces) => {
        return this.convocatoriaEnlaceService.update(wrappedEnlaces.value.id, wrappedEnlaces.value).pipe(
          map((updatedEnlaces) => {
            const index = this.enlace$.value.findIndex((currentEnlaces) => currentEnlaces === wrappedEnlaces);
            this.enlace$.value[index] = new StatusWrapper<IConvocatoriaEnlace>(updatedEnlaces);
          }),
          tap(() => this.logger.debug(ConvocatoriaEnlaceFragment.name,
            `updateEnlaces()`, 'end'))
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.enlace$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.enlaceEliminados.length > 0 || touched);
  }


  getSelectedUrls(): string[] {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `getSelectedUrls()`, 'start');
    const urls = this.enlace$.value.map(enlace => enlace.value.url);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `getSelectedUrls()`, 'end');
    return urls;
  }
}
