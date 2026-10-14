import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ESTADO_MEMORIA_MAP } from '@core/models/eti/tipo-estado-memoria';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, shareReplay, switchMap } from 'rxjs/operators';
import { IMemoriaReportData, IMemoriaReportOptions } from './memoria-listado-export.service';

const MEMORIA_REFERENCIA_KEY = marker('eti.peticion-evaluacion.report.memoria.referencia');
const MEMORIA_COMITE_KEY = marker('eti.peticion-evaluacion.report.memoria.comite');
const MEMORIA_ESTADO_KEY = marker('eti.peticion-evaluacion.report.memoria.estado');
const MEMORIA_FECHA_EVALUACION_KEY = marker('eti.peticion-evaluacion.report.memoria.fecha-evaluacion');
const SOLICITANTE_NOMBRE_KEY = marker('eti.peticion-evaluacion.report.solicitante.nombre');
const SOLICITANTE_APELLIDOS_KEY = marker('eti.peticion-evaluacion.report.solicitante.apellidos');
const SOLICITANTE_DEPARTAMENTO_KEY = marker('eti.peticion-evaluacion.report.solicitante.departamento');
const MEMORIA_RESPONSABLE_NOMBRE_KEY = marker('eti.memoria.report.responsable.nombre');
const MEMORIA_RESPONSABLE_APELLIDOS_KEY = marker('eti.memoria.report.responsable.apellidos');

@Injectable()
export class MemoriaGeneralListadoExportService extends
  AbstractTableExportFillService<IMemoriaReportData, IMemoriaReportOptions> {

  private readonly vinculacionesCache = new Map<string, Observable<IVinculacion>>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.vinculacionesCache.clear();
  }

  public getData(memoriaData: IMemoriaReportData): Observable<IMemoriaReportData> {
    return of(memoriaData).pipe(
      switchMap(data => this.getSolicitanteData(data)),
      switchMap(data => this.getVinculacionSolicitanteData(data)),
      switchMap(data => this.getResponsableData(data))
    );
  }

  private getVinculacionSolicitanteData(data: IMemoriaReportData): Observable<IMemoriaReportData> {
    if (!data.solicitante?.id) {
      return of(data);
    }
    return this.findVinculacion(data.solicitante.id).pipe(
      map((vinculacion: IVinculacion) => {
        data.solicitante.vinculacion = vinculacion;
        return data;
      })
    );
  }


  private getSolicitanteData(data: IMemoriaReportData): Observable<IMemoriaReportData> {
    if (!data.solicitante?.id) {
      return of(data);
    }
    return this.personaService.findById(data.solicitante.id).pipe(
      map((persona: IPersona) => {
        data.solicitante = persona;
        return data;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  private getResponsableData(data: IMemoriaReportData): Observable<IMemoriaReportData> {
    if (!data.responsableRef) {
      return of(data);
    }
    return this.personaService.findById(data.responsableRef).pipe(
      map((persona: IPersona) => {
        data.responsable = persona;
        return data;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }
  /**
   * Devuelve la vinculación de la persona, reutilizando la petición si ya se ha hecho para la misma persona durante la
   * exportación en curso. Si la vinculación no se puede recuperar se devuelve null.
   *
   * @param personaId identificador de la persona
   * @returns vinculación de la persona o null
   */
  private findVinculacion(personaId: string): Observable<IVinculacion> {
    let vinculacion$ = this.vinculacionesCache.get(personaId);
    if (!vinculacion$) {
      vinculacion$ = this.vinculacionService.findByPersonaId(personaId).pipe(
        catchError(err => {
          this.logger.error(err);
          return of(null as IVinculacion);
        }),
        shareReplay(1)
      );
      this.vinculacionesCache.set(personaId, vinculacion$);
    }
    return vinculacion$;
  }

  public fillColumns(
    peticiones: IMemoriaReportData[],
    reportConfig: IReportConfig<IMemoriaReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(MEMORIA_REFERENCIA_KEY),
        name: 'referencia',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(MEMORIA_COMITE_KEY),
        name: 'comite',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(MEMORIA_ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(MEMORIA_FECHA_EVALUACION_KEY),
        name: 'fechaEvaluacion',
        type: ColumnType.DATE,
      }, {
        title: this.translate.instant(SOLICITANTE_NOMBRE_KEY),
        name: 'solicitanteNombre',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(SOLICITANTE_APELLIDOS_KEY),
        name: 'solicitanteApellidos',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(SOLICITANTE_DEPARTAMENTO_KEY),
        name: 'solicitanteDepartamento',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(MEMORIA_RESPONSABLE_NOMBRE_KEY),
        name: 'responsableNombre',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(MEMORIA_RESPONSABLE_APELLIDOS_KEY),
        name: 'responsableApellidos',
        type: ColumnType.STRING,
      }
    ] as ISgiColumnReport[];

    return columns;
  }

  public fillRows(
    memorias: IMemoriaReportData[],
    index: number, reportConfig: IReportConfig<IMemoriaReportOptions>
  ): any[] {

    const memoriaData = memorias[index];

    const elementsRow: any[] = [
      memoriaData.numReferencia ?? '',
      memoriaData.comite.codigo ?? '',
      memoriaData.estadoActual.id ? this.translate.instant(ESTADO_MEMORIA_MAP.get(memoriaData.estadoActual.id)) : '',
      LuxonUtils.toBackend(memoriaData.fechaEvaluacion) ?? '',
      memoriaData.solicitante?.nombre ?? '',
      memoriaData.solicitante?.apellidos ?? '',
      memoriaData.solicitante?.vinculacion?.departamento?.nombre ?? '',
      memoriaData.responsable?.nombre ?? '',
      memoriaData.responsable?.apellidos ?? ''
    ];

    return elementsRow;
  }
}
