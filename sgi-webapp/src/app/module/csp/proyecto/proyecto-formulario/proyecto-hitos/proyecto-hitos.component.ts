import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Subscription } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoHitosFragment } from './proyecto-hitos.fragment';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogService } from '@core/services/dialog.service';
import { ProyectoHitosModalComponent, ProyectoHitosModalComponentData } from '../../modals/proyecto-hitos-modal/proyecto-hitos-modal.component';

const MSG_DELETE = marker('csp.proyecto.hito.listado.borrar');

@Component({
  selector: 'sgi-proyecto-hitos',
  templateUrl: './proyecto-hitos.component.html',
  styleUrls: ['./proyecto-hitos.component.scss']
})
export class ProyectoHitosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoHitosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['tipoHito', 'fecha', 'comentario', 'aviso', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoHito>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    protected proyectoReunionService: ProyectoService,
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.HITOS, actionService);
    this.logger.debug(ProyectoHitosComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoHitosFragment;
    this.logger.debug(ProyectoHitosComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoHitosComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoHito>, property: string) => {
        switch (property) {
          case 'fecha':
            return wrapper.value.fecha;
          case 'tipoHito':
            return wrapper.value.tipoHito.nombre;
          case 'comentario':
            return wrapper.value.comentario;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.hitos$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ProyectoHitosComponent.name, 'ngOnInit()', 'end');
    }));
  }

  /**
   * Apertura de modal de hitos (edición/creación)
   * @param idHito Identificador de hito a editar.
   */
  openModal(wrapper?: StatusWrapper<IProyectoHito>): void {
    this.logger.debug(ProyectoHitosComponent.name, `openModal()`, 'start');

    const data: ProyectoHitosModalComponentData = {
      hitos: this.dataSource.data.map(hito => hito.value),
      hito: wrapper ? wrapper.value : {} as IProyectoHito,
      idModeloEjecucion: this.actionService.modeloEjecucionId,
      readonly: this.formPart.readonly
    };

    if (wrapper) {
      data.hitos = this.dataSource.data.filter(element =>
        element.value.tipoHito !== data.hito.tipoHito
      ).map(element => element.value);
    }

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data
    };
    const dialogRef = this.matDialog.open(ProyectoHitosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (proyectoHito) => {
        if (proyectoHito) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addHito(proyectoHito);
          }
        }
        this.logger.debug(ProyectoHitosComponent.name, `openModal()`, 'end');
      }
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoHitosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoHitosComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Eliminar proyecto hito
   */
  deleteHito(wrapper: StatusWrapper<IProyectoHito>) {
    this.logger.debug(ProyectoHitosComponent.name,
      `deleteHito(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteHito(wrapper);
          }
          this.logger.debug(ProyectoHitosComponent.name,
            `deleteHito(${wrapper})`, 'end');
        }
      )
    );
  }


}
