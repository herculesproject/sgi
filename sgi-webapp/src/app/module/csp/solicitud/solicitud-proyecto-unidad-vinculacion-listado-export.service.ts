import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoUnidadVinculacion } from '@core/models/csp/solicitud-proyecto-unidad-vinculacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudProyectoUnidadVinculacionListado } from './solicitud-formulario/solicitud-proyecto-unidades-vinculacion/solicitud-proyecto-unidades-vinculacion.fragment';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const UNIDAD_KEY = marker('csp.solicitud-proyecto-unidad-vinculacion');
const TIPO_UNIDAD_KEY = marker('csp.solicitud-proyecto-unidad-vinculacion.tipo-unidad');
const UNIDAD_FIELD_KEY = marker('csp.solicitud-proyecto-unidad-vinculacion.unidad');

const TIPO_UNIDAD_FIELD = 'tipoUnidad';
const UNIDAD_FIELD = 'unidad';

@Injectable()
export class SolicitudProyectoUnidadVinculacionListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions> {

  private readonly unidadesVinculacionCache = new Map<string, IUnidadVinculacion>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private readonly solicitudProyectoService: SolicitudProyectoService,
    private readonly unidadVinculacionService: UnidadVinculacionService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.unidadesVinculacionCache.clear();
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findSolicitudProyecto(solicitudData.id).pipe(
      switchMap(solicitudProyecto => {
        if (!solicitudProyecto?.id) {
          return of(solicitudData);
        }

        return this.solicitudProyectoService.findUnidadesVinculacion(solicitudProyecto.id).pipe(
          switchMap(response => this.populateUnidadesVinculacionConTipo(response.items)),
          map(unidades => {
            solicitudData.unidadesVinculacion = unidades;
            return solicitudData;
          })
        );
      })
    );
  }

  /**
   * Construye el listado de unidades de vinculación de la solicitud resolviendo, en lote contra el SGO, tanto el detalle
   * de cada unidad asignada (`unidadVinculacion`) como su elemento raíz en la jerarquía (`tipoUnidad`).
   */
  private populateUnidadesVinculacionConTipo(
    items: ISolicitudProyectoUnidadVinculacion[]
  ): Observable<ISolicitudProyectoUnidadVinculacionListado[]> {
    if (!items?.length) {
      return of([]);
    }

    const listado: ISolicitudProyectoUnidadVinculacionListado[] = items.map(item => ({
      id: item.id,
      solicitudProyectoId: item.solicitudProyectoId,
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
   * @param items     listado al que asignar el `tipoUnidad`.
   * @param requested ids ya solicitados al SGO; evita re-pedir predecesores inexistentes (datos inconsistentes).
   */
  private populateTipoUnidad(
    items: ISolicitudProyectoUnidadVinculacionListado[],
    requested: Set<string> = new Set()
  ): Observable<ISolicitudProyectoUnidadVinculacionListado[]> {
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
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsUnidadExcel(solicitudes);
    }
  }

  private getColumnsUnidadExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumUnidades = Math.max(...solicitudes.map(s => s.unidadesVinculacion ? s.unidadesVinculacion.length : 0));
    const titleUnidadesVinculacion = this.translate.instant(UNIDAD_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumUnidades; i++) {
      const idx: string = String(i + 1);

      columns.push(
        {
          name: TIPO_UNIDAD_FIELD + idx,
          title: `${titleUnidadesVinculacion}: ${this.translate.instant(TIPO_UNIDAD_KEY)} ${idx}`,
          type: ColumnType.STRING,
        },
        {
          name: UNIDAD_FIELD + idx,
          title: `${titleUnidadesVinculacion}: ${this.translate.instant(UNIDAD_FIELD_KEY)} ${idx}`,
          type: ColumnType.STRING,
        }
      );
    }

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];
    const elementsRow: any[] = [];

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumUnidades = Math.max(...solicitudes.map(s => s.unidadesVinculacion ? s.unidadesVinculacion.length : 0));
      for (let i = 0; i < maxNumUnidades; i++) {
        const unidad = solicitud.unidadesVinculacion ? solicitud.unidadesVinculacion[i] ?? null : null;
        this.fillRowExcel(elementsRow, unidad);
      }
    }
    return elementsRow;
  }

  private fillRowExcel(elementsRow: any[], solicitudProyectoUnidadVinculacion: ISolicitudProyectoUnidadVinculacionListado): void {
    if (solicitudProyectoUnidadVinculacion) {
      elementsRow.push(
        solicitudProyectoUnidadVinculacion.tipoUnidad?.nombre ?? '',
        solicitudProyectoUnidadVinculacion.unidadVinculacion?.nombre ?? '');
    } else {
      elementsRow.push(
        '',
        ''
      );
    }
  }

}
