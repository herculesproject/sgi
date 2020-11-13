import { Component, OnInit } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { FormGroup, FormControl } from '@angular/forms';
import { forkJoin, Observable, of } from 'rxjs';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { catchError, map, switchMap } from 'rxjs/operators';

import { ROUTE_NAMES } from '@core/route.names';

import { SnackBarService } from '@core/services/snack-bar.service';
import { IConvocatoria } from '@core/models/csp/convocatoria';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';

const MSG_BUTTON_NEW = marker('footer.csp.convocatoria.crear');
const MSG_ERROR = marker('csp.convocatoria.listado.error');

interface IConvocatoriaListado {
  convocatoria: IConvocatoria;
  fase: IConvocatoriaFase;
  entidadConvocante: IConvocatoriaEntidadConvocante;
  entidadConvocanteEmpresa: IEmpresaEconomica;
  entidadFinanciadora: IConvocatoriaEntidadFinanciadora;
  entidadFinanciadoraEmpresa: IEmpresaEconomica;
}

@Component({
  selector: 'sgi-convocatoria-listado',
  templateUrl: './convocatoria-listado.component.html',
  styleUrls: ['./convocatoria-listado.component.scss']
})
export class ConvocatoriaListadoComponent extends AbstractTablePaginationComponent<IConvocatoriaListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  convocatorias$: Observable<IConvocatoriaListado[]>;
  textoCrear = MSG_BUTTON_NEW;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly empresaEconomicaService: EmpresaEconomicaService,
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(ConvocatoriaListadoComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(ConvocatoriaListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.formGroup = new FormGroup({
      codigo: new FormControl(''),
      titulo: new FormControl(''),
      activo: new FormControl('todos'),
    });
    this.filter = this.createFilters();
    this.logger.debug(ConvocatoriaListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IConvocatoriaListado>> {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createObservable.name}()`, 'start');
    const observable$ = this.convocatoriaService.findAllTodosRestringidos(this.getFindOptions()).pipe(
      map(result => {
        const convocatorias = result.items.map((convocatoria) => {
          return {
            convocatoria,
            entidadConvocante: {} as IConvocatoriaEntidadConvocante,
            entidadConvocanteEmpresa: {} as IEmpresaEconomica,
            entidadFinanciadora: {} as IConvocatoriaEntidadFinanciadora,
            entidadFinanciadoraEmpresa: {} as IEmpresaEconomica,
            fase: {} as IConvocatoriaFase
          } as IConvocatoriaListado;
        });
        return {
          page: result.page,
          total: result.total,
          items: convocatorias
        } as SgiRestListResult<IConvocatoriaListado>;
      }),
      switchMap(convocatoriaListados => {
        return forkJoin(
          convocatoriaListados.items.map(
            element => {
              return this.convocatoriaService.findEntidadesFinanciadoras(element.convocatoria.id).pipe(
                map(entidadFinanciadora => {
                  if (entidadFinanciadora.items.length > 0) {
                    element.entidadFinanciadora = entidadFinanciadora.items[0];
                  }
                  return element;
                }),
                switchMap(convocatoriaListado => {
                  if (convocatoriaListado.entidadFinanciadora.id) {
                    return this.empresaEconomicaService.findById(convocatoriaListado.entidadFinanciadora.entidadRef).pipe(
                      map(empresaEconomica => {
                        convocatoriaListado.entidadFinanciadoraEmpresa = empresaEconomica;
                        return empresaEconomica;
                      }),
                    );
                  }
                  return of({} as IEmpresaEconomica);
                }),
                catchError(() => of(element))
              );
            })
        ).pipe(
          switchMap(() => {
            return of({
              page: convocatoriaListados.page,
              total: convocatoriaListados.total,
              items: convocatoriaListados.items
            } as SgiRestListResult<IConvocatoriaListado>);
          })
        );
      }),
      switchMap(convocatoriaListados => {
        return forkJoin(
          convocatoriaListados.items.map(
            element => {
              return this.convocatoriaService.findAllConvocatoriaFases(element.convocatoria.id).pipe(
                map(convocatoriaFase => {
                  if (convocatoriaFase.items.length > 0) {
                    element.fase = convocatoriaFase.items[0];
                  }
                  return element;
                }),
                catchError(() => of(element))
              );
            })
        ).pipe(
          switchMap(() => {
            return of({
              page: convocatoriaListados.page,
              total: convocatoriaListados.total,
              items: convocatoriaListados.items
            } as SgiRestListResult<IConvocatoriaListado>);
          })
        );
      }),
      switchMap(convocatoriaListados => {
        return forkJoin(
          convocatoriaListados.items.map(
            element => {
              return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(element.convocatoria.id).pipe(
                map(convocatoriaEntidadConvocante => {
                  if (convocatoriaEntidadConvocante.items.length > 0) {
                    element.entidadConvocante = convocatoriaEntidadConvocante.items[0];
                  }
                  return element;
                }),
                switchMap(convocatoriaListado => {
                  if (convocatoriaListado.entidadFinanciadora.id) {
                    return this.empresaEconomicaService.findById(convocatoriaListado.entidadConvocante.entidadRef).pipe(
                      map(empresaEconomica => {
                        convocatoriaListado.entidadConvocanteEmpresa = empresaEconomica;
                        return empresaEconomica;
                      }),
                    );
                  }
                  return of({} as IEmpresaEconomica);
                }),
                catchError(() => of(element))
              );
            })
        ).pipe(
          switchMap(() => {
            return of({
              page: convocatoriaListados.page,
              total: convocatoriaListados.total,
              items: convocatoriaListados.items
            } as SgiRestListResult<IConvocatoriaListado>);
          })
        );
      })
    );
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createObservable.name}()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.initColumns.name}()`, 'start');
    this.columnas = [
      'codigo', 'titulo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
      'entidadConvocante', 'planInvestigacion', 'entidadFinanciadora',
      'fuenteFinanciacion', 'activo', 'acciones'
    ];
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.initColumns.name}()`, 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.loadTable.name}(${reset})`, 'start');
    this.convocatorias$ = this.getObservableLoadTable(reset);
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.loadTable.name}(${reset})`, 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createFilters.name}()`, 'start');
    const filtros = [];
    this.addFiltro(filtros, 'codigo', SgiRestFilterType.LIKE, this.formGroup.controls.codigo.value);
    this.addFiltro(filtros, 'titulo', SgiRestFilterType.LIKE, this.formGroup.controls.titulo.value);
    if (this.formGroup.controls.activo.value !== 'todos') {
      this.addFiltro(filtros, 'activo', SgiRestFilterType.EQUALS, this.formGroup.controls.activo.value);
    }
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createFilters.name}()`, 'end');
    return filtros;
  }

  onClearFilters() {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.onClearFilters.name}()`, 'start');
    this.formGroup.controls.activo.setValue('todos');
    this.formGroup.controls.codigo.setValue('');
    this.formGroup.controls.titulo.setValue('');
    this.onSearch();
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.onClearFilters.name}()`, 'end');
  }

}
