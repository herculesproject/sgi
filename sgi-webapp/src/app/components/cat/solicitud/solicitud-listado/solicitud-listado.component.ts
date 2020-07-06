import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {Registro} from '@core/models/registro';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {ServicioService} from '@core/services/servicio.service';
import {SolicitudService} from '@core/services/solicitud.service';
import {NGXLogger} from 'ngx-logger';
import {UrlUtils} from '@core/utils/url-utils';
import {TraductorService} from '@core/services/traductor.service';
import {DialogService} from '@core/services/dialog.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {Servicio} from '@core/models/servicio';


@Component({
  selector: 'app-solicitud-listado',
  templateUrl: './solicitud-listado.component.html',
  styleUrls: ['./solicitud-listado.component.scss']
})
export class SolicitudListadoComponent implements OnInit, OnChanges, OnDestroy {
  UrlUtils = UrlUtils;
  displayedColumns: string[] = ['nombre', 'contacto', 'acciones'];
  dataSource: MatTableDataSource<Servicio>;
  servicioSeleccionado: Servicio;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @Input() registro: Registro;

  constructor(
    private logger: NGXLogger,
    private servicioService: ServicioService,
    private solicitudService: SolicitudService,
    private readonly traductor: TraductorService,
    private dialogService: DialogService,
    private snackBarService: SnackBarService) {
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<Servicio>([]);

    this.servicioService.findAll().subscribe(
      (servicios: Servicio[]) => {
        this.dataSource.data = servicios;
      });

    this.dataSource.sort = this.sort;
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.registro) {
      if (this.registro.servicio) {
        this.servicioSeleccionado = this.registro.servicio;
      }
    }
  }

  /**
   * Realiza una búsqueda rápida sobre la tabla
   * @param event variable filtro
   */
  applyFilter(event: Event) {
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
  }

  /**
   * Selecciona el servicio en el que se quiere dar de alta y se actualiza el registro
   * @param servicio Servicio
   */
  seleccionarServicio(servicio: Servicio): void {
    this.servicioSeleccionado = servicio;
    this.solicitudService.registro.servicio = servicio;
    this.actualizarSolicitud();
  }

  /**
   * Actualiza un registro existente en el back
   */
  private actualizarSolicitud() {
    this.logger.debug(
      SolicitudListadoComponent.name,
      'actualizarSolicitud()',
      'start'
    );
    this.registro = this.solicitudService.registro;
    this.solicitudService
      .update(this.registro, this.registro.id)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('solicitud.alta.correcto')
          );
          this.logger.debug(
            SolicitudListadoComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('solicitud.alta.error')
          );
          this.logger.debug(
            SolicitudListadoComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        }
      );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      SolicitudListadoComponent.name, 'ngOnDestroy() - start');
    this.logger.debug(
      SolicitudListadoComponent.name, 'ngOnDestroy() - end');
  }

}
