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
  templateUrl: './memoria-documentacion-memoria-modal.component.html',
  styleUrls: ['./memoria-documentacion-memoria-modal.component.scss']
})
export class MemoriaDocumentacionMemoriaModalComponent implements OnInit {

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
    public readonly matDialogRef: MatDialogRef<MemoriaDocumentacionMemoriaModalComponent>,
    private readonly snackBarService: SnackBarService,
    private readonly tipoDocumentoService: TipoDocumentoService,
    @Inject(MAT_DIALOG_DATA) public documentacionesMemoria: StatusWrapper<IDocumentacionMemoria>[]) {

    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'constructor()', 'start');
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
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {


    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'ngOnInit()', 'start');

    this.initFormGroup();

    this.loadTiposDocumento();

    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'ngOnInit()', 'start');
  }

  /**
   * Inicializa formulario de añadir documentació.
   */
  private initFormGroup() {
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'initFormGroup()', 'start');
    this.formGroup = new FormGroup({
      tipoDocumento: new FormControl(null, [new NullIdValidador().isValid()]),
      nombreDocumento: new FormControl(null)
    });
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'initFormGroup()', 'end');
  }

  /**
   * Recupera los tips de documento iniciales de una memoria
   */
  loadTiposDocumento() {

    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'loadTiposDocumento()', 'start');
    this.suscripciones.push(
      this.tipoDocumentoService.findTiposDocumentoIniciales().subscribe(
        (res: SgiRestListResult<ITipoDocumento>) => {
          this.tiposDcoumentacionFiltered = res.items;
          this.tiposDcoumentacion = this.formGroup.controls.tipoDocumento.valueChanges
            .pipe(

              startWith(''),
              map(value => this.filtroTipoDocumento(value))
            );
          this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'loadTiposDocumento()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'loadTiposDocumento()', 'end');
        }
      )
    );
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'loadTiposDocumento()', 'end');
  }

  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoDocumento(value: string): ITipoDocumento[] {
    const filterValue = value.toString().toLowerCase();
    return this.tiposDcoumentacionFiltered.filter(tipoDocumento => tipoDocumento.nombre.toLowerCase().includes(filterValue));
  }


  /**
   * Devuelve el nombre de un tipo de documento.
   * @param tipoDocumento tipo de documento.
   * @returns nombre de un tipo de documento.
   */
  getTipoDocumento(tipoDocumento?: ITipoDocumento): string | undefined {
    return typeof tipoDocumento === 'string' ? tipoDocumento : tipoDocumento?.nombre;
  }


  /**
   * Cierra la ventana modal y devuelve el documento aportado.
   *
   */
  closeModal(): void {
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(this.documentacionesMemoria);
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'closeModal()', 'end');
  }


  save(): void {
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'save()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal();
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'save()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'loadDatosForm()', 'start');
    this.documentacionesMemoria.map(documentacionMemoria => {
      if (documentacionMemoria.value.tipoDocumento.nombre === FormGroupUtil.getValue(this.formGroup, 'tipoDocumento').nombre) {
        documentacionMemoria.value.tipoDocumento = FormGroupUtil.getValue(this.formGroup, 'tipoDocumento');
        documentacionMemoria.value.aportado = true;

        if (documentacionMemoria.value.id) {
          documentacionMemoria.setEdited();
        } else {
          documentacionMemoria.setCreated();
        }
      }
    });



    this.logger.debug(MemoriaDocumentacionMemoriaModalComponent.name, 'loadDatosForm()', 'end');
  }

}
