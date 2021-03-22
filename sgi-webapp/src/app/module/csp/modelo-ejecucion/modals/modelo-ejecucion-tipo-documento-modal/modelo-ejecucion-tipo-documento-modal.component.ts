import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { ITipoDocumento, ITipoFase } from '@core/models/csp/tipos-configuracion';
import { TipoDocumentoService } from '@core/services/csp/tipo-documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MODELO_EJECUCUION_TIPO_DOCUMENTO_KEY = marker('csp.tipo-documento');
const MODELO_EJECUCUION_TIPO_DOCUMENTO_TIPO_KEY = marker('csp.documento.tipo');
const MODELO_EJECUCUION_TIPO_DOCUMENTO_FASE_KEY = marker('csp.modelo-ejecucion-tipo-documento.fase');
const TITLE_NEW_ENTITY = marker('title.new.entity');
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

  msgParamFaseEntiy = {};
  msgParamTipoEntiy = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ModeloEjecucionTipoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloTipoDocumentoModalData,
    private tipoDocumentoService: TipoDocumentoService,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, data.modeloTipoDocumento);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

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
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCUION_TIPO_DOCUMENTO_FASE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFaseEntiy = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      MODELO_EJECUCUION_TIPO_DOCUMENTO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE });


    this.translate.get(
      MODELO_EJECUCUION_TIPO_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          TITLE_NEW_ENTITY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.title = value);

  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoDocumento: new FormControl(this.data.modeloTipoDocumento?.tipoDocumento),
      tipoFase: new FormControl(this.data.modeloTipoDocumento?.modeloTipoFase)
    });
    return formGroup;
  }

  protected getDatosForm(): IModeloTipoDocumento {
    const modeloTipoDocumento = this.data.modeloTipoDocumento;
    modeloTipoDocumento.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    modeloTipoDocumento.modeloTipoFase = {
      tipoFase: this.formGroup.get('tipoFase').value
    } as IModeloTipoFase;
    return modeloTipoDocumento;
  }

  private loadTipoDocumentos(): Observable<ITipoDocumento[]> {
    return this.tipoDocumentoService.findAll().pipe(
      map(
        (result) => {
          // Se quitan de la lista los documentos que no pueden ser asignados

          const tipoDocumentoDisponibles = result.items.filter(
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
        })
    );
  }

  private getTipoFaseDisponibles(tipoDocumento: ITipoDocumento): ITipoFase[] {
    const tipoFaseDisponibles: ITipoFase[] = this.data.tipoFases.filter(
      (modeloTipofase) => {
        return !this.data.modeloTipoDocumentos.find((modeloTipoDocumento) => {
          return (modeloTipoDocumento.tipoDocumento.id === tipoDocumento.id)
            && modeloTipoDocumento.modeloTipoFase &&
            (modeloTipofase.id === modeloTipoDocumento.modeloTipoFase.tipoFase.id);
        });
      });
    return tipoFaseDisponibles;
  }
}
