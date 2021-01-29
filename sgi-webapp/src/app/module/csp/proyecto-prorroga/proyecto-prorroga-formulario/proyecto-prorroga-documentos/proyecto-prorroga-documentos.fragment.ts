import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ProyectoProrrogaDocumentoService } from '@core/services/csp/proyecto-prorroga-documento.service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class NodeDocumento {
  parent: NodeDocumento;
  key: string;
  title: string;
  documento?: StatusWrapper<IProyectoProrrogaDocumento>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumento[];
  get childs(): NodeDocumento[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(key: string, title: string, level: number, documento?: StatusWrapper<IProyectoProrrogaDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = marker('csp.proyecto-prorroga.documentos.sinFase.title');
    }
    else if (level === 1 && !title) {
      this.title = marker('csp.proyecto-prorroga.documentos.sinTipoDocumento.title');
    }
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumento) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  removeChild(child: NodeDocumento) {
    this._childs = this._childs.filter((documento) => documento !== child);
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }
}

function sortByTitle(nodes: NodeDocumento[]): NodeDocumento[] {
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

export class ProyectoProrrogaDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumento[]>([]);
  private documentosEliminados: IProyectoProrrogaDocumento[] = [];

  private nodeLookup = new Map<string, NodeDocumento>();

  constructor(
    private logger: NGXLogger,
    key: number,
    private prorrogaService: ProyectoProrrogaService,
    private prorrogaDocumentoService: ProyectoProrrogaDocumentoService,
    private documentoService: DocumentoService,
    public proyecto: IProyecto,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.onInitialize.name}()`, 'start');
    if (this.getKey()) {
      this.prorrogaService.findDocumentos(this.getKey() as number).pipe(
        map(response => {
          // TODO este filtro debería hacerse en el back
          let items = response.items;
          if (this.readonly) {
            items = items.filter(documento => documento.visible);
          }
          return this.buildTree(items);
        })
      ).subscribe(
        (documento) => {
          this.publishNodes(documento);
        },
        (error) => {
          this.logger.error(ProyectoProrrogaDocumentosFragment.name, `${this.onInitialize.name}()`, error);
        }
      );
    }
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.onInitialize.name}()`, 'end');
  }

  private buildTree(documentos: IProyectoProrrogaDocumento[]): NodeDocumento[] {
    const nodes: NodeDocumento[] = [];
    documentos.forEach((documento) => {
      const keyTipoFase = "0";
      const keyTipoDocumento = `${keyTipoFase}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
      let faseNode = this.nodeLookup.get(keyTipoFase);
      if (!faseNode) {
        faseNode = new NodeDocumento(keyTipoFase, undefined, 0);
        this.nodeLookup.set(keyTipoFase, faseNode);
        nodes.push(faseNode);
      }
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 1);
        faseNode.addChild(tipoDocNode);
        this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
      }
      const docNode = new NodeDocumento(null, documento.nombre, 2, new StatusWrapper<IProyectoProrrogaDocumento>(documento));
      tipoDocNode.addChild(docNode);
    });
    return nodes;
  }

  publishNodes(rootNodes?: NodeDocumento[]) {
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
  }

  public addNode(node: NodeDocumento): NodeDocumento {
    const keyTipoFase = "0";
    const keyTipoDocumento = `${keyTipoFase}-${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeFase = this.nodeLookup.get(keyTipoFase);
    let addToRoot = false;
    if (!nodeFase) {
      nodeFase = new NodeDocumento(keyTipoFase, undefined, 0);
      this.nodeLookup.set(keyTipoFase, nodeFase);
      addToRoot = true;
    }
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
      nodeFase.addChild(nodeTipoDoc);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
    }
    const nodeDocumento = new NodeDocumento(keyTipoDocumento, node.title, 2, node.documento);
    nodeDocumento.documento.setCreated();
    nodeDocumento.fichero = node.fichero;
    nodeDocumento.documento.value.documentoRef = node.fichero?.documentoRef;
    nodeTipoDoc.addChild(nodeDocumento);
    const current = this.documentos$.value;
    if (addToRoot) {
      current.push(nodeFase);
    }
    this.publishNodes(current);
    this.setChanges(true);
    return nodeDocumento;
  }

  public updateNode(node: NodeDocumento) {
    if (!node.documento.created) {
      node.documento.setEdited();
    }
    node.documento.value.documentoRef = node.fichero?.documentoRef;

    const keyTipoFase = "0";
    const keyTipoDocumento = `${keyTipoFase}-${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeFase = this.nodeLookup.get(keyTipoFase);
    let addToRoot = false;
    let removedRootNode: NodeDocumento;
    if (!nodeFase) {
      nodeFase = new NodeDocumento(keyTipoFase, undefined, 0);
      this.nodeLookup.set(keyTipoFase, nodeFase);
      addToRoot = true;
    }
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
      nodeFase.addChild(nodeTipoDoc);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
    }
    // Si el padre ha cambiado limpiamos la rama y establecemos el nuevo padre
    if (nodeTipoDoc !== node.parent) {
      node.parent.removeChild(node);
      removedRootNode = this.removeEmptyParentNodes(node.parent);
      nodeTipoDoc.addChild(node);
    }
    else {
      // Ordenamos los hijos, porque puede haber cambiado el nombre
      node.parent.sortChildsByTitle();
    }
    let current = this.documentos$.value;
    if (removedRootNode) {
      current = current.filter((n) => n !== removedRootNode);
    }
    if (addToRoot) {
      current.push(nodeFase);
    }
    this.publishNodes(current);
    this.setChanges(true);
  }

  public deleteNode(node: NodeDocumento) {
    let removedRootNode: NodeDocumento;

    if (!node.documento.created) {
      this.documentosEliminados.push(node.documento.value);
    }

    node.parent.removeChild(node);
    removedRootNode = this.removeEmptyParentNodes(node.parent);

    let current = this.documentos$.value;
    if (removedRootNode) {
      current = current.filter((n) => n !== removedRootNode);
    }

    this.publishNodes(current);
    this.setChanges(true);
  }

  private removeEmptyParentNodes(node: NodeDocumento): NodeDocumento {
    let removedNode: NodeDocumento;
    if (node.childs.length === 0) {
      this.nodeLookup.delete(node.key);
      if (!node.parent) {
        removedNode = node;
      }
      else {
        node.parent.removeChild(node);
      }
    }
    if (node.parent) {
      removedNode = this.removeEmptyParentNodes(node.parent);
    }
    return removedNode;
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.saveOrUpdate.name}()`, 'start');
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
      tap(() => this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.saveOrUpdate.name}()`, 'end'))
    );
  }

  private getUpdated(documentos: NodeDocumento[]): NodeDocumento[] {
    const updated: NodeDocumento[] = [];
    documentos.forEach((node) => {
      if (node.documento && node.documento.edited) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getUpdated(node.childs));
      }
    });
    return updated;
  }

  private getCreated(programas: NodeDocumento[]): NodeDocumento[] {
    const updated: NodeDocumento[] = [];
    programas.forEach((node) => {
      if (node.documento && node.documento.created) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getCreated(node.childs));
      }
    });
    return updated;
  }

  private deleteDocumentos(): Observable<void> {
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.deleteDocumentos.name}()`, 'start');
    if (this.documentosEliminados.length === 0) {
      this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.deleteDocumentos.name}()`, 'end');
      return of(void 0);
    }
    return from(this.documentosEliminados).pipe(
      mergeMap((documento) => {
        return this.prorrogaDocumentoService.deleteById(documento.id)
          .pipe(
            switchMap(() => {
              this.documentosEliminados = this.documentosEliminados.filter(deleted =>
                deleted.id !== documento.id);
              return this.documentoService.eliminarFichero(documento.documentoRef).pipe(
                tap(() => this.logger.debug(ProyectoProrrogaDocumentosFragment.name,
                  `${this.documentoService.eliminarFichero.name}()`, 'end'))
              );
            }),
            tap(() => this.logger.debug(ProyectoProrrogaDocumentosFragment.name,
              `${this.deleteDocumentos.name}()`, 'end'))
          );
      }));

  }

  private updateDocumentos(nodes: NodeDocumento[]): Observable<void> {
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.updateDocumentos.name}()`, 'start');
    if (nodes.length === 0) {
      this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.updateDocumentos.name}()`, 'end');
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.prorrogaDocumentoService.update(node.documento.value.id, node.documento.value).pipe(
          map((updated) => {
            node.documento = new StatusWrapper<IProyectoProrrogaDocumento>(updated);
          }),
          tap(() => this.logger.debug(ProyectoProrrogaDocumentosFragment.name,
            `${this.updateDocumentos.name}()`, 'end'))
        );
      }));
  }

  private createDocumentos(nodes: NodeDocumento[]): Observable<void> {
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.createDocumentos.name}()`, 'start');
    if (nodes.length === 0) {
      this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.createDocumentos.name}()`, 'end');
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap(node => {
        node.documento.value.proyectoProrroga = {
          id: this.getKey() as number
        } as IProyectoProrroga;
        return this.prorrogaDocumentoService.create(node.documento.value).pipe(
          map(created => {
            node.documento = new StatusWrapper<IProyectoProrrogaDocumento>(created);
          }),
          tap(() => this.logger.debug(ProyectoProrrogaDocumentosFragment.name,
            `${this.createDocumentos.name}()`, 'end'))
        );
      }));
  }

  private isSaveOrUpdateComplete(nodes: NodeDocumento[]): boolean {
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'start');
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
    this.logger.debug(ProyectoProrrogaDocumentosFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'end');
    return true;
  }

}
