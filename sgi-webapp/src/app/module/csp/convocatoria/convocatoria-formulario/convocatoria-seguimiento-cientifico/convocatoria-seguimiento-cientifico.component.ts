import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { ISeguimientoCientifico } from '@core/models/csp/seguimiento-cientifico';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaSeguimientoCientificoFragment } from './convocatoria-seguimiento-cientifico.fragment';

@Component({
  selector: 'sgi-convocatoria-seguimiento-cientifico',
  templateUrl: './convocatoria-seguimiento-cientifico.component.html',
  styleUrls: ['./convocatoria-seguimiento-cientifico.component.scss']
})
export class ConvocatoriaSeguimientoCientificoComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaSeguimientoCientificoFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  columnas: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<ISeguimientoCientifico>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService
  ) {
    super(actionService.FRAGMENT.SEGUIMIENTO_CIENTIFICO, actionService);
    this.logger.debug(ConvocatoriaSeguimientoCientificoComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaSeguimientoCientificoFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];
    this.logger.debug(ConvocatoriaSeguimientoCientificoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<ISeguimientoCientifico>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.seguimientosCientificos$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaSeguimientoCientificoComponent.name, 'ngOnInit()', 'end');
    }));
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaSeguimientoCientificoComponent.name, 'ngOnDestroy()', 'end');
  }
}
