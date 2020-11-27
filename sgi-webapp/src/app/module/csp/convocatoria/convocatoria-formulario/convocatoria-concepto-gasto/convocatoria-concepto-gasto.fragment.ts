import { FormFragment } from '@core/services/action-service';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { tap, map, takeLast, mergeMap } from 'rxjs/operators';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { IsEntityValidator } from '@core/validators/is-entity-validador';


export class ConvocatoriaConceptoGastoFragment extends FormFragment<IConvocatoriaConceptoGasto[]> {
  convocatoriaConceptoGastoPermitido$ = new BehaviorSubject<StatusWrapper<IConvocatoriaConceptoGasto>[]>([]);
  convocatoriaConceptoGastoNoPermitido$ = new BehaviorSubject<StatusWrapper<IConvocatoriaConceptoGasto>[]>([]);
  convocatoriaConceptoGastoEliminados: StatusWrapper<IConvocatoriaConceptoGasto>[] = [];

  constructor(
    private fb: FormBuilder,
    private logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, 'constructor()', 'start');
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `buildFormGroup()`, 'start');
    const fb = this.fb.group({
      porcentajeCosteIndirecto: [null, [Validators.compose(
        [Validators.min(0), Validators.max(100)])]],
      costeIndirecto: [null, [IsEntityValidator.isValid()]]
    });
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `buildFormGroup()`, 'end');
    return fb;
  }

  protected buildPatch(values: IConvocatoriaConceptoGasto[]): { [key: string]: any } {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `buildPatch(values: ${values})`, 'start');
    let porcentajeCosteIndirecto = null;
    let costeIndirecto = null;

    values.forEach(convocatoriaConceptoGasto => {
      if (convocatoriaConceptoGasto.porcentajeCosteIndirecto !== null) {
        costeIndirecto = convocatoriaConceptoGasto.conceptoGasto;
        porcentajeCosteIndirecto = convocatoriaConceptoGasto.porcentajeCosteIndirecto;
      }
    });

    this.convocatoriaConceptoGastoPermitido$.next(values.map(
      convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
    );
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `buildPatch(values: ${values})`, 'end');
    return {
      porcentajeCosteIndirecto,
      costeIndirecto
    };
  }
  protected initializer(key: string | number): Observable<IConvocatoriaConceptoGasto[]> {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, 'initializer()', 'start');
    if (this.getKey()) {

      this.convocatoriaService.getConvocatoriaConceptoGastosNoPermitidos(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((convocatoriaConceptoGasto) => {
        this.convocatoriaConceptoGastoNoPermitido$.next(convocatoriaConceptoGasto.map(
          convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
        );
        this.logger.debug(ConvocatoriaConceptoGastoFragment.name, 'initializer()', 'end');
      });

      return this.convocatoriaService.getConvocatoriaConceptoGastosPermitidos(this.getKey() as number).pipe(
        map((response) => {
          if (response.items) {
            return response.items;
          }
        })
      );
    }
  }

  getValue(): IConvocatoriaConceptoGasto[] {
    throw new Error('Method not implemented');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getConvocatoriaConceptoGastosPermitidos(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((convocatoriaConceptoGasto) => {
        this.convocatoriaConceptoGastoPermitido$.next(convocatoriaConceptoGasto.map(
          convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
        );
      });

      this.convocatoriaService.getConvocatoriaConceptoGastosNoPermitidos(this.getKey() as number).pipe(
        map((response) => {
          if (response.items) {
            return response.items;
          }
          else {
            return [];
          }
        })
      ).subscribe((convocatoriaConceptoGasto) => {
        this.convocatoriaConceptoGastoNoPermitido$.next(convocatoriaConceptoGasto.map(
          convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
        );
        this.logger.debug(ConvocatoriaConceptoGastoFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  addConvocatoriaConceptoGasto(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto) {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
      `addConvocatoriaConceptoGasto(addConvocatoriaConceptoGasto: ${convocatoriaConceptoGasto})`, 'start');
    const wrapped = new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGasto);
    wrapped.setCreated();
    const permitido = wrapped.value.permitido;
    if (permitido) {
      const current = this.convocatoriaConceptoGastoPermitido$.value;
      current.push(wrapped);
      this.convocatoriaConceptoGastoPermitido$.next(current);
    } else {
      const current = this.convocatoriaConceptoGastoNoPermitido$.value;
      current.push(wrapped);
      this.convocatoriaConceptoGastoNoPermitido$.next(current);
    }
    this.setChanges(true);
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
      `addConvocatoriaConceptoGasto(addConvocatoriaConceptoGasto: ${convocatoriaConceptoGasto})`, 'end');
  }

  deleteConvocatoriaConceptoGasto(wrapper: StatusWrapper<IConvocatoriaConceptoGasto>) {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
      `deleteConvocatoriaConceptoGasto(wrapper: ${wrapper})`, 'start');
    const permitido = wrapper.value.permitido;
    if (permitido) {
      const current = this.convocatoriaConceptoGastoPermitido$.value;
      const index = current.findIndex(
        (value: StatusWrapper<IConvocatoriaConceptoGasto>) => value === wrapper
      );
      if (index >= 0) {
        if (!wrapper.created) {
          this.convocatoriaConceptoGastoEliminados.push(current[index]);
        }
        current.splice(index, 1);
        this.convocatoriaConceptoGastoPermitido$.next(current);
        this.setChanges(true);
      }
    } else {
      const current = this.convocatoriaConceptoGastoNoPermitido$.value;
      const index = current.findIndex(
        (value: StatusWrapper<IConvocatoriaConceptoGasto>) => value === wrapper
      );
      if (index >= 0) {
        if (!wrapper.created) {
          this.convocatoriaConceptoGastoEliminados.push(current[index]);
        }
        current.splice(index, 1);
        this.convocatoriaConceptoGastoNoPermitido$.next(current);
        this.setChanges(true);
      }
    }


    this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
      `deleteConvocatoriaConceptoGasto(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `saveOrUpdate()`, 'start');
    this.applyCostesIndirectos();
    return merge(
      this.deleteConvocatoriaConceptoGastos(),
      this.updateConvocatoriaConceptoGastos(),
      this.createConvocatoriaConceptoGastos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteConvocatoriaConceptoGastos(): Observable<void> {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `deleteConvocatoriaConceptoGastos()`, 'start');
    if (this.convocatoriaConceptoGastoEliminados.length === 0) {
      this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `deleteConvocatoriaConceptoGastos()`, 'end');
      return of(void 0);
    }
    return from(this.convocatoriaConceptoGastoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaConceptoGastoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.convocatoriaConceptoGastoEliminados = this.convocatoriaConceptoGastoEliminados.filter(
                deletedConvocatoriaConceptoGasto =>
                  deletedConvocatoriaConceptoGasto.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
              `deleteConvocatoriaConceptoGastos()`, 'end'))
          );
      })
    );
  }

  private createConvocatoriaConceptoGastos(): Observable<void> {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `createConvocatoriaConceptoGastos()`, 'start');
    const createdConvocatoriaConceptoGastos =
      this.convocatoriaConceptoGastoPermitido$.value.filter(
        (convocatoriaConceptoGastoPermitido) => convocatoriaConceptoGastoPermitido.created).concat(
          this.convocatoriaConceptoGastoNoPermitido$.value.filter(
            (convocatoriaConceptoGastoNoPermitido) => convocatoriaConceptoGastoNoPermitido.created)
        );
    if (createdConvocatoriaConceptoGastos.length === 0) {
      this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `createConvocatoriaConceptoGastos()`, 'end');
      return of(void 0);
    }
    createdConvocatoriaConceptoGastos.forEach(
      (wrapper: StatusWrapper<IConvocatoriaConceptoGasto>) => wrapper.value.convocatoria = {
        id: this.getKey(),
        activo: true
      } as IConvocatoria
    );
    return from(createdConvocatoriaConceptoGastos).pipe(
      mergeMap((wrappedConvocatoriaConceptoGastos) => {
        return this.convocatoriaConceptoGastoService.create(wrappedConvocatoriaConceptoGastos.value).pipe(
          map((updatedConvocatoriaConceptoGastos) => {
            const indexPermitido = this.convocatoriaConceptoGastoPermitido$.value.findIndex(
              (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoPermitido$.value[indexPermitido] =
              new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoPermitido$.next(this.convocatoriaConceptoGastoPermitido$.value);

            const indexNoPermitido = this.convocatoriaConceptoGastoNoPermitido$.value.findIndex(
              (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoNoPermitido$.value[indexNoPermitido] =
              new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoNoPermitido$.next(this.convocatoriaConceptoGastoNoPermitido$.value);
          }),
          tap(() => this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
            `createConvocatoriaConceptoGastos()`, 'end'))
        );
      }));
  }

  private updateConvocatoriaConceptoGastos(): Observable<void> {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `updateConvocatoriaConceptoGastos()`, 'start');
    const updateConvocatoriaConceptoGastos = this.convocatoriaConceptoGastoPermitido$.value.filter(
      (convocatoriaConceptoGastoPermitido) => convocatoriaConceptoGastoPermitido.edited).concat(
        this.convocatoriaConceptoGastoNoPermitido$.value.filter(
          (convocatoriaConceptoGastoNoPermitido) => convocatoriaConceptoGastoNoPermitido.edited));
    if (updateConvocatoriaConceptoGastos.length === 0) {
      this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `updateConvocatoriaConceptoGastos()`, 'end');
      return of(void 0);
    }
    return from(updateConvocatoriaConceptoGastos).pipe(
      mergeMap((wrappedConvocatoriaConceptoGastos) => {
        return this.convocatoriaConceptoGastoService.update(
          wrappedConvocatoriaConceptoGastos.value.id, wrappedConvocatoriaConceptoGastos.value).pipe(
            map((updatedConvocatoriaConceptoGastos) => {
              const indexPermitido = this.convocatoriaConceptoGastoPermitido$.value.findIndex(
                (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
              this.convocatoriaConceptoGastoPermitido$.value[indexPermitido] =
                new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);

              const indexNoPermitido = this.convocatoriaConceptoGastoNoPermitido$.value.findIndex(
                (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
              this.convocatoriaConceptoGastoNoPermitido$.value[indexNoPermitido] =
                new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);
            }),
            tap(() => this.logger.debug(ConvocatoriaConceptoGastoFragment.name,
              `updateConvocatoriaConceptoGastos()`, 'end'))
          );
      })
    );
  }

  private applyCostesIndirectos() {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `applyCostesIndirectos()`, 'start');
    if (this.getFormGroup().controls.costeIndirecto.value != null) {
      this.convocatoriaConceptoGastoPermitido$.value.forEach(wrapper => {
        if (wrapper.value.conceptoGasto.id === this.getFormGroup().controls.costeIndirecto.value.id
          && wrapper.value.porcentajeCosteIndirecto !== this.getFormGroup().controls.porcentajeCosteIndirecto.value) {
          wrapper.value.porcentajeCosteIndirecto = this.getFormGroup().controls.porcentajeCosteIndirecto.value;
          if (!wrapper.created) {
            wrapper.setEdited();
          }

          this.convocatoriaConceptoGastoPermitido$.value.filter(wrap =>
            wrap.value.conceptoGasto.id !== wrapper.value.conceptoGasto.id && wrap.value.porcentajeCosteIndirecto !== null)
            .map(
              wrapperBuscado => {
                wrapperBuscado.value.porcentajeCosteIndirecto = null;
                if (!wrapperBuscado.created) {
                  wrapperBuscado.setEdited();
                }
              });
        }
      });
    }
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `applyCostesIndirectos()`, 'end');
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.convocatoriaConceptoGastoPermitido$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ConvocatoriaConceptoGastoFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.convocatoriaConceptoGastoEliminados.length > 0 || touched);
  }

}
