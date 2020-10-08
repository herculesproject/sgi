import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { ITipoFinalidad } from '@core/models/csp/tipo-finalidad';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SgiRestListResult, SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { Subscription } from 'rxjs/internal/Subscription';
import { TipoFinalidadModalComponent } from '../tipo-finalidad-modal/tipo-finalidad-modal.component';

const MSG_ERROR = marker('csp.tipo.finalidad.listado.error');
const MSG_ERROR_SAVE = marker('csp.tipo.finalidad.añadir.error');
const MSG_ERROR_UPDATE = marker('csp.tipo.finalidad.actualizar.error');
const MSG_SAVE = marker('csp.tipo.finalidad.añadir');
const MSG_UPDATE = marker('csp.tipo.finalidad.actualizar');
const MSG_DEACTIVATE = marker('csp.tipo.finalidad.desactivar');
const MSG_SUCCESS_DEACTIVATE = marker('csp.tipo.finalidad.desactivar.correcto');
const MSG_ERROR_DEACTIVATE = marker('csp.tipo.finalidad.desactivar.error');

@Component({
  selector: 'sgi-tipo-finalidad-listado',
  templateUrl: './tipo-finalidad-listado.component.html',
  styleUrls: ['./tipo-finalidad-listado.component.scss']
})
export class TipoFinalidadListadoComponent extends AbstractTablePaginationComponent<ITipoFinalidad> implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  tiposFinalidad$: Observable<ITipoFinalidad[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly tipoFinalidadService: TipoFinalidadService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService
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
    this.onClearFilters();
    this.logger.debug(TipoFinalidadListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<ITipoFinalidad>> {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.createObservable.name}()`, 'start');
    const observable$ = this.tipoFinalidadService.findAll(this.getFindOptions());
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.createObservable.name}()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.initColumns.name}()`, 'start');
    this.columnas = ['nombre', 'descripcion', 'activo', 'acciones'];
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.initColumns.name}()`, 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.loadTable.name}(${reset})`, 'start');
    this.tiposFinalidad$ = this.getObservableLoadTable(reset);
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.loadTable.name}(${reset})`, 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.createFilters.name}()`, 'start');
    const filtros = [];
    this.addFiltro(filtros, 'nombre', SgiRestFilterType.LIKE, this.formGroup.controls.nombre.value);
    this.addFiltro(filtros, 'activo', SgiRestFilterType.EQUALS, this.formGroup.controls.activo.value);
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.createFilters.name}()`, 'end');
    return filtros;
  }

  onClearFilters() {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.onClearFilters.name}()`, 'start');
    this.formGroup = new FormGroup({
      nombre: new FormControl(''),
      activo: new FormControl('true')
    });
    this.onSearch();
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.onClearFilters.name}()`, 'end');
  }

  /**
   * Abre un modal para añadir o actualizar un tipo de finalidad
   *
   * @param tipoFinalidad Tipo de finalidad
   */
  openModal(tipoFinalidad?: ITipoFinalidad): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.openModal.name}(tipoFinalidad?: ITipoFinalidad)`, 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: tipoFinalidad ? Object.assign({}, tipoFinalidad) : { activo: true } as ITipoFinalidad
    };
    const dialogRef = this.matDialog.open(TipoFinalidadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITipoFinalidad) => {
        if (result) {
          let subscription: Subscription;
          if (tipoFinalidad) {
            subscription = this.tipoFinalidadService.update(tipoFinalidad.id, result).subscribe(
              () => {
                this.snackBarService.showSuccess(MSG_UPDATE);
                this.loadTable();
                this.logger.debug(TipoFinalidadListadoComponent.name, `${this.openModal.name}(tipoFinalidad?: ITipoFinalidad)`, 'end');
              },
              () => {
                this.snackBarService.showError(MSG_ERROR_UPDATE);
                this.logger.error(TipoFinalidadListadoComponent.name, `${this.openModal.name}(tipoFinalidad?: ITipoFinalidad)`, 'error');
              }
            );
          } else {
            subscription = this.tipoFinalidadService.create(result).subscribe(
              () => {
                this.snackBarService.showSuccess(MSG_SAVE);
                this.loadTable();
                this.logger.debug(TipoFinalidadListadoComponent.name, `${this.openModal.name}(tipoFinalidad?: ITipoFinalidad)`, 'end');
              },
              () => {
                this.snackBarService.showError(MSG_ERROR_SAVE);
                this.logger.error(TipoFinalidadListadoComponent.name, `${this.openModal.name}(tipoFinalidad?: ITipoFinalidad)`, 'error');
              }
            );
          }
          this.suscripciones.push(subscription);
        }
      }
    );
  }

  disableTipoFinalidad(tipoFinalidad: ITipoFinalidad): void {
    this.logger.debug(TipoFinalidadListadoComponent.name, `${this.disableTipoFinalidad.name}()`, 'start');
    const subcription = this.dialogService.showConfirmation(MSG_DEACTIVATE).subscribe(
      (accept) => {
        if (accept) {
          const deleteSubcription = this.tipoFinalidadService.deleteById(tipoFinalidad.id).subscribe(
            () => {
              this.snackBarService.showSuccess(MSG_SUCCESS_DEACTIVATE);
              this.loadTable();
              this.logger.debug(TipoFinalidadListadoComponent.name, `${this.disableTipoFinalidad.name}()`, 'end');
            },
            () => {
              this.snackBarService.showError(MSG_ERROR_DEACTIVATE);
              this.logger.error(TipoFinalidadListadoComponent.name, `${this.disableTipoFinalidad.name}()`, 'error');
            }
          );
          this.suscripciones.push(deleteSubcription);
        }
      }
    );
    this.suscripciones.push(subcription);
  }
}
