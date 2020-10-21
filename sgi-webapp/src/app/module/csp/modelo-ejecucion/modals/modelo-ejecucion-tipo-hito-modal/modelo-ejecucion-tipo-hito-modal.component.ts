import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { TipoHitoService } from '@core/services/csp/tipo-hito.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_CHECK = marker('csp.tipo.hito.check.continuar');

export interface ModeloEjecucionTipoHitoModalData {
  modeloTipoHito: IModeloTipoHito;
  tipoHitos: ITipoHito[];
}

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-hito-modal',
  templateUrl: './modelo-ejecucion-tipo-hito-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-hito-modal.component.scss']
})
export class ModeloEjecucionTipoHitoModalComponent extends
  BaseModalComponent<IModeloTipoHito, ModeloEjecucionTipoHitoModalComponent> implements OnInit, OnDestroy {

  tipoHitos$: Observable<ITipoHito[]>;
  private subscriptions: Subscription[] = [];

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoHitoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloEjecucionTipoHitoModalData,
    private readonly tipoHitoService: TipoHitoService,
    protected readonly dialogService: DialogService
  ) {
    super(logger, snackBarService, matDialogRef, data.modeloTipoHito);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'constructor()', 'start');
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (!this.data.modeloTipoHito.tipoHito) {
      this.tipoHitos$ = this.tipoHitoService.findTodos().pipe(
        switchMap((result: SgiRestListResult<ITipoHito>) => {
          const list = this.filterExistingTipoHito(result);
          return of(list);
        })
      );
    } else {
      this.tipoHitos$ = this.tipoHitoService.findTodos().pipe(
        switchMap((result: SgiRestListResult<ITipoHito>) => {
          return of(result.items);
        })
      );
    }
  }

  /**
   * Comprueba que no hay 2 valores en la lista
   *
   * @param result resultado
   */
  private filterExistingTipoHito(result: SgiRestListResult<ITipoHito>): ITipoHito[] {
    return result.items.filter((tipoHito: ITipoHito) => {
      return this.data.tipoHitos.find((currentTipo) => currentTipo.id === tipoHito.id) ? false : true;
    });
  }

  protected getDatosForm(): IModeloTipoHito {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getDatosForm.name}()`, 'start');
    const modeloTipoHito = this.data.modeloTipoHito;
    modeloTipoHito.tipoHito = this.formGroup.get('tipoHito').value;
    modeloTipoHito.convocatoria = this.formGroup.get('checkConvocatoria').value;
    modeloTipoHito.proyecto = this.formGroup.get('checkProyecto').value;
    modeloTipoHito.solicitud = this.formGroup.get('checkSolictud').value;
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getDatosForm.name}()`, 'end');
    return modeloTipoHito;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getFormGroup.name}()`, 'start');
    const formGroup = new FormGroup({
      tipoHito: new FormControl(this.data.modeloTipoHito?.tipoHito),
      checkConvocatoria: new FormControl(this.data.modeloTipoHito?.convocatoria),
      checkProyecto: new FormControl(this.data.modeloTipoHito?.proyecto),
      checkSolictud: new FormControl(this.data.modeloTipoHito?.solicitud)
    });
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getFormGroup.name}()`, 'end');
    return formGroup;
  }

  /**
   * Activar/desactivar convocatoria
   * @param event evento
   */
  setConvocatoriaActivo(event: MatCheckboxChange) {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name,
      `${this.setConvocatoriaActivo.name}(event: ${event})`, 'start');
    this.formGroup.controls.checkConvocatoria.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name,
      `${this.setConvocatoriaActivo.name}(event: ${event})`, 'end');
  }

  /**
   * Activar/desactivar proyecto
   * @param event evento
   */
  setProyectoActivo(event: MatCheckboxChange) {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name,
      `${this.setProyectoActivo.name}(event: ${event})`, 'start');
    this.formGroup.controls.checkProyecto.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name,
      `${this.setProyectoActivo.name}(event: ${event})`, 'end');
  }

  /**
   * Activar/desactivar solictud
   * @param event evento
   */
  setSolictudActivo(event: MatCheckboxChange) {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name,
      `${this.setSolictudActivo.name}(event: ${event})`, 'start');
    this.formGroup.controls.checkSolictud.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name,
      `${this.setSolictudActivo.name}(event: ${event})`, 'end');
  }

  /**
   * Miramos si está activado al menos un check y si no lo está
   * mostramos mensaje para que confirme que lo quiere así
   */
  confirmActivosCheck(): void {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.confirmActivosCheck.name}()`, 'start');
    if (this.formGroup.controls.checkProyecto.value === false
      && this.formGroup.controls.checkConvocatoria.value === false
      && this.formGroup.controls.checkSolictud.value === false) {
      this.subscriptions.push(this.dialogService.showConfirmation(MSG_CHECK).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.data.modeloTipoHito.convocatoria = true;
            this.data.modeloTipoHito.proyecto = false;
            this.data.modeloTipoHito.solicitud = false;
            this.saveOrUpdate();
          }
        }
      ));
    } else {
      this.saveOrUpdate();
    }
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.confirmActivosCheck.name}()`, 'end');
  }

  ngOnDestroy() {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(x => x.unsubscribe());
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'ngOnDestroy()', 'end');
  }

  equals(o1?: ITipoHito, o2?: ITipoHito): boolean {
    return o1.id === o2.id;
  }
}
