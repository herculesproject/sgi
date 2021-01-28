import { Component, OnDestroy, ViewChild, OnInit } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { ProyectoSocioPeriodoJustificacionDocumentosFragment, NodeDocumentoProyecto } from './proyecto-socio-periodo-justificacion-documentos.fragment';
import { Subscription, Observable, of } from 'rxjs';
import { ProyectoSocioPeriodoJustificacionActionService } from '../../proyecto-socio-periodo-justificacion.action.service';
import { NGXLogger } from 'ngx-logger';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlattener, MatTreeFlatDataSource } from '@angular/material/tree';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { Group } from '@core/services/action-service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DialogService } from '@core/services/dialog.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { map } from 'rxjs/operators';
import { TipoDocumentoService } from '@core/services/csp/tipo-documento.service';
import { SgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';

const MSG_FILE_NOT_FOUND_ERROR = marker('file.info.error');
const MSG_UPLOAD_SUCCESS = marker('file.upload.success');
const MSG_UPLOAD_ERROR = marker('file.upload.error');
const MSG_DOWNLOAD_ERROR = marker('file.download.error');
const MSG_DELETE = marker('csp.proyecto-socio-periodo-justificacion.documentos.documento.eliminar.msg');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-documentos',
  templateUrl: './proyecto-socio-periodo-justificacion-documentos.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-documentos.component.scss']
})
export class ProyectoSocioPeriodoJustificacionDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {
  VIEW_MODE = VIEW_MODE;

  formPart: ProyectoSocioPeriodoJustificacionDocumentosFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl: FlatTreeControl<NodeDocumentoProyecto>;
  private treeFlattener: MatTreeFlattener<NodeDocumentoProyecto, NodeDocumentoProyecto>;
  dataSource: MatTreeFlatDataSource<NodeDocumentoProyecto, NodeDocumentoProyecto>;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  viewingNode: NodeDocumentoProyecto;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  uploading = false;

  disableUpload = true;
  tiposDocumento$: Observable<ITipoDocumento[]> = of([]);

  private getLevel = (node: NodeDocumentoProyecto) => node.level;
  private isExpandable = (node: NodeDocumentoProyecto) => node.childs.length > 0;
  private getChildren = (node: NodeDocumentoProyecto): NodeDocumentoProyecto[] => node.childs;
  private transformer = (node: NodeDocumentoProyecto, level: number) => node;

  hasChild = (_: number, node: NodeDocumentoProyecto) => node.childs.length > 0;
  compareTipoDocumento = (option: ITipoDocumento, value: ITipoDocumento) => option?.id === value?.id;

