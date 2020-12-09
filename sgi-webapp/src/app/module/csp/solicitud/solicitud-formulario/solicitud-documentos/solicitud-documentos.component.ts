import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, FileModel, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SolicitudActionService } from '../../solicitud.action.service';
import { NodeDocumentoSolicitud, SolicitudDocumentosFragment } from './solicitud-documentos.fragment';

const MSG_FILE_NOT_FOUND_ERROR = marker('file.info.error');
const MSG_UPLOAD_SUCCESS = marker('file.upload.success');
const MSG_UPLOAD_ERROR = marker('file.upload.error');
const MSG_DOWNLOAD_ERROR = marker('file.download.error');
const MSG_DELETE = marker('csp.solicitud.documentos.documento.eliminar.msg');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-solicitud-documentos',
  templateUrl: './solicitud-documentos.component.html',
  styleUrls: ['./solicitud-documentos.component.scss']
})
export class SolicitudDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {
  VIEW_MODE = VIEW_MODE;

  formPart: SolicitudDocumentosFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl: FlatTreeControl<NodeDocumentoSolicitud>;
  private treeFlattener: MatTreeFlattener<NodeDocumentoSolicitud, NodeDocumentoSolicitud>;
  dataSource: MatTreeFlatDataSource<NodeDocumentoSolicitud, NodeDocumentoSolicitud>;
  @ViewChild('fileUpload') private fileUploadInput: ElementRef;

  viewingNode: NodeDocumentoSolicitud;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  fileToUpload: FileModel;
  tiposDocumento: ITipoDocumento[] = [];

  disableUpload = true;

  private getLevel = (node: NodeDocumentoSolicitud) => node.level;
  private isExpandable = (node: NodeDocumentoSolicitud) => node.childs.length > 0;
  private getChildren = (node: NodeDocumentoSolicitud): NodeDocumentoSolicitud[] => node.childs;
  private transformer = (node: NodeDocumentoSolicitud, level: number) => node;

  hasChild = (_: number, node: NodeDocumentoSolicitud) => node.childs.length > 0;
  compareTipoDocumento = (option: ITipoDocumento, value: ITipoDocumento) => option?.id === value?.id;

  constructor(
    protected logger: NGXLogger,
    public actionService: SolicitudActionService,
    private modeloEjecucionService: ModeloEjecucionService,
    private documentoService: DocumentoService,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private snackBar: SnackBarService,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.DOCUMENTOS, actionService);
    this.logger.debug(SolicitudDocumentosComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as SolicitudDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumentoSolicitud>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.logger.debug(SolicitudDocumentosComponent.name, 'constructor()', 'start');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    const subcription = this.formPart.documentos$.subscribe(
      (documentos) => {
        this.dataSource.data = documentos;
      },
      (error) => {
        this.logger.error(SolicitudDocumentosComponent.name, 'ngOnInit()', error);
      }
    );
    this.subscriptions.push(subcription);
    this.group.load(new FormGroup({
      nombre: new FormControl('', Validators.required),
      fileInfo: new FormControl(null),
      fichero: new FormControl({ value: null, disabled: this.disableUpload }, Validators.required),
      tipoDocumento: new FormControl(null, IsEntityValidator.isValid),
      comentarios: new FormControl('')
    }));
    this.group.initialize();
    const convocatoriaId = this.actionService.getDatosGeneralesSolicitud().convocatoria?.id;
    if (convocatoriaId) {
      this.subscriptions.push(
        this.configuracionSolicitudService.findAllConvocatoriaDocumentoRequeridoSolicitud(convocatoriaId).pipe(
          map(documentoRequeridos => documentoRequeridos.items.map(documentoRequerido => documentoRequerido.tipoDocumento))
        ).subscribe(
          (tipos) => {
            this.tiposDocumento = this.sortTipoDocumentos(tipos);
          }
        )
      );
    }
    this.switchToNone();
    this.logger.debug(SolicitudDocumentosComponent.name, 'ngOnInit()', 'end');
  }

