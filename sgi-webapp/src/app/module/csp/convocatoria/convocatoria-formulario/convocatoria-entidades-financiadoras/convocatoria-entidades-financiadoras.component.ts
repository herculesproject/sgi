import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { IEntidadesFinanciadoras } from '@core/models/csp/entidades-financiadoras';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaEntidadesFinanciadorasFragment } from './convocatoria-entidades-financiadoras.fragment';

@Component({
  selector: 'sgi-convocatoria-entidades-financiadoras',
  templateUrl: './convocatoria-entidades-financiadoras.component.html',
  styleUrls: ['./convocatoria-entidades-financiadoras.component.scss']
})
export class ConvocatoriaEntidadesFinanciadorasComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaEntidadesFinanciadorasFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  columnas: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IEntidadesFinanciadoras>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaEntidadesFinanciadorasFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
      'porcentajeFinanciacion', 'acciones'];
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IEntidadesFinanciadoras>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.entidadesFinanciadoras$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnInit()', 'end');
    }));
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnDestroy()', 'end');
  }

}
