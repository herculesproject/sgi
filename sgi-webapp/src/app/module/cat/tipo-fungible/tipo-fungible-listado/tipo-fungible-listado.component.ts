import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component } from '@angular/core';
import { TipoFungible } from '@core/models/cat/tipo-fungible';
import { TipoFungibleService } from '@core/services/cat/tipo-fungible.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { SgiRestFilter } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ROUTE_NAMES } from '@core/route.names';

const MSG_SUCCESS_DELETE = marker('cat.tipo-fungible.listado.eliminarConfirmado');
const MSG_CONFIRM_DELETE = marker('cat.tipo-fungible.listado.eliminar');
const MSG_ERROR = marker('cat.tipo-fungible.listado.error');

@Component({
  selector: 'sgi-tipo-fungible-listado',
  templateUrl: './tipo-fungible-listado.component.html',
  styleUrls: ['./tipo-fungible-listado.component.scss'],
})
export class TipoFungibleListadoComponent extends AbstractPaginacionComponent<TipoFungible> {
  ROUTE_NAMES = ROUTE_NAMES;

  tiposFungible$: Observable<TipoFungible[]>;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly tipoFungibleService: TipoFungibleService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService
  ) {
    super(logger, tipoFungibleService);
    this.logger.debug(TipoFungibleListadoComponent.name, 'constructor()', 'start');
    this.tiposFungible$ = of();
    this.logger.debug(TipoFungibleListadoComponent.name, 'constructor()', 'end');
  }

  protected inicializarColumnas() {
    this.logger.debug(TipoFungibleListadoComponent.name, 'inicializarColumnas()', 'start');
    this.columnas = ['nombre', 'servicio', 'acciones'];
    this.logger.debug(TipoFungibleListadoComponent.name, 'inicializarColumnas()', 'end');
  }

  protected loadTable(reset?: boolean) {
    this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'start');
    this.tiposFungible$ = this.getObservableLoadTable(reset);
    this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'end');
  }

  protected mostrarMensajeErrorLoadTable(): void {
    this.logger.debug(TipoFungibleListadoComponent.name, 'mostrarMensajeErrorLoadTable()', 'start');
    this.snackBarService.showError(MSG_ERROR);
    this.logger.debug(TipoFungibleListadoComponent.name, 'mostrarMensajeErrorLoadTable()', 'end');
  }

  /**
   * Elimina el tipo fungible del listado con el id recibido por parametro.
   * @param tipoFungibleId id tipo fungible
   */
  borrarSeleccionado(tipoFungibleId: number): void {
    this.logger.debug(
      TipoFungibleListadoComponent.name,
      'borrarSeleccionado(tipoFungibleId: number) - start'
    );

    this.subscripciones.push(this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.subscripciones.push(this.tipoFungibleService.deleteById(tipoFungibleId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            )
            .subscribe(() => {
              this.snackBarService.showSuccess(MSG_SUCCESS_DELETE);
            })
          );
        }
        aceptado = false;
      })
    );

    this.logger.debug(
      TipoFungibleListadoComponent.name,
      'borrarSeleccionado(tipoFungibleId: number) - end'
    );
  }

  protected crearFiltros(): SgiRestFilter[] {
    return [];
  }
}
