import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { TipoFaseService } from '@core/services/csp/tipo-fase.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';

const MSG_CHECK = marker('eti.csp.check.continuar');

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-fase-modal',
  templateUrl: './modelo-ejecucion-tipo-fase-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-fase-modal.component.scss']
})
export class ModeloEjecucionTipoFaseModalComponent extends
  BaseModalComponent<IModeloTipoFase, ModeloEjecucionTipoFaseModalComponent> implements OnInit, OnDestroy {

  tipoFases$: Observable<ITipoFase[]>;
  private subscriptions: Subscription[] = [];

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoFaseModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      modeloTipoFase: IModeloTipoFase
      tipoFases: ITipoFase[]
    },

    private readonly tipoFaseService: TipoFaseService,
    protected readonly dialogService: DialogService
  ) {
    super(logger, snackBarService, matDialogRef, data.modeloTipoFase);
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'constructor()', 'start');
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.tipoFases$ = this.tipoFaseService.findAll().pipe(
      switchMap((result: SgiRestListResult<ITipoFase>) => {
        const list = this.filterExistingTipoFase(result);
        return of(list);
      })

    );
  }

  /**
   *  Comprueba que no hay 2 valores en la lista
   * @param result resultado
   */
  private filterExistingTipoFase(result: SgiRestListResult<ITipoFase>): ITipoFase[] {
    return result.items.filter((tipoFase: ITipoFase) => {
      return this.data.tipoFases.find((currentTipo) => currentTipo.id === tipoFase.id) ? false : true;
    });
  }

  protected getDatosForm(): IModeloTipoFase {
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, `${this.getDatosForm.name}()`, 'start');
    const modeloTipoFase = this.data.modeloTipoFase;
    modeloTipoFase.tipoFase = this.formGroup.get('tipoFase').value;
    modeloTipoFase.convocatoria = this.formGroup.get('checkConvocatoria').value;
    modeloTipoFase.proyecto = this.formGroup.get('checkProyecto').value;
    modeloTipoFase.solicitud = false;
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, `${this.getDatosForm.name}()`, 'end');
    return modeloTipoFase;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, `${this.getFormGroup.name}()`, 'start');
    const formGroup = new FormGroup({
      tipoFase: new FormControl(this.data.modeloTipoFase?.tipoFase),
      checkConvocatoria: new FormControl(this.data.modeloTipoFase?.convocatoria),
      checkProyecto: new FormControl(this.data.modeloTipoFase?.proyecto)
    });
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, `${this.getFormGroup.name}()`, 'end');
    return formGroup;
  }

  /**
   * Activar/desactivar convocatoria
   * @param event evento
   */
  setConvocatoriaActivo(event) {
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'setConvocatoriaActivo(event) - start');
    this.formGroup.controls.checkConvocatoria.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'setConvocatoriaActivo(event) - end');
  }

  /**
   * Activar/desactivar proyecto
   * @param event evento
   */
  setProyectoActivo(event) {
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'setProyectoActivo(event) - start');
    this.formGroup.controls.checkProyecto.setValue(event.checked);
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'setProyectoActivo(event) - end');
  }

  /**
   * Miramos si está activado al menos un check y si no lo está
   * mostramos mensaje para que confirme que lo quiere así
   */
  confirmActivosCheck() {
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'alertActivosCheck() - start');
    if ((this.formGroup.controls.checkProyecto.value === null || this.formGroup.controls.checkProyecto.value === false)
      && (this.formGroup.controls.checkConvocatoria.value === null || this.formGroup.controls.checkConvocatoria.value === false)) {
      this.subscriptions.push(this.dialogService.showConfirmation(MSG_CHECK).subscribe((aceptado) => {
        if (aceptado) {
          const modeloTipoFase = this.data.modeloTipoFase;
          modeloTipoFase.convocatoria = true;
          modeloTipoFase.proyecto = false;
          this.saveOrUpdate();
        } else {
          return false;
        }
      }));
    } else {
      this.saveOrUpdate();
    }
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'alertActivosCheck() - end');
  }


  ngOnDestroy() {
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions?.forEach(x => x.unsubscribe());
    this.logger.debug(ModeloEjecucionTipoFaseModalComponent.name, 'ngOnDestroy()', 'end');
  }

}
