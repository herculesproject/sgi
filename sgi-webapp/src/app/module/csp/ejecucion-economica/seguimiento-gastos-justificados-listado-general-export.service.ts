import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IColumna } from '@core/models/sge/columna';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { GastoRequerimientoJustificacionService } from '@core/services/csp/gasto-requerimiento-justificacion/gasto-requerimiento-justificacion.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestSortDirection } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, from, Observable, of } from 'rxjs';
import { catchError, concatMap, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { IGastoJustificadoReportData, IGastoRequerimiento, IGastosJustificadosReportOptions, IRequerimientoJustificacionAlegacion } from './seguimiento-gastos-justificados-listado-export.service';

const CODIGO_PROYECTO_SGE_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.codigo-proyecto-sge');
const ID_PROYECTO_SGI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.id-proyecto-sgi');
const CODIGO_EXTERNO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.codigo-externo');
const TITULO_PROYECTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.titulo-proyecto');
const FECHA_INICIO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.fecha-inicio');
const FECHA_FIN_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.fecha-fin');
const FECHA_FIN_DEFINITIVA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.fecha-fin-definitiva');
const JUSTIFICACION_ID_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.gastos.gasto-justificado-detalle.id-justificacion');
const TITULO_CONVOCATORIA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.titulo-convocatoria');
const IMPORTE_CONCEDIDO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-concedido');
const IMPORTE_CONCEDIDO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-concedido-cd');
const IMPORTE_CONCEDIDO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-concedido-ci');
const FECHA_ULTIMA_JUSTIFICACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.fecha-ultima-justificacion');
const IMPORTE_JUSTIFICADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-justificado');
const IMPORTE_JUSTIFICADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-justificado-cd');
const IMPORTE_JUSTIFICADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-justificado-ci');
const IMPORTE_NO_EJECUTADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-no-ejecutado');
const IMPORTE_NO_EJECUTADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-no-ejecutado-cd');
const IMPORTE_NO_EJECUTADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-no-ejecutado-ci');
const IMPORTE_REINTEGRADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-reintegrado');
const IMPORTE_REINTEGRADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-reintegrado-cd');
const IMPORTE_REINTEGRADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.importe-reintegrado-ci');
const FECHA_REINTEGRO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.fecha-reintegro');
const NUM_JUST_REINTEGRADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export.num-just-reintegro');


@Injectable()
export class SeguimientoGastosJustificadosResumenListadoGeneralExportService
  extends AbstractTableExportFillService<IGastoJustificadoReportData, IGastosJustificadosReportOptions>{
  private columns: IColumna[] = [];
  private maxResponsables = 0;
  private maxEntidades = 0;
  private maxRequerimientos = 0;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private empresaService: EmpresaService,
    private personaService: PersonaService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly proyectoService: ProyectoService,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly conceptoGastoService: ConceptoGastoService,
    private readonly proyectoSeguimientoEjecucionEconomicaService: ProyectoSeguimientoEjecucionEconomicaService,
    private readonly gastoRequerimientoJustificacionService: GastoRequerimientoJustificacionService,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService

  ) {
    super(translate);
    this.seguimientoJustificacionService.getColumnas()
      .pipe(
        map(columnas => this.columns = columnas)
      )
      .subscribe();
  }

  public getData(gastoData: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!gastoData) {
      return of(gastoData);
    }
    return this.seguimientoJustificacionService.findById(
      gastoData.id, { justificacionId: gastoData.justificacionId, proyectoId: gastoData.proyectoId }
    )
      .pipe(
        map(detalle => {
          gastoData.detalle = detalle;
          return gastoData;
        }),
        switchMap(data => this.getProyectoSgi(data)),
        switchMap(data => this.getResponsablesGasto(data)),
        switchMap(data => this.getTituloConvocatoria(data)),
        switchMap(data => this.getEntidadesFinanciadoras(data)),
        switchMap(data => this.getImporteConcedido(data)),
        switchMap(data => this.getUltimaFechaJustificacion(data)),
        switchMap(data => this.getProyectoSeguimientoJustificacion(data)),
        switchMap(data => this.getRequerimientosJustificacion(data)),
      ).pipe(
        catchError(err => {
          this.logger.error(err);
          return of(void (0));
        })
      );
  }

  private getTituloConvocatoria(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.proyectoSgi?.convocatoriaId) {
      return of(data);
    }
    return this.convocatoriaService.findById(data.proyectoSgi.convocatoriaId).pipe(
      map(convocatoria => {
        data.tituloConvocatoria = convocatoria.titulo;
        return data;
      }), catchError(err => {
        this.logger.error(err);
        return of(void (0));
      })
    );
  }
  private getProyectoSgi(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.justificacionId) {
      return of(data);
    }
    return this.proyectoPeriodoJustificacionService.findByIdentificadorJustificacion(data.justificacionId)
      .pipe(
        map(proyectoPeriodoJustificacion => {
          return proyectoPeriodoJustificacion?.proyecto?.id || null;
        }), switchMap(proyectoId => {
          if (!!!proyectoId) {
            return of(data);
          }
          return this.proyectoService.findById(proyectoId).pipe(
            map(proyecto => {
              data.proyectoSgi = proyecto;
              return data;
            })
          );
        })
      );
  }

  private getResponsablesGasto(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.proyectoSgi) {
      return of(data);
    }
    return this.proyectoService.findAllProyectoEquipo(data.proyectoSgi?.id).pipe(
      map(miembrosResponse => {
        data.responsables = [];
        return miembrosResponse;
      }), switchMap(miembrosResponse => {
        if (miembrosResponse.total === 0) {
          return of(data);
        }
        const responsablesVigentes = miembrosResponse.items.filter(responsable => {
          if (responsable.rolProyecto.rolPrincipal && !!!responsable.fechaFin && !!!responsable.fechaInicio) {
            return true;
          } else if (responsable.fechaInicio < DateTime.now() || !responsable.rolProyecto.rolPrincipal) {
            return false;
          } else if (!!!responsable.fechaFin || responsable.fechaFin <= DateTime.now()) {
            return true;
          }
          return false;
        });
        const responsablesIds = new Set<string>(responsablesVigentes.map(responsable => responsable.persona?.id));
        if (responsablesIds.size === 0) {
          return of(data);
        }
        //Se comprueba si este gasto tiene más responsables que los anteriores para saber cuantas columnas hay que pintar
        //se pintan las del mayor número de responsables
        this.maxResponsables = this.maxResponsables < responsablesIds.size ? responsablesIds.size : this.maxResponsables;

        return this.personaService.findAllByIdIn([...responsablesIds]).pipe(
          map(personas => {
            personas.items.forEach(persona => {
              data.responsables.push({
                nombre: persona.nombre,
                apellidos: persona.apellidos,
                email: persona.emails.find(email => email.principal)?.email
              });
            });
            return data;
          }), catchError(err => {
            this.logger.error(err);
            return of(void (0));
          })
        );
      })
    );
  }

  private getEntidadesFinanciadoras(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.proyectoSgi?.id) {
      return of(data);
    }
    return this.proyectoService.findEntidadesFinanciadoras(data.proyectoSgi.id).pipe(
      map(entidadesResponse => {
        data.entidadesFinanciadoras = [];
        return entidadesResponse;
      }), switchMap(entidades => {
        if (entidades.total === 0) {
          return of(data);
        }

        const entidadesIds = new Set<string>(entidades.items.map(entidadFinanciadora => entidadFinanciadora.empresa?.id));
        if (entidadesIds.size === 0) {
          return of(data);
        }
        //Se comprueba si este gasto tiene más entidades financiadoras que los anteriores para saber cuantas columnas hay que pintar
        //se pintan las del mayor número de entidades
        this.maxEntidades = this.maxEntidades < entidadesIds.size ? entidadesIds.size : this.maxEntidades;

        return this.empresaService.findAllByIdIn([...entidadesIds]).pipe(
          map(empresasResp => {
            empresasResp.items.forEach(empresa => {
              data.entidadesFinanciadoras.push({
                nombre: empresa.nombre,
                numIdentificacion: empresa.numeroIdentificacion
              });
            });
            return data;

          })
        );
      })
    );
  }

  private getImporteConcedido(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.proyectoSgi) {
      return of(data);
    }
    if (!!data.proyectoSgi.importePresupuesto || !!data.proyectoSgi.importePresupuestoCostesIndirectos ||
      !!data.proyectoSgi.importeConcedido || !!data.proyectoSgi.importeConcedidoCostesIndirectos ||
      !!data.proyectoSgi.importePresupuestoSocios || !!data.proyectoSgi.importeConcedidoSocios ||
      !!data.proyectoSgi.totalImporteConcedido || !!data.proyectoSgi.totalImportePresupuesto) {

      data.importeConcedido = data.proyectoSgi.importeConcedido + data.proyectoSgi.importeConcedidoCostesIndirectos;
      data.importeConcedidoCD = data.proyectoSgi.importeConcedido;
      data.importeConcedidoCI = data.proyectoSgi.importeConcedido;

      return of(data);
    } else {
      return this.proyectoService.findAllProyectoAnualidadesGasto(data.proyectoSgi.id).pipe(
        map(anualidadesResponse => {
          data.importeConcedidoCD = 0;
          data.importeConcedidoCI = 0;
          data.importeConcedido = anualidadesResponse.items.reduce((total, anualidadGasto) => total + anualidadGasto.importeConcedido, 0);
          return anualidadesResponse;
        }), switchMap(anualidadesResponse => {
          if (anualidadesResponse.total === 0) {
            return of(data);
          }
          return from(anualidadesResponse.items).pipe(
            mergeMap(anualidad => {
              return this.conceptoGastoService.findById(anualidad.conceptoGasto?.id).pipe(
                map(conceptoGasto => {
                  if (!!!conceptoGasto) {
                    return data;
                  }
                  if (!conceptoGasto.costesIndirectos) {
                    data.importeConcedidoCD += anualidad.importeConcedido;
                  }
                  if (conceptoGasto.costesIndirectos) {
                    data.importeConcedidoCI += anualidad.importeConcedido;
                  }
                  return data;
                })
              )
            }), catchError(err => {
              this.logger.error(err);
              return of(void (0));
            })
          );
        })
      );
    }

  }

  private getUltimaFechaJustificacion(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.proyectoSgi?.id) {
      return of(data);
    }
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('fechaPresentacionJustificacion', SgiRestSortDirection.DESC),
    };
    return this.proyectoService.findAllPeriodoJustificacion(data.proyectoSgi.id, options).pipe(
      map(periodos => {
        if (periodos.total === 0) {
          return data;
        }
        data.fechaUltimaJustificacion = periodos.items[0].fechaPresentacionJustificacion;
        return data;
      })
    );
  }

  private getProyectoSeguimientoJustificacion(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data.proyectoId || !!!data.proyectoSgi?.id) {
      return of(data);
    }
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoProyectoSge.proyectoId', SgiRestFilterOperator.EQUALS, data.proyectoSgi.id.toString())
    };

    return this.proyectoSeguimientoEjecucionEconomicaService.findSeguimientosJustificacion(data.proyectoId, options).pipe(
      map(proyectoSeguimientoJustificacionResponse => {
        if (proyectoSeguimientoJustificacionResponse.total === 0) {
          return data;
        }
        data.proyectoSeguimientoJustificacion = proyectoSeguimientoJustificacionResponse.items[0];
      }), catchError(ex => {
        this.logger.error(ex);
        return of(void (0));
      })
    );
  }

  private getRequerimientosJustificacion(data: IGastoJustificadoReportData): Observable<IGastoJustificadoReportData> {
    if (!!!data?.id) {
      return of(data);
    }
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort(
        'requeimientoJustificacion.numRequerimiento, requerimientoJustificacion.fechaNotificacion', SgiRestSortDirection.ASC
      ),
      filter: new RSQLSgiRestFilter('gastoRef', SgiRestFilterOperator.EQUALS, data.id.toString())
    };

    return this.gastoRequerimientoJustificacionService.findAll(options).pipe(
      tap(() => data.requerimientos = []),
      switchMap(response => {
        if (response.total === 0) {
          return of(data);
        }
        this.maxRequerimientos = response.total > this.maxRequerimientos ? response.total : this.maxRequerimientos;
        return this.getRequisitosAndAlegaciones(response, data)
      }),
      catchError(ex => {
        this.logger.error(ex);
        return of(void (0));
      })
    );
  }

  private getRequisitosAndAlegaciones(
    response: SgiRestListResult<IGastoRequerimientoJustificacion>,
    data: IGastoJustificadoReportData
  ): Observable<IGastoJustificadoReportData> {
    return from(response.items).pipe(
      concatMap(gastoRequerimiento => {
        return forkJoin({
          requerimiento: this.requerimientoJustificacionService.findById(gastoRequerimiento.requerimientoJustificacion.id),
          alegacion: this.requerimientoJustificacionService.findAlegacion(gastoRequerimiento.requerimientoJustificacion.id)
        }).pipe(
          map(joinResponse => {
            const requerimiento = gastoRequerimiento as IGastoRequerimiento;

            requerimiento.requerimientosJustificacionAlegacion = {
              ...joinResponse.requerimiento,
              alegacionRequerimiento: joinResponse.alegacion
            };
            data.requerimientos.push(requerimiento);
            return data;
          })
        );
      })
    );
  }

  public fillColumns(
    gastos: IGastoJustificadoReportData[],
    reportConfig: IReportConfig<IGastosJustificadosReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(JUSTIFICACION_ID_KEY),
        name: 'justificacionId',
        type: ColumnType.STRING,
        format: '#'
      }
    ];
    this.columns.forEach((column: IColumna) => {
      columns.push({
        title: column.nombre,
        name: 'col' + column.id,
        type: ColumnType.STRING,
        format: '#'
      });
    });

    columns.push({
      title: this.translate.instant(CODIGO_PROYECTO_SGE_KEY),
      name: 'codigoProyectoSge',
      type: ColumnType.STRING,
      format: '#'
    });

    columns.push({
      title: this.translate.instant(ID_PROYECTO_SGI_KEY),
      name: 'idProyectoSgi',
      type: ColumnType.STRING,
      format: '#'
    });

    columns.push({
      title: this.translate.instant(CODIGO_EXTERNO_KEY),
      name: 'codigoExterno',
      type: ColumnType.STRING,
      format: '#'
    });

    columns.push({
      title: this.translate.instant(TITULO_PROYECTO_KEY),
      name: 'tituloProyecto',
      type: ColumnType.STRING,
      format: '#'
    });

    columns.push({
      title: this.translate.instant(FECHA_INICIO_KEY),
      name: 'fechaInicio',
      type: ColumnType.DATE
    });

    columns.push({
      title: this.translate.instant(FECHA_FIN_KEY),
      name: 'fechaFin',
      type: ColumnType.DATE
    });

    columns.push({
      title: this.translate.instant(FECHA_FIN_DEFINITIVA_KEY),
      name: 'fechaFinDefinitiva',
      type: ColumnType.DATE
    });

    this.getResponsablesColumns(columns);

    columns.push({
      title: this.translate.instant(TITULO_CONVOCATORIA_KEY),
      name: 'tituloConvocatoria',
      type: ColumnType.STRING,
      format: '#'
    });

    this.getEntidadesFinanciadorasColumns(columns);

    columns.push({
      title: this.translate.instant(IMPORTE_CONCEDIDO_KEY),
      name: 'importeConcedido',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_CONCEDIDO_CD_KEY),
      name: 'importeConcedidoCD',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_CONCEDIDO_CI_KEY),
      name: 'importeConcedidoCI',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(FECHA_ULTIMA_JUSTIFICACION_KEY),
      name: 'fechaUltimaJustificacion',
      type: ColumnType.DATE
    });
    this.getProyectoSeguimientoJustificacionColumns(columns);
    this.getRequerimientosJustificadosColumns(columns);

    return columns;
  }

  private getResponsablesColumns(columns: ISgiColumnReport[]): void {
    for (let i = 1; i <= this.maxResponsables; i++) {
      columns.push(
        {
          title: 'Responsable ' + i + ': Nombre',
          name: 'responsableNombre' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );

      columns.push(
        {
          title: 'Responsable ' + i + ': Apellidos',
          name: 'responsableApellidos' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );

      columns.push(
        {
          title: 'Responsable ' + i + ': Email',
          name: 'responsableEmail' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );
    }
  }

  private getEntidadesFinanciadorasColumns(columns: ISgiColumnReport[]): void {
    for (let i = 1; i <= this.maxEntidades; i++) {
      columns.push(
        {
          title: 'Entidad financiadora ' + i + ': Nombre',
          name: 'entidadFinanciadoraNombre' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );

      columns.push(
        {
          title: 'Entidad financiadora ' + i + ': Número identificación',
          name: 'entidadFinanciadoraNumIdentificacion' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );
    }
  }

  private getProyectoSeguimientoJustificacionColumns(columns: ISgiColumnReport[]): void {
    columns.push({
      title: this.translate.instant(IMPORTE_JUSTIFICADO_KEY),
      name: 'importeJustificado',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_JUSTIFICADO_CD_KEY),
      name: 'importeJustificadoCd',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_JUSTIFICADO_CI_KEY),
      name: 'importeJustificadoCi',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_NO_EJECUTADO_KEY),
      name: 'importeNoEjecutado',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_NO_EJECUTADO_CD_KEY),
      name: 'importeNoEjecutadoCd',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_NO_EJECUTADO_CI_KEY),
      name: 'importeNoEjecutadoCi',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_REINTEGRADO_KEY),
      name: 'importeReintegrado',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_REINTEGRADO_CD_KEY),
      name: 'importeReintegradoCd',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(IMPORTE_REINTEGRADO_CI_KEY),
      name: 'importeReintegradoCi',
      type: ColumnType.NUMBER
    });
    columns.push({
      title: this.translate.instant(FECHA_REINTEGRO_KEY),
      name: 'fechaReintegro',
      type: ColumnType.DATE
    });
    columns.push({
      title: this.translate.instant(NUM_JUST_REINTEGRADO_KEY),
      name: 'numJustIntegrado',
      type: ColumnType.STRING,
      format: '#'
    });
  }

  private getRequerimientosJustificadosColumns(columns: ISgiColumnReport[]): void {
    for (let i = 1; i <= this.maxRequerimientos; i++) {
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Núm. req',
          name: 'requerimientoNumReq' + i,
          type: ColumnType.NUMBER,
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Tipo. req',
          name: 'requerimientoTipoReq' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Fecha notificación',
          name: 'requerimientoFechaNotificacion' + i,
          type: ColumnType.DATE
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Gasto aceptado',
          name: 'requerimientoGastoAceptado' + i,
          type: ColumnType.BOOLEAN
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe aceptado gasto',
          name: 'requerimientoImporteAceptadoGasto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe rechazado gasto',
          name: 'requerimientoImporteRechazadoGasto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Motivo rechazo gasto',
          name: 'requerimientoMotivoRechazoGasto' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe alegado gasto',
          name: 'requerimientoImporteAlegadoGasto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Alegación gasto',
          name: 'requerimientoAlegaciónGasto' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );
      //Importes Aceptados Proyecto
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe aceptado proyecto',
          name: 'requerimientoImporteAceptadoProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe aceptado CD proyecto',
          name: 'requerimientoImporteAceptadoCDProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe aceptado CI proyecto',
          name: 'requerimientoImporteAceptadoCIProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      //Importes Rechazados Proyecto
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe rechazado proyecto',
          name: 'requerimientoImporteRechazadoProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe rechazado CD proyecto',
          name: 'requerimientoImporteRechazadoCDProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe rechazado CI proyecto',
          name: 'requerimientoImporteRechazadoCIProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      //Importe Reintegrar Proyecto
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe reintegrar proyecto',
          name: 'requerimientoImporteReintegrarProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe reintegrar CD proyecto',
          name: 'requerimientoImporteReintegrarCDProyecto' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Importe reintegrar CI proyecto',
          name: 'requerimientoImporteReintegrarCIProyecto' + i,
          type: ColumnType.NUMBER
        }
      );

      columns.push(
        {
          title: 'Requerimiento ' + i + ': Subvención justificada',
          name: 'requerimientoSubvencionJustificada' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Defecto subvención',
          name: 'requerimientoDefectoSubvencion' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Anticipo justificado',
          name: 'requerimientoAnticipoJustificado' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Defecto anticipo',
          name: 'requerimientoDefectoAnticipo' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': Recurso estimado',
          name: 'requerimientoRecursoEstimado' + i,
          type: ColumnType.BOOLEAN
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': fecha alegación',
          name: 'requerimientoFechaAlegacion' + i,
          type: ColumnType.DATE
        }
      );
      //Importes Alegados
      columns.push(
        {
          title: 'Requerimiento ' + i + ': importe alegado',
          name: 'requerimientoImporteAlegado' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': importe alegado CD',
          name: 'requerimientoImporteAlegadoCD' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': importe alegado CI',
          name: 'requerimientoImporteAlegadoCI' + i,
          type: ColumnType.NUMBER
        }
      );
      //Importes Reintegrados
      columns.push(
        {
          title: 'Requerimiento ' + i + ': importe reintegrado',
          name: 'requerimientoImporteReintegrado' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': importe reintegrado CD',
          name: 'requerimientoImporteReintegradoCD' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': importe reintegrado CI',
          name: 'requerimientoImporteReintegradoCI' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': intereses reintegrados',
          name: 'requerimientoInteresesReintegrados' + i,
          type: ColumnType.NUMBER
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': fecha reintegro',
          name: 'requerimientoFechaReintegro' + i,
          type: ColumnType.DATE
        }
      );
      columns.push(
        {
          title: 'Requerimiento ' + i + ': justificante reintegro',
          name: 'requerimientoJustificanteReintegro' + i,
          type: ColumnType.STRING,
          format: '#'
        }
      );
    }
  }

  public fillRows(
    resultados: IGastoJustificadoReportData[],
    index: number, reportConfig: IReportConfig<IGastosJustificadosReportOptions>
  ): any[] {
    const gasto = resultados[index];

    if (!!!gasto) {
      return;
    }
    const elementsRow: any[] = [];
    try {
      elementsRow.push(gasto.justificacionId || '');

      //Campos variables
      this.columns.forEach(column => {
        const detail = !!gasto.detalle?.campos ? gasto.detalle.campos.find(campo => campo.nombre === column.nombre) : null;

        elementsRow.push(detail ? detail.valor : '');
      });

      elementsRow.push(gasto.proyectoId || '');
      elementsRow.push(gasto.proyectoSgi?.id || '');
      elementsRow.push(gasto.proyectoSgi?.codigoExterno || '');
      elementsRow.push(gasto.proyectoSgi?.titulo || '');
      elementsRow.push(LuxonUtils.toBackend(gasto.proyectoSgi?.fechaInicio));
      elementsRow.push(LuxonUtils.toBackend(gasto.proyectoSgi?.fechaFin));
      elementsRow.push(LuxonUtils.toBackend(gasto.proyectoSgi?.fechaFinDefinitiva));

      //Responsables, hay que rellenar todas las columnas hayan o no datos.
      this.fillResponsablesRows(gasto.responsables, elementsRow);
      elementsRow.push(gasto?.tituloConvocatoria || '');
      this.fillEntidadesFinanciadorasRows(gasto.entidadesFinanciadoras, elementsRow);
      //Importes
      elementsRow.push(gasto.importeConcedido);
      elementsRow.push(gasto.importeConcedidoCD);
      elementsRow.push(gasto.importeConcedidoCI);
      elementsRow.push(LuxonUtils.toBackend(gasto.fechaUltimaJustificacion));
      this.fillProyectoSeguimientoJustificacionRows(gasto.proyectoSeguimientoJustificacion, elementsRow);
      this.fillGastoRequerimientosRows(gasto.requerimientos, elementsRow);
    } catch (ex) {
      this.logger.error(ex);
    }

    return elementsRow;
  }

  private fillResponsablesRows(responsables: { nombre: string, apellidos: string, email: string }[], elementsRow: any[]): void {
    for (let i = 0; i < this.maxResponsables; i++) {
      if (!!responsables && responsables.length > i) {
        elementsRow.push(responsables[i].nombre || '');
        elementsRow.push(responsables[i].apellidos || '');
        elementsRow.push(responsables[i].email || '');
      } else {
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
      }
    }
  }

  private fillEntidadesFinanciadorasRows(
    entidadesFinanciadoras: { nombre: string, numIdentificacion: string }[],
    elementsRow: any[]
  ): void {
    for (let i = 0; i < this.maxEntidades; i++) {
      if (!!entidadesFinanciadoras && entidadesFinanciadoras.length > i) {
        elementsRow.push(entidadesFinanciadoras[i].nombre || '');
        elementsRow.push(entidadesFinanciadoras[i].numIdentificacion || '');
      } else {
        elementsRow.push('');
        elementsRow.push('');
      }
    }
  }

  private fillProyectoSeguimientoJustificacionRows(
    proyectoSeguimientoJustificacion: IProyectoSeguimientoJustificacion,
    elementsRow: any[]
  ): void {

    elementsRow.push(proyectoSeguimientoJustificacion?.importeJustificado);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeJustificadoCD);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeJustificadoCI);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeNoEjecutado);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeNoEjecutadoCD);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeNoEjecutadoCI);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeReintegrado);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeReintegradoCD);
    elementsRow.push(proyectoSeguimientoJustificacion?.importeReintegradoCI);
    elementsRow.push(LuxonUtils.toBackend(proyectoSeguimientoJustificacion?.fechaReintegro));
    elementsRow.push(proyectoSeguimientoJustificacion?.justificanteReintegro || '');
  }

  private fillGastoRequerimientosRows(requerimientos: IGastoRequerimiento[], elementsRow: any[]): void {
    for (let i = 0; i < this.maxRequerimientos; i++) {
      if (!!requerimientos && requerimientos.length > i) {
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.numRequerimiento || '');
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.tipoRequerimiento?.nombre || '');
        elementsRow.push(LuxonUtils.toBackend(requerimientos[i].requerimientosJustificacionAlegacion?.fechaNotificacion));
        elementsRow.push(this.notIsNullAndNotUndefined(requerimientos[i].aceptado) ? this.getI18nBooleanYesNo(requerimientos[i].aceptado) : '');
        elementsRow.push(requerimientos[i].importeAceptado);
        elementsRow.push(requerimientos[i].importeRechazado);
        elementsRow.push(requerimientos[i].incidencia || '');
        elementsRow.push(requerimientos[i].importeAlegado);
        elementsRow.push(requerimientos[i].alegacion || '');
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeAceptado);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeAceptadoCd);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeAceptadoCi);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeRechazado);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeRechazadoCd);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeRechazadoCi);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeReintegrar);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeReintegrarCd);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.importeReintegrarCi);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.subvencionJustificada);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.defectoSubvencion);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.anticipoJustificado);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.defectoAnticipo);
        elementsRow.push(
          this.notIsNullAndNotUndefined(requerimientos[i].requerimientosJustificacionAlegacion?.recursoEstimado) ?
            this.getI18nBooleanYesNo(requerimientos[i].requerimientosJustificacionAlegacion?.recursoEstimado) : ''
        );
        elementsRow.push(
          LuxonUtils.toBackend(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.fechaAlegacion)
        );
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.importeAlegado);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.importeAlegadoCd);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.importeAlegadoCi);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.importeReintegrado);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.importeReintegradoCd);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.importeReintegradoCi);
        elementsRow.push(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.interesesReintegrados);
        elementsRow.push(
          LuxonUtils.toBackend(requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.fechaReintegro)
        );
        elementsRow.push(
          requerimientos[i].requerimientosJustificacionAlegacion?.alegacionRequerimiento?.justificanteReintegro || ''
        );
      } else {
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
      }
    }
  }
}
