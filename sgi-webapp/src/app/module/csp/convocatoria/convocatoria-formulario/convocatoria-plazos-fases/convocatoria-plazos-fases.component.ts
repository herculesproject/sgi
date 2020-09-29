import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { FragmentComponent } from '@core/component/fragment.component';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-plazos-fases.fragment';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IPlazosFases } from '@core/models/csp/plazos-fases';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';

@Component({
  selector: 'sgi-convocatoria-plazos-fases',
  templateUrl: './convocatoria-plazos-fases.component.html',
  styleUrls: ['./convocatoria-plazos-fases.component.scss']
})

export class ConvocatoriaPlazosFasesComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaPlazosFasesFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IPlazosFases>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService
  ) {
    super(actionService.FRAGMENT.PLAZOS_FASES, actionService);
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaPlazosFasesFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'observaciones', 'acciones'];
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IPlazosFases>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.plazosFase$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnInit()', 'end');
    }));
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnDestroy()', 'end');
  }
}
