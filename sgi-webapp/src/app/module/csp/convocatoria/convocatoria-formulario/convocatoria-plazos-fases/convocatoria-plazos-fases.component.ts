import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { FragmentComponent } from '@core/component/fragment.component';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-plazos-fases.fragment';
import { BehaviorSubject, Subscription, of } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaPlazosFaseModalComponent, ConvocatoriaPlazosFaseModalComponentData } from '../../modals/convocatoria-plazos-fase-modal/convocatoria-plazos-fase-modal.component';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SnackBarService } from '@core/services/snack-bar.service';

const MSG_DELETE = marker('csp.convocatoria.fase.listado.borrar');
const MSG_ERROR = marker('csp.convocatoria.fase.listado.borrar.error');

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
  disableAddFase = true;

  dataSource: MatTableDataSource<StatusWrapper<IConvocatoriaFase>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  plazosFase$: BehaviorSubject<StatusWrapper<IConvocatoriaFase>[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.PLAZOS_FASES, actionService);
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaPlazosFasesFragment;
    this.plazosFase$ = (this.fragment as ConvocatoriaPlazosFasesFragment).plazosFase$;
    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'observaciones', 'acciones'];
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaFase>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.disableAddFase = !Boolean(this.actionService.getDatosGeneralesConvocatoria().modeloEjecucion);

    this.actionService.initializeConfiguracionSolicitud();

    this.subscriptions.push(this.formPart.plazosFase$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnInit()', 'end');
    }));
  }

  /**
   * Apertura de modal de plazos fase
   * @param plazo Identificador de plazos fase al guardar/editar
   */
  openModalPlazos(plazo?: StatusWrapper<IConvocatoriaFase>, numFila?: number): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'openModalPeriodo()', 'start');

    const plazos = this.dataSource.data.map(p => p.value);
    if (numFila) {
      plazos.splice(numFila, 1);
    }

    const datosPlazosFases: ConvocatoriaPlazosFaseModalComponentData = {
      plazos,
      plazo: plazo ? plazo.value : {} as IConvocatoriaFase,
      idModeloEjecucion: this.actionService.getDatosGeneralesConvocatoria().modeloEjecucion?.id
    };

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: datosPlazosFases,
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(ConvocatoriaPlazosFaseModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (plazosFase: IConvocatoriaFase) => {
        if (plazosFase) {
          if (plazo) {
            if (!plazo.created) {
              plazo.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPlazosFases(plazosFase);
          }
        }
        this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'openModalPeriodo()', 'end');
      }
    );
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'openModalPeriodo()', 'end');
  }

  /**
   * Desactivar convocatoria fase
   */
  deleteFase(wrapper: StatusWrapper<IConvocatoriaFase>) {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name,
      `${this.deleteFase.name}(${wrapper})`, 'start');
    if (this.actionService.isDelete(wrapper.value)) {
      this.subscriptions.push(
        this.dialogService.showConfirmation(MSG_DELETE).subscribe(
          (aceptado: boolean) => {
            if (aceptado) {
              this.formPart.deleteFase(wrapper);
            }
            this.logger.debug(ConvocatoriaPlazosFasesComponent.name,
              `${this.deleteFase.name}(${wrapper})`, 'end');
          }
        )
      );
    } else {
      this.snackBarService.showError(MSG_ERROR);
    }
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnDestroy()', 'end');
  }
}
