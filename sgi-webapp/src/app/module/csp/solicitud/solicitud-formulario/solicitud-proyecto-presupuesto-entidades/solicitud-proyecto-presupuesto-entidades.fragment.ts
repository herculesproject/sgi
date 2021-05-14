import { FormControl, FormGroup } from '@angular/forms';
import { IEntidad } from '@core/models/csp/entidad';
import { TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoPresupuestoTotales } from '@core/models/csp/solicitud-proyecto-presupuesto-totales';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { BehaviorSubject, from, merge, Observable, of, Subject } from 'rxjs';
import { map, mergeAll, switchMap, takeLast } from 'rxjs/operators';

export interface EntidadFinanciadoraDesglosePresupuesto {
  entidadFinanciadora: IEntidad;
  ajena: boolean;
}

export class SolicitudProyectoPresupuestoEntidadesFragment extends FormFragment<ISolicitudProyectoPresupuestoTotales> {

  entidadesFinanciadoras$ = new BehaviorSubject<EntidadFinanciadoraDesglosePresupuesto[]>([]);
  private readonly solicitudId: number;
  tipoPresupuestoMixto: boolean;
  tipoPresupuesto$: Subject<TipoPresupuesto> = new BehaviorSubject<TipoPresupuesto>(null);

  constructor(
    key: number,
    public readonly convocatoriaId: number,
    private convocatoriaService: ConvocatoriaService,
    private solicitudService: SolicitudService,
    private empresaService: EmpresaService,
    public readonly readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitudId = key;
  }

  protected initializer(key: number): Observable<ISolicitudProyectoPresupuestoTotales> {
    this.tipoPresupuesto$.subscribe(tipoPresupuesto => {
      this.tipoPresupuestoMixto = tipoPresupuesto === TipoPresupuesto.MIXTO;
      this.loadEntidadesFinanciadoras(tipoPresupuesto);
    });

    return this.solicitudService.getSolicitudProyectoPresupuestoTotales(key);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      totalPresupuesto: new FormControl({ value: '', disabled: true }),
      totalPresupuestoConvocatoria: new FormControl({ value: '', disabled: true }),
      totalPresupuestoAjeno: new FormControl({ value: '', disabled: true })
    });

    return form;
  }

  protected buildPatch(solicitudProyectoPresupuestoTotales: ISolicitudProyectoPresupuestoTotales): { [key: string]: any; } {
    const result = {
      totalPresupuesto: solicitudProyectoPresupuestoTotales.importeTotal,
      totalPresupuestoConvocatoria: solicitudProyectoPresupuestoTotales.importeTotalConvocatoria,
      totalPresupuestoAjeno: solicitudProyectoPresupuestoTotales.importeTotalAjeno
    };

    return result;
  }

  getValue(): ISolicitudProyectoPresupuestoTotales {
    return null;
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

  private loadEntidadesFinanciadoras(tipoPresupuesto: TipoPresupuesto): void {
    let entidades: EntidadFinanciadoraDesglosePresupuesto[] = [];
    let entidades$: Observable<EntidadFinanciadoraDesglosePresupuesto[]>;

    switch (tipoPresupuesto) {
      case TipoPresupuesto.INDIVIDUAL:
        entidades$ = merge(
          this.loadEntidadFinanciadoraConvocatoria(this.convocatoriaId),
          this.loadEntidadFinanciadoraSolicitud(this.solicitudId)
        );
        break;
      case TipoPresupuesto.MIXTO:
        entidades$ = merge(
          this.loadEntidadGestoraConvocatoria(this.convocatoriaId),
          this.loadEntidadFinanciadoraSolicitud(this.solicitudId)
        );
        break;
      default:
        entidades$ = of([]);
        break;
    }

    const subscription = entidades$.subscribe(
      (result) => {
        entidades = entidades.concat(result);
        this.entidadesFinanciadoras$.next(entidades);
      }
    );

    this.subscriptions.push(subscription);
  }

  private loadEntidadFinanciadoraSolicitud(solicitudId: number): Observable<EntidadFinanciadoraDesglosePresupuesto[]> {
    return this.solicitudService.findAllSolicitudProyectoEntidadFinanciadora(solicitudId)
      .pipe(
        map(result => {
          return result.items;
        }),
        switchMap((entidadesFinanciadoras) => {
          if (entidadesFinanciadoras.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(entidadesFinanciadoras)
            .pipe(
              map((entidadesFinanciadora) => {
                return this.empresaService.findById(entidadesFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadesFinanciadora.empresa = empresa;
                      return entidadesFinanciadora;
                    }),
                  );

              }),
              mergeAll(),
              map(() => {
                return entidadesFinanciadoras.map((entidadFinanciadora) => {
                  const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
                    entidadFinanciadora,
                    ajena: true
                  };

                  return entidadFinanciadoraDesglosePresupuesto;
                });
              })
            );
        }),
        takeLast(1)
      );
  }

  private loadEntidadFinanciadoraConvocatoria(convocatoriaId: number): Observable<EntidadFinanciadoraDesglosePresupuesto[]> {
    if (!convocatoriaId) {
      return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
    }

    return this.convocatoriaService.findEntidadesFinanciadoras(convocatoriaId)
      .pipe(
        map(result => result.items),
        switchMap((entidadesFinanciadoras) => {
          if (entidadesFinanciadoras.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(entidadesFinanciadoras)
            .pipe(
              map((entidadesFinanciadora) => {
                return this.empresaService.findById(entidadesFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadesFinanciadora.empresa = empresa;
                      return entidadesFinanciadora;
                    }),
                  );
              }),
              mergeAll(),
              map(() => {
                return entidadesFinanciadoras.map((entidadFinanciadora) => {
                  const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
                    entidadFinanciadora,
                    ajena: false
                  };

                  return entidadFinanciadoraDesglosePresupuesto;
                });
              })
            );
        }),
        takeLast(1)
      );
  }

  private loadEntidadGestoraConvocatoria(convocatoriaId: number): Observable<EntidadFinanciadoraDesglosePresupuesto[]> {
    if (!convocatoriaId) {
      return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
    }

    return this.convocatoriaService.findAllConvocatoriaEntidadGestora(convocatoriaId)
      .pipe(
        map(result => result.items),
        switchMap((entidadesGestoras) => {
          if (entidadesGestoras.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(entidadesGestoras)
            .pipe(
              map((entidadGestora) => {
                return this.empresaService.findById(entidadGestora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadGestora.empresa = empresa;
                      return entidadGestora;
                    }),
                  );
              }),
              mergeAll(),
              map(() => {
                return entidadesGestoras.map((entidadGestora) => {
                  const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
                    entidadFinanciadora: entidadGestora,
                    ajena: false
                  };

                  return entidadFinanciadoraDesglosePresupuesto;
                });
              })
            );
        }),
        takeLast(1)
      );
  }

}
