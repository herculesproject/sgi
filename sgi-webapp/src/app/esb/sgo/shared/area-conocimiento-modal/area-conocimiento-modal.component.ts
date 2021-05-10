import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTree, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const MSG_ERROR_LOAD = marker('error.load');

export interface AreaConocimientoDataModal {
  selectedAreasConocimiento: IAreaConocimiento[];
  multiSelect: boolean;
}

class NodeAreaConocimiento {
  disabled: boolean;
  parent: NodeAreaConocimiento;
  areaConocimiento: IAreaConocimiento;
  checked: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeAreaConocimiento[];
  // tslint:disable-next-line: variable-name
  _childsLoaded: boolean;
  // tslint:disable-next-line: variable-name
  _level: number;

  get childs(): NodeAreaConocimiento[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  setChildsLoaded(): void {
    this._childsLoaded = true;
  }

  get childsLoaded() {
    return this._childsLoaded;
  }

  setCheckedAndDisabled() {
    this.checked = true;
    this.disabled = true;
  }

  constructor(areaConocimiento: IAreaConocimiento, level: number) {
    this.areaConocimiento = areaConocimiento;
    this._childs = [];
    this._childsLoaded = false;
    this._level = level;
  }

  addChild(child: NodeAreaConocimiento) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodeAreaConocimiento) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodeAreaConocimiento[]): NodeAreaConocimiento[] {
  return nodes.sort((a, b) => {
    if (a.areaConocimiento.nombre < b.areaConocimiento.nombre) {
      return -1;
    }
    if (a.areaConocimiento.nombre > b.areaConocimiento.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './area-conocimiento-modal.component.html',
  styleUrls: ['./area-conocimiento-modal.component.scss']
})
export class AreaConocimientoModalComponent
  extends BaseModalComponent<IAreaConocimiento[], AreaConocimientoModalComponent>
  implements OnInit {

  areasConocimiento$ = new BehaviorSubject<NodeAreaConocimiento[]>([]);
  selectedAreasConocimiento = [] as IAreaConocimiento[];

  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodeAreaConocimiento>;
  treeControl: FlatTreeControl<NodeAreaConocimiento>;
  private treeFlattener: MatTreeFlattener<NodeAreaConocimiento, NodeAreaConocimiento>;
  dataSource: MatTreeFlatDataSource<NodeAreaConocimiento, NodeAreaConocimiento>;

  private getLevel = (node: NodeAreaConocimiento) => node.level;
  private isExpandable = (node: NodeAreaConocimiento) => node.childs.length > 0;
  private getChildren = (node: NodeAreaConocimiento): NodeAreaConocimiento[] => node.childs;
  private transformer = (node: NodeAreaConocimiento, level: number) => node;

  hasChild = (_: number, node: NodeAreaConocimiento) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<AreaConocimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AreaConocimientoDataModal,
    private areaConocimientoService: AreaConocimientoService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(snackBarService, matDialogRef, data.selectedAreasConocimiento);

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeAreaConocimiento>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadInitialTree();

    this.subscriptions.push(
      this.areasConocimiento$
        .subscribe((areasConocimiento) => {
          this.dataSource.data = areasConocimiento;
        })
    );
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({});
    return formGroup;
  }

  protected getDatosForm(): IAreaConocimiento[] {
    return this.selectedAreasConocimiento;
  }

  /**
   * Actualiza los nodos del arbol que estan seleccionados.
   *
   * @param node nodo del arbol seleccionado o deseleccionado
   * @param $event evento de seleccion
   */
  onCheckNode(node: NodeAreaConocimiento, $event: MatCheckboxChange): void {
    if ($event.checked) {
      if (this.data.multiSelect) {
        node.checked = true;
        this.selectedAreasConocimiento.push(node.areaConocimiento);
      } else {
        this.treeControl.dataNodes.forEach(dataNode =>
          dataNode.checked = $event.checked && dataNode.areaConocimiento.id === node.areaConocimiento.id
        );
        this.selectedAreasConocimiento = [node.areaConocimiento];
      }
    } else {
      node.checked = false;
      this.selectedAreasConocimiento = this.selectedAreasConocimiento.filter(checkedNode => checkedNode.id !== node.areaConocimiento.id);
    }
  }

  /**
   * Recupera los hijos de los hijos del nodo expandido si no se han cargado previamente.
   *
   * @param node node del arbol expandido o colapsado.
   */
  onToggleNode(node: NodeAreaConocimiento): void {
    if (this.treeControl.isExpanded(node)) {
      if (node.childs.some(child => !child.childsLoaded)) {
        this.areaConocimientoService.findAllHijos(node.childs.filter(child => !child.childsLoaded).map(c => c.areaConocimiento.id))
          .pipe(
            tap((areasConocimiento) => this.buildTree(areasConocimiento.items))
          ).subscribe(
            () => {
              node.childs.forEach(childNode => childNode.setChildsLoaded());
              this.publishNodes();
            },
            (error) => {
              this.logger.error(error);
              this.snackBarService.showError(MSG_ERROR_LOAD);
            }
          );
      }
    }
  }

  /**
   * Crea el arbol con los nodos de primer nivel y sus hijos
   */
  private loadInitialTree() {
    this.subscriptions.push(
      this.areaConocimientoService.findAllRamasConocimiento()
        .pipe(
          map((ramasConocimiento) => ramasConocimiento.items.map(
            (ramaConocimiento) => new NodeAreaConocimiento(ramaConocimiento, 0))
          ),
          tap((nodes) => this.publishNodes(nodes)),
          switchMap((nodes) => {
            const ids = nodes.map(node => node.areaConocimiento.id);
            return this.areaConocimientoService.findAllHijos(ids).pipe(
              map((areasConocimiento) => {
                nodes.forEach(node => node.childs.forEach(childNode => childNode.setChildsLoaded()));
                this.buildTree(areasConocimiento.items);
                return nodes;
              })
            );
          })
        ).subscribe(
          (nodes) => {
            this.publishNodes(nodes, true);
          },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_ERROR_LOAD);
          }
        )
    );
  }

  /**
   * Crea los nodos correspondientes a la lista de areas de conocimiento en las
   * posiciones del arbol correspondientes (añade nodos como hijos de otros nodos).
   *
   * @param areasConocimiento lista de IAreaConocimiento.
   */
  private buildTree(areasConocimiento: IAreaConocimiento[]): void {
    areasConocimiento.forEach((areaConocimiento: IAreaConocimiento) => {
      const areaConocimientoPadreNode = this.treeControl.dataNodes.find(node => node.areaConocimiento.id === areaConocimiento.padreId);
      const areaConocimientoNode = new NodeAreaConocimiento(areaConocimiento, areaConocimientoPadreNode.level + 1);
      areaConocimientoPadreNode.addChild(areaConocimientoNode);
    });
  }

  /**
   * Actualiza el arbol con la lista de nodos indicada.
   *
   * @param rootNodes (opcional) nodos raiz del arbol, si no se indica se actualiza los nodos actuales del arbol.
   * @param recreateTree (por defecto false) indica si se hace un "reset" del arbol antes de hacer la actualizacion.
   */
  private publishNodes(rootNodes?: NodeAreaConocimiento[], recreateTree = false): void {
    let nodes = rootNodes ? rootNodes : this.areasConocimiento$.value;
    nodes = sortByName(nodes);
    this.data.selectedAreasConocimiento?.forEach(areaSeleccionada => {
      this.treeControl.dataNodes.find(node => node.areaConocimiento.id === areaSeleccionada.id)?.setCheckedAndDisabled();
    });
    this.refreshTree(nodes, recreateTree);
    this.areasConocimiento$.next(nodes);
  }


  /**
   * Actualiza el arbol con los cambios realizados en la lista de nodos.
   *
   * @param nodes lista de nodos del arbol
   * @param recreate indica si se hace un "reset" del arbol antes de hacer la actualizacion (por defecto false)
   */
  private refreshTree(nodes: NodeAreaConocimiento[], recreate = false): void {
    // There is no way to update the tree without recreating it,
    // because CDK Tree detect changes only when a root nodes changes
    // See: https://github.com/angular/components/issues/11381
    if (recreate) {
      this.matTree.renderNodeChanges([]);
    }

    this.matTree.renderNodeChanges(nodes);
  }

}
