import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const RELACION_INSTITUCIONAL_EXPORT_PREFIX_KEY = marker('csp.grupo-relacion-institucional.export.prefix');
const RELACION_INSTITUCIONAL_INSTITUCION_KEY = marker('csp.grupo-relacion-institucional.institucion');

const RELACION_INSTITUCIONAL_FIELD = 'relacionInstitucional';

@Injectable()
export class GrupoRelacionInstitucionalListadoExportService
  extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private grupoService: GrupoService,
    private empresaService: EmpresaService
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findRelacionesInstitucionales(grupoData?.id).pipe(
      switchMap((response) => this.populateEntidades(response.items)),
      map((relaciones) => {
        relaciones.sort((a, b) => this.getNombreInstitucion(a).localeCompare(this.getNombreInstitucion(b)));
        grupoData.relacionesInstitucionales = relaciones;
        return grupoData;
      })
    );
  }

  private populateEntidades(relaciones: IGrupoRelacionInstitucional[]): Observable<IGrupoRelacionInstitucional[]> {
    const ids = [...new Set(relaciones.map(r => r.entidad?.id).filter(id => !!id))];
    if (!ids.length) {
      return of(relaciones);
    }

    return this.empresaService.findAllByIdIn(ids).pipe(
      map((response) => {
        const empresasById = new Map<string, IEmpresa>(response.items.map(e => [e.id, e]));
        relaciones.forEach(relacion => {
          if (relacion.entidad?.id) {
            relacion.entidad = empresasById.get(relacion.entidad.id) ?? relacion.entidad;
          }
        });

        return relaciones;
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsRelacionExcel(grupos);
    }
  }

  private getColumnsRelacionExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumRelaciones = this.getMaxNumRelaciones(grupos);
    const prefix = this.translate.instant(RELACION_INSTITUCIONAL_EXPORT_PREFIX_KEY);
    const institucion = this.translate.instant(RELACION_INSTITUCIONAL_INSTITUCION_KEY);

    for (let i = 0; i < maxNumRelaciones; i++) {
      const idRelacion = String(i + 1);
      columns.push({
        name: RELACION_INSTITUCIONAL_FIELD + idRelacion,
        title: prefix + ': ' + institucion + ' ' + idRelacion,
        type: ColumnType.STRING,
      });
    }

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];

    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumRelaciones = this.getMaxNumRelaciones(grupos);
      for (let i = 0; i < maxNumRelaciones; i++) {
        const relacion = grupo.relacionesInstitucionales?.[i] ?? null;
        elementsRow.push(this.getNombreInstitucion(relacion));
      }
    }

    return elementsRow;
  }

  private getNombreInstitucion(relacion: IGrupoRelacionInstitucional): string {
    if (!relacion) {
      return '';
    }

    return relacion.entidad?.nombre ?? relacion.institucion ?? '';
  }

  private getMaxNumRelaciones(grupos: IGrupoReportData[]): number {
    return Math.max(0, ...grupos.map(g => g.relacionesInstitucionales?.length ?? 0));
  }

}
