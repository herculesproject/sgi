import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { TipoFungible } from '@core/models/tipo-fungible';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { TipoFungibleService } from '@core/services/tipo-fungible.service';
import { NGXLogger } from 'ngx-logger';
import { UrlUtils } from '@core/utils/url-utils';
import { switchMap } from 'rxjs/operators';
import { TraductorService } from '@core/services/traductor.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-tipo-fungible-listado',
  templateUrl: './tipo-fungible-listado.component.html',
  styleUrls: ['./tipo-fungible-listado.component.scss']
})
export class TipoFungibleListadoComponent implements OnInit, OnDestroy {
  UrlUtils = UrlUtils;
  displayedColumns: string[] = ['nombre', 'servicio', 'acciones'];
  dataSource: MatTableDataSource<TipoFungible>;
  tipoFungibleServiceSubscription: Subscription;
  dialogServiceSubscription: Subscription;
  tipoFungibleServiceDeleteSubscription: Subscription;

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private logger: NGXLogger,
    private tipoFungibleService: TipoFungibleService,
    private readonly traductor: TraductorService,
    private dialogService: DialogService,
    private snackBarService: SnackBarService) { }

  ngOnInit(): void {
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<TipoFungible>([]);

    this.tipoFungibleServiceSubscription = this.tipoFungibleService.findAll().subscribe(
      (tiposFungible: TipoFungible[]) => {
        this.dataSource.data = tiposFungible;
      });

    this.dataSource.sort = this.sort;
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Realiza una búsqueda rápida sobre la tabla
   * @param event variable filtro
   */
  applyFilter(event: Event) {
    this.logger.debug(TipoFungibleListadoComponent.name,
      'applyFilter($event: Event) - start');

    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    // Realiza la búsqueda también sobre los datos de objetos mostrados en la tabla
    this.dataSource.filterPredicate = (order, filter: string) => {
      const transformedFilter = filter.trim().toLowerCase();

      const listAsFlatString = (obj): string => {
        let returnVal = '';

        Object.values(obj).forEach((val) => {
          if (typeof val !== 'object') {
            returnVal = returnVal + ' ' + val;
          } else if (val !== null) {
            returnVal = returnVal + ' ' + listAsFlatString(val);
          }
        });

        return returnVal.trim().toLowerCase();
      };

      return listAsFlatString(order).includes(transformedFilter);
    };

    this.logger.debug(TipoFungibleListadoComponent.name,
      'applyFilter($event: Event) - end');
  }

  /**
   * Elimina el tipo fungible del listado con el id recibido por parametro.
   * @param tipoFungibleId id tipo fungible
   * @param index posicion en la tabla
   * @param $event evento
   */
  borrarSeleccionado(tipoFungibleId: number, $event: Event): void {
    this.logger.debug(TipoFungibleListadoComponent.name,
      'borrarSeleccionado(tipoFungibleId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogService.dialogGenerico(this.traductor.getTexto('tipo-fungible.listado.eliminar'),
      this.traductor.getTexto('tipo-fungible.listado.aceptar'), this.traductor.getTexto('tipo-fungible.listado.cancelar'));

    this.dialogServiceSubscription = this.dialogService.getAccionConfirmada().subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.tipoFungibleServiceDeleteSubscription = this.tipoFungibleService.delete(tipoFungibleId).pipe(
            switchMap(_ => {
              return this.tipoFungibleService.findAll();
            })
          ).subscribe((tiposFungibles: TipoFungible[]) => {
            this.snackBarService
              .mostrarMensajeSuccess(this.traductor.getTexto('tipo-fungible.listado.eliminarConfirmado'));
            this.dataSource.data = tiposFungibles;
          });
        }
        aceptado = false;
      });

    this.logger.debug(TipoFungibleListadoComponent.name, 'borrarSeleccionado(tipoFungibleId: number, $event: Event) - end');
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoFungibleListadoComponent.name, 'ngOnDestroy() - start');

    this.tipoFungibleServiceSubscription.unsubscribe();
    this.dialogServiceSubscription.unsubscribe();
    this.tipoFungibleServiceDeleteSubscription.unsubscribe();

    this.logger.debug(
      TipoFungibleListadoComponent.name, 'ngOnDestroy() - end');
  }

}
