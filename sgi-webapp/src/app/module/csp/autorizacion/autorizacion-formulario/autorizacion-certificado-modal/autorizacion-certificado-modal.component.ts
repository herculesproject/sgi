import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const MSG_ACEPTAR = marker('btn.ok');
const MSG_FILE_NOT_FOUND_ERROR = marker('error.file.info');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const CERTIFICADO_AUTORIZACION_KEY = marker('csp.certificado-autorizacion');
const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const CERTIFICADO_AUTORIZACION_NOMBRE_KEY = marker('csp.certificado-autorizacion.nombre');
const CERTIFICADO_AUTORIZACION_DOCUMENTO_KEY = marker('csp.certificado-autorizacion.documento');
const CERTIFICADO_AUTORIZACION_PUBLICO_KEY = marker('csp.certificado-autorizacion.publico');

export interface ICertificadoAutorizacionModalData {
  id: number;
  autorizacion: IAutorizacion;
  nombre: string;
  documento: IDocumento;
  visible: boolean;
  hasSomeOtherCertificadoAutorizacionVisible: boolean;
}

@Component({
  selector: 'sgi-autorizacion-certificado-modal',
  templateUrl: './autorizacion-certificado-modal.component.html',
  styleUrls: ['./autorizacion-certificado-modal.component.scss']
})
export class AutorizacionCertificadoModalComponent extends BaseModalComponent<ICertificadoAutorizacion, AutorizacionCertificadoModalComponent>
  implements OnInit {

  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  showTipoRelacion: boolean;

  textSaveOrUpdate: string;
  title: string;

  uploading = false;

  msgParamTipoEntidadRelacionada = {};
  msgParamEntidadRelacionada = {};
  msgParamTipoRelacion = {};
  msgParamObservaciones = {};
  msgParamFicheroEntity = {};
  msgParamPublicoEntity = {};
  msgParamDocumentoEntity = {};

  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public matDialogRef: MatDialogRef<AutorizacionCertificadoModalComponent>,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: ICertificadoAutorizacionModalData,
    private readonly translate: TranslateService,
    private autorizacionService: AutorizacionService,
    readonly sgiAuthService: SgiAuthService,
  ) {
    super(snackBarService, matDialogRef, null);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initFlexProperties();
    this.setupI18N();

  }

  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.uploader.uploadSelection().subscribe(
        () => this.matDialogRef.close(this.getDatosForm())
      );
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  protected getDatosForm(): ICertificadoAutorizacion {
    this.data.nombre = this.formGroup.controls.nombre.value;
    this.data.visible = this.formGroup.controls.publico.value;
    this.data.documento = this.formGroup.controls.documento.value;

    return this.data;
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.data.nombre),
      publico: new FormControl(this.data.visible, [Validators.required,
      this.buildValidadorHasSomeOtherCertificadoAutorizacionVisible(this.data?.hasSomeOtherCertificadoAutorizacionVisible)]),
      documento: new FormControl(this.data.documento, Validators.required),
      generadoAutomatico: new FormControl(null),
    });
  }

  private setupI18N(): void {
    this.translate.get(
      CERTIFICADO_AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.title = value);

    this.translate.get(
      CERTIFICADO_AUTORIZACION_DOCUMENTO_KEY,
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CERTIFICADO_AUTORIZACION_PUBLICO_KEY,
    ).subscribe((value) => this.msgParamPublicoEntity = { field: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }

  private buildValidadorHasSomeOtherCertificadoAutorizacionVisible(hasSomeOtherCertificadoAutorizacionVisible: boolean): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (hasSomeOtherCertificadoAutorizacionVisible && control.value) {
        return { hasCertificadoPublico: true };
      } else {
        return null;
      }
    };
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.snackBarService.showSuccess(MSG_UPLOAD_SUCCESS);
        this.uploading = false;
        break;
      case 'error':
        this.snackBarService.showError(MSG_UPLOAD_ERROR);
        this.uploading = false;
        break;
    }
  }
}
