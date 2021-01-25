import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { SolicitudProyectoPeriodoJustificacionesFragment } from './solicitud-proyecto-periodo-justificaciones.fragment';
import { Subscription } from 'rxjs';
import { ISolicitudProyectoPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-periodo-justificacion';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SolicitudProyectoPeriodoJustificacionesModalData, SolicitudProyectoPeriodoJustificacionesModalComponent } from '../../modals/solicitud-proyecto-periodo-justificaciones-modal/solicitud-proyecto-periodo-justificaciones-modal.component';
import { SolicitudService } from '@core/services/csp/solicitud.service';

const MSG_DELETE = marker('csp.solicitud-proyecto-socio.periodo-justificacion.borrar');

@Component({
  selector: 'sgi-solicitud-proyecto-periodo-justificaciones',
  templateUrl: './solicitud-proyecto-periodo-justificaciones.component.html',
  styleUrls: ['./solicitud-proyecto-periodo-justificaciones.component.scss']
})
export class SolicitudProyectoPeriodoJustificacionesComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: SolicitudProyectoPeriodoJustificacionesFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicio', 'fechaFin', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoPeriodoJustificacion>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    private actionService: SolicitudProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.PERIODOS_JUSTIFICACION, actionService);
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as SolicitudProyectoPeriodoJustificacionesFragment;
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subcription = this.formPart.periodoJustificaciones$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.subscriptions.push(subcription);
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      switch (property) {
        case 'fechaInicio':
          return wrapper.value.fechaInicio;
        case 'fechaFin':
          return wrapper.value.fechaFin;
        default:
          return wrapper.value[property];
      }
    };
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoPeriodoJustificacion>): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `openModal()`, 'start');
    const periodoJustificacion: ISolicitudProyectoPeriodoJustificacion = {
      fechaFin: undefined,
      fechaInicio: undefined,
      id: undefined,
      mesFinal: undefined,
      mesInicial: undefined,
      numPeriodo: this.dataSource.data.length + 1,
      observaciones: undefined,
      solicitudProyectoSocio: this.actionService.getSolicitudProyectoSocio()
    };
    const data: SolicitudProyectoPeriodoJustificacionesModalData = {
      periodoJustificacion: wrapper ? wrapper.value : periodoJustificacion,
      selectedPeriodoJustificaciones: this.dataSource.data.map(element => element.value),
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      const index = data.selectedPeriodoJustificaciones.findIndex((element) => element === wrapper.value);
      if (index >= 0) {
        data.selectedPeriodoJustificaciones.splice(index, 1);
      }
    }

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(SolicitudProyectoPeriodoJustificacionesModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudProyectoPeriodoJustificacionesModalData) => {
        if (modalData) {
          if (wrapper) {
            this.formPart.updatePeriodoJustificacion(wrapper);
          } else {
            this.formPart.addPeriodoJustificacion(modalData.periodoJustificacion);
          }
        }
        this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `openModal()`, 'end');
      }
    );
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<ISolicitudProyectoPeriodoJustificacion>): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `deletePeriodoJustificacion(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoJustificacion(wrapper);
          }
          this.logger.debug(SolicitudProyectoPeriodoJustificacionesComponent.name, `deletePeriodoJustificacion(${wrapper})`, 'end');
        }
      )
    );
  }
}
