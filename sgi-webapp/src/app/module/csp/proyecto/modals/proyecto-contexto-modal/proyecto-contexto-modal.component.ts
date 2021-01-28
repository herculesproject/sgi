import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface ProyectoContextoModalData {
  root: IAreaTematica;
  areaTematica: IAreaTematica;
}
class NodeAreaTematica {
  parent: NodeAreaTematica;
  areaTematica: StatusWrapper<IAreaTematica>;
  // tslint:disable-next-line: variable-name
  _childs: NodeAreaTematica[];
  get childs(): NodeAreaTematica[] {
    return this._childs;
  }

  constructor(areaTematica: StatusWrapper<IAreaTematica>) {
    this.areaTematica = areaTematica;
    this._childs = [];
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodeAreaTematica[]): NodeAreaTematica[] {
  return nodes.sort((a, b) => {
    if (a.areaTematica.value.nombre < b.areaTematica.value.nombre) {
      return -1;
    }
    if (a.areaTematica.value.nombre > b.areaTematica.value.nombre) {
      return 1;
    }
    return 0;
  });
}

const MSG_ERROR_AREA_TEMATICA = marker('csp.proyecto.contexto.area.tematica.modal.error.areas');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

@Component({
  templateUrl: './proyecto-contexto-modal.component.html',
  styleUrls: ['./proyecto-contexto-modal.component.scss']
})
export class ProyectoContextoModalComponent extends
  BaseModalComponent<ProyectoContextoModalData, ProyectoContextoModalComponent> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  areasTematicas$: Observable<IAreaTematica[]>;
  treeControl = new NestedTreeControl<NodeAreaTematica>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodeAreaTematica>();
  private nodeMap = new Map<number, NodeAreaTematica>();

  checkedNode: NodeAreaTematica;
  textSaveOrUpdate: string;

