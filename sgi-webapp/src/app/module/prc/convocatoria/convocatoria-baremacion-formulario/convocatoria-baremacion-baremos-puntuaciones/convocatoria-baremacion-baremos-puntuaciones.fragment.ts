import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaremo } from '@core/models/prc/baremo';
import { IConfiguracionBaremo, TIPO_NODOS_PESO, TipoNodo } from '@core/models/prc/configuracion-baremo';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { Fragment } from '@core/services/action-service';
import { ConfiguracionBaremoService } from '@core/services/prc/configuracion-baremo/configuracion-baremo.service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, tap, toArray } from 'rxjs/operators';

export class NodeConfiguracionBaremo {
  configuracionBaremo: IConfiguracionBaremo;
  baremo: IBaremo;
  parent: NodeConfiguracionBaremo;
  disabled: boolean;
  checked: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeConfiguracionBaremo[];
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _hasError: boolean;
  // tslint:disable-next-line: variable-name
  _errorsMsgMap: Map<string, string>;

  constructor(
    configuracionBaremo: IConfiguracionBaremo, baremo: IBaremo,
    level: number, parent: NodeConfiguracionBaremo = null, disabled = true) {
    this.configuracionBaremo = configuracionBaremo;
    this.baremo = baremo;
    this._childs = [];
    this.disabled = disabled;
    this._level = level;
    this.parent = parent;
    this._errorsMsgMap = new Map<string, string>();
    this._hasError = false;
  }

  get childs(): NodeConfiguracionBaremo[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  addChild(child: NodeConfiguracionBaremo): void {
    child.parent = this;
    this._childs.push(child);
  }

  addChilds(childs: NodeConfiguracionBaremo[]): void {
    this._childs = childs;
  }

  removeChild(childToRemove: NodeConfiguracionBaremo): void {
    this._childs = this._childs.filter((child) => child !== childToRemove);
  }

  addError(error: string): void {
    this._errorsMsgMap.set(error, error);
    this._hasError = this._errorsMsgMap.size > 0;
  }

  removeError(error: string): void {
    this._errorsMsgMap.delete(error);
    this._hasError = this._errorsMsgMap.size > 0;
  }

  get errorsMsg(): string[] {
    return Array.from(this._errorsMsgMap.values());
  }

  get hasError(): boolean {
    return this._hasError;
  }
}

const ERROR_MSG_PESO_TOTAL = marker('error.prc.baremo.peso-total');
const ERROR_MSG_PESO_NODE_SIN_NODOS_HOJA = marker('error.prc.baremo.nodo-peso-sin-nodos-hoja');

export class ConvocatoriaBaremacionBaremosPuntuacionesFragment extends Fragment {
  private readonly PESO_REQUIRED = 100;
  private configuracionBaremoNodes$ = new BehaviorSubject<NodeConfiguracionBaremo[]>([]);
  private baremoLookup: Map<number, IBaremo>;
  private selectedNodes: NodeConfiguracionBaremo[];
  hasErrorNoneSelectedNode = false;

  get convocatoriaBaremacion(): IConvocatoriaBaremacion {
    return this._convocatoriaBaremacion;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    // tslint:disable-next-line: variable-name
    private _convocatoriaBaremacion: IConvocatoriaBaremacion,
    public readonly isEditPerm: boolean,
    private readonly configuracionBaremoService: ConfiguracionBaremoService,
    private readonly convocatoriaBaremacionService: ConvocatoriaBaremacionService
  ) {
    super(key);
  }

