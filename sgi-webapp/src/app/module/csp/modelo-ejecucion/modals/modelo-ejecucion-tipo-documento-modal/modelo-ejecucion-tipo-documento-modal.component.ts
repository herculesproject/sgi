import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { ITipoDocumento, ITipoFase } from '@core/models/csp/tipos-configuracion';
import { TipoDocumentoService } from '@core/services/csp/tipo-documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export interface ModeloTipoDocumentoModalData {
  modeloTipoDocumento: IModeloTipoDocumento;
  tipoDocumentos: ITipoDocumento[];
  tipoFases: ITipoFase[];
  modeloTipoDocumentos: IModeloTipoDocumento[],
  id: number;
}

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-documento-modal',
  templateUrl: './modelo-ejecucion-tipo-documento-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-documento-modal.component.scss']
})
export class ModeloEjecucionTipoDocumentoModalComponent extends
  BaseModalComponent<IModeloTipoDocumento, ModeloEjecucionTipoDocumentoModalComponent> implements OnInit {
  tipoDocumentos$: Observable<ITipoDocumento[]>;
  tipoFases$: Observable<ITipoFase[]>;
  isFaseRequired: boolean = false;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ModeloEjecucionTipoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloTipoDocumentoModalData,
    private tipoDocumentoService: TipoDocumentoService) {
    super(logger, snackBarService, matDialogRef, data.modeloTipoDocumento);
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, 'constructor()', 'start');
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();

    this.tipoDocumentos$ = this.loadTipoDocumentos();

    this.subscriptions.push(this.formGroup.controls.tipoDocumento.valueChanges.subscribe((value: ITipoDocumento) => {
      // Actualizar el selector de fases disponibles al cambiar el tipoDocumento seleccionado
      this.tipoFases$ = (value) ? of(this.getTipoFaseDisponibles(value)) : of([]);

      // Se establece la fase como requerida si el documento ya está añadido
      const documentoSinAsignar = !this.data.modeloTipoDocumentos.find((modeloTipoDocumento) => {
        return (modeloTipoDocumento.tipoDocumento.id === value.id);
      });
      this.isFaseRequired = !documentoSinAsignar;
    }));

    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `ngOnInit()`, 'end');
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup({
      tipoDocumento: new FormControl(this.data.modeloTipoDocumento?.tipoDocumento),
      tipoFase: new FormControl(this.data.modeloTipoDocumento?.modeloTipoFase)
    });
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }

  protected getDatosForm(): IModeloTipoDocumento {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `getDatosForm()`, 'start');
    const modeloTipoDocumento = this.data.modeloTipoDocumento;
    modeloTipoDocumento.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    modeloTipoDocumento.modeloTipoFase = {
      tipoFase: this.formGroup.get('tipoFase').value
    } as IModeloTipoFase;
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `getDatosForm}()`, 'end');
    return modeloTipoDocumento;
  }

  private loadTipoDocumentos(): Observable<ITipoDocumento[]> {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `loadTipoDocumentos()`, 'start');
    return this.tipoDocumentoService.findAll().pipe(
      map(
        (result) => {
          // Se quitan de la lista los documentos que no pueden ser asignados

          let tipoDocumentoDisponibles = result.items.filter(
            (tipoDocumento) => {

              // Se filtra para quitar de la lista aquellos ya asignados sin ModeloTipoFase
              let isSeleccionable = !this.data.modeloTipoDocumentos.find((modeloTipoDocumento) => {
                return (modeloTipoDocumento.tipoDocumento.id === tipoDocumento.id)
                  && !modeloTipoDocumento.modeloTipoFase?.tipoFase?.id;
              });

              // Se filtra para quitar de la lista aquellos que ya tienen todas las fases asignadas
              if (isSeleccionable && this.data.tipoFases.length > 0) {
                const fasesDisponibles = this.getTipoFaseDisponibles(tipoDocumento);
                isSeleccionable = fasesDisponibles.length > 0;
              }
              return isSeleccionable;
            });

          return tipoDocumentoDisponibles;
        }),
      tap(() => this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `loadTipoDocumentos()`, 'end'))
    );
  }

  private getTipoFaseDisponibles(tipoDocumento: ITipoDocumento): ITipoFase[] {
    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `getTipoFaseDisponibles(tipoDocumento: ITipoDocumento): ITipoFase[]`, 'start')

    const tipoFaseDisponibles: ITipoFase[] = this.data.tipoFases.filter(
      (modeloTipofase) => {
        return !this.data.modeloTipoDocumentos.find((modeloTipoDocumento) => {
          return (modeloTipoDocumento.tipoDocumento.id === tipoDocumento.id)
            && modeloTipoDocumento.modeloTipoFase &&
            (modeloTipofase.id === modeloTipoDocumento.modeloTipoFase.tipoFase.id);
        });
      });

    this.logger.debug(ModeloEjecucionTipoDocumentoModalComponent.name, `getTipoFaseDisponibles(tipoDocumento: ITipoDocumento): ITipoFase[]`, 'end')
    return tipoFaseDisponibles;
  }
}
