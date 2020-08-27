import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { TipoFungible } from '@core/models/cat/tipo-fungible';
import { ROUTE_NAMES } from '@core/route.names';
import { TipoFungibleService } from '@core/services/cat/tipo-fungible.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

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
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(TipoFungibleListadoComponent.name, 'constructor()', 'start');
    this.tiposFungible$ = of();
    this.logger.debug(TipoFungibleListadoComponent.name, 'constructor()', 'end');
  }

  protected initColumnas() {
    this.logger.debug(TipoFungibleListadoComponent.name, 'initColumnas()', 'start');
    this.columnas = ['nombre', 'servicio', 'acciones'];
    this.logger.debug(TipoFungibleListadoComponent.name, 'initColumnas()', 'end');
  }

  protected loadTable(reset?: boolean) {
    this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'start');
    this.tiposFungible$ = this.getObservableLoadTable(reset);
    this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<TipoFungible>> {
    this.logger.debug(TipoFungibleListadoComponent.name, 'createObservable()', 'start');
    const observable$ = this.tipoFungibleService.findAll(this.getFindOptions());
    this.logger.debug(TipoFungibleListadoComponent.name, 'createObservable()', 'end');
    return observable$;
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

    this.suscripciones.push(this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.suscripciones.push(this.tipoFungibleService.deleteById(tipoFungibleId)
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

  protected createFiltros(): SgiRestFilter[] {
    return [];
  }
}
