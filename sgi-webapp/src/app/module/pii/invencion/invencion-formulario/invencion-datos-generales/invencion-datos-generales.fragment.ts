import { FormControl, FormGroup, Validators } from "@angular/forms";
import { IInvencion } from "@core/models/pii/invencion";
import { IInvencionSectorAplicacion } from "@core/models/pii/invencion-sector-aplicacion";
import { ISectorAplicacion } from "@core/models/pii/sector-aplicacion";
import { FormFragment } from "@core/services/action-service";
import { ProyectoService } from "@core/services/csp/proyecto.service";
import { InvencionService } from "@core/services/pii/invencion/invencion.service";
import { StatusWrapper } from "@core/utils/status-wrapper";
import { DateTime } from "luxon";
import { NGXLogger } from "ngx-logger";
import { BehaviorSubject, EMPTY, forkJoin, Observable, of } from "rxjs";
import { catchError, concatMap, map, switchMap, takeLast, tap } from "rxjs/operators";

interface IInvencionDatosGeneralesFragmentStatus {
  hasChangesSectoresAplicacion: boolean;
}

const FRAGMENT_STATUS_INITIAL_DATA: IInvencionDatosGeneralesFragmentStatus = {
  hasChangesSectoresAplicacion: false
} as const;

export class InvencionDatosGeneralesFragment extends FormFragment<IInvencion> {