  protected onInitialize(): void | Observable<any> {
    if (this.getKey()) {
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.IS_NULL, '')
      };
      forkJoin({
        baremos: this.convocatoriaBaremacionService.findBaremos(Number(this.getKey())),
        configuracionesBaremo: this.configuracionBaremoService.findAll(findOptions)
      }).pipe(
        tap(({ baremos }) => this.buildBaremoLookup(baremos.items)),
        map(({ configuracionesBaremo }) => configuracionesBaremo.items.map(configuracionBaremo =>
          this.createNodeBaremable(configuracionBaremo, null)
        )),
        mergeMap(configuracionBaremoNodes => this.getChildrenNodes(configuracionBaremoNodes)),
      ).subscribe(configuracionBaremoNodes => this.configuracionBaremoNodes$.next(configuracionBaremoNodes));
    }
  }

  private buildBaremoLookup(baremos: IBaremo[]): void {
    this.baremoLookup = new Map(
      baremos.map(baremo => ([baremo?.configuracionBaremo?.id, baremo]))
    );
  }

  getChildrenNodes(nodes: NodeConfiguracionBaremo[]): Observable<NodeConfiguracionBaremo[]> {
    return from(nodes).pipe(
      concatMap(node => this.fetchChildrenNodes(node)),
      toArray()
    );
  }

  fetchChildrenNodes(parentNode: NodeConfiguracionBaremo): Observable<NodeConfiguracionBaremo> {
    if (this.hasNodeChildren(parentNode)) {
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('padreId', SgiRestFilterOperator.EQUALS, parentNode?.configuracionBaremo?.id?.toString())
      };
      return this.configuracionBaremoService.findAll(findOptions).pipe(
        map(configuracionesBaremo =>
          configuracionesBaremo.items.map(configuracionBaremo =>
            this.isConfiguracionBaremoNoBaremable(configuracionBaremo) ?
              this.createNodeNoBaremable(configuracionBaremo, parentNode) : this.createNodeBaremable(configuracionBaremo, parentNode))

        ),
        mergeMap(childrenNodes => this.getChildrenNodes(childrenNodes)),
        map(childrenNodes => {
          parentNode.addChilds(childrenNodes);
          return parentNode;
        })
      );
    } else {
      return of(parentNode);
    }
  }

  private hasNodeChildren(node: NodeConfiguracionBaremo): boolean {
    const tipoNodo = node.configuracionBaremo.tipoNodo;
    return tipoNodo === TipoNodo.NO_BAREMABLE || tipoNodo === TipoNodo.PESO;
  }

  private isConfiguracionBaremoNoBaremable(configuracionBaremo: IConfiguracionBaremo): boolean {
    return configuracionBaremo.tipoNodo === TipoNodo.NO_BAREMABLE;
  }

  private createNodeNoBaremable(configuracionBaremo: IConfiguracionBaremo, parentNode: NodeConfiguracionBaremo): NodeConfiguracionBaremo {
    return new NodeConfiguracionBaremo(
      configuracionBaremo, this.getBaremo(configuracionBaremo), parentNode.level + 1, parentNode
    );
  }

  private createNodeBaremable(configuracionBaremo: IConfiguracionBaremo, parentNode: NodeConfiguracionBaremo): NodeConfiguracionBaremo {
    const baremo = this.getBaremo(configuracionBaremo);
    const level = parentNode ? parentNode.level + 1 : 0;
    return new NodeConfiguracionBaremo(configuracionBaremo, baremo, level, parentNode, !this.isEditPerm || !!!baremo);
  }

  private getBaremo({ id }: IConfiguracionBaremo): IBaremo | undefined {
    return this.baremoLookup.get(id);
  }

  editBaremo(node: NodeConfiguracionBaremo, baremo: IBaremo) {
    baremo.convocatoriaBaremacion = this.convocatoriaBaremacion;
    node.baremo = baremo;
    this.setErrors(this.checkPesoTotalEqualsToPesoRequired(this.selectedNodes));
    this.setChanges(true);
  }

  checklistSelectionInitialization(selectedNodes: NodeConfiguracionBaremo[]): void {
    this.selectedNodes = selectedNodes;
  }

  checklistSelectionChanged(selectedNodes: NodeConfiguracionBaremo[]): void {
    this.selectedNodes = selectedNodes;
    this.checkSelectionValid(selectedNodes);
    this.setChanges(true);
  }

  private checkSelectionValid(selectedNodes: NodeConfiguracionBaremo[]): void {
    this.hasErrorNoneSelectedNode = selectedNodes.length === 0;
    const hasErrorPesoTotal = this.checkPesoTotalEqualsToPesoRequired(selectedNodes);
    const hasErrorLeafNodeSelected = this.checkPesoNodeHasAnyDescendantLeafNodeSelected(selectedNodes);
    this.setErrors(this.hasErrorNoneSelectedNode || hasErrorPesoTotal || hasErrorLeafNodeSelected);
  }

  private checkPesoTotalEqualsToPesoRequired(selectedNodes: NodeConfiguracionBaremo[]): boolean {
    const pesoNodes = selectedNodes.filter(node => TIPO_NODOS_PESO.includes(node.configuracionBaremo.tipoNodo));
    const pesoTotal = pesoNodes.reduce((accum, node) => (node.baremo.peso ?? 0) + accum, 0);
    const hasErrorPesoTotal = pesoTotal !== this.PESO_REQUIRED;
    pesoNodes.forEach(pesoNode =>
      hasErrorPesoTotal ? pesoNode.addError(ERROR_MSG_PESO_TOTAL) : pesoNode.removeError(ERROR_MSG_PESO_TOTAL)
    );
    return hasErrorPesoTotal;
  }

  private checkPesoNodeHasAnyDescendantLeafNodeSelected(selectedNodes: NodeConfiguracionBaremo[]): boolean {
    const pesoNodes = selectedNodes.filter(selectedNode => selectedNode.configuracionBaremo.tipoNodo === TipoNodo.PESO);
    const leafNodes = selectedNodes.filter(selectedNode =>
      selectedNode.configuracionBaremo.tipoNodo === TipoNodo.PUNTOS ||
      selectedNode.configuracionBaremo.tipoNodo === TipoNodo.SIN_PUNTOS);
    let hasAnyErrorPesoNodes = false;
    // Checks if any leaf node has a peso node as root parent
    pesoNodes.forEach(pesoNode => {
      const hasErrorPesoNode = !leafNodes.some(
        leafNode => this.getParentRootNode(leafNode)?.configuracionBaremo.id === pesoNode.configuracionBaremo.id);
      hasErrorPesoNode ? pesoNode.addError(ERROR_MSG_PESO_NODE_SIN_NODOS_HOJA) : pesoNode.removeError(ERROR_MSG_PESO_NODE_SIN_NODOS_HOJA);
      hasAnyErrorPesoNodes = hasErrorPesoNode || hasAnyErrorPesoNodes;
    });
    return hasAnyErrorPesoNodes;
  }

  private getParentRootNode(node: NodeConfiguracionBaremo): NodeConfiguracionBaremo {
    const parent = node.parent;
    // Search for parent root node
    return parent ? this.getParentRootNode(parent) : node;
  }

  getBaremoConfiguracionNodes$(): Observable<NodeConfiguracionBaremo[]> {
    return this.configuracionBaremoNodes$.asObservable();
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    const baremosToSave = this.selectedNodes.map(node => node.baremo);
    this.convocatoriaBaremacionService.updateBaremos(this.convocatoriaBaremacion.id, baremosToSave);
    return this.convocatoriaBaremacionService.updateBaremos(this.convocatoriaBaremacion.id, baremosToSave)
      .pipe(
        tap(baremos => this.buildBaremoLookup(baremos)),
        tap(() => this.refreshNodeConfiguracionBaremoData()),
        tap(() => this.setChanges(false)),
        map(() => this.convocatoriaBaremacion.id)
      );
  }

  private refreshNodeConfiguracionBaremoData(): void {
    const nodes = this.configuracionBaremoNodes$.value;
    nodes.forEach(node => {
      const baremo = this.getBaremo(node.configuracionBaremo);
      node.baremo = baremo;
      node.disabled = typeof baremo === 'undefined';
      this.refreshChildrenNodesConfiguracionBaremoData(node);
    });
  }

  private refreshChildrenNodesConfiguracionBaremoData(node: NodeConfiguracionBaremo): void {
    node.childs.forEach(child => {
      if (child.configuracionBaremo.tipoNodo !== TipoNodo.NO_BAREMABLE) {
        const baremo = this.getBaremo(child.configuracionBaremo);
        child.baremo = baremo;
        child.disabled = typeof baremo === 'undefined';
      }
      this.refreshChildrenNodesConfiguracionBaremoData(child);
    });
  }
}
