import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { TipoReservable } from '@core/models/tipo-reservable';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { TipoReservableService } from '@core/services/tipo-reservable.service';
import { NGXLogger } from 'ngx-logger';
import { UrlUtils } from '@core/utils/url-utils';
import { switchMap } from 'rxjs/operators';
import { TraductorService } from '@core/services/traductor.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tipo-reservable-listado',
  templateUrl: './tipo-reservable-listado.component.html',
  styleUrls: ['./tipo-reservable-listado.component.scss']
})
export class TipoReservableListadoComponent implements OnInit, OnDestroy {

  UrlUtils = UrlUtils;
  displayedColumns: string[] = ['descripcion', 'servicio', 'estado', 'duracionMin', 'diasAntelacion', 'horasAnulacion', 'dias', 'acciones'];
  dataSource: MatTableDataSource<TipoReservable>;
  tipoReservableSubscription: Subscription;
  dialogServiceSubscription: Subscription;

  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private logger: NGXLogger,
    private tipoReservableService: TipoReservableService,
    private readonly traductor: TraductorService,
    private dialogService: DialogService,
    private snackBarService: SnackBarService) { }

  ngOnInit(): void {
    this.logger.debug(TipoReservableListadoComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<TipoReservable>([]);

    this.tipoReservableSubscription = this.tipoReservableService.findAll().subscribe(
      (tiposReservables: TipoReservable[]) => {
        this.dataSource.data = tiposReservables;
      });

    this.dataSource.sort = this.sort;
    this.logger.debug(TipoReservableListadoComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Realiza una búsqueda rápida sobre la tabla
   * @param event variable filtro
   */
  applyFilter(event: Event) {
    this.logger.debug(TipoReservableListadoComponent.name,
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

    this.logger.debug(TipoReservableListadoComponent.name,
      'applyFilter($event: Event) - end');
  }

  /**
   * Elimina la unidad de listado con el id recibido por parametro.
   * @param tipoReservableId id tipo reservable
   * @param index posicion en la tabla
   * @param $event evento
   */
  borrarSeleccionado(tipoReservableId: number, $event: Event): void {
    this.logger.debug(TipoReservableListadoComponent.name,
      'borrarSeleccionado(tipoReservableId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogService.dialogGenerico(this.traductor.getTexto('tipo-reservable.listado.eliminar'),
      this.traductor.getTexto('tipo-reservable.listado.aceptar'), this.traductor.getTexto('tipo-reservable.listado.cancelar'));

    this.dialogService.getAccionConfirmada().subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.tipoReservableService.delete(tipoReservableId).pipe(
            switchMap(_ => {
              return this.tipoReservableService.findAll();
            })
          ).subscribe((tiposReservables: TipoReservable[]) => {
            this.snackBarService
              .mostrarMensajeSuccess(this.traductor.getTexto('tipo-reservable.listado.eliminarConfirmado'));
            this.dataSource.data = tiposReservables;
          });
        }
        aceptado = false;
      });

    this.logger.debug(TipoReservableListadoComponent.name, 'borrarSeleccionado(tipoReservableId: number, $event: Event) - end');
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoReservableListadoComponent.name, 'ngOnDestroy() - start');

    this.tipoReservableSubscription.unsubscribe();
    this.dialogServiceSubscription.unsubscribe();

    this.logger.debug(
      TipoReservableListadoComponent.name, 'ngOnDestroy() - end');
  }

}
