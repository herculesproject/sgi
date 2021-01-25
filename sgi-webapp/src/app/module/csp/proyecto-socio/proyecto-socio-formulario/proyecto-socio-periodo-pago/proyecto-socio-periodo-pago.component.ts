import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { MatSort } from '@angular/material/sort';
import { ProyectoSocioPeriodoPagoFragment } from './proyecto-socio-periodo-pago.fragment';
import { NGXLogger } from 'ngx-logger';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { ProyectoSocioPeriodoPagoModalData, ProyectoSocioPeriodoPagoModalComponent } from '../../modals/proyecto-socio-periodo-pago-modal/proyecto-socio-periodo-pago-modal.component';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';

const MSG_DELETE = marker('csp.proyecto-socio.periodo-pago.borrar');

@Component({
  selector: 'sgi-proyecto-socio-periodo-pago',
  templateUrl: './proyecto-socio-periodo-pago.component.html',
  styleUrls: ['./proyecto-socio-periodo-pago.component.scss']
})
export class ProyectoSocioPeriodoPagoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoSocioPeriodoPagoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'fechaPrevistaPago', 'importe', 'fechaPago', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocioPeriodoPago>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    actionService: ProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.PERIODO_PAGO, actionService);
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as ProyectoSocioPeriodoPagoFragment;
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subcription = this.formPart.periodoPagos$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
    this.subscriptions.push(subcription);
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<IProyectoSocioPeriodoPago>): void {
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `openModal()`, 'start');
    const proyectoSocioPeriodoPago: IProyectoSocioPeriodoPago = {
      id: undefined,
      importe: undefined,
      fechaPago: undefined,
      fechaPrevistaPago: undefined,
      numPeriodo: this.dataSource.data.length + 1,
      proyectoSocio: undefined
    };
    const data: ProyectoSocioPeriodoPagoModalData = {
      proyectoSocioPeriodoPago: wrapper ? wrapper.value : proyectoSocioPeriodoPago,
      selectedFechaPrevistas: this.dataSource.data.map(element => element.value.fechaPrevistaPago),
      isEdit: Boolean(wrapper)
    };
    if (wrapper) {
      const index = data.selectedFechaPrevistas.findIndex((element) => element === wrapper.value.fechaPrevistaPago);
      if (index >= 0) {
        data.selectedFechaPrevistas.splice(index, 1);
      }
    }
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(ProyectoSocioPeriodoPagoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoSocioPeriodoPagoModalData) => {
        if (modalData) {
          if (wrapper) {
            this.formPart.updatePeriodoPago(wrapper);
          } else {
            this.formPart.addPeriodoPago(modalData.proyectoSocioPeriodoPago);
          }
        }
        this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `openModal()`, 'end');
      }
    );
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<IProyectoSocioPeriodoPago>): void {
    this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `deleteProyectoEquipo(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoPago(wrapper);
          }
          this.logger.debug(ProyectoSocioPeriodoPagoComponent.name, `deleteProyectoEquipo(${wrapper})`, 'end');
        }
      )
    );
  }

}
