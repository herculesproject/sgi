import { Component, OnDestroy, ViewChild, OnInit } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { Subscription } from 'rxjs';
import { SolicitudProyectoSocioEquipoSocioFragment } from './solicitud-proyecto-socio-equipo-socio.fragment';
import { MatTableDataSource } from '@angular/material/table';
import { ISolicitudProyectoEquipoSocio } from '@core/models/csp/solicitud-proyecto-equipo-socio';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { MatSort } from '@angular/material/sort';
import { DialogService } from '@core/services/dialog.service';
import { MatDialog } from '@angular/material/dialog';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { NGXLogger } from 'ngx-logger';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SolicitudProyectoEquipoSocioModalData, SolicitudProyectoSocioEquipoSocioModalComponent } from '../../modals/solicitud-proyecto-socio-equipo-socio-modal/solicitud-proyecto-socio-equipo-socio-modal.component';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const MSG_DELETE = marker('csp.solicitud.proyecto.socio.equipo.borrar');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-equipo-socio',
  templateUrl: './solicitud-proyecto-socio-equipo-socio.component.html',
  styleUrls: ['./solicitud-proyecto-socio-equipo-socio.component.scss']
})
export class SolicitudProyectoSocioEquipoSocioComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: SolicitudProyectoSocioEquipoSocioFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['persona', 'nombre', 'apellidos', 'rolProyecto', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoEquipoSocio>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    actionService: SolicitudProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.EQUIPO_SOCIO, actionService);
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as SolicitudProyectoSocioEquipoSocioFragment;
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subcription = this.formPart.proyectoEquipoSocios$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.subscriptions.push(subcription);
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      switch (property) {
        case 'persona':
          return wrapper.value.persona.personaRef;
        case 'nombre':
          return wrapper.value.persona.nombre;
        case 'apellidos':
          const persona = wrapper.value.persona;
          return `${persona.primerApellido} ${persona.segundoApellido}`;
        case 'rolProyecto':
          return wrapper.value.rolProyecto.nombre;
        default:
          return wrapper.value[property];
      }
    };
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoEquipoSocio>): void {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `openModal()`, 'start');
    const solicitudProyectoEquipo: ISolicitudProyectoEquipoSocio = {
      id: undefined,
      mesFin: undefined,
      mesInicio: undefined,
      persona: null,
      rolProyecto: undefined,
      solicitudProyectoSocio: undefined
    };
    const data: SolicitudProyectoEquipoSocioModalData = {
      solicitudProyectoEquipoSocio: wrapper ? wrapper.value : solicitudProyectoEquipo,
      selectedProyectoEquipoSocios: this.dataSource.data.map(element => element.value),
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      const index = data.selectedProyectoEquipoSocios.findIndex((element) => element === wrapper.value);
      if (index >= 0) {
        data.selectedProyectoEquipoSocios.splice(index, 1);
      }
    }

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(SolicitudProyectoSocioEquipoSocioModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudProyectoEquipoSocioModalData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addProyectoEquipoSocio(modalData.solicitudProyectoEquipoSocio);
          }
        }
        this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `openModal()`, 'end');
      }
    );
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipoSocio>): void {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `deleteProyectoEquipo(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoEquipoSocio(wrapper);
          }
          this.logger.debug(SolicitudProyectoSocioEquipoSocioComponent.name, `deleteProyectoEquipo(${wrapper})`, 'end');
        }
      )
    );
  }
}
