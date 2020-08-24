import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnDestroy } from '@angular/core';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { UnidadMedidaService } from '@core/services/cat/unidad-medida.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { SgiRestFilter } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ROUTE_NAMES } from '@core/route.names';

const MSG_SUCCESS_DELETE = marker('cat.unidad-medida.listado.eliminarConfirmado');
const MSG_CONFIRM_DELETE = marker('cat.unidad-medida.listado.eliminar');
const MSG_ERROR = marker('cat.unidad-medida.listado.error');

@Component({
  selector: 'sgi-unidad-medida-listado',
  templateUrl: './unidad-medida-listado.component.html',
  styleUrls: ['./unidad-medida-listado.component.scss'],
})
export class UnidadMedidaListadoComponent extends AbstractPaginacionComponent<UnidadMedida> implements OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  unidadesMedida$: Observable<UnidadMedida[]>;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly unidadMedidaService: UnidadMedidaService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService
  ) {
    super(logger, unidadMedidaService);
    this.unidadesMedida$ = of();
  }

  protected inicializarColumnas() {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'inicializarColumnas()', 'start');
    this.columnas = ['abreviatura', 'descripcion', 'acciones'];
    this.logger.debug(UnidadMedidaListadoComponent.name, 'inicializarColumnas()', 'end');
  }

  protected loadTable(reset?: boolean) {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'start');
    this.unidadesMedida$ = this.getObservableLoadTable(reset);
    this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'end');
  }

  protected mostrarMensajeErrorLoadTable(): void {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'mostrarMensajeErrorLoadTable()', 'start');
    this.snackBarService.showError(MSG_ERROR);
    this.logger.debug(UnidadMedidaListadoComponent.name, 'mostrarMensajeErrorLoadTable()', 'end');
  }

  /**
   * Elimina la unidad de listado con el id recibido por parametro.
   * @param unidadMedidaId id unidad medida
   */
  borrarSeleccionado(unidadMedidaId: number) {
    this.logger.debug(
      UnidadMedidaListadoComponent.name,
      'borrarSeleccionado(unidadMedidaId: number) - start'
    );
    this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe((aceptado: boolean) => {
      if (aceptado) {
        this.subscripciones.push(this.unidadMedidaService
          .deleteById(unidadMedidaId)
          .pipe(
            map(() => {
              return this.loadTable();
            })
          )
          .subscribe(() => {
            this.snackBarService.showSuccess(MSG_SUCCESS_DELETE);
            this.logger.debug(UnidadMedidaListadoComponent.name, 'borrarSeleccionado(unidadMedidaId: number) - end');
          })
        );
      }
    });
  }

  protected crearFiltros(): SgiRestFilter[] {
    return [];
  }
}
