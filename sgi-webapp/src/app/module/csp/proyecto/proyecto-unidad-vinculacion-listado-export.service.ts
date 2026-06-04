import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoUnidadVinculacion } from '@core/models/csp/proyecto-unidad-vinculacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoUnidadVinculacionListado } from './proyecto-formulario/proyecto-unidades-vinculacion/proyecto-unidades-vinculacion.fragment';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const UNIDAD_KEY = marker('csp.proyecto-unidad-vinculacion');
const TIPO_UNIDAD_KEY = marker('csp.proyecto-unidad-vinculacion.tipo-unidad');
const UNIDAD_FIELD_KEY = marker('csp.proyecto-unidad-vinculacion.unidad');

const TIPO_UNIDAD_FIELD = 'tipoUnidad';
const UNIDAD_FIELD = 'unidad';

@Injectable()
export class ProyectoUnidadVinculacionListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  private readonly unidadesVinculacionCache = new Map<string, IUnidadVinculacion>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private proyectoService: ProyectoService,
    private unidadVinculacionService: UnidadVinculacionService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.unidadesVinculacionCache.clear();
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findUnidadesVinculacion(proyectoData?.id).pipe(
      switchMap(response => this.populateUnidadesVinculacionConTipo(response.items)),
      map(unidades => {
        proyectoData.unidadesVinculacion = unidades;
        return proyectoData;
      })
    );
  }

  /**
   * Construye el listado de unidades de vinculación del proyecto resolviendo, en lote contra el SGO, tanto el detalle
   * de cada unidad asignada (`unidadVinculacion`) como su elemento raíz en la jerarquía (`tipoUnidad`).
   */
  private populateUnidadesVinculacionConTipo(items: IProyectoUnidadVinculacion[]): Observable<IProyectoUnidadVinculacionListado[]> {
    if (!items?.length) {
      return of([]);
    }

    const listado: IProyectoUnidadVinculacionListado[] = items.map(item => ({
      id: item.id,
      proyectoId: item.proyectoId,
      unidadVinculacion: item.unidadVinculacion,
      tipoUnidad: undefined
    }));

    const ids = listado.map(item => item.unidadVinculacion?.id).filter(id => !!id);
    return this.fetchUnidadesNotCached(ids).pipe(
      switchMap(() => {
        listado.forEach(item => {
          if (item.unidadVinculacion?.id) {
            item.unidadVinculacion = this.unidadesVinculacionCache.get(item.unidadVinculacion.id) ?? item.unidadVinculacion;
          }
        });

        return this.populateTipoUnidad(listado);
      })
    );
  }

  /**
   * Asigna a cada item su `tipoUnidad` (la unidad raíz de la jerarquía) subiendo por la cadena de `predecesorId`.
   *
   * @param items listado al que asignar el `tipoUnidad`.
   * @param requested ids ya solicitados al SGO; evita re-pedir predecesores inexistentes (datos inconsistentes).
   */
  private populateTipoUnidad(
    items: IProyectoUnidadVinculacionListado[],
    requested: Set<string> = new Set()
  ): Observable<IProyectoUnidadVinculacionListado[]> {
    const predecesorIds = [...new Set(
      [...this.unidadesVinculacionCache.values()]
        .map(unidad => unidad.predecesorId)
        .filter(id => !!id && !this.unidadesVinculacionCache.has(id) && !requested.has(id))
    )];

    if (!predecesorIds.length) {
      items.forEach(item => {
        item.tipoUnidad = this.getRootUnidadVinculacion(item.unidadVinculacion);
      });

      return of(items);
    }

    predecesorIds.forEach(id => requested.add(id));
    return this.fetchUnidadesNotCached(predecesorIds).pipe(
      switchMap(() => this.populateTipoUnidad(items, requested))
    );
  }

  private fetchUnidadesNotCached(ids: string[]): Observable<void> {
    const missingIds = [...new Set(ids.filter(id => !this.unidadesVinculacionCache.has(id)))];
    if (!missingIds.length) {
      return of(void 0);
    }

    return this.unidadVinculacionService.findAllByIdIn(missingIds, false).pipe(
      map(response => {
        response.items.forEach(u => this.unidadesVinculacionCache.set(u.id, u));
      })
    );
  }

  private getRootUnidadVinculacion(unidadVinculacion: IUnidadVinculacion): IUnidadVinculacion {
    let current = unidadVinculacion;
    while (current?.predecesorId && this.unidadesVinculacionCache.has(current.predecesorId)) {
      current = this.unidadesVinculacionCache.get(current.predecesorId);
    }

    return current;
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsUnidadExcel(proyectos);
    }
  }

  private getColumnsUnidadExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumUnidades = Math.max(...proyectos.map(p => p.unidadesVinculacion ? p.unidadesVinculacion.length : 0));
    const titleUnidadesVinculacion = this.translate.instant(UNIDAD_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumUnidades; i++) {
      const idx: string = String(i + 1);

      columns.push({
        name: TIPO_UNIDAD_FIELD + idx,
        title: `${titleUnidadesVinculacion}: ${this.translate.instant(TIPO_UNIDAD_KEY)} ${idx}`,
        type: ColumnType.STRING,
      });

      columns.push({
        name: UNIDAD_FIELD + idx,
        title: `${titleUnidadesVinculacion}: ${this.translate.instant(UNIDAD_FIELD_KEY)} ${idx}`,
        type: ColumnType.STRING,
      });
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];
    const elementsRow: any[] = [];

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumUnidades = Math.max(...proyectos.map(p => p.unidadesVinculacion ? p.unidadesVinculacion.length : 0));
      for (let i = 0; i < maxNumUnidades; i++) {
        const unidad = proyecto.unidadesVinculacion ? proyecto.unidadesVinculacion[i] ?? null : null;
        this.fillRowExcel(elementsRow, unidad);
      }
    }
    return elementsRow;
  }

  private fillRowExcel(elementsRow: any[], proyectoUnidadVinculacion: IProyectoUnidadVinculacionListado): void {
    if (proyectoUnidadVinculacion) {
      elementsRow.push(proyectoUnidadVinculacion.tipoUnidad?.nombre ?? '');
      elementsRow.push(proyectoUnidadVinculacion.unidadVinculacion?.nombre ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }

}
