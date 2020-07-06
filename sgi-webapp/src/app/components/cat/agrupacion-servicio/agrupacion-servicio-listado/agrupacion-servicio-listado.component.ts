import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

import { NGXLogger } from 'ngx-logger';

import { UrlUtils } from '@core/utils/url-utils';
import { Servicio } from '@core/models/servicio';

import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ServicioService } from '@core/services/servicio.service';
import { TraductorService } from '@core/services/traductor.service';
import { MatSort } from '@angular/material/sort';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';


@Component({
  selector: 'app-agrupacion-servicio-listado',
  templateUrl: './agrupacion-servicio-listado.component.html',
  styleUrls: ['./agrupacion-servicio-listado.component.scss']
})
export class AgrupacionServicioListadoComponent implements OnInit, OnDestroy {

  UrlUtils = UrlUtils;
  displayedColumns: string[] = ['abreviatura', 'nombre', 'contacto', 'seccion', 'acciones'];
  dataSource: MatTableDataSource<Servicio>;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  serviciosSubscription: Subscription;
  dialogSubscription: Subscription;
  servicioDeleteSubscription: Subscription;

  constructor(
    private logger: NGXLogger,
    private servicioService: ServicioService,
    private readonly traductor: TraductorService,
    private dialogService: DialogService,
    private snackBarService: SnackBarService
  ) { }


  ngOnInit(): void {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<Servicio>([]);

    this.serviciosSubscription = this.servicioService
      .findAll()
      .subscribe((servicios: Servicio[]) => {
        this.dataSource.data = servicios;
      });

    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = this.agrupacionServicioSortingDataAccessor;

    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Realiza una búsqueda rápida sobre la tabla
   * @param event variable filtro
   */
  applyFilter(event: Event) {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'applyFilter(event: Event)', 'start');
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    // Realiza la búsqueda también sobre los datos de objetos mostrados en la tabla
    this.dataSource.filterPredicate = (order, filter: string) => {
      const transformedFilter = filter.trim().toLowerCase();

      const listAsFlatString = (obj): string => {
        let returnVal = '';

        Object.values(obj).forEach((val) => {
          if (typeof val !== 'object') {
            this.logger.debug(AgrupacionServicioListadoComponent.name, 'applyFilter(event: Event)', 'end');
            returnVal = returnVal + ' ' + val;
          } else if (val !== null) {
            this.logger.debug(AgrupacionServicioListadoComponent.name, 'applyFilter(event: Event)', 'end');
            returnVal = returnVal + ' ' + listAsFlatString(val);
          }
        });
        this.logger.debug(AgrupacionServicioListadoComponent.name, 'applyFilter(event: Event)', 'end');
        return returnVal.trim().toLowerCase();
      };
      this.logger.debug(AgrupacionServicioListadoComponent.name, 'applyFilter(event: Event)', 'end');
      return listAsFlatString(order).includes(transformedFilter);
    };
  }


  /**
   * Personaliza los valores que se utilizan para hacer la ordenacion de los servicios.
   */
  agrupacionServicioSortingDataAccessor = (servicio: Servicio, propiedad: string) => {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'agrupacionServicioSortingDataAccessor()', 'start');
    if (!servicio) {
      return;
    }

    switch (propiedad) {
      case 'abreviatura':
        return servicio.abreviatura?.toLowerCase();
      case 'nombre':
        return servicio.nombre?.toLowerCase();
      case 'contacto':
        return servicio.contacto?.toLowerCase();
      case 'seccion':
        return servicio.seccion?.nombre?.toLowerCase();
    }
  }


  /**
   * Elimina la agrupación servicio seleccionado.
   * @param servicioId id del servicio a eliminar.
   * @param event evento lanzado.
   */
  borrarSeleccionado(servicioId: number, $event: Event): void {
    this.logger.debug(AgrupacionServicioListadoComponent.name,
      'borrarSeleccionado(servicioId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogService.dialogGenerico(this.traductor.getTexto('servicio.listado.eliminar'),
      this.traductor.getTexto('servicio.listado.aceptar'), this.traductor.getTexto('servicio.listado.cancelar'));

    this.dialogSubscription = this.dialogService.getAccionConfirmada().subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.servicioDeleteSubscription = this.servicioService.delete(servicioId).pipe(
            switchMap(_ => {
              return this.servicioService.findAll();
            })
          ).subscribe((servicios: Servicio[]) => {
            this.snackBarService
              .mostrarMensajeSuccess(this.traductor.getTexto('servicio.listado.eliminarConfirmado'));
            this.dataSource.data = servicios;
          });
        }
        aceptado = false;
      });

    this.logger.debug(AgrupacionServicioListadoComponent.name,
      'borrarSeleccionado(servicioId: number, $event: Event) - end');
  }


  ngOnDestroy(): void {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnDestroy()', 'start');
    this.serviciosSubscription?.unsubscribe();
    this.dialogSubscription?.unsubscribe();
    this.servicioDeleteSubscription?.unsubscribe();
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnDestroy()', 'end');
  }

}
