import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { TipoEnlaceService } from '@core/services/csp/tipo-enlace.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const ENLACE_KEY = marker('csp.grupo-enlace');
const ENLACE_TIPO_KEY = marker('csp.grupo-enlace.tipo-enlace');
const ENLACE_URL_KEY = marker('csp.grupo-enlace.enlace');

const ENLACE_TIPO_FIELD = 'enlaceTipo';
const ENLACE_URL_FIELD = 'enlaceUrl';

@Injectable()
export class GrupoEnlaceListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private grupoService: GrupoService,
    private tipoEnlaceService: TipoEnlaceService,
    private languageService: LanguageService
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findEnlaces(grupoData?.id).pipe(
      switchMap((response) => this.populateTiposEnlace(response.items)),
      map((enlaces) => {
        grupoData.enlaces = enlaces;
        return grupoData;
      })
    );
  }

  private populateTiposEnlace(enlaces: IGrupoEnlace[]): Observable<IGrupoEnlace[]> {
    const ids = [...new Set(enlaces.map(e => e.tipoEnlace?.id).filter(id => !!id))];
    if (!ids?.length) {
      return of(enlaces);
    }

    return this.tipoEnlaceService.findAllByIdIn(ids).pipe(
      map((response) => {
        const tiposEnlaceById = new Map<number, ITipoEnlace>(response.items.map(t => [t.id, t]));
        enlaces.forEach(enlace => {
          if (enlace.tipoEnlace?.id) {
            enlace.tipoEnlace = tiposEnlaceById.get(enlace.tipoEnlace.id) ?? enlace.tipoEnlace;
          }
        });

        return enlaces;
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEnlaceExcel(grupos);
    }
  }

  private getColumnsEnlaceExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEnlacees = Math.max(...grupos.map(g => g.enlaces ? g.enlaces?.length : 0));
    const titleEnlace = this.translate.instant(ENLACE_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumEnlacees; i++) {
      const idEnlace: string = String(i + 1);

      const columnUrl: ISgiColumnReport = {
        name: ENLACE_URL_FIELD + idEnlace,
        title: titleEnlace + idEnlace + ': ' + this.translate.instant(ENLACE_URL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnUrl);

      const columnTipoEnlace: ISgiColumnReport = {
        name: ENLACE_TIPO_FIELD + idEnlace,
        title: titleEnlace + idEnlace + ': ' + this.translate.instant(ENLACE_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoEnlace);
    }

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];

    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumEnlacees = Math.max(...grupos.map(g => g.enlaces ? g.enlaces?.length : 0));
      for (let i = 0; i < maxNumEnlacees; i++) {
        const enlace = grupo.enlaces ? grupo.enlaces[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, enlace);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadExcel(elementsRow: any[], grupoEnlace: IGrupoEnlace) {
    if (grupoEnlace) {
      elementsRow.push(grupoEnlace.enlace ?? '');
      elementsRow.push(this.languageService.getFieldValue(grupoEnlace.tipoEnlace?.nombre));
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }

}
