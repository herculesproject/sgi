import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { TipoHitoService } from '@core/services/csp/tipo-hito.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { requiredChecked } from '@core/validators/checkbox-validator';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

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
    const disponible = this.formGroup.controls.disponible as FormGroup;
    modeloTipoHito.tipoHito = this.formGroup.get('tipoHito').value;
    modeloTipoHito.convocatoria = disponible.get('convocatoria').value;
    modeloTipoHito.proyecto = disponible.get('proyecto').value;
    modeloTipoHito.solicitud = disponible.get('solicitud').value;
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getDatosForm.name}()`, 'end');
    return modeloTipoHito;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getFormGroup.name}()`, 'start');
    const formGroup = new FormGroup({
      tipoHito: new FormControl(this.data.modeloTipoHito?.tipoHito, Validators.required),
      disponible: new FormGroup({
        convocatoria: new FormControl(this.data.modeloTipoHito?.convocatoria),
        proyecto: new FormControl(this.data.modeloTipoHito?.proyecto),
        solicitud: new FormControl(this.data.modeloTipoHito?.solicitud)
      }, [requiredChecked(1)]),
    });
    this.logger.debug(ModeloEjecucionTipoHitoModalComponent.name, `${this.getFormGroup.name}()`, 'end');
    return formGroup;
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
