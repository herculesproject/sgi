import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IPrograma } from '@core/models/csp/programa';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProgramaService } from '@core/services/csp/programa.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, startWith, switchMap, takeLast, tap } from 'rxjs/operators';


const PROYECTO_ENTIDAD_CONVOCANTE_KEY = marker('csp.proyecto-entidad-convocante');
const PROYECTO_ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.proyecto-entidad-convocante.plan');
const PROYECTO_ENTIDAD_CONVOCANTE_PROGRAMA_KEY = marker('csp.proyecto-entidad-convocante.programa');
const MSG_CONTINUE_ENTITY_NOTSET_KEY = marker('msg.continue.entity.not-set');

const MSG_ERROR_FORM_GROUP = marker('form-group.error');

export interface ProyectoEntidadConvocanteModalData {
  proyectoEntidadConvocante: IProyectoEntidadConvocante;
  selectedEmpresas: IEmpresaEconomica[];
}

class NodePrograma {
  parent: NodePrograma;
  programa: IPrograma;
  // tslint:disable-next-line: variable-name
  _childs: NodePrograma[];
  get childs(): NodePrograma[] {
    return this._childs;
  }

  constructor(programa: IPrograma) {
    this.programa = programa;
    this._childs = [];
  }

  addChild(child: NodePrograma) {
    child.parent = this;
    child.programa.padre = this.programa;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodePrograma) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodePrograma[]): NodePrograma[] {
  return nodes.sort((a, b) => {
    if (a.programa.nombre < b.programa.nombre) {
      return -1;
    }
    if (a.programa.nombre > b.programa.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './proyecto-entidad-convocante-modal.component.html',
  styleUrls: ['./proyecto-entidad-convocante-modal.component.scss']
})
export class ProyectoEntidadConvocanteModalComponent extends
  BaseModalComponent<ProyectoEntidadConvocanteModalData, ProyectoEntidadConvocanteModalComponent> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};
  msgParamPlanEntity = {};
  msgParamProgramaEntity = {};

  planes$: Observable<IPrograma[]>;
  private programaFiltered = [] as IPrograma[];

  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodePrograma>();
  private nodeMap = new Map<number, NodePrograma>();

  // indica si estamos en modo creación (true) o actualización (false)
  create: boolean;

  checkedNode: NodePrograma;
  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    protected logger: NGXLogger,
    public matDialogRef: MatDialogRef<ProyectoEntidadConvocanteModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoEntidadConvocanteModalData,
    protected snackBarService: SnackBarService,
    private programaService: ProgramaService,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, 'constructor()', 'start');

    this.setupLayout();

    if (!data.proyectoEntidadConvocante) {
      data.proyectoEntidadConvocante = {} as IProyectoEntidadConvocante;
      this.create = true;
    } else {
      this.create = false;
    }

    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, 'constructor()', 'end');
  }

  private setupLayout(): void {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
  }

  ngOnInit() {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.programaService.findAllPlan().subscribe(
      list => {
        this.programaFiltered = list.items;
        this.planes$ = this.formGroup.get('plan').valueChanges.pipe(
          startWith(''),
          map(value => this.filterPrograma(value)),
          tap(() => {
            // Reset selected node on first user change
            if (this.formGroup.get('plan').value?.id !== this.getPlan(this.data.proyectoEntidadConvocante)?.id) {
              this.formGroup.get('programa').setValue(undefined);
              this.checkedNode = undefined;
            }
            this.loadTreePrograma();
          })
        );
      }
    );
    this.subscriptions.push(subcription);
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, 'ngOnInit()', 'end');
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_KEY
    ).subscribe((value) => this.msgParamEntity = { entity: value });
    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_PLAN_KEY
    ).subscribe((value) => this.msgParamPlanEntity = { entity: value });
    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_PROGRAMA_KEY
    ).subscribe((value) => this.msgParamProgramaEntity = { entity: value });
  }

  private updateProgramas(programa: NodePrograma[]) {
    this.dataSource.data = programa;
  }

  private filterPrograma(value: string): IPrograma[] {
    const filterValue = value.toString().toLowerCase();
    return this.programaFiltered.filter(programa =>
      programa.nombre.toLowerCase().includes(filterValue));
  }

  getNombrePlan(plan: IPrograma): string {
    return plan?.nombre;
  }

  private loadTreePrograma() {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `loadTreePrograma()`, 'start');
    if (this.data.proyectoEntidadConvocante.programaConvocatoria) {
      const node = new NodePrograma(this.data.proyectoEntidadConvocante.programaConvocatoria);
      this.nodeMap.set(node.programa.id, node);
      const subscription = this.getChilds(node).pipe(map(() => node)).pipe(
        tap(() => {
          this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
          this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
            `loadTreePrograma()`, 'end');
        })
      ).subscribe((nodePrograma) => {
        this.dataSource.data = [nodePrograma];
      });
      this.subscriptions.push(subscription);
    } else {
      const id = this.formGroup.get('plan').value?.id;
      if (id && !isNaN(id)) {
        this.checkedNode = undefined;
        const subscription = this.programaService.findAllHijosPrograma(id).pipe(
          switchMap(response => {
            return from(response.items).pipe(
              mergeMap((programa) => {
                const node = new NodePrograma(programa);
                this.nodeMap.set(node.programa.id, node);
                return this.getChilds(node).pipe(map(() => node));
              })
            );
          })
        ).subscribe(
          (programa) => {
            const current = this.dataSource.data;
            current.push(programa);
            this.publishNodes(current);
            this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
              `loadTreePrograma()`, 'end');
          },
          (error) => {
            this.logger.error(ProyectoEntidadConvocanteModalComponent.name,
              `loadTreePrograma()`, error);
          },
          () => {
            this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value);
            if (this.checkedNode) {
              this.expandNodes(this.checkedNode);
            }
          }
        );
        this.subscriptions.push(subscription);
      }
      else {
        this.updateProgramas([]);
        this.nodeMap.clear();
      }
    }
  }

  private getChilds(parent: NodePrograma): Observable<NodePrograma[]> {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getChilds(parent: ${parent})`, 'start');
    return this.programaService.findAllHijosPrograma(parent.programa.id).pipe(
      map((result) => {
        const childs: NodePrograma[] = result.items.map(
          (programa) => {
            const child = new NodePrograma(programa);
            child.parent = parent;
            this.nodeMap.set(child.programa.id, child);
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
        return of([]);
      }),
      takeLast(1),
      tap(() => this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
        `getChilds(parent: ${parent})`, 'end'))
    );
  }

  private expandNodes(node: NodePrograma) {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `expandNodes(node: ${node})`, 'start');
    if (node && node.parent) {
      this.treeControl.expand(node.parent);
      this.expandNodes(node.parent);
    }
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `expandNodes(node: ${node})`, 'start');
  }

  private publishNodes(rootNodes?: NodePrograma[]) {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, `publishNodes()`, 'start');
    let nodes = rootNodes ? rootNodes : this.dataSource.data;
    nodes = sortByName(nodes);
    this.updateProgramas(nodes);
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, `publishNodes()`, 'end');
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup({
      entidad: new FormControl({ value: this.data.proyectoEntidadConvocante.entidad, disabled: !this.create }, Validators.required),
      plan: new FormControl(
        {
          value: this.getPlan(this.data.proyectoEntidadConvocante),
          disabled: !(this.create || this.data.proyectoEntidadConvocante.programaConvocatoria == null)
        }, IsEntityValidator.isValid()),
      programa: new FormControl(this.data.proyectoEntidadConvocante.programa?.id)
    });
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }

  protected getDatosForm(): ProyectoEntidadConvocanteModalData {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getDatosForm()`, 'start');
    this.data.proyectoEntidadConvocante.entidad = this.formGroup.get('entidad').value;
    if (this.checkedNode) {
      // Se ha seleccionado modalidad
      this.data.proyectoEntidadConvocante.programa = this.checkedNode?.programa;
    } else {
      // No se ha seleccionado modalidad
      // se limpia porque se ha deseleccionado
      this.data.proyectoEntidadConvocante.programa = null;
      if (this.formGroup.get('plan').value?.id) {
        // Pero se ha seleccionado plan
        if (!this.data.proyectoEntidadConvocante.programaConvocatoria) {
          // Si el plan no viene fijado por la Convocatoria
          // el plan se debe guardar como modalidad
          this.data.proyectoEntidadConvocante.programa = this.formGroup.get('plan').value;
        }
      }
    }
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getDatosForm()`, 'start');
    return this.data;
  }

  onCheckNode(node: NodePrograma, $event: MatCheckboxChange): void {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `onCheckNode(node: ${node}, $event: ${$event})`, 'start');
    this.checkedNode = $event.checked ? node : undefined;
    this.formGroup.get('programa').setValue(this.checkedNode?.programa?.id);
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `onCheckNode(node: ${node}, $event: ${$event})`, 'end');
  }

  saveOrUpdate(): void {
    this.logger.debug(BaseModalComponent.name, `saveOrUpdate()`, 'start');
    if (this.formGroup.valid) {
      const plan = this.formGroup.get('plan').value;
      const programa = this.checkedNode;
      if (!plan && !programa) {
        this.saveIncompleteFormGroup(MSG_CONTINUE_ENTITY_NOTSET_KEY, this.msgParamPlanEntity);
      } else if (!programa) {
        this.saveIncompleteFormGroup(MSG_CONTINUE_ENTITY_NOTSET_KEY, this.msgParamProgramaEntity);
      } else {
        this.closeModal(this.getDatosForm());
      }
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(BaseModalComponent.name, `saveOrUpdate()`, 'end');
  }

  private saveIncompleteFormGroup(message: string, params?: {}): void {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `saveIncompleteFormGroup()`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(message, params).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.closeModal(this.getDatosForm());
          }
          this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
            `saveIncompleteFormGroup()`, 'end');
        }
      )
    );
  }

  private getPlan(value: IProyectoEntidadConvocante): IPrograma {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getPlan(value: ${value})`, 'start');
    if (value.programa != null) {
      this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
        `getPlan(value: ${value})`, 'end');
      return this.getTopLevel(value.programa);
    }
    if (value.programaConvocatoria != null) {
      this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
        `getPlan(value: ${value})`, 'end');
      return this.getTopLevel(value.programaConvocatoria);
    }
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getPlan(value: ${value})`, 'end');
    return null;
  }

  private getTopLevel(programa: IPrograma): IPrograma {
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getTopLevel(programa: ${programa})`, 'start');
    if (programa.padre == null) {
      this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
        `getTopLevel(programa: ${programa})`, 'end');
      return programa;
    }
    this.logger.debug(ProyectoEntidadConvocanteModalComponent.name,
      `getTopLevel(programa: ${programa})`, 'end');
    return this.getTopLevel(programa.padre);
  }
}
