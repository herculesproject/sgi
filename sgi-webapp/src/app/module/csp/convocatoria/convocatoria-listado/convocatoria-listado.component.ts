import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult, SgiRestSortDirection } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { delay } from 'rxjs/operators';

import { ROUTE_NAMES } from '@core/route.names';

import { SnackBarService } from '@core/services/snack-bar.service';
import { IConvocatoria } from '@core/models/csp/convocatoria';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';

const MSG_BUTTON_NEW = marker('footer.csp.convocatoria.crear');
const MSG_ERROR = marker('csp.convocatoria.listado.error');

@Component({
  selector: 'sgi-convocatoria-listado',
  templateUrl: './convocatoria-listado.component.html',
  styleUrls: ['./convocatoria-listado.component.scss']
})
export class ConvocatoriaListadoComponent extends AbstractTablePaginationComponent<IConvocatoria> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  convocatorias$: Observable<IConvocatoria[]>;
  buscadorFormGroup: FormGroup;
  textoCrear = MSG_BUTTON_NEW;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly convocatoriaService: ConvocatoriaService) {
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
    this.buscadorFormGroup = new FormGroup({
      titulo: new FormControl(''),
      referencia: new FormControl(''),
    });
    this.logger.debug(ConvocatoriaListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IConvocatoria>> {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createObservable.name}()`, 'start');
    const observable$ = this.convocatoriaService.findConvocatoria(this.getFindOptions()).pipe(
      delay(1)
    );
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createObservable.name}()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.initColumns.name}()`, 'start');
    this.columnas = [
      'referencia', 'titulo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
      'estadoConvocante', 'planInvestigacion', 'entidadFinanciadora', 'fuenteFinanciacion',
      'activo', 'acciones'
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
    this.addFiltro(filtros, 'titulo', SgiRestFilterType.LIKE, this.formGroup.controls.comite.value.id);
    this.addFiltro(filtros, 'referencia', SgiRestFilterType.LIKE,
      this.formGroup.controls.memoriaNumReferencia.value);
    this.logger.debug(ConvocatoriaListadoComponent.name, `${this.createFilters.name}()`, 'end');
    return filtros;
  }

}
