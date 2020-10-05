import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { Observable, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { TipoDocumentoService } from '@core/services/eti/tipo-documento.service';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { startWith, map } from 'rxjs/operators';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { StatusWrapper } from '@core/utils/status-wrapper';

const MSG_ERROR_INIT = marker('eti.memoria.documentacion.error.cargar');
const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './memoria-documentacion-seguimientos-modal.component.html',
  styleUrls: ['./memoria-documentacion-seguimientos-modal.component.scss']
})
export class MemoriaDocumentacionSeguimientosModalComponent implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  FormGroupUtil = FormGroupUtil;
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxFlexProperties3: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  tiposDcoumentacionFiltered: ITipoDocumento[];
  tiposDcoumentacion: Observable<ITipoDocumento[]>;

  suscripciones: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    public readonly matDialogRef: MatDialogRef<MemoriaDocumentacionSeguimientosModalComponent>,
    private readonly snackBarService: SnackBarService,
    private readonly tipoDocumentoService: TipoDocumentoService,
    @Inject(MAT_DIALOG_DATA) public documentacionesMemoria: StatusWrapper<IDocumentacionMemoria>[]) {

    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(15%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.order = '3';

    this.fxFlexProperties3 = new FxFlexProperties();
    this.fxFlexProperties3.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {


    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'ngOnInit()', 'start');

    this.initFormGroup();

    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'ngOnInit()', 'start');
  }

  /**
   * Inicializa formulario de añadir documentació.
   */
  private initFormGroup() {
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'initFormGroup()', 'start');
    this.formGroup = new FormGroup({
      nombreDocumento: new FormControl(null)
    });
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'initFormGroup()', 'end');
  }




  /**
   * Cierra la ventana modal y devuelve el documento aportado.
   *
   */
  closeModal(documentacionMemoria?: IDocumentacionMemoria): void {
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(documentacionMemoria);
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'closeModal()', 'end');
  }


  save(): void {
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'save()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {

      this.closeModal(this.loadDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'save()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): IDocumentacionMemoria {
    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'loadDatosForm()', 'start');
    const documentacionMemoria = {} as IDocumentacionMemoria;
    documentacionMemoria.aportado = true;

    documentacionMemoria.documentoRef = 'DocRef001';

    this.logger.debug(MemoriaDocumentacionSeguimientosModalComponent.name, 'loadDatosForm()', 'end');
    return documentacionMemoria;

  }

}
