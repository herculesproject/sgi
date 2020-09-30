import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { BehaviorSubject, Subscription } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IHito } from '@core/models/csp/hito';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaHitosFragment } from './convocatoria-hitos.fragment';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaHitosModalComponent } from '../../modals/convocatoria-hitos-modal/convocatoria-hitos-modal.component';

@Component({
  selector: 'sgi-convocatoria-hitos',
  templateUrl: './convocatoria-hitos.component.html',
  styleUrls: ['./convocatoria-hitos.component.scss']
})
export class ConvocatoriaHitosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[];

  private formPart: ConvocatoriaHitosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IHito>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  hitos$: BehaviorSubject<StatusWrapper<IHito>[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly convocatoriaReunionService: ConvocatoriaService,
    actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.HITOS, actionService);
    this.formPart = this.fragment as ConvocatoriaHitosFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.hitos$ = (this.fragment as ConvocatoriaHitosFragment).hitos$;

    this.displayedColumns = ['fechaInicio', 'tipoHito', 'comentario', 'aviso', 'acciones'];

  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaHitosComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IHito>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.hitos$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaHitosComponent.name, 'ngOnInit()', 'end');
    }));
  }



  /**
   * Apertura de modal de hitos (edición/creación)
   * @param idHito Identificador de hito a editar.
   */
  openModalHito(hito?: StatusWrapper<IHito>): void {
    this.logger.debug(ConvocatoriaHitosComponent.name, 'openModalHito()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: hito?.value,
      autoFocus: false
    };
    this.matDialog.open(ConvocatoriaHitosModalComponent, config);

    this.logger.debug(ConvocatoriaHitosComponent.name, 'openModalHito()', 'end');

  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaHitosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaHitosComponent.name, 'ngOnDestroy()', 'end');
  }



}