  private sectoresAplicacion$ = new BehaviorSubject<StatusWrapper<IInvencionSectorAplicacion>[]>([]);
  private invencion: IInvencion;
  private fragmentStatus: IInvencionDatosGeneralesFragmentStatus;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly invencionService: InvencionService,
    private readonly proyectoService: ProyectoService,
    private readonly isEditPerm: boolean
  ) {
    super(key, true);
    this.invencion = {} as IInvencion;
    this.invencion.activo = true;
    this.fragmentStatus = FRAGMENT_STATUS_INITIAL_DATA;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      id: new FormControl({ value: '', disabled: true }),
      titulo: new FormControl('', [Validators.maxLength(50)]),
      fechaComunicacion: new FormControl(DateTime.now()),
      descripcion: new FormControl('', [Validators.maxLength(250)]),
      tipoProteccion: new FormControl(null, [Validators.required]),
      subtipoProteccion: new FormControl(null),
      proyecto: new FormControl(null),
      comentarios: new FormControl('', [Validators.maxLength(250)]),
    });

    if (!this.hasEditPerm()) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(invencion: IInvencion): { [key: string]: any; } {
    const result = {
      id: invencion.id,
      titulo: invencion.titulo,
      fechaComunicacion: invencion.fechaComunicacion,
      descripcion: invencion.descripcion,
      tipoProteccion: invencion.tipoProteccion.padre ?? invencion.tipoProteccion,
      subtipoProteccion: invencion.tipoProteccion.padre !== null ? invencion.tipoProteccion : null,
      proyecto: invencion.proyecto,
      comentarios: invencion.comentarios
    };
    this.invencion = invencion;

    return result;
  }

  protected initializer(key: number): Observable<IInvencion> {
    return forkJoin({ invencion: this.invencionService.findById(key), sectoresAplicacion: this.loadSectoresAplicacion(key) }).pipe(
      tap(({ sectoresAplicacion }) => this.sectoresAplicacion$.next(sectoresAplicacion)),
      switchMap(({ invencion }) => {
        if (invencion.proyecto?.id) {
          return this.proyectoService.findById(invencion.proyecto.id).pipe(
            map(proyecto =>
              ({ ...invencion, proyecto: proyecto })
            ));
        } else {
          return of(invencion);
        }
      }),
      catchError((err) => {
        this.logger.error(err);
        return EMPTY;
      })
    );
  }

  private loadSectoresAplicacion(invencionId: number): Observable<StatusWrapper<IInvencionSectorAplicacion>[]> {
    return this.invencionService.findSectoresAplicacion(invencionId).pipe(
      map(invencionSectoresAplicacion => invencionSectoresAplicacion.map(
        invencionSectorAplicacion => new StatusWrapper<IInvencionSectorAplicacion>(invencionSectorAplicacion))
      )
    );
  }

  getValue(): IInvencion {
    const form = this.getFormGroup().value;
    const invencion = this.invencion;
    invencion.titulo = form.titulo;
    invencion.fechaComunicacion = form.fechaComunicacion;
    invencion.descripcion = form.descripcion;
    invencion.tipoProteccion = form.subtipoProteccion ?? form.tipoProteccion;
    invencion.proyecto = form.proyecto;
    invencion.comentarios = form.comentarios;

    return invencion;
  }

  hasEditPerm(): boolean {
    return this.isEditPerm;
  }

  getSectoresAplicacion$(): Observable<StatusWrapper<IInvencionSectorAplicacion>[]> {
    return this.sectoresAplicacion$.asObservable();
  }

  saveOrUpdate(): Observable<string | number | void> {
    const invencion = this.getValue();
    const observable$ = this.isEdit() ? this.update(invencion) : this.create(invencion);

    return observable$.pipe(
      map((result: IInvencion) => {
        this.invencion = result;
        return result?.id;
      })
    );
  }

  private create(invencion: IInvencion): Observable<IInvencion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.createInvencion(invencion))
      );
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart("hasChangesSectoresAplicacion")) {
      cascade = cascade.pipe(
        concatMap((createdInvencion: IInvencion) => this.saveOrUpdateSectoresAplicacion(createdInvencion))
      );
    }
    return cascade;
  }

  private update(invencion: IInvencion): Observable<IInvencion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.updateInvencion(invencion))
      );
    } else {
      cascade = cascade.pipe(
        switchMap(() => of(invencion))
      )
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart("hasChangesSectoresAplicacion")) {
      cascade = cascade.pipe(
        concatMap((updatedInvencion: IInvencion) => this.saveOrUpdateSectoresAplicacion(updatedInvencion))
      );
    }
    return cascade;
  }

  private createInvencion(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.create(invencion).pipe(
      tap((result: IInvencion) => {
        this.invencion = result;
        this.refreshInitialState(true);
        this.setChangesInvencionDatosGeneralesFragment();
      })
    );
  }

  private updateInvencion(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.update(Number(this.getKey()), invencion).pipe(
      tap((result: IInvencion) => {
        this.invencion = result;
      })
    );
  }

  private saveOrUpdateSectoresAplicacion(invencion: IInvencion): Observable<IInvencion> {
    const values = this.sectoresAplicacion$.value.map(wrapper => {
      wrapper.value.invencion = invencion;
      return wrapper.value;
    }
    );

    return this.invencionService.updateSectoresAplicacion(invencion.id, values)
      .pipe(
        takeLast(1),
        tap(() => this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: false })),
        map((results) => {
          this.sectoresAplicacion$.next(
            results.map(value => new StatusWrapper<IInvencionSectorAplicacion>(value)));
        }),
        switchMap(() => of(invencion))
      );
  }

  addSectorAplicacion(sectorAplicacion: ISectorAplicacion) {
    if (sectorAplicacion) {
      const invencionSectorAplicacion = {
        sectorAplicacion
      } as IInvencionSectorAplicacion;
      const wrapped = new StatusWrapper<IInvencionSectorAplicacion>(invencionSectorAplicacion);
      wrapped.setCreated();
      const current = this.sectoresAplicacion$.value;
      current.push(wrapped);
      this.sectoresAplicacion$.next(current);
      this.setComplete(true);
      this.setErrors(false);
      this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: true });
    }
  }

  deleteSectorAplicacion(wrapper: StatusWrapper<IInvencionSectorAplicacion>): void {
    const current = this.sectoresAplicacion$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.sectoresAplicacion$.next(current);
      if (!current.length) {
        this.setComplete(false);
        this.setErrors(true);
      }
      this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: true });
    }
  }

  setChangesInvencionDatosGeneralesFragment(status: Partial<IInvencionDatosGeneralesFragmentStatus> = {}): void {
    this.fragmentStatus = { ...this.fragmentStatus, ...status };
    this.setChanges(this.hasChangesInvecionDatosGeneralesFragment());
  }

  private hasChangesInvecionDatosGeneralesFragment(): boolean {
    return Object.values(this.fragmentStatus).includes(true);
  }

  private hasChangesInvecionDatosGeneralesFragmentPart(key: keyof IInvencionDatosGeneralesFragmentStatus): boolean {
    return this.fragmentStatus[key];
  }
}
