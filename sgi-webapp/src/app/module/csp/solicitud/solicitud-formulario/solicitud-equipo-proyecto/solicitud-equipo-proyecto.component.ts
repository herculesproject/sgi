import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { EquipoProyectoModalData, SolicitudEquipoProyectoModalComponent } from '../../modals/solicitud-equipo-proyecto-modal/solicitud-equipo-proyecto-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudEquipoProyectoFragment } from './solicitud-equipo-proyecto.fragment';
import { IPersona } from '@core/models/sgp/persona';

const MSG_DELETE = marker('csp.solicitud.equipo.proyecto.borrar');

@Component({
  selector: 'sgi-solicitud-equipo-proyecto',
  templateUrl: './solicitud-equipo-proyecto.component.html',
  styleUrls: ['./solicitud-equipo-proyecto.component.scss']
})
export class SolicitudEquipoProyectoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudEquipoProyectoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['persona', 'nombre', 'apellidos', 'rolProyecto', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoEquipo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    private actionService: SolicitudActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.EQUIPO_PROYECTO, actionService);
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as SolicitudEquipoProyectoFragment;
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    this.actionService.existsDatosProyectos();
    this.formPart.solicitantePersonaRef = this.actionService.getSolicitantePersonaRef();
    const subcription = this.formPart.proyectoEquipos$.subscribe(
      (proyectoEquipos) => {
        if (proyectoEquipos.length === 0 || this.formPart.getIndexSolicitante(proyectoEquipos) >= 0) {
          this.formPart.setErrors(false);
        } else {
          this.formPart.setErrors(true);
        }
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.subscriptions.push(subcription);
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `ngOnDestroy()`, 'end');
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoEquipo>): void {
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `openModal()`, 'start');
    const persona: IPersona = {
      identificadorLetra: '',
      identificadorNumero: '',
      nivelAcademico: '',
      nombre: '',
      personaRef: '',
      primerApellido: '',
      segundoApellido: '',
      vinculacion: ''
    };
    const solicitudProyectoEquipo: ISolicitudProyectoEquipo = {
      id: undefined,
      mesFin: undefined,
      mesInicio: undefined,
      persona: null,
      rolProyecto: undefined,
      solicitudProyectoDatos: undefined
    };
    const data: EquipoProyectoModalData = {
      solicitudProyectoEquipo: wrapper ? wrapper.value : solicitudProyectoEquipo,
      selectedProyectoEquipos: this.dataSource.data.map(element => element.value),
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      data.selectedProyectoEquipos = this.dataSource.data.filter(element =>
        element.value.persona.personaRef !== data.solicitudProyectoEquipo.persona.personaRef &&
        element.value.mesInicio !== data.solicitudProyectoEquipo.mesInicio &&
        element.value.mesFin !== data.solicitudProyectoEquipo.mesFin
      ).map(element => element.value);
    }

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(SolicitudEquipoProyectoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: EquipoProyectoModalData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addProyectoEquipo(modalData.solicitudProyectoEquipo);
          }
        }
        this.logger.debug(SolicitudEquipoProyectoComponent.name, `openModal()`, 'end');
      }
    );
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipo>) {
    this.logger.debug(SolicitudEquipoProyectoComponent.name, `deleteProyectoEquipo(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoEquipo(wrapper);
          }
          this.logger.debug(SolicitudEquipoProyectoComponent.name, `deleteProyectoEquipo(${wrapper})`, 'end');
        }
      )
    );
  }
}
