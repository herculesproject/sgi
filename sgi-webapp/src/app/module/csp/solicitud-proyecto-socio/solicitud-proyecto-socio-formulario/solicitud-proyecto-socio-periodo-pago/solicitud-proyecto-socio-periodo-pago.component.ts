import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { SolicitudProyectoSocioPeriodoPagoFragment } from './solicitud-proyecto-socio-periodo-pago.fragment';
import { Subscription } from 'rxjs';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { FragmentComponent } from '@core/component/fragment.component';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SolicitudProyectoSocioPeriodoPagoModalData, SolicitudProyectoSocioPeriodoPagoModalComponent } from '../../modals/solicitud-proyecto-socio-periodo-pago-modal/solicitud-proyecto-socio-periodo-pago-modal.component';

const MSG_DELETE = marker('csp.solicitud-proyecto-socio.periodo-pago.borrar');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-periodo-pago',
  templateUrl: './solicitud-proyecto-socio-periodo-pago.component.html',
  styleUrls: ['./solicitud-proyecto-socio-periodo-pago.component.scss']
})
export class SolicitudProyectoSocioPeriodoPagoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: SolicitudProyectoSocioPeriodoPagoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'mes', 'importe', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoPeriodoPago>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    actionService: SolicitudProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.PERIODOS_PAGOS, actionService);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as SolicitudProyectoSocioPeriodoPagoFragment;
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subcription = this.formPart.periodoPagos$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
    this.subscriptions.push(subcription);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoPeriodoPago>): void {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `openModal()`, 'start');
    const solicitudProyectoPeriodoPago: ISolicitudProyectoPeriodoPago = {
      id: undefined,
      importe: undefined,
      mes: undefined,
      numPeriodo: this.dataSource.data.length + 1,
      solicitudProyectoSocio: undefined
    };
    const data: SolicitudProyectoSocioPeriodoPagoModalData = {
      solicitudProyectoPeriodoPago: wrapper ? wrapper.value : solicitudProyectoPeriodoPago,
      selectedMeses: this.dataSource.data.map(element => element.value.mes),
      isEdit: Boolean(wrapper)
    };
    if (wrapper) {
      const index = data.selectedMeses.findIndex((element) => element === wrapper.value.mes);
      if (index >= 0) {
        data.selectedMeses.splice(index, 1);
      }
    }
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(SolicitudProyectoSocioPeriodoPagoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudProyectoSocioPeriodoPagoModalData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPeriodoPago(modalData.solicitudProyectoPeriodoPago);
          }
        }
        this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `openModal()`, 'end');
      }
    );
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoPeriodoPago>): void {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `deleteProyectoEquipo(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoPago(wrapper);
          }
          this.logger.debug(SolicitudProyectoSocioPeriodoPagoComponent.name, `deleteProyectoEquipo(${wrapper})`, 'end');
        }
      )
    );
  }

}
