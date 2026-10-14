import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_ACTIVIDAD_MAP } from '@core/models/eti/tipo-actividad';
import { TIPO_INVESTIGACION_TUTELADA_MAP } from '@core/models/eti/tipo-investigacion-tutelada';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, shareReplay, switchMap } from 'rxjs/operators';
import { IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions } from './peticion-evaluacion-listado-export.service';

const SOLICITANTE_NOMBRE_KEY = marker('eti.peticion-evaluacion.report.solicitante.nombre');
const SOLICITANTE_APELLIDOS_KEY = marker('eti.peticion-evaluacion.report.solicitante.apellidos');
const SOLICITANTE_DEPARTAMENTO_KEY = marker('eti.peticion-evaluacion.report.solicitante.departamento');
const CODIGO_KEY = marker('eti.peticion-evaluacion.report.codigo');
const CODIGO_SOLICITUD_CONVOCATORIA_KEY = marker('eti.peticion-evaluacion.report.codigo-solicitud-convocatoria');
const TITULO_PROYECTO_KEY = marker('eti.peticion-evaluacion.proyecto-titulo');
const TIPO_ACTIVIDAD_KEY = marker('eti.peticion-evaluacion.report.tipo-actividad');
const TIPO_INVESTIGACION_TUTELADA_KEY = marker('eti.peticion-evaluacion.tipo-investigacion-tutelada');
const ORGANO_FINANCIADOR_KEY = marker('eti.peticion-evaluacion.organo-financiador');
const IMPORTE_FINANCIACION_KEY = marker('eti.peticion-evaluacion.importe-financiacion');
const FECHA_INICIO_KEY = marker('eti.peticion-evaluacion.fecha-inicio');
const FECHA_FIN_KEY = marker('eti.peticion-evaluacion.fecha-fin');

@Injectable()
export class PeticionEvaluacionGeneralListadoExportService extends
  AbstractTableExportFillService<IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions> {

  private readonly vinculacionesCache = new Map<string, Observable<IVinculacion>>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private readonly personaService: PersonaService,
    private readonly languageService: LanguageService,
    private readonly vinculacionService: VinculacionService
  ) {
    super(translate);
  }

  public clearCache(): void {
    this.vinculacionesCache.clear();
  }

  public getData(peticionEvaluacionData: IPeticionEvaluacionReportData): Observable<IPeticionEvaluacionReportData> {
    return of(peticionEvaluacionData).pipe(
      switchMap((data: IPeticionEvaluacionReportData) => {
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
      }), switchMap((data: IPeticionEvaluacionReportData) => {
        if (!data.solicitudConvocatoriaRef) {
          return of(data);
        }
        return this.solicitudService.getCodigoRegistroInterno(data.solicitudConvocatoriaRef as unknown as number).pipe(
          map((codigo: string) => {
            data.codigoSolicitudConvocatoria = codigo;
            return data;
          }), catchError(err => {
            this.logger.error(err);
            return of(err);
          })
        );
      }),
      switchMap((data: IPeticionEvaluacionReportData) => this.getVinculacionSolicitanteData(data))
    );
  }

  private getVinculacionSolicitanteData(data: IPeticionEvaluacionReportData): Observable<IPeticionEvaluacionReportData> {
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
    peticiones: IPeticionEvaluacionReportData[],
    reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [
      {
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
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(CODIGO_KEY),
        name: 'codigo',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(CODIGO_SOLICITUD_CONVOCATORIA_KEY),
        name: 'codigoSolicitudConvocatoria',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(TITULO_PROYECTO_KEY),
        name: 'tituloProyecto',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(TIPO_ACTIVIDAD_KEY),
        name: 'tipoActividad',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(TIPO_INVESTIGACION_TUTELADA_KEY),
        name: 'tipoInvestigacionTutelada',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(ORGANO_FINANCIADOR_KEY),
        name: 'organoFinanciador',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(IMPORTE_FINANCIACION_KEY),
        name: 'importeFinanciacion',
        type: ColumnType.NUMBER
      }, {
        title: this.translate.instant(FECHA_INICIO_KEY),
        name: 'fechaInicio',
        type: ColumnType.DATE
      }, {
        title: this.translate.instant(FECHA_FIN_KEY),
        name: 'fechaFin',
        type: ColumnType.DATE
      }
    ] as ISgiColumnReport[];

    return columns;
  }

  public fillRows(
    peticiones: IPeticionEvaluacionReportData[],
    index: number,
    reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>
  ): any[] {

    const peticionData = peticiones[index];

    const elementsRow: any[] = [
      peticionData.solicitante?.nombre ?? '',
      peticionData.solicitante?.apellidos ?? '',
      peticionData.solicitante?.vinculacion?.departamento?.nombre ?? '',
      peticionData.codigo ?? '',
      peticionData.codigoSolicitudConvocatoria ?? '',
      this.languageService.getFieldValue(peticionData.titulo) ?? '',
      peticionData.tipoActividad?.id ? this.translate.instant(TIPO_ACTIVIDAD_MAP.get(peticionData.tipoActividad?.id)) : '',
      peticionData.tipoInvestigacionTutelada?.id
        ? this.translate.instant(TIPO_INVESTIGACION_TUTELADA_MAP.get(peticionData.tipoInvestigacionTutelada?.id))
        : '',
      this.languageService.getFieldValue(peticionData.fuenteFinanciacion) ?? '',
      peticionData.importeFinanciacion ?? '',
      LuxonUtils.toBackend(peticionData.fechaInicio) ?? '',
      LuxonUtils.toBackend(peticionData.fechaFin) ?? ''
    ];

    return elementsRow;
  }
}
