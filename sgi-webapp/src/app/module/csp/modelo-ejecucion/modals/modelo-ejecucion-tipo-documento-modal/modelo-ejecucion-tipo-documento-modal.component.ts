import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { ITipoDocumento, ITipoFase } from '@core/models/csp/tipos-configuracion';
import { TipoDocumentoService } from '@core/services/csp/tipo-documento.service';
import { TipoFaseService } from '@core/services/csp/tipo-fase.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SgiRestFilter, SgiRestFilterType, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-documento-modal',
  templateUrl: './modelo-ejecucion-tipo-documento-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-documento-modal.component.scss']
})
export class ModeloEjecucionTipoDocumentoModalComponent extends
  BaseModalComponent<IModeloTipoDocumento, ModeloEjecucionTipoDocumentoModalComponent> implements OnInit {
  tipoDocumentos$: Observable<ITipoDocumento[]>;
  tipoFases$: Observable<ITipoFase[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public modeloTipoDocumento: IModeloTipoDocumento,
    private readonly tipoDocumentoService: TipoDocumentoService,
    private readonly tipoFaseService: TipoFaseService
  ) {
    super(logger, snackBarService, matDialogRef, modeloTipoDocumento);
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, 'constructor()', 'start');
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, 'constructor()', 'end');
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

    this.tipoDocumentos$ = this.tipoDocumentoService.findAll(options).pipe(
      switchMap(
        (result: SgiRestListResult<ITipoDocumento>) => of(result.items)
      )
    );

    this.tipoFases$ = this.tipoFaseService.findAll(options).pipe(
      switchMap((result: SgiRestListResult<ITipoFase>) => of(result.items))
    );
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `${this.getFormGroup.name}()`, 'start');
    const formGroup = new FormGroup({
      tipoDocumento: new FormControl(this.modeloTipoDocumento?.tipoDocumento),
      tipoFase: new FormControl(this.modeloTipoDocumento?.tipoFase)
    });
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `${this.getFormGroup.name}()`, 'end');
    return formGroup;
  }

  protected getDatosForm(): IModeloTipoDocumento {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `${this.getDatosForm.name}()`, 'start');
    const modeloTipoDocumento = this.modeloTipoDocumento;
    modeloTipoDocumento.tipoDocumento = FormGroupUtil.getValue(this.formGroup, 'tipoDocumento');
    modeloTipoDocumento.tipoFase = FormGroupUtil.getValue(this.formGroup, 'tipoFase');
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `${this.getDatosForm.name}()`, 'end');
    return modeloTipoDocumento;
  }

  equals(o1: ITipoDocumento, o2: ITipoDocumento) {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `${this.equals.name}(o1: ${o1}, o2: ${o2})`, 'start');
    const result = o1?.id === o2?.id;
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `${this.equals.name}(o1: ${o1}, o2: ${o2})`, 'start');
    return result;
  }
}
