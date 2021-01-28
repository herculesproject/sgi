import { Fragment } from '@core/services/action-service';
import { OnDestroy } from '@angular/core';
import { Subscription, BehaviorSubject, Observable, from } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { SocioPeriodoJustificacionDocumentoService } from '@core/services/csp/socio-periodo-justificacion-documento.service';
import { map, takeLast, tap, mergeMap, switchMap } from 'rxjs/operators';
import { IDocumento } from '@core/models/sgdoc/documento';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { DocumentoService } from '@core/services/sgdoc/documento.service';

const SIN_TIPO_DOCUMENTO = marker('csp.proyecto-socio-periodo-justificacion.documentos.sin-tipo-documento');

export class NodeDocumentoProyecto {
  parent: NodeDocumentoProyecto;
  key: string;
  title: string;
  documento?: StatusWrapper<ISocioPeriodoJustificacionDocumento>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumentoProyecto[];
  get childs(): NodeDocumentoProyecto[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(key: string, title: string, level: number, documento?: StatusWrapper<ISocioPeriodoJustificacionDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = SIN_TIPO_DOCUMENTO;
    }
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumentoProyecto) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  removeChild(child: NodeDocumentoProyecto) {
    this._childs = this._childs.filter((documento) => documento !== child);
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }
}

function sortByTitle(nodes: NodeDocumentoProyecto[]): NodeDocumentoProyecto[] {
  return nodes.sort((a, b) => {
    // Force ordering last for level 0 and key 0
    if ((a.level === 0 || b.level === 0) && (a.key === '0' || b.key === '0')) {
      // A is the last
      if (a.key === '0') {
        return 1;
      }
      // B is the last
      if (b.key === '0') {
        return -1;
      }
      return 0;
    }

    // Force ordering last for level 1 and key ?-0
    if ((a.level === 1 || b.level === 1) && (a.key.endsWith('-0') || b.key.endsWith('-0'))) {
      // A is the last
      if (a.key.endsWith('-0')) {
        return 1;
      }
      // B is the last
      if (b.key.endsWith('-0')) {
        return -1;
      }
      return 0;
    }

    if (a.title < b.title) {
      return -1;
    }
    if (a.title > b.title) {
      return 1;
    }
    return 0;
  });
}

export class ProyectoSocioPeriodoJustificacionDocumentosFragment extends Fragment implements OnDestroy {
  documentos$ = new BehaviorSubject<NodeDocumentoProyecto[]>([]);
  private documentosEliminados: ISocioPeriodoJustificacionDocumento[] = [];

