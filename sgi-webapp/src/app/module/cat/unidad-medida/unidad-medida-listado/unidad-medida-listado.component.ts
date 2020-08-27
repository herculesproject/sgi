import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { ROUTE_NAMES } from '@core/route.names';
import { UnidadMedidaService } from '@core/services/cat/unidad-medida.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

const MSG_SUCCESS_DELETE = marker('cat.unidad-medida.listado.eliminarConfirmado');
const MSG_CONFIRM_DELETE = marker('cat.unidad-medida.listado.eliminar');
const MSG_ERROR = marker('cat.unidad-medida.listado.error');

@Component({
  selector: 'sgi-unidad-medida-listado',
  templateUrl: './unidad-medida-listado.component.html',
  styleUrls: ['./unidad-medida-listado.component.scss'],
})
export class UnidadMedidaListadoComponent extends AbstractPaginacionComponent<UnidadMedida> {
  ROUTE_NAMES = ROUTE_NAMES;

  unidadesMedida$: Observable<UnidadMedida[]>;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly unidadMedidaService: UnidadMedidaService,
    private readonly dialogService: DialogService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.unidadesMedida$ = of();
  }

  protected initColumnas() {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'initColumnas()', 'start');
    this.columnas = ['abreviatura', 'descripcion', 'acciones'];
    this.logger.debug(UnidadMedidaListadoComponent.name, 'initColumnas()', 'end');
  }

  protected loadTable(reset?: boolean) {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'start');
    this.unidadesMedida$ = this.getObservableLoadTable(reset);
    this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<UnidadMedida>> {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'createObservable()', 'start');
    const observable$ = this.unidadMedidaService.findAll(this.getFindOptions());
    this.logger.debug(UnidadMedidaListadoComponent.name, 'createObservable()', 'start');
    return observable$;
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
        this.suscripciones.push(this.unidadMedidaService
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

  protected createFiltros(): SgiRestFilter[] {
    return [];
  }
}
