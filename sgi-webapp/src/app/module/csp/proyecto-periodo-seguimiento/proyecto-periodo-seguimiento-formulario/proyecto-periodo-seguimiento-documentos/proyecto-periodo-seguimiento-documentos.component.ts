import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { ITipoDocumento, ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ProyectoPeriodoSeguimientoActionService } from '../../proyecto-periodo-seguimiento.action.service';
import { ProyectoPeriodoSeguimientoDocumentosFragment, NodeDocumento } from './proyecto-periodo-seguimiento-documentos.fragment';

const MSG_DELETE = marker('csp.proyecto-periodo-seguimiento-documento.documento.eliminar.msg');
const MSG_UPLOAD_SUCCES = marker('file.upload.success');
const MSG_UPLOAD_ERROR = marker('file.upload.error');
const MSG_DOWNLOAD_ERROR = marker('file.download.error');
const MSG_FILE_NOT_FOUND_ERROR = marker('file.info.error');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-proyecto-periodo-seguimiento-documentos',
  templateUrl: './proyecto-periodo-seguimiento-documentos.component.html',
  styleUrls: ['./proyecto-periodo-seguimiento-documentos.component.scss']
})
export class ProyectoPeriodoSeguimientoDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoPeriodoSeguimientoDocumentosFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl: FlatTreeControl<NodeDocumento>;
  private treeFlattener: MatTreeFlattener<NodeDocumento, NodeDocumento>;
  dataSource: MatTreeFlatDataSource<NodeDocumento, NodeDocumento>;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  viewingNode: NodeDocumento;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  uploading = false;

  tiposDocumento: ITipoDocumento[] = [];

  private getLevel = (node: NodeDocumento) => node.level;
  private isExpandable = (node: NodeDocumento) => node.childs.length > 0;
  private getChildren = (node: NodeDocumento): NodeDocumento[] => node.childs;
  private transformer = (node: NodeDocumento, level: number) => node;

  hasChild = (_: number, node: NodeDocumento) => node.childs.length > 0;

  compareFase = (option: ITipoFase, value: ITipoFase) => option?.id === value?.id;
  compareTipoDocumento = (option: ITipoDocumento, value: ITipoDocumento) => option?.id === value?.id;

  constructor(
    protected logger: NGXLogger,
    private dialogService: DialogService,
    public actionService: ProyectoPeriodoSeguimientoActionService,
    private modeloEjecucionService: ModeloEjecucionService,
    private documentoService: DocumentoService,
    private snackBar: SnackBarService
  ) {
    super(actionService.FRAGMENT.DOCUMENTOS, actionService);
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as ProyectoPeriodoSeguimientoDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumento>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, 'constructor()', 'end');
  }

  ngOnInit() {
    super.ngOnInit();
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, 'ngOnInit()', 'start');
    this.subscriptions.push(this.formPart.documentos$.subscribe((documentos) => {
      this.dataSource.data = documentos;
    }));
    this.group.load(new FormGroup({
      nombre: new FormControl('', Validators.required),
      fichero: new FormControl(null, Validators.required),
      tipoDocumento: new FormControl(null, IsEntityValidator.isValid),
      visible: new FormControl(null, Validators.required),
      comentario: new FormControl('')
    }));
    this.group.initialize();
    const id = this.formPart.proyecto?.modeloEjecucion?.id;
    this.subscriptions.push(
      this.modeloEjecucionService.findModeloTipoDocumento(id).subscribe(
        (tipos) => {
          this.tiposDocumento = tipos.items.filter(tipo => tipo.modeloTipoFase).map(t => t.tipoDocumento);
        }
      )
    );

    this.switchToNone();
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy() {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, 'ngOnDestroy()', 'end');
  }

  showNodeDetails(node: NodeDocumento) {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.showNodeDetails.name}(node: ${node})`, 'start');
    this.viewingNode = node;
    if (!node.fichero && node.documento?.value.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documentoRef).subscribe(
        (info) => {
          node.fichero = info;
          this.switchToView();
        },
        () => {
          // TODO: Eliminar cuando los datos sean consistentens
          this.snackBar.showError(MSG_FILE_NOT_FOUND_ERROR);
          this.switchToView();
        }
      ));
    }
    else {
      this.switchToView();
    }
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.showNodeDetails.name}(node: ${node})`, 'end');
  }

  hideNodeDetails() {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.hideNodeDetails.name}()`, 'start');
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.hideNodeDetails.name}()`, 'end');
  }

  switchToNew() {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.switchToNew.name}()`, 'start');
    const wrapper = new StatusWrapper<IProyectoPeriodoSeguimientoDocumento>({} as IProyectoPeriodoSeguimientoDocumento);
    const newNode: NodeDocumento = new NodeDocumento(null, undefined, 2, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.switchToNew.name}()`, 'end');
  }

  switchToEdit() {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.switchToEdit.name}()`, 'start');
    this.viewMode = VIEW_MODE.EDIT;
    this.loadDetails(this.viewingNode);
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.switchToEdit.name}()`, 'end');
  }

  switchToView() {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.switchToView.name}()`, 'start');
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.switchToView.name}()`, 'end');
  }

  private switchToNone() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
  }

  private loadDetails(node: NodeDocumento) {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name,
      `${this.loadDetails.name}()`, 'start');
    this.formGroup.enable();

    this.formGroup.reset();
    this.formGroup.controls.nombre.setValue(node?.documento?.value?.nombre);
    this.formGroup.controls.fichero.setValue(node?.fichero);
    this.formGroup.controls.tipoDocumento.setValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.controls.visible.setValue(node?.documento?.value?.visible);
    this.formGroup.controls.comentario.setValue(node?.documento?.value?.comentario);

    this.group.refreshInitialState(Boolean(node?.documento));
    if (this.viewMode !== VIEW_MODE.NEW && this.viewMode !== VIEW_MODE.EDIT) {
      this.formGroup.disable();
    }

    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name,
      `${this.loadDetails.name}()`, 'start');
  }

  cancelDetail() {
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
  }

  acceptDetail() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.viewMode === VIEW_MODE.NEW) {
        if (this.uploader.uploadSelection()) {
          this.uploader.uploadSelection().subscribe(
            () => this.addNode(this.getDetailNode())
          );
        } else {
          this.addNode(this.getDetailNode());
        }
      }
      else if (this.viewMode === VIEW_MODE.EDIT) {
        if (this.uploader.uploadSelection()) {
          this.uploader.uploadSelection().subscribe(
            () => this.updateNode(this.getDetailNode())
          );
        } else {
          this.updateNode(this.getDetailNode())
        }
      }
    }
  }

  private getDetailNode(): NodeDocumento {
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.controls.nombre.value;
    detail.title = detail.documento.value.nombre;
    detail.documento.value.tipoDocumento = this.formGroup.controls.tipoDocumento.value;
    detail.documento.value.visible = this.formGroup.controls.visible.value;
    detail.documento.value.comentario = this.formGroup.controls.comentario.value;
    detail.fichero = this.formGroup.controls.fichero.value;
    return detail;
  }

  private addNode(node: NodeDocumento) {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.addNode.name}()`, 'start');

    const createdNode = this.formPart.addNode(node);
    this.expandParents(createdNode);
    this.switchToNone();

    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.addNode.name}()`, 'end');
  }

  private updateNode(node: NodeDocumento) {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.updateNode.name}()`, 'start');

    this.formPart.updateNode(node);
    this.expandParents(node);
    this.switchToView();

    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name, `${this.updateNode.name}()`, 'end');
  }

  private expandParents(node: NodeDocumento) {
    if (node.parent) {
      this.treeControl.expand(node.parent);
      this.expandParents(node.parent);
    }
  }

  deleteDetail() {
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name,
      `${this.deleteDetail.name}()`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteNode(this.viewingNode);
            this.switchToNone();
          }
        }
      )
    );
    this.logger.debug(ProyectoPeriodoSeguimientoDocumentosComponent.name,
      `${this.deleteDetail.name}()`, 'end');
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.snackBar.showSuccess(MSG_UPLOAD_SUCCES);
        this.uploading = false;
        break;
      case 'error':
        this.snackBar.showError(MSG_UPLOAD_ERROR);
        this.uploading = false;
        break;
    }
  }

  downloadFile(node: NodeDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(node.fichero.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.fichero.nombre);
      },
      () => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }
}
