import { OnDestroy } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const SIN_TIPO_DOCUMENTO = marker('csp.solicitud.documentos.sinTipoDocumento.title');

export class NodeDocumentoSolicitud {
  parent: NodeDocumentoSolicitud;
  key: string;
  title: string;
  documento?: StatusWrapper<ISolicitudDocumento>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumentoSolicitud[];
  get childs(): NodeDocumentoSolicitud[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(
    key: string, title: string, level: number,
    documento?: StatusWrapper<ISolicitudDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = SIN_TIPO_DOCUMENTO;
    }
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumentoSolicitud) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  removeChild(child: NodeDocumentoSolicitud) {
    this._childs = this._childs.filter((documento) => documento !== child);
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }
}

function sortByTitle(nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud[] {
  return nodes.sort((a, b) => {
    if ((a.level === 0 || b.level === 0)) {
      if (a.key < b.key) {
        return -1;
      }
      if (a.key > b.key) {
        return 1;
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

export class SolicitudDocumentosFragment extends Fragment implements OnDestroy {
  documentos$ = new BehaviorSubject<NodeDocumentoSolicitud[]>([]);
  private documentosEliminados: ISolicitudDocumento[] = [];

  private nodeLookup = new Map<string, NodeDocumentoSolicitud>();
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    private solicitudId: number,
    private convocatoriaId: number,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private solicitudService: SolicitudService,
    private solicitudDocumentoService: SolicitudDocumentoService
  ) {
    super(solicitudId);
    this.logger.debug(SolicitudDocumentosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudDocumentosFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudDocumentosFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subcription => subcription.unsubscribe());
    this.logger.debug(SolicitudDocumentosFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudDocumentosFragment.name, `onInitialize()`, 'start');
    const id = this.convocatoriaId;
    const subscription = this.configuracionSolicitudService.findAllConvocatoriaDocumentoRequeridoSolicitud(id).pipe(
      map((result) => result.items),
      map((documentosRequeridos) => documentosRequeridos.map(
        (documento) => this.createNode(documento.tipoDocumento, []))
      ),
      switchMap((nodes) => {
        return this.solicitudService.findDocumentos(this.solicitudId).pipe(
          map((solicitudDocumentos) => nodes.concat(this.buildTree(solicitudDocumentos.items)))
        );
      })
    ).subscribe(
      (nodes) => {
        this.publishNodes(nodes);
        this.logger.debug(SolicitudDocumentosFragment.name, `onInitialize()`, 'end');
      },
      (error) => {
        this.logger.error(SolicitudDocumentosFragment.name, `onInitialize()`, error);
      }
    );
    this.subscriptions.push(subscription);
  }

  private createNode(tipoDocumento: ITipoDocumento, nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud {
    const keyTipoDocumento = `${tipoDocumento ? tipoDocumento.id : 0}`;
    const tipoDocNode = new NodeDocumentoSolicitud(keyTipoDocumento, tipoDocumento?.nombre, 0);
    this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
    nodes.push(tipoDocNode);
    return tipoDocNode;
  }

  private buildTree(documentos: ISolicitudDocumento[]): NodeDocumentoSolicitud[] {
    this.logger.debug(SolicitudDocumentosFragment.name, `buildTree(documentos: ${documentos})`, 'start');
    const nodes: NodeDocumentoSolicitud[] = [];
    documentos.forEach((documento: ISolicitudDocumento) => {
      const keyTipoDocumento = `${documento.tipoDocumento ? documento.tipoDocumento?.id : 0}`;
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = this.createNode(documento.tipoDocumento, nodes);
      }
      const docNode = new NodeDocumentoSolicitud('', documento.nombre, 1,
        new StatusWrapper<ISolicitudDocumento>(documento));
      tipoDocNode.addChild(docNode);
    });
    this.logger.debug(SolicitudDocumentosFragment.name, `buildTree(documentos: ${documentos})`, 'end');
    return nodes;
  }



  publishNodes(rootNodes?: NodeDocumentoSolicitud[]) {
    this.logger.debug(SolicitudDocumentosFragment.name, `publishNodes(rootNodes?: ${rootNodes})`, 'start');
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
    this.logger.debug(SolicitudDocumentosFragment.name, `publishNodes(rootNodes?: ${rootNodes})`, 'end');
  }

  public addNode(node: NodeDocumentoSolicitud): NodeDocumentoSolicitud {
    this.logger.debug(SolicitudDocumentosFragment.name, `addNode(node: ${node})`, 'start');
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumentoSolicitud(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      addToRoot = true;
    }
    const nodeDocumento = new NodeDocumentoSolicitud(keyTipoDocumento, node.title, 1, node.documento);
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
    this.logger.debug(SolicitudDocumentosFragment.name, `addNode(node: ${node})`, 'end');
    return nodeDocumento;
  }

  public updateNode(node: NodeDocumentoSolicitud) {
    this.logger.debug(SolicitudDocumentosFragment.name, `updateNode(node: ${node})`, 'start');
    if (!node.documento.created) {
      node.documento.setEdited();
    }
    node.documento.value.documentoRef = node.fichero?.documentoRef;
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumentoSolicitud(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
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
    this.logger.debug(SolicitudDocumentosFragment.name, `updateNode(node: ${node})`, 'end');
  }

  deleteNode(node: NodeDocumentoSolicitud) {
    this.logger.debug(SolicitudDocumentosFragment.name, `deleteNode(node: ${node})`, 'start');
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
    this.logger.debug(SolicitudDocumentosFragment.name, `deleteNode(node: ${node})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudDocumentosFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteDocumentos(),
      this.updateDocumentos(this.getUpdated(this.documentos$.value)),
      this.createDocumentos(this.getCreated(this.documentos$.value))
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete(this.documentos$.value)) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(SolicitudDocumentosFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteDocumentos(): Observable<void> {
    this.logger.debug(SolicitudDocumentosFragment.name, `deleteDocumentos()`, 'start');
    if (this.documentosEliminados.length === 0) {
      this.logger.debug(SolicitudDocumentosFragment.name, `deleteDocumentos()`, 'end');
      return of(void 0);
    }
    return from(this.documentosEliminados).pipe(
      mergeMap((documento) => {
        return this.solicitudDocumentoService.deleteById(documento.id)
          .pipe(
            tap(() => {
              this.documentosEliminados = this.documentosEliminados.filter(deleted =>
                deleted.id !== documento.id);
            }),
            tap(() => this.logger.debug(SolicitudDocumentosFragment.name, `deleteDocumentos()`, 'end'))
          );
      })
    );
  }

  private updateDocumentos(nodes: NodeDocumentoSolicitud[]): Observable<void> {
    this.logger.debug(SolicitudDocumentosFragment.name, `updateDocumentos()`, 'start');
    if (nodes.length === 0) {
      this.logger.debug(SolicitudDocumentosFragment.name, `updateDocumentos()`, 'end');
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.solicitudDocumentoService.update(node.documento.value.id, node.documento.value).pipe(
          map((updated) => {
            node.documento = new StatusWrapper<ISolicitudDocumento>(updated);
          }),
          tap(() => this.logger.debug(SolicitudDocumentosFragment.name, `updateDocumentos()`, 'end'))
        );
      })
    );
  }

  private getUpdated(nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud[] {
    this.logger.debug(SolicitudDocumentosFragment.name, `getUpdated(documentos: ${nodes})`, 'start');
    const updated: NodeDocumentoSolicitud[] = [];
    nodes.forEach((node) => {
      if (node.documento && node.documento.edited) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getUpdated(node.childs));
      }
    });
    this.logger.debug(SolicitudDocumentosFragment.name, `getUpdated(documentos: ${nodes})`, 'end');
    return updated;
  }

  private createDocumentos(nodes: NodeDocumentoSolicitud[]): Observable<void> {
    this.logger.debug(SolicitudDocumentosFragment.name, `createDocumentos()`, 'start');
    if (nodes.length === 0) {
      this.logger.debug(SolicitudDocumentosFragment.name, `createDocumentos()`, 'end');
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap(node => {
        node.documento.value.solicitud = {
          id: this.getKey() as number
        } as ISolicitud;
        return this.solicitudDocumentoService.create(node.documento.value).pipe(
          map(created => {
            node.documento = new StatusWrapper<ISolicitudDocumento>(created);
          }),
          tap(() => this.logger.debug(SolicitudDocumentosFragment.name, `createDocumentos()`, 'end'))
        );
      })
    );
  }

  private getCreated(nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud[] {
    this.logger.debug(SolicitudDocumentosFragment.name, `getCreated(programas: ${nodes})`, 'start');
    const updated: NodeDocumentoSolicitud[] = [];
    nodes.forEach((node) => {
      if (node.documento && node.documento.created) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getCreated(node.childs));
      }
    });
    this.logger.debug(SolicitudDocumentosFragment.name, `getCreated(programas: ${nodes})`, 'end');
    return updated;
  }

  private isSaveOrUpdateComplete(nodes: NodeDocumentoSolicitud[]): boolean {
    this.logger.debug(SolicitudDocumentosFragment.name, `isSaveOrUpdateComplete(node: ${nodes})`, 'start');
    let pending = this.documentosEliminados.length > 0;
    if (pending) {
      return false;
    }
    nodes.forEach((node) => {
      if (node.documento) {
        pending = node.documento.touched;
        if (pending) {
          return false;
        }
      }
      if (node.childs.length) {
        pending = this.isSaveOrUpdateComplete(node.childs);
        if (pending) {
          return false;
        }
      }
    });
    this.logger.debug(SolicitudDocumentosFragment.name, `isSaveOrUpdateComplete(node: ${nodes})`, 'end');
    return true;
  }
}