  private sortTipoDocumentos(tipoDocumentos: ITipoDocumento[]): ITipoDocumento[] {
    return tipoDocumentos.sort((a, b) => {
      if (a.nombre < b.nombre) {
        return -1;
      }
      if (a.nombre > b.nombre) {
        return 1;
      }
      return 0;
    });
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudDocumentosComponent.name, 'ngOnDestroy()', 'end');
  }

  showNodeDetails(node: NodeDocumentoSolicitud) {
    this.logger.debug(SolicitudDocumentosComponent.name, `showNodeDetails(node: ${node})`, 'start');
    this.viewingNode = node;
    if (!node.fichero && node.documento?.value.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documentoRef).subscribe(
        (info) => {
          node.fichero = info;
          this.switchToView();
          this.logger.debug(SolicitudDocumentosComponent.name, `showNodeDetails(node: ${node})`, 'end');
        },
        (error) => {
          // TODO: Eliminar cuando los datos sean consistentes
          this.snackBar.showError(MSG_FILE_NOT_FOUND_ERROR);
          this.switchToView();
          this.logger.error(SolicitudDocumentosComponent.name, `showNodeDetails(node: ${node})`, error);
        }
      ));
    } else {
      this.switchToView();
      this.logger.debug(SolicitudDocumentosComponent.name, `showNodeDetails(node: ${node})`, 'end');
    }
  }

  switchToView() {
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToView()`, 'start');
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToView()`, 'end');
  }

  private loadDetails(node: NodeDocumentoSolicitud) {
    this.logger.debug(SolicitudDocumentosComponent.name, `loadDetails(node: ${node})`, 'start');
    this.formGroup.enable();
    this.setUploadDisabled(false);

    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.documento?.value?.nombre);
    this.cleanFileUploadInput();
    this.formGroup.get('fileInfo').patchValue(node?.fichero);
    this.formGroup.get('fichero').patchValue(node?.fichero?.nombre);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.get('comentarios').patchValue(node?.documento?.value?.comentario);

    this.group.refreshInitialState(Boolean(node?.documento));
    if (this.viewMode !== VIEW_MODE.NEW && this.viewMode !== VIEW_MODE.EDIT) {
      this.formGroup.disable();
      this.setUploadDisabled(true);
    }
    this.logger.debug(SolicitudDocumentosComponent.name, `loadDetails(node: ${node})`, 'end');
  }

  private setUploadDisabled(value: boolean): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `setUploadDisabled(value: ${value})`, 'start');
    if (value) {
      this.disableUpload = true;
      this.formGroup.controls.fichero.disable();
    } else {
      this.disableUpload = false;
      this.formGroup.controls.fichero.enable();
    }
    this.logger.debug(SolicitudDocumentosComponent.name, `setUploadDisabled(value: ${value})`, 'start');
  }

  private cleanFileUploadInput(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `cleanFileUploadInput()`, 'start');
    if (this.fileUploadInput) {
      this.fileUploadInput.nativeElement.value = null;
    }
    this.logger.debug(SolicitudDocumentosComponent.name, `cleanFileUploadInput()`, 'end');
  }

  hideNodeDetails() {
    this.logger.debug(SolicitudDocumentosComponent.name, `hideNodeDetails()`, 'start');
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.logger.debug(SolicitudDocumentosComponent.name, `hideNodeDetails()`, 'end');
  }

  onSelectFile(files: FileList) {
    this.logger.debug(SolicitudDocumentosComponent.name, `onSelectFile(files: ${files})`, 'start');
    if (files && files.length) {
      this.fileToUpload = {
        progress: 0,
        file: files.item(0),
        status: undefined
      };
      this.setUploadDisabled(true);
      this.subscriptions.push(this.documentoService.uploadFichero(this.fileToUpload).subscribe(
        (fileModel) => {
          this.snackBar.showSuccess(MSG_UPLOAD_SUCCESS);
          this.formGroup.controls.fichero.setValue(fileModel.nombre);
          this.formGroup.controls.fileInfo.setValue(fileModel);
          this.setUploadDisabled(false);
          this.logger.debug(SolicitudDocumentosComponent.name, `onSelectFile(files: ${files})`, 'end');
        },
        (error) => {
          this.fileToUpload.status = 'error';
          this.snackBar.showError(MSG_UPLOAD_ERROR);
          this.setUploadDisabled(false);
          this.logger.error(SolicitudDocumentosComponent.name, `onSelectFile(files: ${files})`, error);
        }
      ));
    } else {
      this.fileToUpload = undefined;
      this.logger.debug(SolicitudDocumentosComponent.name, `onSelectFile(files: ${files})`, 'end');
    }
  }

  downloadFile(node: NodeDocumentoSolicitud): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `downloadFile()`, 'start');
    this.subscriptions.push(this.documentoService.downloadFichero(node.fichero.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.fichero.nombre);
        this.logger.debug(SolicitudDocumentosComponent.name, `downloadFile()`, 'end');
      },
      (error) => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
        this.logger.error(SolicitudDocumentosComponent.name, `downloadFile()`, error);
      }
    ));
  }

  acceptDetail(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `acceptDetail()`, 'start');
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.viewMode === VIEW_MODE.NEW) {
        this.addNode(this.getDetailNode());
      }
      else if (this.viewMode === VIEW_MODE.EDIT) {
        this.updateNode(this.getDetailNode());
      }
    }
    this.logger.debug(SolicitudDocumentosComponent.name, `acceptDetail()`, 'end');
  }

  private getDetailNode(): NodeDocumentoSolicitud {
    this.logger.debug(SolicitudDocumentosComponent.name, `getDetailNode()`, 'start');
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.get('nombre').value;
    detail.title = detail.documento.value.nombre;
    detail.documento.value.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    detail.documento.value.comentario = this.formGroup.get('comentarios').value;
    detail.fichero = this.formGroup.get('fileInfo').value;
    this.logger.debug(SolicitudDocumentosComponent.name, `getDetailNode()`, 'end');
    return detail;
  }

  private addNode(node: NodeDocumentoSolicitud): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `addNode()`, 'start');
    const createdNode = this.formPart.addNode(node);
    this.expandParents(createdNode);
    this.switchToNone();
    this.logger.debug(SolicitudDocumentosComponent.name, `addNode()`, 'end');
  }

  private switchToNone() {
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToNone()`, 'start');
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToNone()`, 'start');
  }

  private updateNode(node: NodeDocumentoSolicitud): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `updateNode(node: ${node})`, 'start');
    this.formPart.updateNode(node);
    this.expandParents(node);
    this.switchToView();
    this.logger.debug(SolicitudDocumentosComponent.name, `updateNode(node: ${node})`, 'end');
  }

  private expandParents(node: NodeDocumentoSolicitud): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `expandParents(node: ${node})`, 'start');
    if (node.parent) {
      this.treeControl.expand(node.parent);
      this.expandParents(node.parent);
    }
    this.logger.debug(SolicitudDocumentosComponent.name, `expandParents(node: ${node})`, 'end');
  }

  cancelDetail(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `cancelDetail()`, 'start');
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
    this.logger.debug(SolicitudDocumentosComponent.name, `cancelDetail()`, 'end');
  }

  switchToEdit(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToEdit()`, 'start');
    this.viewMode = VIEW_MODE.EDIT;
    this.loadDetails(this.viewingNode);
    this.formGroup.get('tipoDocumento').disable();
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToEdit()`, 'end');
  }


  deleteDetail(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `deleteDetail()`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteNode(this.viewingNode);
            this.switchToNone();
          }
        }
      )
    );
    this.logger.debug(SolicitudDocumentosComponent.name, `deleteDetail()`, 'end');
  }

  switchToNew(): void {
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToNew()`, 'start');
    const wrapper = new StatusWrapper<ISolicitudDocumento>({} as ISolicitudDocumento);
    const newNode: NodeDocumentoSolicitud = new NodeDocumentoSolicitud(null, undefined, 2, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
    this.logger.debug(SolicitudDocumentosComponent.name, `switchToNew()`, 'end');
  }
}
