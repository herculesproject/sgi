import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_ERROR = marker('csp.proyecto.listado.error');
const MSG_BUTTON_NEW = marker('footer.csp.proyecto.crear');
const MSG_DEACTIVATE = marker('csp.proyecto.desactivar');
const MSG_SUCCESS_DEACTIVATE = marker('csp.proyecto.desactivar.correcto');
const MSG_ERROR_DEACTIVATE = marker('csp.proyecto.desactivar.error');
const MSG_REACTIVE = marker('csp.proyecto.reactivar');
const MSG_SUCCESS_REACTIVE = marker('csp.proyecto.reactivar.correcto');
const MSG_ERROR_REACTIVE = marker('csp.proyecto.reactivar.error');

@Component({
  selector: 'sgi-proyecto-listado',
  templateUrl: './proyecto-listado.component.html',
  styleUrls: ['./proyecto-listado.component.scss']
})
export class ProyectoListadoComponent extends AbstractTablePaginationComponent<IProyecto> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear = MSG_BUTTON_NEW;

  TipoEstadoProyecto = TipoEstadoProyecto;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  proyecto$: Observable<IProyecto[]>;

  estadoProyecto = Object.keys(TipoEstadoProyecto).map<string>(
    (key) => TipoEstadoProyecto[key]);

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly proyectoService: ProyectoService,
    private readonly dialogService: DialogService,
    public authService: SgiAuthService,
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(ProyectoListadoComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(ProyectoListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.formGroup = new FormGroup({
      titulo: new FormControl(''),
      acronimo: new FormControl(''),
      estado: new FormControl('')
    });
    this.filter = this.createFilters();
    this.logger.debug(ProyectoListadoComponent.name, 'ngOnInit()', 'end');
  }

  onClearFilters() {
    this.logger.debug(ProyectoListadoComponent.name, `${this.onClearFilters.name}()`, 'start');
    this.formGroup.controls.estado.setValue('');
    this.formGroup.controls.titulo.setValue('');
    this.formGroup.controls.acronimo.setValue('');
    this.onSearch();
    this.logger.debug(ProyectoListadoComponent.name, `${this.onClearFilters.name}()`, 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IProyecto>> {
    this.logger.debug(ProyectoListadoComponent.name, `${this.createObservable.name}()`, 'start');
    const observable$ = this.proyectoService.findAll(this.getFindOptions());
    this.logger.debug(ProyectoListadoComponent.name, `${this.createObservable.name}()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(ProyectoListadoComponent.name, `${this.initColumns.name}()`, 'start');
    this.columnas = ['acronimo', 'titulo', 'fechaInicio', 'fechaFin', 'estado', 'activo', 'acciones'];
    this.logger.debug(ProyectoListadoComponent.name, `${this.initColumns.name}()`, 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(ProyectoListadoComponent.name, `${this.loadTable.name}(${reset})`, 'start');
    this.proyecto$ = this.getObservableLoadTable(reset);
    this.logger.debug(ProyectoListadoComponent.name, `${this.loadTable.name}(${reset})`, 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(ProyectoListadoComponent.name, `${this.createFilters.name}()`, 'start');
    const filtros = [];
    this.addFiltro(filtros, 'acronimo', SgiRestFilterType.LIKE, this.formGroup.controls.acronimo.value);
    this.addFiltro(filtros, 'titulo', SgiRestFilterType.LIKE, this.formGroup.controls.titulo.value);
    this.addFiltro(filtros, 'acronimo', SgiRestFilterType.LIKE, this.formGroup.controls.acronimo.value);

    const estado = Object.keys(TipoEstadoProyecto)
      .filter(key => TipoEstadoProyecto[key] === this.formGroup.controls.estado.value)[0];
    this.addFiltro(filtros, 'estado.estado', SgiRestFilterType.EQUALS, estado);

    this.logger.debug(ProyectoListadoComponent.name, `${this.createFilters.name}()`, 'end');
    return filtros;
  }

  /**
   * Desactivar proyecto
   * @param proyecto proyecto
   */
  deactivateProyecto(proyecto: IProyecto): void {
    this.logger.debug(ProyectoListadoComponent.name, `${this.deactivateProyecto.name}()`, 'start');
    const subcription = this.dialogService.showConfirmation(MSG_DEACTIVATE).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.proyectoService.desactivar(proyecto.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_DEACTIVATE);
          this.loadTable();
          this.logger.debug(ProyectoListadoComponent.name,
            `${this.deactivateProyecto.name}(proyecto: ${proyecto})`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_DEACTIVATE);
          this.logger.error(ProyectoListadoComponent.name,
            `${this.deactivateProyecto.name}(proyecto: ${proyecto})`, 'error');
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activamos una proyecto
   * @param proyecto proyecto
   */
  activateProyecto(proyecto: IProyecto): void {
    this.logger.debug(ProyectoListadoComponent.name,
      `${this.activateProyecto.name}(proyecto: ${proyecto})`, 'start');

    const suscription = this.dialogService.showConfirmation(MSG_REACTIVE).pipe(
      switchMap((accept) => {
        if (accept) {
          proyecto.activo = true;
          return this.proyectoService.reactivar(proyecto.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_REACTIVE);
          this.loadTable();
          this.logger.debug(ProyectoListadoComponent.name,
            `${this.activateProyecto.name}(proyecto: ${proyecto})`, 'end');
        },
        () => {
          proyecto.activo = false;
          this.snackBarService.showError(MSG_ERROR_REACTIVE);
          this.logger.error(ProyectoListadoComponent.name,
            `${this.activateProyecto.name}(proyecto: ${proyecto})`, 'error');
        }
      );
    this.suscripciones.push(suscription);
  }

}