  private nodeLookup = new Map<string, NodeDocumentoProyecto>();
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService,
    private socioPeriodoJustificacionDocumentoService: SocioPeriodoJustificacionDocumentoService,
    private documentoService: DocumentoService
  ) {
    super(key);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoSocioPeriodoJustificacionService.findAllSocioPeriodoJustificacionDocumento(id).pipe(
          map((result) => result.items),
          map((documentosRequeridos) => this.buildTree(documentosRequeridos))
        ).subscribe(
          (nodes) => {
            this.publishNodes(nodes);
            this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `onInitialize()`, 'end');
          },
          (error) => {
            this.logger.error(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `onInitialize()`, error);
          }
        )
      );
    }
  }

  private createNode(tipoDocumento: ITipoDocumento, nodes: NodeDocumentoProyecto[]): NodeDocumentoProyecto {
    const keyTipoDocumento = `${tipoDocumento ? tipoDocumento.id : 0}`;
    const tipoDocNode = new NodeDocumentoProyecto(keyTipoDocumento, tipoDocumento?.nombre, 0);
    this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
    nodes.push(tipoDocNode);
    return tipoDocNode;
  }

  private buildTree(documentos: ISocioPeriodoJustificacionDocumento[]): NodeDocumentoProyecto[] {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `buildTree(documentos: ${documentos})`, 'start');
    const nodes: NodeDocumentoProyecto[] = [];
    documentos.forEach((documento: ISocioPeriodoJustificacionDocumento) => {
      const keyTipoDocumento = `${documento.tipoDocumento ? documento.tipoDocumento?.id : 0}`;
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = this.createNode(documento.tipoDocumento, nodes);
      }
      const docNode = new NodeDocumentoProyecto('', documento.nombre, 1,
        new StatusWrapper<ISocioPeriodoJustificacionDocumento>(documento));
      tipoDocNode.addChild(docNode);
    });
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `buildTree(documentos: ${documentos})`, 'end');
    return nodes;
  }

  publishNodes(rootNodes?: NodeDocumentoProyecto[]) {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `publishNodes(rootNodes?: ${rootNodes})`, 'start');
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `publishNodes(rootNodes?: ${rootNodes})`, 'end');
  }

  public addNode(node: NodeDocumentoProyecto): NodeDocumentoProyecto {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `addNode(node: ${node})`, 'start');
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumentoProyecto(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      addToRoot = true;
    }
    const nodeDocumento = new NodeDocumentoProyecto(keyTipoDocumento, node.title, 1, node.documento);
    nodeDocumento.documento.setCreated();
    nodeDocumento.fichero = node.fichero;
    nodeDocumento.documento.value.documentoRef = node.fichero?.documentoRef;
    nodeTipoDoc.addChild(nodeDocumento);
    const current = this.documentos$.value;
    if (addToRoot) {
      current.push(nodeTipoDoc);
    }
    this.publishNodes(current);
    this.setChanges(true);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `addNode(node: ${node})`, 'end');
    return nodeDocumento;
  }

  public updateNode(node: NodeDocumentoProyecto) {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `updateNode(node: ${node})`, 'start');
    if (!node.documento.created) {
      node.documento.setEdited();
    }
    node.documento.value.documentoRef = node.fichero?.documentoRef;
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumentoProyecto(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
    }
    // Si el padre ha cambiado limpiamos la rama y establecemos el nuevo padre
    if (nodeTipoDoc !== node.parent) {
      node.parent.removeChild(node);
      nodeTipoDoc.addChild(node);
    }
    else {
      // Ordenamos los hijos, porque puede haber cambiado el nombre
      node.parent.sortChildsByTitle();
    }
    const current = this.documentos$.value;
    this.publishNodes(current);
    this.setChanges(true);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `updateNode(node: ${node})`, 'end');
  }

  deleteNode(node: NodeDocumentoProyecto) {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `deleteNode(node: ${node})`, 'start');
    if (!node.documento.created) {
      this.documentosEliminados.push(node.documento.value);
    }
    node.parent.removeChild(node);
    let current = this.documentos$.value;
    if (node) {
      current = current.filter((n) => n !== node);
    }
    this.publishNodes(current);
    this.setChanges(true);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `deleteNode(node: ${node})`, 'end');
  }

  private getDocumentos(nodes: NodeDocumentoProyecto[]): ISocioPeriodoJustificacionDocumento[] {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `getDocumentos(nodes: ${nodes})`, 'start');
    const documentos: ISocioPeriodoJustificacionDocumento[] = [];
    nodes.forEach((node) => {
      if (node.documento) {
        documentos.push(node.documento.value);
      }
      if (node.childs.length) {
        documentos.push(...this.getDocumentos(node.childs));
      }
    });
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `getDocumentos(nodes: ${nodes})`, 'end');
    return documentos;
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `saveOrUpdate()`, 'start');
    const documentos = this.getDocumentos(this.documentos$.value);
    const id = this.getKey() as number;
    return this.socioPeriodoJustificacionDocumentoService.updateList(id, documentos).pipe(
      takeLast(1),
      map((results) => {
        this.documentos$.next(this.buildTree(results));
      }),
      tap(() =>
        this.logger.debug(ProyectoSocioPeriodoJustificacionDocumentosFragment.name, `saveOrUpdate()`, 'end')
      )
    );
  }
}
