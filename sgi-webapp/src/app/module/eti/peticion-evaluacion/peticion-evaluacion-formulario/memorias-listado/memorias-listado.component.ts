import { Component, OnInit, OnDestroy } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NGXLogger } from 'ngx-logger';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IMemoria } from '@core/models/eti/memoria';
import { Subscription } from 'rxjs';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogService } from '@core/services/dialog.service';
import { MemoriasListadoFragment } from './memorias-listado.fragment';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { FragmentComponent } from '@core/component/fragment.component';
import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoriaPeticionEvaluacion';
import { MEMORIAS_ROUTE } from '../../../memoria/memoria-route-names';

const MSG_SUCCESS_DELETE = marker('eti.peticionEvaluacion.formulario.memorias.listado.eliminarConfirmado');
const MSG_CONFIRM_DELETE = marker('eti.peticionEvaluacion.formulario.memorias.listado.eliminar');
const MSG_ERROR = marker('eti.peticionEvaluacion.formulario.memorias.listado.error');

@Component({
  selector: 'sgi-memorias-listado',
  templateUrl: './memorias-listado.component.html',
  styleUrls: ['./memorias-listado.component.scss']
})
export class MemoriasListadoComponent extends FragmentComponent implements OnInit, OnDestroy {
  MEMORIAS_ROUTE = MEMORIAS_ROUTE;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];


  datasource: MatTableDataSource<StatusWrapper<IMemoria>> = new MatTableDataSource<StatusWrapper<IMemoria>>();

  private subscriptions: Subscription[] = [];
  private listadoFragment: MemoriasListadoFragment;

  constructor(
    protected readonly dialogService: DialogService,
    protected readonly logger: NGXLogger,
    protected readonly memoriaService: MemoriaService,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    protected readonly snackBarService: SnackBarService,
    actionService: PeticionEvaluacionActionService
  ) {
    super(actionService.FRAGMENT.MEMORIAS, actionService);
    this.listadoFragment = this.fragment as MemoriasListadoFragment;

    this.displayedColumns = ['numReferencia', 'comite.id', 'estadoActual.id', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.listadoFragment.memorias$.subscribe((memorias) => {
      this.datasource.data = memorias;
    });
  }


  /**
   * Elimina la memoria.
   *
   * @param wrappedMemoria la memoria a eliminar.
   */
  delete(wrappedMemoria: StatusWrapper<IMemoriaPeticionEvaluacion>): void {
    this.logger.debug(MemoriasListadoComponent.name, 'delete(memoria: StatusWrapper<IMemoria>) - start');

    const dialogSubscription = this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteMemoria(wrappedMemoria);
      }
    });

    this.subscriptions.push(dialogSubscription);

    this.logger.debug(MemoriasListadoComponent.name, 'delete(memoria: StatusWrapper<IMemoria>) - end');
  }

  ngOnDestroy(): void {
    this.logger.debug(MemoriasListadoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions?.forEach(x => x.unsubscribe());
    this.logger.debug(MemoriasListadoComponent.name, 'ngOnDestroy()', 'end');
  }

}
