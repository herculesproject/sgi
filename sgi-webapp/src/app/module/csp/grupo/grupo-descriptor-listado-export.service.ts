import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { TipoDescriptorGrupoService } from '@core/services/csp/tipo-descriptor-grupo/tipo-descriptor-grupo.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const DESCRIPTOR_KEY = marker('csp.grupo-descriptor');
const DESCRIPTOR_TIPO_KEY = marker('csp.grupo-descriptor.tipo-descriptor-grupo');
const DESCRIPTOR_VALOR_KEY = marker('csp.grupo-descriptor.valor');

const DESCRIPTOR_TIPO_FIELD = 'descriptorTipo';
const DESCRIPTOR_VALOR_FIELD = 'descriptorValor';

@Injectable()
export class GrupoDescriptorListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions> {

  private readonly tiposDescriptorCache = new Map<number, Observable<ITipoDescriptorGrupo>>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly languageService: LanguageService,
    private readonly grupoService: GrupoService,
    private readonly tipoDescriptorGrupoService: TipoDescriptorGrupoService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.tiposDescriptorCache.clear();
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findDescriptores(grupoData.id).pipe(
      switchMap(response => this.populateTiposDescriptor(response.items)),
      map(descriptores => {
        grupoData.descriptores = descriptores;
        return grupoData;
      })
    );
  }

  private populateTiposDescriptor(descriptores: IGrupoDescriptor[]): Observable<IGrupoDescriptor[]> {
    if (!descriptores.length) {
      return of(descriptores);
    }

    return forkJoin(
      descriptores.map(descriptor => {
        const id = descriptor.tipoDescriptorGrupo?.id;
        if (!id) {
          return of(descriptor);
        }

        return this.getTipoDescriptorCached(id).pipe(
          map(tipo => {
            descriptor.tipoDescriptorGrupo = tipo;
            return descriptor;
          })
        );
      })
    );
  }

  private getTipoDescriptorCached(id: number): Observable<ITipoDescriptorGrupo> {
    let cached = this.tiposDescriptorCache.get(id);

    if (!cached) {
      cached = this.tipoDescriptorGrupoService.findById(id).pipe(shareReplay(1));
      this.tiposDescriptorCache.set(id, cached);
    }

    return cached;
  }

  public fillColumns(grupos: IGrupoReportData[], reportConfig: IReportConfig<IGrupoReportOptions>): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return [];
    }
    const columns: ISgiColumnReport[] = [];
    const titleDescriptores = this.translate.instant(DESCRIPTOR_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    const titleTipo = this.translate.instant(DESCRIPTOR_TIPO_KEY);
    const titleValor = this.translate.instant(DESCRIPTOR_VALOR_KEY);

    this.getDistinctTiposDescriptorGrupo(grupos).forEach((tipo, tipoIdx) => {
      const indexTipo = String(tipoIdx + 1);

      columns.push({
        name: DESCRIPTOR_TIPO_FIELD + indexTipo,
        title: `${titleDescriptores} ${indexTipo}: ${titleTipo}`,
        type: ColumnType.STRING,
      });

      const maxValores = this.getMaxNumValoresTipoDescriptor(grupos, tipo.id);
      for (let j = 0; j < maxValores; j++) {
        const indexValor = String(j + 1);
        columns.push({
          name: `${DESCRIPTOR_VALOR_FIELD}${indexTipo}_${indexValor}`,
          title: `${titleDescriptores} ${indexTipo}:  ${titleValor} ${indexValor}`,
          type: ColumnType.STRING,
        });
      }
    });

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return [];
    }
    const grupo = grupos[index];
    const elementsRow: any[] = [];

    this.getDistinctTiposDescriptorGrupo(grupos).forEach(tipo => {
      const descriptores = (grupo.descriptores ?? []).filter(d => d.tipoDescriptorGrupo?.id === tipo.id);
      elementsRow.push(this.languageService.getFieldValue(tipo.nombre) ?? '');

      const maxValores = this.getMaxNumValoresTipoDescriptor(grupos, tipo.id);
      for (let j = 0; j < maxValores; j++) {
        const descriptor = descriptores[j] ?? null;
        elementsRow.push(descriptor ? (this.languageService.getFieldValue(descriptor.texto) ?? '') : '');
      }
    });

    return elementsRow;
  }


  private getDistinctTiposDescriptorGrupo(grupos: IGrupoReportData[]): ITipoDescriptorGrupo[] {
    const tiposDescriptor = new Map<number, ITipoDescriptorGrupo>();
    grupos.forEach(grupo =>
      (grupo.descriptores ?? []).forEach(descriptor => {
        if (descriptor.tipoDescriptorGrupo?.id != null) {
          tiposDescriptor.set(descriptor.tipoDescriptorGrupo.id, descriptor.tipoDescriptorGrupo);
        }
      })
    );
    return [...tiposDescriptor.values()].sort((a, b) => {
      const nombreA = this.languageService.getFieldValue(a.nombre) ?? '';
      const nombreB = this.languageService.getFieldValue(b.nombre) ?? '';
      return nombreA.localeCompare(nombreB);
    });
  }

  private getMaxNumValoresTipoDescriptor(grupos: IGrupoReportData[], tipoDescriptorId: number): number {
    return Math.max(
      ...grupos.map(grupo => (grupo.descriptores ?? [])
        .filter(descriptor => descriptor.tipoDescriptorGrupo?.id === tipoDescriptorId)
        .length
      )
    );
  }
}
