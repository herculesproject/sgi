import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { TipoHitoService } from '@core/services/csp/tipo-hito.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_CHECK = marker('eti.csp.hito.check.continuar');

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
    @Inject(MAT_DIALOG_DATA) public data: {
      modeloTipoHito: IModeloTipoHito
      tipoHitos: ITipoHito[]
    },

    private readonly tipoHitoService: TipoHitoService,
    protected readonly dialogService: DialogService
  ) {
    super(logger, snackBarService, matDialogRef, data.modeloTipoHito);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'constructor()', 'start');
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    super.ngOnInit();
    const options = {
      filters: [{
        field: 'activo',
        type: SgiRestFilterType.EQUALS,
        value: 'true',
      } as SgiRestFilter],
    } as SgiRestFindOptions;
    this.tipoHitos$ = this.tipoHitoService.findAll(options).pipe(
      switchMap((result: SgiRestListResult<ITipoHito>) => {
        const list = this.filterExistingTipoFase(result);
        return of(list);
      })
    );
  }

  /**
   *  Comprueba que no hay 2 valores en la lista
   * @param result resultado
   */
  private filterExistingTipoFase(result: SgiRestListResult<ITipoHito>): ITipoHito[] {
    return result.items.filter((tipoFase: ITipoHito) => {
      return this.data.tipoHitos.find((currentTipo) => currentTipo.id === tipoFase.id) ? false : true;
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
  setConvocatoriaActivo(event) {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'setConvocatoriaActivo(event) - start');
    this.formGroup.controls.checkConvocatoria.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'setConvocatoriaActivo(event) - end');
  }

  /**
   * Activar/desactivar proyecto
   * @param event evento
   */
  setProyectoActivo(event) {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'setProyectoActivo(event) - start');
    this.formGroup.controls.checkProyecto.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'setProyectoActivo(event) - end');
  }

  /**
   * Activar/desactivar solictud
   * @param event evento
   */
  setSolictudActivo(event) {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'setSolictudActivo(event) - start');
    this.formGroup.controls.checkSolictud.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'setSolictudActivo(event) - end');
  }

  /**
   * Miramos si está activado al menos un check y si no lo está
   * mostramos mensaje para que confirme que lo quiere así
   */
  confirmActivosCheck() {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'alertActivosCheck() - start');
    if ((this.formGroup.controls.checkProyecto.value === null || this.formGroup.controls.checkProyecto.value === false)
      && (this.formGroup.controls.checkConvocatoria.value === null || this.formGroup.controls.checkConvocatoria.value === false)) {
      this.subscriptions.push(this.dialogService.showConfirmation(MSG_CHECK).subscribe((aceptado) => {
        if (aceptado) {
          const modeloTipoFase = this.data.modeloTipoHito;
          modeloTipoFase.convocatoria = true;
          modeloTipoFase.proyecto = false;
          modeloTipoFase.solicitud = false;
          this.saveOrUpdate();
        } else {
          return false;
        }
      }));
    } else {
      this.saveOrUpdate();
    }
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'alertActivosCheck() - end');
  }


  ngOnDestroy() {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions?.forEach(x => x.unsubscribe());
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, 'ngOnDestroy()', 'end');
  }

}
