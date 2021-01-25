import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ProyectoPlazosModalComponent, ProyectoPlazosModalComponentData } from '../../modals/proyecto-plazos-modal/proyecto-plazos-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoPlazosFragment } from './proyecto-plazos.fragment';

const MSG_DELETE = marker('csp.proyecto.plazo.listado.borrar');

@Component({
  selector: 'sgi-proyecto-plazos',
  templateUrl: './proyecto-plazos.component.html',
  styleUrls: ['./proyecto-plazos.component.scss']
})
export class ProyectoPlazosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoPlazosFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'aviso', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IProyectoPlazos>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  plazos$: BehaviorSubject<StatusWrapper<IProyectoPlazos>[]>;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.PLAZOS, actionService);
    this.logger.debug(ProyectoPlazosComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoPlazosFragment;
    this.plazos$ = (this.fragment as ProyectoPlazosFragment).plazos$;
    this.logger.debug(ProyectoPlazosComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoPlazosComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.dataSource = new MatTableDataSource<StatusWrapper<IProyectoPlazos>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.plazos$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ProyectoPlazosComponent.name, 'ngOnInit()', 'end');
    }));
  }

  /**
   * Apertura de modal de plazos fase
   * @param plazo Identificador de plazos fase al guardar/editar
   */
  openModalPlazos(plazo?: StatusWrapper<IProyectoPlazos>): void {
    this.logger.debug(ProyectoPlazosComponent.name, 'openModalPeriodo()', 'start');

    const datosPlazosFases: ProyectoPlazosModalComponentData = {
      plazos: this.dataSource.data.map(plazos => plazos.value),
      plazo: plazo ? plazo.value : {} as IProyectoPlazos,
      idModeloEjecucion: this.actionService.modeloEjecucionId,
      readonly: this.formPart.readonly
    };

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: datosPlazosFases,
    };

    const dialogRef = this.matDialog.open(ProyectoPlazosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (plazosFase: IProyectoPlazos) => {
        if (plazosFase) {
          if (plazo) {
            if (!plazo.created) {
              plazo.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPlazos(plazosFase);
          }
        }
        this.logger.debug(ProyectoPlazosComponent.name, 'openModalPeriodo()', 'end');
      }
    );
    this.logger.debug(ProyectoPlazosComponent.name, 'openModalPeriodo()', 'end');
  }

  /**
   * Desactivar proyecto fase
   */
  deleteFase(wrapper: StatusWrapper<IProyectoPlazos>) {
    this.logger.debug(ProyectoPlazosComponent.name,
      `deleteFase(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePlazo(wrapper);
          }
          this.logger.debug(ProyectoPlazosComponent.name,
            `deleteFase(${wrapper})`, 'end');
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoPlazosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoPlazosComponent.name, 'ngOnDestroy()', 'end');
  }
}
