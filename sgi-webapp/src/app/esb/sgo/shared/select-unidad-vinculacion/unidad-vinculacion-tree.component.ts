import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatTree, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { MSG_PARAMS } from '@core/i18n';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { BehaviorSubject, Subscription } from 'rxjs';

export interface UnidadVinculacionSelected {
  tipo: IUnidadVinculacion;
  unidad: IUnidadVinculacion;
}

class NodeUnidadVinculacion {
  disabled: boolean;
  parent: NodeUnidadVinculacion;
  unidadVinculacion: IUnidadVinculacion;
  checked: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeUnidadVinculacion[];
  // tslint:disable-next-line: variable-name
  _childsLoaded: boolean;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _sortChildsWith: (a1: NodeUnidadVinculacion, a2: NodeUnidadVinculacion) => number;

  get childs(): NodeUnidadVinculacion[] { return this._childs; }
  get level(): number { return this._level; }
  get childsLoaded(): boolean { return this._childsLoaded; }

  setChildsLoaded(): void { this._childsLoaded = true; }

  setCheckedAndDisabled(): void {
    this.checked = true;
    this.disabled = true;
  }

  constructor(
    unidadVinculacion: IUnidadVinculacion,
    level: number,
    sortChildsWith: (a1: NodeUnidadVinculacion, a2: NodeUnidadVinculacion) => number
  ) {
    this.unidadVinculacion = unidadVinculacion;
    this._childs = [];
    this._childsLoaded = false;
    this._level = level;
    this._sortChildsWith = sortChildsWith;
  }

  addChild(child: NodeUnidadVinculacion): void {
    child.parent = this;
    this._childs.push(child);
    this._childs = sortNodes(this._childs, this._sortChildsWith);
  }
}

function sortNodes(
  nodes: NodeUnidadVinculacion[],
  sortWith: (a1: NodeUnidadVinculacion, a2: NodeUnidadVinculacion) => number
): NodeUnidadVinculacion[] {
  return nodes.sort(sortWith);
}

@Component({
  selector: 'sgi-unidad-vinculacion-tree',
  templateUrl: './unidad-vinculacion-tree.component.html',
})
export class UnidadVinculacionTreeComponent implements OnInit, OnChanges, OnDestroy {

  /** Refs de unidades ya asignadas: se muestran marcadas y deshabilitadas. */
  @Input() selectedRefs: string[] = [];
  @Input() onlyActive?: boolean;

  /** Emite cada vez que el usuario selecciona o deselecciona un nodo. */
  @Output() selectionChange = new EventEmitter<UnidadVinculacionSelected | null>();

  readonly tipos$ = new BehaviorSubject<IUnidadVinculacion[]>([]);
  readonly formGroup = new FormGroup({
    tipo: new FormControl(null, Validators.required)
  });

  treeControl: FlatTreeControl<NodeUnidadVinculacion>;
  dataSource: MatTreeFlatDataSource<NodeUnidadVinculacion, NodeUnidadVinculacion>;

  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodeUnidadVinculacion>;

  private arbolesPorTipo = new Map<string, NodeUnidadVinculacion[]>();
  private unidadesVinculacionTree$ = new BehaviorSubject<NodeUnidadVinculacion[]>([]);
  private selectedNode: NodeUnidadVinculacion;
  private subscriptions: Subscription[] = [];

  private readonly getLevel = (node: NodeUnidadVinculacion) => node.level;
  private readonly isExpandable = (node: NodeUnidadVinculacion) => node.childs.length > 0;
  private readonly getChildren = (node: NodeUnidadVinculacion) => node.childs;
  private readonly transformer = (node: NodeUnidadVinculacion) => node;
  private readonly sortNodesByNombre = (a1: NodeUnidadVinculacion, a2: NodeUnidadVinculacion) =>
    a1.unidadVinculacion?.nombre.localeCompare(a2.unidadVinculacion?.nombre);

