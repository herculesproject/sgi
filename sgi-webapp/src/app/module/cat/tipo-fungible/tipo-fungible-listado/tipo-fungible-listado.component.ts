import { Component } from '@angular/core';
import { TipoFungible } from '@core/models/cat/tipo-fungible';
import { TipoFungibleService } from '@core/services/cat/tipo-fungible.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { SgiRestFilter } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-tipo-fungible-listado',
  templateUrl: './tipo-fungible-listado.component.html',
  styleUrls: ['./tipo-fungible-listado.component.scss'],
})
export class TipoFungibleListadoComponent extends AbstractPaginacionComponent<TipoFungible> {

  tiposFungible$: Observable<TipoFungible[]>;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly tipoFungibleService: TipoFungibleService,
    private readonly traductor: TraductorService,
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
    this.snackBarService.mostrarMensajeError(
      this.traductor.getTexto('cat.tipo-fungible.listado.error')
    );
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
    this.dialogService.dialogGenerico(
      this.traductor.getTexto('cat.tipo-fungible.listado.eliminar'),
      this.traductor.getTexto('cat.tipo-fungible.listado.aceptar'),
      this.traductor.getTexto('cat.tipo-fungible.listado.cancelar')
    );

    this.subscripciones.push(this.dialogService.getAccionConfirmada().subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.subscripciones.push(this.tipoFungibleService.deleteById(tipoFungibleId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            )
            .subscribe(() => {
              this.snackBarService.mostrarMensajeSuccess(
                this.traductor.getTexto('tipo-fungible.listado.eliminarConfirmado')
              );
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