  hasChild = (_: number, node: NodeAreaTematica) => node.childs.length > 0;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoContextoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoContextoModalData,
    private areaTematicaService: AreaTematicaService
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(ProyectoContextoModalComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
    this.textSaveOrUpdate = this.data?.areaTematica ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(ProyectoContextoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoContextoModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadAreasTematicas();
    this.loadTreeAreaTematica();
    const subscription = this.formGroup.get('padre').valueChanges.subscribe(() => this.loadTreeAreaTematica());
    this.subscriptions.push(subscription);
    this.logger.debug(ProyectoContextoModalComponent.name, 'ngOnInit()', 'start');
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ProyectoContextoModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup({
      padre: new FormControl({
        value: this.data?.root,
        disabled: Boolean(this.data?.root?.nombre),
      }, [Validators.required, IsEntityValidator.isValid()]),
    });
    this.logger.debug(ProyectoContextoModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }

  protected getDatosForm(): ProyectoContextoModalData {
    this.logger.debug(ProyectoContextoModalComponent.name, `getDatosForm()`, 'start');
    const padre = this.formGroup.get('padre').value;
    const areaTematica = this.checkedNode?.areaTematica?.value;
    if (areaTematica) {
      this.data.areaTematica = areaTematica ? areaTematica : padre;
    } else {
      this.data.areaTematica = null;
    }

    this.data.root = padre;
    this.logger.debug(ProyectoContextoModalComponent.name, `getDatosForm()`, 'end');
    return this.data;
  }

  private loadAreasTematicas(): void {
    this.logger.debug(ProyectoContextoModalComponent.name,
      `loadAreasTematicas()`, 'start');
    this.dataSource.data = null;
    this.areasTematicas$ = this.areaTematicaService.findAllGrupo().pipe(
      map(res => res.items),
      tap(() => this.logger.debug(ProyectoContextoModalComponent.name,
        `loadAreasTematicas()`, 'end')),
      catchError(error => {
        this.snackBarService.showError(MSG_ERROR_AREA_TEMATICA);
        this.logger.error(ProyectoContextoModalComponent.name,
          `loadAreasTematicas()`, error);
        return of([]);
      })
    );
  }


  private loadTreeAreaTematica(): void {
    this.logger.debug(ProyectoContextoModalComponent.name,
      `loadTreeAreaTematica()`, 'start');
    this.nodeMap.clear();
    this.dataSource.data = [];
    const padre = this.formGroup.get('padre').value;
    if (padre) {
      const susbcription = this.areaTematicaService.findAllHijosArea(padre.id).pipe(
        switchMap(response => {
          return from(response.items).pipe(
            mergeMap((areaTematica) => {
              const node = new NodeAreaTematica(new StatusWrapper<IAreaTematica>(areaTematica));
              this.nodeMap.set(node.areaTematica.value.id, node);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (result) => {
          const current = this.dataSource.data ? this.dataSource.data : [];
          current.push(result);
          this.publishNodes(current);
          this.checkedNode = this.nodeMap?.get(this.data?.areaTematica?.id);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
          this.logger.debug(ProyectoContextoModalComponent.name,
            `loadTreeAreaTematica()`, 'end');
        },
        (error) => {
          this.logger.error(ProyectoContextoModalComponent.name,
            `loadTreeAreaTematica()`, error);
        }
      );
      this.subscriptions.push(susbcription);
    }
  }

  getNombreAreaTematica(areaTematica?: IAreaTematica): string | undefined {
    return typeof areaTematica === 'string' ? areaTematica : areaTematica?.nombre;
  }

  private expandNodes(node: NodeAreaTematica) {
    this.logger.debug(ProyectoContextoModalComponent.name,
      `expandNodes(node: ${node})`, 'start');
    if (node && node.parent) {
      this.treeControl.expand(node.parent);
      this.expandNodes(node.parent);
    }
    this.logger.debug(ProyectoContextoModalComponent.name,
      `expandNodes(node: ${node})`, 'start');
  }

  private getChilds(parent: NodeAreaTematica): Observable<NodeAreaTematica[]> {
    this.logger.debug(ProyectoContextoModalComponent.name,
      `getChilds(parent: ${parent})`, 'start');
    return this.areaTematicaService.findAllHijosArea(parent.areaTematica.value.id).pipe(
      map((result) => {
        const childs: NodeAreaTematica[] = result.items.map(
          (areaTematica) => {
            const child = new NodeAreaTematica(new StatusWrapper<IAreaTematica>(areaTematica));
            child.parent = parent;
            this.nodeMap.set(child.areaTematica.value.id, child);
            return child;
          });
        return childs;
      }),
      switchMap((nodes) => {
        parent.childs.push(...nodes);
        parent.sortChildsByName();
        if (nodes.length > 0) {
          return from(nodes).pipe(
            mergeMap((node) => {
              return this.getChilds(node);
            })
          );
        }
        else {
          return of([]);
        }
      }),
      takeLast(1),
      tap(() => this.logger.debug(ProyectoContextoModalComponent.name,
        `getChilds(parent: ${parent})`, 'end'))
    );
  }

  private publishNodes(rootNodes?: NodeAreaTematica[]) {
    this.logger.debug(ProyectoContextoModalComponent.name, `publishNodes()`, 'start');
    let nodes = rootNodes ? rootNodes : this.dataSource.data;
    nodes = sortByName(nodes);
    this.dataSource.data = nodes;
    this.logger.debug(ProyectoContextoModalComponent.name, `publishNodes()`, 'end');
  }

  onCheckNode(node: NodeAreaTematica, $event: MatCheckboxChange): void {
    this.logger.debug(ProyectoContextoModalComponent.name,
      `onCheckNode(node: ${node}, $event: ${$event})`, 'start');
    this.checkedNode = $event.checked ? node : undefined;
    this.logger.debug(ProyectoContextoModalComponent.name,
      `onCheckNode(node: ${node}, $event: ${$event})`, 'end');
  }
}