  hasChild = (_: number, node: NodeUnidadVinculacion) => node.childs.length > 0;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(private unidadVinculacionService: UnidadVinculacionService) {
    const treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeUnidadVinculacion>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, treeFlattener);
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.unidadVinculacionService.findAllPadres(this.onlyActive).subscribe(response => {
        const tipos = response.items;
        this.tipos$.next(tipos);
        if (tipos.length === 1) {
          this.formGroup.controls.tipo.setValue(tipos[0]);
        }
      })
    );

    this.subscriptions.push(
      this.formGroup.controls.tipo.valueChanges.subscribe((tipo: IUnidadVinculacion) => {
        this.selectedNode = undefined;
        this.selectionChange.emit(null);
        this.loadUnidadTree(tipo?.id);
      })
    );

    this.subscriptions.push(
      this.unidadesVinculacionTree$.subscribe(nodes => {
        this.dataSource.data = nodes;
      })
    );
  }

  ngOnChanges(): void {
    this.arbolesPorTipo.clear();
    this.selectedNode = undefined;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
    this.tipos$.complete();
    this.unidadesVinculacionTree$.complete();
  }

  onCheckNode(node: NodeUnidadVinculacion, $event: MatCheckboxChange): void {
    node.checked = $event.checked;
    if ($event.checked) {
      if (this.selectedNode && this.selectedNode !== node) {
        this.selectedNode.checked = false;
      }
      this.selectedNode = node;
      this.selectionChange.emit({
        tipo: this.formGroup.controls.tipo.value,
        unidad: node.unidadVinculacion
      });
    } else if (this.selectedNode === node) {
      this.selectedNode = undefined;
      this.selectionChange.emit(null);
    }
  }

  onToggleNode(node: NodeUnidadVinculacion): void {
    if (this.treeControl.isExpanded(node) && !node.childsLoaded) {
      this.subscriptions.push(
        this.unidadVinculacionService.findAllHijos(node.unidadVinculacion.id, this.onlyActive).subscribe(response => {
          this.buildTree(response.items);
          node.setChildsLoaded();
          this.publishNodes();
        })
      );
    }
  }

  private loadUnidadTree(tipoId: string): void {
    if (!tipoId) {
      this.publishNodes([], true);
      return;
    }
    const cached = this.arbolesPorTipo.get(tipoId);
    if (cached) {
      this.publishNodes(cached, true);
      return;
    }
    this.subscriptions.push(
      this.unidadVinculacionService.findAllHijos(tipoId, this.onlyActive).subscribe(response => {
        const nodes = response.items.map(u => new NodeUnidadVinculacion(u, 0, this.sortNodesByNombre));
        this.publishNodes(nodes, true);
        this.arbolesPorTipo.set(tipoId, nodes);
      })
    );
  }

  private buildTree(unidades: IUnidadVinculacion[]): void {
    unidades.forEach(unidad => {
      const padre = this.treeControl.dataNodes.find(node => node.unidadVinculacion.id === unidad.predecesorId);
      if (padre) {
        padre.addChild(new NodeUnidadVinculacion(unidad, padre.level + 1, this.sortNodesByNombre));
      }
    });
  }

  private publishNodes(rootNodes?: NodeUnidadVinculacion[], recreateTree = false): void {
    let nodes = rootNodes ?? this.unidadesVinculacionTree$.value;
    nodes = sortNodes(nodes, this.sortNodesByNombre);
    this.refreshTree(nodes, recreateTree);
    this.unidadesVinculacionTree$.next(nodes);
    this.selectedRefs?.forEach(ref => {
      this.treeControl.dataNodes.find(node => node.unidadVinculacion.id === ref)?.setCheckedAndDisabled();
    });
  }

  private refreshTree(nodes: NodeUnidadVinculacion[], recreate = false): void {
    if (recreate) {
      this.matTree.renderNodeChanges([]);
    }
    this.matTree.renderNodeChanges(nodes);
  }
}
