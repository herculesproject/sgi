import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IPeticionEvaluacionUnidadVinculacion } from '@core/models/eti/peticion-evaluacion-unidad-vinculacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';
import { IMemoriaReportData, IMemoriaReportOptions, IMemoriaUnidadVinculacionListado } from './memoria-listado-export.service';

const UNIDAD_KEY = marker('eti.peticion-evaluacion-unidad-vinculacion');
const TIPO_UNIDAD_KEY = marker('eti.peticion-evaluacion-unidad-vinculacion.tipo-unidad');
const VALOR_KEY = marker('eti.peticion-evaluacion-unidad-vinculacion.valor');

const TIPO_UNIDAD_FIELD = 'tipoUnidad';
const UNIDAD_FIELD = 'unidad';

/**
 * Bloque de unidades de vinculación del informe de memorias: las unidades de vinculación de una memoria son las de la
 * petición de evaluación a la que pertenece.
 */
@Injectable()
export class MemoriaUnidadVinculacionListadoExportService
  extends AbstractTableExportFillService<IMemoriaReportData, IMemoriaReportOptions> {

  private readonly unidadesVinculacionCache = new Map<string, IUnidadVinculacion>();
  private readonly unidadesVinculacionPeticionEvaluacionCache =
    new Map<number, Observable<IMemoriaUnidadVinculacionListado[]>>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly memoriaService: MemoriaService,
    private readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly unidadVinculacionService: UnidadVinculacionService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.unidadesVinculacionCache.clear();
    this.unidadesVinculacionPeticionEvaluacionCache.clear();
  }

  public getData(memoriaData: IMemoriaReportData): Observable<IMemoriaReportData> {
    return this.memoriaService.findById(memoriaData.id).pipe(
      switchMap(memoria => {
        if (!memoria?.peticionEvaluacion?.id) {
          return of([]);
        }
        return this.findUnidadesVinculacion(memoria.peticionEvaluacion.id);
      }),
      map(unidades => {
        memoriaData.unidadesVinculacion = unidades;
        return memoriaData;
      })
    );
  }

  public fillColumns(
    memorias: IMemoriaReportData[],
    reportConfig: IReportConfig<IMemoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return [];
    }
    return this.getColumnsUnidadesVinculacion(memorias);
  }

  public fillRows(memorias: IMemoriaReportData[], index: number, reportConfig: IReportConfig<IMemoriaReportOptions>): any[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return [];
    }
    return this.getRowUnidadesVinculacion(memorias, index);
  }

  /**
   * Devuelve las unidades de vinculación de la petición de evaluación con su tipo de unidad. Varias memorias pueden
   * pertenecer a la misma petición de evaluación, por lo que sus unidades solo se recuperan una vez.
   *
   * @param peticionEvaluacionId identificador de la petición de evaluación.
   * @returns las unidades de vinculación de la petición de evaluación.
   */
  private findUnidadesVinculacion(peticionEvaluacionId: number): Observable<IMemoriaUnidadVinculacionListado[]> {
    let cached = this.unidadesVinculacionPeticionEvaluacionCache.get(peticionEvaluacionId);

    if (!cached) {
      cached = this.peticionEvaluacionService.findUnidadesVinculacion(peticionEvaluacionId).pipe(
        switchMap(response => this.populateUnidadesVinculacionConTipo(response.items)),
        shareReplay(1)
      );
      this.unidadesVinculacionPeticionEvaluacionCache.set(peticionEvaluacionId, cached);
    }

    return cached;
  }

  /**
   * Genera las columnas del bloque de unidades de vinculación: por cada tipo de unidad, una columna con el tipo y tantas
   * columnas de valor como unidades de ese tipo tenga la memoria que más tenga.
   *
   * @param memorias memorias del informe.
   * @returns las columnas del bloque.
   */
  private getColumnsUnidadesVinculacion(memorias: IMemoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    const titleUnidadesVinculacion = this.translate.instant(UNIDAD_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    const titleTipo = this.translate.instant(TIPO_UNIDAD_KEY);
    const titleValor = this.translate.instant(VALOR_KEY);

    this.getDistinctTiposUnidad(memorias).forEach((tipoUnidad, tipoIdx) => {
      const indexTipo = String(tipoIdx + 1);

      columns.push({
        name: TIPO_UNIDAD_FIELD + indexTipo,
        title: `${titleUnidadesVinculacion} ${indexTipo}: ${titleTipo}`,
        type: ColumnType.STRING,
      });

      const maxUnidades = this.getMaxNumUnidadesByTipo(memorias, tipoUnidad.id);
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

  /**
   * Genera los valores del bloque de unidades de vinculación de la memoria indicada, alineados con las columnas
   * generadas por {@link getColumnsUnidadesVinculacion}.
   *
   * @param memorias memorias del informe.
   * @param index fila del informe.
   * @returns los valores del bloque para la fila.
   */
  private getRowUnidadesVinculacion(memorias: IMemoriaReportData[], index: number): any[] {
    const elementsRow: any[] = [];

    this.getDistinctTiposUnidad(memorias).forEach(tipoUnidad => {
      const unidades = (memorias[index]?.unidadesVinculacion ?? []).filter(unidad => unidad.tipoUnidad?.id === tipoUnidad.id);
      elementsRow.push(tipoUnidad.nombre ?? '');

      const maxUnidades = this.getMaxNumUnidadesByTipo(memorias, tipoUnidad.id);
      for (let j = 0; j < maxUnidades; j++) {
        elementsRow.push(unidades[j]?.unidadVinculacion?.nombre ?? '');
      }
    });

    return elementsRow;
  }

  private populateUnidadesVinculacionConTipo(
    items: IPeticionEvaluacionUnidadVinculacion[]
  ): Observable<IMemoriaUnidadVinculacionListado[]> {
    if (!items?.length) {
      return of([]);
    }

    const listado: IMemoriaUnidadVinculacionListado[] = items.map(item => ({
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
    items: IMemoriaUnidadVinculacionListado[],
    requested: Set<string> = new Set()
  ): Observable<IMemoriaUnidadVinculacionListado[]> {
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
        response.items.forEach(unidad => this.unidadesVinculacionCache.set(unidad.id, unidad));
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

  private getDistinctTiposUnidad(memorias: IMemoriaReportData[]): IUnidadVinculacion[] {
    const tiposUnidad = new Map<string, IUnidadVinculacion>();
    memorias.forEach(memoria =>
      (memoria.unidadesVinculacion ?? []).forEach(unidad => {
        if (unidad.tipoUnidad?.id != null) {
          tiposUnidad.set(unidad.tipoUnidad.id, unidad.tipoUnidad);
        }
      })
    );
    return [...tiposUnidad.values()].sort((a, b) => (a.nombre ?? '').localeCompare(b.nombre ?? ''));
  }

  private getMaxNumUnidadesByTipo(memorias: IMemoriaReportData[], tipoUnidadId: string): number {
    return Math.max(
      ...memorias.map(memoria => (memoria.unidadesVinculacion ?? [])
        .filter(unidad => unidad.tipoUnidad?.id === tipoUnidadId)
        .length
      )
    );
  }

}
