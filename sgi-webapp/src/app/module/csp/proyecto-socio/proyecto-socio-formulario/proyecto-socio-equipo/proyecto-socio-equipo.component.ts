import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { Subscription } from 'rxjs';
import { ProyectoSocioEquipoFragment } from './proyecto-socio-equipo.fragment';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { NGXLogger } from 'ngx-logger';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { ProyectoEquipoSocioModalData, ProyectoSocioEquipoModalComponent } from '../../modals/proyecto-socio-equipo-modal/proyecto-socio-equipo-modal.component';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';

const MSG_DELETE = marker('csp.proyecto-equipo.socio-equipo.borrar');

@Component({
  selector: 'sgi-proyecto-socio-equipo',
  templateUrl: './proyecto-socio-equipo.component.html',
  styleUrls: ['./proyecto-socio-equipo.component.scss']
})
export class ProyectoSocioEquipoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoSocioEquipoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['nombre', 'apellidos', 'rolProyecto', 'fechaInicio', 'fechaFin', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocioEquipo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    actionService: ProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.EQUIPO, actionService);
    this.logger.debug(ProyectoSocioEquipoComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as ProyectoSocioEquipoFragment;
    this.logger.debug(ProyectoSocioEquipoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoSocioEquipoComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subcription = this.formPart.proyectoEquipoSocios$.subscribe(
      (proyectoEquipos) => this.dataSource.data = proyectoEquipos
    );
    this.subscriptions.push(subcription);
    this.logger.debug(ProyectoSocioEquipoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioEquipoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioEquipoComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<IProyectoSocioEquipo>): void {
    this.logger.debug(ProyectoSocioEquipoComponent.name, `openModal()`, 'start');
    const proyectoSocioEquipo: IProyectoSocioEquipo = {
      id: undefined,
      fechaFin: undefined,
      fechaInicio: undefined,
      persona: null,
      proyectoSocio: undefined,
      rolProyecto: undefined
    };
    const data: ProyectoEquipoSocioModalData = {
      proyectoSocioEquipo: wrapper ? wrapper.value : proyectoSocioEquipo,
      selectedProyectoSocioEquipos: this.dataSource.data.map(element => element.value),
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      data.selectedProyectoSocioEquipos = this.dataSource.data.filter(element =>
        element.value.persona.personaRef !== data.proyectoSocioEquipo.persona.personaRef &&
        element.value.fechaFin !== data.proyectoSocioEquipo.fechaFin &&
        element.value.fechaInicio !== data.proyectoSocioEquipo.fechaInicio
      ).map(element => element.value);
    }

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(ProyectoSocioEquipoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoEquipoSocioModalData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addProyectoSocioEquipo(modalData.proyectoSocioEquipo);
          }
        }
        this.logger.debug(ProyectoSocioEquipoComponent.name, `openModal()`, 'end');
      }
    );
  }

  deleteProyectoSocioEquipo(wrapper: StatusWrapper<IProyectoSocioEquipo>): void {
    this.logger.debug(ProyectoSocioEquipoComponent.name, `deleteProyectoSocioEquipo(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoSocioEquipo(wrapper);
          }
          this.logger.debug(ProyectoSocioEquipoComponent.name, `deleteProyectoSocioEquipo(${wrapper})`, 'end');
        }
      )
    );
  }
}
