import { Component, OnDestroy, ViewChild, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { ProyectoSocioPeriodoJustificacionFragment } from './proyecto-socio-periodo-justificacion.fragment';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { MatSort } from '@angular/material/sort';
import { NGXLogger } from 'ngx-logger';
import { DialogService } from '@core/services/dialog.service';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE } from '../../../proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-names';
import { ROUTE_NAMES } from '@core/route.names';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { Router } from '@angular/router';

const MSG_DELETE = marker('csp.proyecto-socio.periodo-justificacion.borrar');

export interface IProyectoSocioPeriodoJustificacionState {
  periodoJustificacion: IProyectoSocioPeriodoJustificacion;
  selectedPeriodosJustificacion: IProyectoSocioPeriodoJustificacion[];
  proyectoId: number;
  proyectoSocio: IProyectoSocio;
  urlProyecto: string;
  urlProyectoSocio: string;
}

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion',
  templateUrl: './proyecto-socio-periodo-justificacion.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion.component.scss']
})
export class ProyectoSocioPeriodoJustificacionComponent extends FragmentComponent implements OnInit, OnDestroy {
  PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE = PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE;
  ROUTE_NAMES = ROUTE_NAMES;

  formPart: ProyectoSocioPeriodoJustificacionFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'fechaInicio', 'fechaFin', 'fechaInicioPresentacion', 'fechaFinPresentacion',
    'documentacionRecibida', 'fechaRecepcion', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocioPeriodoJustificacion>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    private actionService: ProyectoSocioActionService,
    private dialogService: DialogService,
    private router: Router
  ) {
    super(actionService.FRAGMENT.PERIODO_JUSTIFICACION, actionService);
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as ProyectoSocioPeriodoJustificacionFragment;
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subcription = this.formPart.periodoJustificaciones$.subscribe(
      (periodoJustificaciones) => {
        this.dataSource.data = periodoJustificaciones;
      }
    );
    this.subscriptions.push(subcription);
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name, 'ngOnDestroy()', 'end');
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<IProyectoSocioPeriodoJustificacion>): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name,
      `deletePeriodoJustificacion(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoJustificacion(wrapper);
          }
          this.logger.debug(ProyectoSocioPeriodoJustificacionComponent.name,
            `deletePeriodoJustificacion(${wrapper})`, 'end');
        }
      )
    );
  }

  createState(wrapper?: StatusWrapper<IProyectoSocioPeriodoJustificacion>): IProyectoSocioPeriodoJustificacionState {
    const periodoJustificacion: IProyectoSocioPeriodoJustificacion = {
      documentacionRecibida: false,
      fechaFin: undefined,
      fechaFinPresentacion: undefined,
      fechaInicio: undefined,
      fechaInicioPresentacion: undefined,
      fechaRecepcion: undefined,
      id: undefined,
      numPeriodo: this.dataSource.data.length + 1,
      observaciones: undefined,
      proyectoSocio: this.actionService.getProyectoSocio()
    };

    const state: IProyectoSocioPeriodoJustificacionState = {
      periodoJustificacion: wrapper ? wrapper.value : periodoJustificacion,
      selectedPeriodosJustificacion: this.dataSource.data.map(element => element.value),
      proyectoId: this.actionService.getProyectoId(),
      proyectoSocio: this.actionService.getProyectoSocio(),
      urlProyecto: this.actionService.getUrlProyecto(),
      urlProyectoSocio: this.router.url
    };

    if (wrapper) {
      const index = state.selectedPeriodosJustificacion.findIndex(
        (element) => element === wrapper.value);
      if (index >= 0) {
        state.selectedPeriodosJustificacion.splice(index, 1);
      }
    }
    return state;
  }
}
