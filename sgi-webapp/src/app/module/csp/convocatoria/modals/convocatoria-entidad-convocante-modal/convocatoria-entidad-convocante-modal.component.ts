import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Subscription, Observable } from 'rxjs';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { startWith, map } from 'rxjs/operators';

import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlattener, MatTreeFlatDataSource } from '@angular/material/tree';
import { IEntidadConvocante } from '@core/models/csp/entidad-convocante';
import { IObjectTree } from '@core/models/csp/object-tree';
import { ProgramaService } from '@core/services/csp/programa.service';

const MSG_ERROR_INIT = marker('csp.convocatoria.entidad.convocante.error.cargar');

/** Flat node with expandable and level information */
interface FlatNode {
  expandable: boolean;
  name: string;
  level: number;
}

@Component({
  templateUrl: './convocatoria-entidad-convocante-modal.component.html',
  styleUrls: ['./convocatoria-entidad-convocante-modal.component.scss']
})
export class ConvocatoriaEntidadConvocanteaModalComponent implements OnInit {

  private _transformer = (node: IObjectTree, level: number) => {
    return {
      expandable: !!node.children && node.children.length > 0,
      name: node.nombre,
      level: level,
    };
  }

  treeControl = new FlatTreeControl<FlatNode>(
    node => node.level, node => node.expandable);

  treeFlattener = new MatTreeFlattener(
    this._transformer, node => node.level, node => node.expandable, node => node.children);

  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxFlexProperties3: FxFlexProperties;
  fxFlexProperties4: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  suscripciones: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaEntidadConvocanteaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public entidadConvocante: IEntidadConvocante,
    private readonly snackBarService: SnackBarService,
    private readonly programaService: ProgramaService) {

    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';

    this.dataSource.data = [];

    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'constructor()', 'end');
  }

  hasChild = (_: number, node: FlatNode) => node.expandable;

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'ngOnInit()', 'start');

    this.initFormGroup();

    this.loadProgramas();

    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Carga los datos de programas
   */
  loadProgramas() {
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'loadProgramas()', 'start');
    this.suscripciones.push(
      this.programaService.findAll().subscribe(
        (res: SgiRestListResult<IObjectTree>) => {
          this.dataSource.data = res.items;

          this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'loadProgramas()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'loadProgramas()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'loadProgramas()', 'end');
  }


  /**
   * Inicializa formulario de creación/edición de la entidad convocante
   */
  private initFormGroup() {
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'initFormGroup()', 'start');
    this.formGroup = new FormGroup({
      entidadConvocante: new FormControl(this.entidadConvocante?.nombre, Validators.required),
      plan: new FormControl(this.entidadConvocante?.plan),
      item: new FormControl(this.entidadConvocante?.itemPrograma)
    });
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'initFormGroup()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve la entidad convocante modificado o creado.
   *
   * @param entidadConvocante entidad convocante modificada o creada.
   */
  closeModal(entidadConvocante?: IEntidadConvocante): void {
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(entidadConvocante);
    this.logger.debug(ConvocatoriaEntidadConvocanteaModalComponent.name, 'closeModal()', 'end');
  }

}