  constructor(
    protected logger: NGXLogger,
    public actionService: ProyectoSocioPeriodoJustificacionActionService,
    private documentoService: DocumentoService,
    private tipoDocumentoService: TipoDocumentoService,
    private snackBar: SnackBarService,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.DOCUMENTOS, actionService);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as ProyectoSocioPeriodoJustificacionDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumentoProyecto>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'constructor()', 'start');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    const subcription = this.formPart.documentos$.subscribe(
      (documentos) => {
        this.dataSource.data = documentos;
      },
      (error) => {
        this.logger.error(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'ngOnInit()', error);
      }
    );
    this.subscriptions.push(subcription);
    this.group.load(new FormGroup({
      nombre: new FormControl('', Validators.required),
      fichero: new FormControl(null, Validators.required),
      tipoDocumento: new FormControl(null, IsEntityValidator.isValid),
      comentarios: new FormControl('', Validators.maxLength(2_000)),
      visible: new FormControl(false)
    }));
    this.group.initialize();
    const sort: SgiRestSort = {
      direction: SgiRestSortDirection.ASC,
      field: 'nombre'
    };
    const options: SgiRestFindOptions = {
      sort
    };
    this.tiposDocumento$ = this.tipoDocumentoService.findTodos(options).pipe(
      map(result => result.items)
    );
    this.switchToNone();
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, 'ngOnDestroy()', 'end');
  }

  showNodeDetails(node: NodeDocumentoProyecto) {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `showNodeDetails(node: ${node})`, 'start');
    this.viewingNode = node;
    if (!node.fichero && node.documento?.value.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documentoRef).subscribe(
        (info) => {
          node.fichero = info;
          this.switchToView();
          this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `showNodeDetails(node: ${node})`, 'end');
        },
        (error) => {
          // TODO: Eliminar cuando los datos sean consistentes
          this.snackBar.showError(MSG_FILE_NOT_FOUND_ERROR);
          this.switchToView();
          this.logger.error(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `showNodeDetails(node: ${node})`, error);
        }
      ));
    } else {
      this.switchToView();
      this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `showNodeDetails(node: ${node})`, 'end');
    }
  }

  switchToView() {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToView()`, 'start');
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToView()`, 'end');
  }

  private loadDetails(node: NodeDocumentoProyecto) {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `loadDetails(node: ${node})`, 'start');
    this.formGroup.enable();

    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.documento?.value?.nombre);
    this.formGroup.get('fichero').patchValue(node?.fichero);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.get('comentarios').patchValue(node?.documento?.value?.comentario);
    this.formGroup.get('visible').patchValue(Boolean(node?.documento?.value?.visible));

    this.group.refreshInitialState(Boolean(node?.documento));
    if (this.viewMode !== VIEW_MODE.NEW && this.viewMode !== VIEW_MODE.EDIT) {
      this.formGroup.disable();
    }
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `loadDetails(node: ${node})`, 'end');
  }

  hideNodeDetails() {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `hideNodeDetails()`, 'start');
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `hideNodeDetails()`, 'end');
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.snackBar.showSuccess(MSG_UPLOAD_SUCCESS);
        this.uploading = false;
        break;
      case 'error':
        this.snackBar.showError(MSG_UPLOAD_ERROR);
        this.uploading = false;
        break;
    }
  }


  downloadFile(node: NodeDocumentoProyecto): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `downloadFile()`, 'start');
    this.subscriptions.push(this.documentoService.downloadFichero(node.fichero.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.fichero.nombre);
        this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `downloadFile()`, 'end');
      },
      (error) => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
        this.logger.error(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `downloadFile()`, error);
      }
    ));
  }

  acceptDetail(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `acceptDetail()`, 'start');
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.viewMode === VIEW_MODE.NEW) {
        this.uploader.uploadSelection().subscribe(
          () => this.addNode(this.getDetailNode())
        );
      }
      else if (this.viewMode === VIEW_MODE.EDIT) {
        this.uploader.uploadSelection().subscribe(
          () => this.updateNode(this.getDetailNode())
        );
      }
    }
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `acceptDetail()`, 'end');
  }

  private getDetailNode(): NodeDocumentoProyecto {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `getDetailNode()`, 'start');
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.get('nombre').value;
    detail.title = detail.documento.value.nombre;
    detail.documento.value.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    detail.documento.value.comentario = this.formGroup.get('comentarios').value;
    detail.documento.value.visible = Boolean(this.formGroup.get('visible').value);
    detail.fichero = this.formGroup.get('fichero').value;
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `getDetailNode()`, 'end');
    return detail;
  }

  private addNode(node: NodeDocumentoProyecto): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `addNode()`, 'start');
    const createdNode = this.formPart.addNode(node);
    this.expandParents(createdNode);
    this.switchToNone();
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `addNode()`, 'end');
  }

  private switchToNone() {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToNone()`, 'start');
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToNone()`, 'start');
  }

  private updateNode(node: NodeDocumentoProyecto): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `updateNode(node: ${node})`, 'start');
    this.formPart.updateNode(node);
    this.expandParents(node);
    this.switchToView();
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `updateNode(node: ${node})`, 'end');
  }

  private expandParents(node: NodeDocumentoProyecto): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `expandParents(node: ${node})`, 'start');
    if (node.parent) {
      this.treeControl.expand(node.parent);
      this.expandParents(node.parent);
    }
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `expandParents(node: ${node})`, 'end');
  }

  cancelDetail(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `cancelDetail()`, 'start');
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `cancelDetail()`, 'end');
  }

  switchToEdit(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToEdit()`, 'start');
    this.viewMode = VIEW_MODE.EDIT;
    this.loadDetails(this.viewingNode);
    this.formGroup.get('tipoDocumento').disable();
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToEdit()`, 'end');
  }


  deleteDetail(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `deleteDetail()`, 'start');
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
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `deleteDetail()`, 'end');
  }

  switchToNew(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToNew()`, 'start');
    const wrapper = new StatusWrapper<ISocioPeriodoJustificacionDocumento>({} as ISocioPeriodoJustificacionDocumento);
    const newNode: NodeDocumentoProyecto = new NodeDocumentoProyecto(null, undefined, 2, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosComponent.name, `switchToNew()`, 'end');
  }

}
