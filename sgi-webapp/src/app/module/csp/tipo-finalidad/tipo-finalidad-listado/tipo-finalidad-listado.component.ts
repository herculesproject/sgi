import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { ITipoFinalidad } from '@core/models/csp/tipo-finalidad';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestListResult, SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_BUTTON_NEW = marker('footer.csp.tipo.finalidad.crear');
const MSG_ERROR = marker('csp.tipo.finalidad.listado.error');

@Component({
  selector: 'sgi-tipo-finalidad-listado',
  templateUrl: './tipo-finalidad-listado.component.html',
  styleUrls: ['./tipo-finalidad-listado.component.scss']
})
export class TipoFinalidadListadoComponent extends AbstractTablePaginationComponent<ITipoFinalidad> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear = MSG_BUTTON_NEW;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  tiposFinalidad$: Observable<ITipoFinalidad[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly tipoFinalidadService: TipoFinalidadService
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(TipoFinalidadListadoComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(TipoFinalidadListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.formGroup = new FormGroup({
      nombre: new FormControl(''),
      activo: new FormControl('')
    });
    this.logger.debug(TipoFinalidadListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<ITipoFinalidad>> {
    this.logger.debug(TipoFinalidadListadoComponent.name, 'createObservable()', 'start');
    const observable$ = this.tipoFinalidadService.findAll(this.getFindOptions());
    this.logger.debug(TipoFinalidadListadoComponent.name, 'createObservable()', 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, 'initColumns()', 'start');
    this.columnas = ['nombre', 'descripcion', 'activo', 'acciones'];
    this.logger.debug(TipoFinalidadListadoComponent.name, 'initColumns()', 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, `loadTable(${reset})`, 'start');
    this.tiposFinalidad$ = this.getObservableLoadTable(reset);
    this.logger.debug(TipoFinalidadListadoComponent.name, `loadTable(${reset})`, 'end');
  }

  protected createFilters(formGroup?: FormGroup): SgiRestFilter[] {
    this.logger.debug(TipoFinalidadListadoComponent.name, `createFilters()`, 'start');
    const filtros = [];
    this.addFiltro(filtros, 'nombre', SgiRestFilterType.LIKE, this.formGroup.controls.nombre.value);
    this.addFiltro(filtros, 'activo', SgiRestFilterType.EQUALS, this.formGroup.controls.activo.value);
    this.logger.debug(TipoFinalidadListadoComponent.name, `createFilters()`, 'end');
    return filtros;
  }
}
