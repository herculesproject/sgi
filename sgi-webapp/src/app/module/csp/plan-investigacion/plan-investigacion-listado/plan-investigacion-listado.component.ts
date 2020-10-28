import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IPlan } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { PlanService } from '@core/services/csp/plan.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';

const MSG_ERROR = marker('csp.plan.investigacion.listado.error');
const MSG_BUTTON_NEW = marker('footer.csp.plan.investigacion.crear');
const MSG_DEACTIVATE = marker('csp.plan.investigacion.desactivar');
const MSG_SUCCESS_DEACTIVATE = marker('csp.plan.investigacion.desactivar.correcto');
const MSG_ERROR_DEACTIVATE = marker('csp.plan.investigacion.desactivar.error');
const MSG_REACTIVE = marker('csp.plan.investigacion.reactivar');
const MSG_SUCCESS_REACTIVE = marker('csp.plan.investigacion.reactivar.correcto');
const MSG_ERROR_REACTIVE = marker('csp.plan.investigacion.reactivar.error');

@Component({
  selector: 'sgi-plan-investigacion-listado',
  templateUrl: './plan-investigacion-listado.component.html',
  styleUrls: ['./plan-investigacion-listado.component.scss']
})
export class PlanInvestigacionListadoComponent extends AbstractTablePaginationComponent<IPlan> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear = MSG_BUTTON_NEW;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  plan$: Observable<IPlan[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly planService: PlanService,
    private readonly dialogService: DialogService
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(PlanInvestigacionListadoComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(PlanInvestigacionListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(PlanInvestigacionListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.formGroup = new FormGroup({
      nombre: new FormControl(''),
      activo: new FormControl('true')
    });
    this.filter = this.createFilters();
    this.logger.debug(PlanInvestigacionListadoComponent.name, 'ngOnInit()', 'end');
  }

  onClearFilters() {
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.onClearFilters.name}()`, 'start');
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.nombre.setValue('');
    this.onSearch();
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.onClearFilters.name}()`, 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IPlan>> {
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.createObservable.name}()`, 'start');
    const observable$ = this.planService.findTodos(this.getFindOptions());
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.createObservable.name}()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.initColumns.name}()`, 'start');
    this.columnas = ['nombre', 'descripcion', 'activo', 'acciones'];
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.initColumns.name}()`, 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.loadTable.name}(${reset})`, 'start');
    this.plan$ = this.getObservableLoadTable(reset);
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.loadTable.name}(${reset})`, 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.createFilters.name}()`, 'start');
    const filtros = [];
    this.addFiltro(filtros, 'nombre', SgiRestFilterType.LIKE, this.formGroup.controls.nombre.value);
    if (this.formGroup.controls.activo.value !== 'todos') {
      this.addFiltro(filtros, 'activo', SgiRestFilterType.EQUALS, this.formGroup.controls.activo.value);
    }
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.createFilters.name}()`, 'end');
    return filtros;
  }

  /**
   * Desactivar plan
   * @param plan plan
   */
  deactivatePlan(plan: IPlan): void {
    this.logger.debug(PlanInvestigacionListadoComponent.name, `${this.deactivatePlan.name}()`, 'start');
    const subcription = this.dialogService.showConfirmation(MSG_DEACTIVATE).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.planService.deleteById(plan.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_DEACTIVATE);
          this.loadTable();
          this.logger.debug(PlanInvestigacionListadoComponent.name,
            `${this.deactivatePlan.name}(plan: ${plan})`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_DEACTIVATE);
          this.logger.error(PlanInvestigacionListadoComponent.name,
            `${this.deactivatePlan.name}(plan: ${plan})`, 'error');
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activamos un plan desactivado
   * @param plan plan
   */
  activePlan(plan: IPlan): void {
    this.logger.debug(PlanInvestigacionListadoComponent.name,
      `${this.activePlan.name}(plan: ${plan})`, 'start');

    const suscription = this.dialogService.showConfirmation(MSG_REACTIVE).pipe(
      switchMap((accept) => {
        if (accept) {
          plan.activo = true;
          return this.planService.update(plan.id, plan);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_REACTIVE);
          this.loadTable();
          this.logger.debug(PlanInvestigacionListadoComponent.name,
            `${this.activePlan.name}(plan: ${plan})`, 'end');
        },
        () => {
          plan.activo = false;
          this.snackBarService.showError(MSG_ERROR_REACTIVE);
          this.logger.error(PlanInvestigacionListadoComponent.name,
            `${this.activePlan.name}(plan: ${plan})`, 'error');
        }
      );
    this.suscripciones.push(suscription);
  }

}
