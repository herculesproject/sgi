import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE } from '../../../proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-names';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { ProyectoSocioPeriodoJustificacionFragment } from './proyecto-socio-periodo-justificacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY = marker('title.csp.proyecto-socio-periodo-justificacion');

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

  msgParamEntity = {};
  textoDelete: string;

  constructor(
    private actionService: ProyectoSocioActionService,
    private dialogService: DialogService,
    private router: Router,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PERIODO_JUSTIFICACION, actionService);
    this.formPart = this.fragment as ProyectoSocioPeriodoJustificacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.periodoJustificaciones$.subscribe(
      (periodoJustificaciones) => {
        this.dataSource.data = periodoJustificaciones;
      }
    );
    this.subscriptions.push(subcription);
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<IProyectoSocioPeriodoJustificacion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoJustificacion(wrapper);
          }
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
