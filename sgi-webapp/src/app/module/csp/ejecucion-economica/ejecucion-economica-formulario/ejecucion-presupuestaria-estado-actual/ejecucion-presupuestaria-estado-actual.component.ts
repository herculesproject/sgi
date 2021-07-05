import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { CAUSA_EXENCION_MAP } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IProyectoRelacion, RowTreeDesglose } from '../ejecucion-presupuestaria.fragment';
import { EjecucionPresupuestariaEstadoActualFragment } from './ejecucion-presupuestaria-estado-actual.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-proyecto-sge.identificador-sge');

@Component({
  selector: 'sgi-ejecucion-presupuestaria-estado-actual',
  templateUrl: './ejecucion-presupuestaria-estado-actual.component.html',
  styleUrls: ['./ejecucion-presupuestaria-estado-actual.component.scss']
})
export class EjecucionPresupuestariaEstadoActualComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: EjecucionPresupuestariaEstadoActualFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = [
    'proyectoSgeRef',
    'proyectoAnualidad.proyecto.codigoExterno',
    'proyectoAnualidad.proyecto.titulo',
    'proyectoAnualidad.proyecto.fechaInicio',
    'proyectoAnualidad.proyecto.fechaFin',
    'nombreIP'
  ];

  msgParamEntity = {};
  textoDelete: string;

  readonly dataSource = new MatTableDataSource<IProyectoRelacion>();
  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService
  ) {
    super(actionService.FRAGMENT.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, actionService);

    this.formPart = this.fragment as EjecucionPresupuestariaEstadoActualFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.relaciones$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
