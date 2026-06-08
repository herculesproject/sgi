import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoUnidadVinculacion } from '@core/models/csp/grupo-unidad-vinculacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGrupoUnidadVinculacionListado } from './grupo-formulario/grupo-unidades-vinculacion/grupo-unidades-vinculacion.fragment';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const UNIDAD_KEY = marker('csp.grupo-unidad-vinculacion');
const TIPO_UNIDAD_KEY = marker('csp.grupo-unidad-vinculacion.tipo-unidad');
const VALOR_KEY = marker('csp.grupo-unidad-vinculacion.valor');

const TIPO_UNIDAD_FIELD = 'tipoUnidad';
const UNIDAD_FIELD = 'unidad';

@Injectable()
export class GrupoUnidadVinculacionListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions> {

  private readonly unidadesVinculacionCache = new Map<string, IUnidadVinculacion>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly grupoService: GrupoService,
    private readonly unidadVinculacionService: UnidadVinculacionService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.unidadesVinculacionCache.clear();
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findUnidadesVinculacion(grupoData?.id).pipe(
      switchMap(response => this.populateUnidadesVinculacionConTipo(response.items)),
      map(unidades => {
        grupoData.unidadesVinculacion = unidades;
        return grupoData;
      })
    );
  }

  private populateUnidadesVinculacionConTipo(items: IGrupoUnidadVinculacion[]): Observable<IGrupoUnidadVinculacionListado[]> {
    if (!items?.length) {
      return of([]);
    }

    const listado: IGrupoUnidadVinculacionListado[] = items.map(item => ({
      id: item.id,
      grupoId: item.grupoId,
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
   * En cada pasada recolecta los predecesores que aún no estén en la caché de TODAS las unidades ya conocidas —no solo
   * de las asignadas a los items—, los pide en lote y se vuelve a invocar; así la jerarquía se resuelve un nivel por
   * pasada hasta la raíz. Cuando no quedan predecesores por traer, fija el `tipoUnidad` de cada item.
   *
   * @param items listado al que asignar el `tipoUnidad`.
   * @param requested ids ya solicitados al SGO; evita re-pedir predecesores inexistentes (datos inconsistentes) y
   *                  garantiza la terminación.
   */
  private populateTipoUnidad(
    items: IGrupoUnidadVinculacionListado[],
    requested: Set<string> = new Set()
  ): Observable<IGrupoUnidadVinculacionListado[]> {
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

  /**
   * Recupera la unidad de vinculación raíz (el tipo) subiendo por la cadena de predecesores cacheados.
   *
   * @param unidadVinculacion unidad para la que se busca su raíz
   * @returns la unidad de vinculación de nivel superior correspondiente a la unidadVinculacion indicada
   */
  private getRootUnidadVinculacion(unidadVinculacion: IUnidadVinculacion): IUnidadVinculacion {
    let current = unidadVinculacion;
    while (current?.predecesorId && this.unidadesVinculacionCache.has(current.predecesorId)) {
      current = this.unidadesVinculacionCache.get(current.predecesorId);
    }

    return current;
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsUnidadExcel(grupos);
    }
  }

  private getColumnsUnidadExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    const titleUnidadesVinculacion = this.translate.instant(UNIDAD_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    const titleTipo = this.translate.instant(TIPO_UNIDAD_KEY);
    const titleValor = this.translate.instant(VALOR_KEY);

    this.getDistinctTiposUnidad(grupos).forEach((tipoUnidad, tipoIdx) => {
      const indexTipo = String(tipoIdx + 1);

      columns.push({
        name: TIPO_UNIDAD_FIELD + indexTipo,
        title: `${titleUnidadesVinculacion} ${indexTipo}: ${titleTipo}`,
        type: ColumnType.STRING,
      });

      const maxUnidades = this.getMaxNumUnidadesByTipo(grupos, tipoUnidad.id);
      for (let j = 0; j < maxUnidades; j++) {
        const indexValor = String(j + 1);
        columns.push({
          name: `${UNIDAD_FIELD}${indexTipo}_${indexValor}`,
          title: `${titleUnidadesVinculacion} ${indexTipo}: ${titleValor} ${indexValor}`,
          type: ColumnType.STRING,
        });
      }
    });

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];
    const elementsRow: any[] = [];

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      this.getDistinctTiposUnidad(grupos).forEach(tipoUnidad => {
        const unidades = (grupo.unidadesVinculacion ?? []).filter(unidad => unidad.tipoUnidad?.id === tipoUnidad.id);
        elementsRow.push(tipoUnidad.nombre ?? '');

        const maxUnidades = this.getMaxNumUnidadesByTipo(grupos, tipoUnidad.id);
        for (let j = 0; j < maxUnidades; j++) {
          elementsRow.push(unidades[j]?.unidadVinculacion?.nombre ?? '');
        }
      });
    }
    return elementsRow;
  }

  private getDistinctTiposUnidad(grupos: IGrupoReportData[]): IUnidadVinculacion[] {
    const tiposUnidad = new Map<string, IUnidadVinculacion>();
    grupos.forEach(grupo =>
      (grupo.unidadesVinculacion ?? []).forEach(unidad => {
        if (unidad.tipoUnidad?.id != null) {
          tiposUnidad.set(unidad.tipoUnidad.id, unidad.tipoUnidad);
        }
      })
    );
    return [...tiposUnidad.values()].sort((a, b) => (a.nombre ?? '').localeCompare(b.nombre ?? ''));
  }

  private getMaxNumUnidadesByTipo(grupos: IGrupoReportData[], tipoUnidadId: string): number {
    return Math.max(
      ...grupos.map(grupo => (grupo.unidadesVinculacion ?? [])
        .filter(unidad => unidad.tipoUnidad?.id === tipoUnidadId)
        .length
      )
    );
  }

}
