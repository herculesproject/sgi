import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { Fragment } from '@core/services/action-service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { concatMap, map, switchMap, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from '../../../ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export interface IGastoRequerimientoJustificadoTableData extends IGastoRequerimientoJustificacion {
  proyectoSgiId: number;
}

export interface IGastoJustificadoWithProyectoPeriodoJustificacion extends IGastoJustificado {
  proyectoPeriodoJustificacion: IProyectoPeriodoJustificacion;
}

export class SeguimientoJustificacionRequerimientoGastosFragment extends Fragment {
  gastosRequerimientoTableData$ = new BehaviorSubject<StatusWrapper<IGastoRequerimientoJustificadoTableData>[]>([]);
  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];
  private proyectosPeriodosJustificacionLookUp: Map<string, IProyectoPeriodoJustificacion>;

  constructor(
    private readonly requerimientoJustificacion: IRequerimientoJustificacion,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService
  ) {
    super(requerimientoJustificacion?.id);
    this.proyectosPeriodosJustificacionLookUp = new Map();
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    if (this.requerimientoJustificacion?.id) {
      this.subscriptions.push(
        this.getColumns().pipe(
          tap((columns) => {
            this.columns = columns;
            this.displayColumns = [
              'proyectoSgiId',
              'justificacionId',
              ...columns.map(column => column.id),
              'aceptado',
              'importeAceptado',
              'importeRechazado',
              'importeAlegado',
              'acciones'
            ];
          }),
          switchMap(() =>
            forkJoin(
              {
                gastosRequerimiento: this.getGastosRequerimiento$(this.requerimientoJustificacion.id),
                gastosJustificadosWithProyectoPeriodoJustificacion:
                  this.getGastosJustificadosWithProyectoSgi$(this.requerimientoJustificacion.proyectoProyectoSge.proyectoSge.id)
              }).pipe(
                map(({ gastosRequerimiento, gastosJustificadosWithProyectoPeriodoJustificacion }) => {
                  const gastosJustificadosWithProyectoPeriodoJustificacionProcessed =
                    gastosJustificadosWithProyectoPeriodoJustificacion
                      .map(gastoJustificadoWithProyectoPeriodoJustificacion => ({
                        ...gastoJustificadoWithProyectoPeriodoJustificacion,
                        columnas: this.processColumnsValues(gastoJustificadoWithProyectoPeriodoJustificacion.columnas, this.columns)
                      }));
                  return gastosRequerimiento
                    .map(gastoRequerimiento => {
                      const relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed =
                        gastosJustificadosWithProyectoPeriodoJustificacionProcessed
                          .find(gastoJustificadoWithProyectoPeriodoJustificacionProcessed =>
                            gastoRequerimiento.gasto.id === gastoJustificadoWithProyectoPeriodoJustificacionProcessed.id
                          );
                      return new StatusWrapper(
                        this.createGastoRequerimientoJustificadoTableData(
                          gastoRequerimiento, relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed)
                      );
                    });
                })
              )
          )
        ).subscribe(
          (gastosRequerimientoTableData) => {
            this.gastosRequerimientoTableData$.next(gastosRequerimientoTableData);
          }
        ));
    }
  }

  private getColumns(): Observable<IColumnDefinition[]> {
    return this.seguimientoJustificacionService.getColumnas()
      .pipe(
        map(columnas => columnas.map(columna => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
          };
        })
        )
      );
  }

  private getGastosRequerimiento$(requerimientoId: number): Observable<IGastoRequerimientoJustificacion[]> {
    return this.requerimientoJustificacionService.findGastos(requerimientoId)
      .pipe(
        map(response => response.items)
      );
  }

  private getGastosJustificadosWithProyectoSgi$(proyectoSgeId: string): Observable<IGastoJustificadoWithProyectoPeriodoJustificacion[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoSgeId)
    };
    return this.seguimientoJustificacionService.findAll(options)
      .pipe(
        map(({ items }) => items),
        concatMap(gastosJustificados =>
          from(gastosJustificados)
            .pipe(
              concatMap(gastoJustificado => {
                if (this.proyectosPeriodosJustificacionLookUp.has(gastoJustificado.justificacionId)) {
                  return of({
                    ...gastoJustificado,
                    proyectoPeriodoJustificacion: this.proyectosPeriodosJustificacionLookUp.get(gastoJustificado.justificacionId)
                  });
                } else {
                  return this.proyectoPeriodoJustificacionService.findByIdentificadorJustificacion(gastoJustificado.justificacionId)
                    .pipe(
                      map(proyectoPeriodoJustificacion => {
                        this.proyectosPeriodosJustificacionLookUp.set(gastoJustificado.justificacionId, proyectoPeriodoJustificacion);
                        return {
                          ...gastoJustificado,
                          proyectoPeriodoJustificacion
                        } as IGastoJustificadoWithProyectoPeriodoJustificacion;
                      })
                    );
                }
              }),
              toArray()
            )
        )
      );
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

  private createGastoRequerimientoJustificadoTableData(
    gastoRequerimiento: IGastoRequerimientoJustificacion,
    relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed: IGastoJustificadoWithProyectoPeriodoJustificacion)
    : IGastoRequerimientoJustificadoTableData {
    const { proyectoPeriodoJustificacion, ...gastoJustificado } = relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed;
    if (gastoRequerimiento) {
      return {
        ...gastoRequerimiento,
        gasto: gastoJustificado,
        proyectoSgiId: proyectoPeriodoJustificacion?.proyecto?.id
      } as IGastoRequerimientoJustificadoTableData;
    } else {
      return {
        gasto: gastoJustificado,
        proyectoSgiId: proyectoPeriodoJustificacion?.proyecto?.id
      } as IGastoRequerimientoJustificadoTableData;
    }
  }

  getGastosRequerimientoTableData$(): Observable<StatusWrapper<IGastoRequerimientoJustificadoTableData>[]> {
    return this.gastosRequerimientoTableData$.asObservable();
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }
}
