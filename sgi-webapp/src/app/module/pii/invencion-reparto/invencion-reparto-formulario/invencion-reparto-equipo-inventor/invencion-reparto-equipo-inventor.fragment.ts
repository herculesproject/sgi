import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { Fragment } from '@core/services/action-service';
import { InvencionGastoService } from '@core/services/pii/invencion/invencion-gasto/invencion-gasto.service';
import { InvencionIngresoService } from '@core/services/pii/invencion/invencion-ingreso/invencion-ingreso.service';
import { RepartoService } from '@core/services/pii/reparto/reparto.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { GastosInvencionService, TipoOperacion } from '@core/services/sgepii/gastos-invencion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export class InvencionRepartoEquipoInventorFragment extends Fragment {
  private repartoGastos$ = new BehaviorSubject<IRepartoGasto[]>([]);
  private repartoIngresos$ = new BehaviorSubject<IRepartoIngreso[]>([]);
  private gastosInvencion: IDatoEconomico[];
  private ingresosInvencion: IDatoEconomico[];


  displayGastosColumns: string[] = [];
  gastosColumns: IColumnDefinition[] = [];
  displayIngresosColumns: string[] = [];
  ingresosColumns: IColumnDefinition[] = [];

  constructor(
    private readonly invencion: IInvencion,
    private readonly reparto: IReparto,
    private readonly repartoService: RepartoService,
    private readonly gastosInvencionService: GastosInvencionService,
    private readonly solicitudProteccionService: SolicitudProteccionService,
    private readonly ingresosInvencionService: IngresosInvencionService,
    private readonly invencionGastoService: InvencionGastoService,
    private readonly invencionIngresoService: InvencionIngresoService
  ) {
    super(reparto?.id);
  }

  protected onInitialize(): void | Observable<any> {
    this.initializeGastos();
    this.initializeIngresos();
  }

  private initializeGastos(): void {
    const invencionIdQueryParam = this.invencion.id.toString();
    this.subscriptions.push(
      this.getGastosColumns(invencionIdQueryParam).pipe(
        tap((columns) => {
          this.gastosColumns = columns;
          this.displayGastosColumns = [
            ...columns.map(column => column.id),
            'solicitudProteccion',
            'importePendienteDeducir',
            'importeADeducir',
          ];
        }),
        switchMap(() =>
          forkJoin({
            repartoGastos: this.repartoService.findGastos(this.reparto.id),
            gastosInvencion: this.gastosInvencionService.getGastos(invencionIdQueryParam, TipoOperacion.REPARTO)
          })),
        tap(({ gastosInvencion }) => this.proccessGastosInvencion(gastosInvencion)),
        map(({ repartoGastos }) => repartoGastos.items),
        switchMap(repartoGastos => this.fillRepartoGastosAdditionalData$(repartoGastos))
      ).subscribe(repartoGastos =>
        this.repartoGastos$.next(repartoGastos)
      ));
  }

  private getGastosColumns(invencionId: string): Observable<IColumnDefinition[]> {
    return this.gastosInvencionService.getColumnas(invencionId)
      .pipe(
        map(columnas => columnas.map(columna => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
            importeReparto: columna.importeReparto
          };
        })
        )
      );
  }

  private proccessGastosInvencion(gastosInvencion: IDatoEconomico[]) {
    this.gastosInvencion = gastosInvencion.map(gastoInvencion => ({
      ...gastoInvencion,
      columnas: this.processColumnsValues(gastoInvencion.columnas, this.gastosColumns)
    }));
  }

  private fillRepartoGastosAdditionalData$(repartoGastos: IRepartoGasto[]): Observable<IRepartoGasto[]> {
    return from(repartoGastos).pipe(
      mergeMap(gasto => this.fillRepartoGastoAdditionalData$(gasto)),
      toArray()
    );
  }

  private fillRepartoGastoAdditionalData$(repartoGasto: IRepartoGasto): Observable<IRepartoGasto> {
    return this.invencionGastoService.findById(repartoGasto.invencionGasto.id).pipe(
      mergeMap(invencionGasto =>
        invencionGasto.solicitudProteccion?.id ? this.fillRelatedSolicitudProteccion(invencionGasto) : of(invencionGasto)),
      map(invencionGasto => this.fillRelatedGasto(invencionGasto)),
      map(invencionGasto => {
        repartoGasto.invencionGasto = invencionGasto;
        return repartoGasto;
      })
    );
  }

  private fillRelatedSolicitudProteccion(invencionGasto: IInvencionGasto): Observable<IInvencionGasto> {
    return this.solicitudProteccionService.findById(invencionGasto.solicitudProteccion.id).pipe(
      map(solicitudProteccion => {
        invencionGasto.solicitudProteccion = solicitudProteccion;
        return invencionGasto;
      }),
      catchError(() => of(invencionGasto))
    );
  }

  private fillRelatedGasto(invencionGasto: IInvencionGasto): IInvencionGasto {
    // No existe en la API de Murcia el findById del InvencionGasto
    invencionGasto.gasto = this.gastosInvencion.find(gastoInvencion => gastoInvencion.id === invencionGasto.gasto.id);
    return invencionGasto;
  }

  private initializeIngresos(): void {
    this.subscriptions.push(this.getIngresosColumns()
      .pipe(
        tap((columns) => {
          this.ingresosColumns = columns;
          this.displayIngresosColumns = [
            ...columns.map((column) => column.id),
            'importePendienteRepartir',
            'importeARepartir',
          ];
        }),
        switchMap(() =>
          forkJoin({
            repartoIngreso: this.repartoService.findIngresos(this.reparto.id),
            ingresosInvencion: this.ingresosInvencionService.getIngresosByInvencionId(this.invencion.id),
          })
        ),
        tap(({ ingresosInvencion }) => this.proccessIngresosInvencion(ingresosInvencion)),
        map(({ repartoIngreso }) => repartoIngreso.items),
        switchMap(repartoIngreso => this.fillRepartoIngresosAdditionalData$(repartoIngreso))
      )
      .subscribe(repartoIngreso =>
        this.repartoIngresos$.next(repartoIngreso)
      ));
  }

  private getIngresosColumns(): Observable<IColumnDefinition[]> {
    return this.ingresosInvencionService.getColumnas().pipe(
      map((columnas) =>
        columnas.map((columna) => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
            importeReparto: columna.importeReparto
          };
        })
      )
    );
  }

  private proccessIngresosInvencion(ingresosInvencion: IDatoEconomico[]) {
    this.ingresosInvencion = ingresosInvencion.map(
      (ingresoInvencion) => ({
        ...ingresoInvencion,
        columnas: this.processColumnsValues(
          ingresoInvencion.columnas,
          this.ingresosColumns
        ),
      })
    );
  }

  private fillRepartoIngresosAdditionalData$(repartoIngresos: IRepartoIngreso[]): Observable<IRepartoIngreso[]> {
    return from(repartoIngresos).pipe(
      mergeMap(ingreso => this.fillRepartoIngresoAdditionalData$(ingreso)),
      toArray()
    );
  }

  private fillRepartoIngresoAdditionalData$(repartoIngreso: IRepartoIngreso): Observable<IRepartoIngreso> {
    return this.invencionIngresoService.findById(repartoIngreso.invencionIngreso.id).pipe(
      map(invencionIngreso => this.fillRelatedIngreso(invencionIngreso)),
      map(invencionIngreso => {
        repartoIngreso.invencionIngreso = invencionIngreso;
        return repartoIngreso;
      })
    );
  }

  private fillRelatedIngreso(invencionIngreso: IInvencionIngreso): IInvencionIngreso {
    // No existe en la API de Murcia el findById del InvencionIngreso
    invencionIngreso.ingreso = this.ingresosInvencion.find(ingresoInvencion => ingresoInvencion.id === invencionIngreso.ingreso.id);
    return invencionIngreso;
  }

  getRepartoGastos$(): Observable<IRepartoGasto[]> {
    return this.repartoGastos$.asObservable();
  }

  getRepartoIngresos$(): Observable<IRepartoIngreso[]> {
    return this.repartoIngresos$.asObservable();
  }

  saveOrUpdate(): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }

  private processColumnsValues(
    columns: { [name: string]: string | number; },
    columnDefinitions: IColumnDefinition[],
  ): { [name: string]: string | number; } {
    const values = {};
    columnDefinitions.forEach(column => {
      if (column.compute) {
        values[column.id] = columns[column.id] ? Number.parseFloat(columns[column.id] as string) : 0;
      }
      else {
        values[column.id] = columns[column.id];
      }
    });
    return values;
  }
}
