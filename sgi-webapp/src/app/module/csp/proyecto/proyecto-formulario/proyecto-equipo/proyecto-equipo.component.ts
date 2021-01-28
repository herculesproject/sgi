import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ProyectoEquiposModalComponentData } from '../../modals/proyecto-equipo-modal/proyecto-equipo-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoEquipoFragment } from './proyecto-equipo.fragment';
import { ProyectoEquipoModalComponent } from './../../modals/proyecto-equipo-modal/proyecto-equipo-modal.component';

const MSG_DELETE = marker('csp.proyecto.equipo.listado.borrar');

@Component({
  selector: 'sgi-proyecto-equipo',
  templateUrl: './proyecto-equipo.component.html',
  styleUrls: ['./proyecto-equipo.component.scss']
})
export class ProyectoEquipoComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: ProyectoEquipoFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numIdentificacion', 'nombre', 'apellidos', 'rolEquipo', 'fechaInicio', 'fechaFin', 'horas', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoEquipo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    protected proyectoService: ProyectoService,
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.EQUIPO_PROYECTO, actionService);
    this.logger.debug(ProyectoEquipoComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoEquipoFragment;
    this.logger.debug(ProyectoEquipoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoEquipoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoEquipo>, property: string) => {
        switch (property) {
          case 'numIdentificacion':
            return wrapper.value.persona.identificadorNumero;
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.primerApellido;
          case 'rolEquipo':
            return wrapper.value.rolProyecto.nombre;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          case 'horas':
            return wrapper.value.horasDedicacion;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.equipos$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ProyectoEquipoComponent.name, 'ngOnInit()', 'end');
    }));
  }

  /**
   * Apertura de modal de equipos (edición/creación)
   * @param idEquipo Identificador de equipo a editar.
   */
  openModal(wrapper?: StatusWrapper<IProyectoEquipo>): void {
    this.logger.debug(ProyectoEquipoComponent.name, `openModal()`, 'start');
    const proyectoEquipo: IProyectoEquipo = {
      id: undefined,
      fechaFin: undefined,
      fechaInicio: undefined,
      persona: undefined,
      proyecto: undefined,
      rolProyecto: undefined,
      horasDedicacion: undefined
    };
    const data: ProyectoEquiposModalComponentData = {
      equipo: wrapper ? wrapper.value : proyectoEquipo,
      equipos: this.dataSource.data.map(element => element.value),
      fechaInicioProyecto: this.actionService.getProyecto.fechaInicio,
      fechaFinProyecto: this.actionService.getProyecto.fechaFin,
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      data.equipos = this.dataSource.data.filter(element =>
        element.value.persona.personaRef !== data.equipo.persona.personaRef &&
        element.value.fechaFin !== data.equipo.fechaFin &&
        element.value.fechaInicio !== data.equipo.fechaInicio
      ).map(element => element.value);
    }

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(ProyectoEquipoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoEquiposModalComponentData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addProyectoEquipo(modalData.equipo);
          }
        }
        this.logger.debug(ProyectoEquipoComponent.name, `openModal()`, 'end');
      }
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoEquipoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoEquipoComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Eliminar proyecto equipo
   */
  deleteEquipo(wrapper: StatusWrapper<IProyectoEquipo>) {
    this.logger.debug(ProyectoEquipoComponent.name,
      `deleteEquipo(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoEquipo(wrapper);
          }
          this.logger.debug(ProyectoEquipoComponent.name,
            `deleteEquipo(${wrapper})`, 'end');
        }
      )
    );
  }


}
