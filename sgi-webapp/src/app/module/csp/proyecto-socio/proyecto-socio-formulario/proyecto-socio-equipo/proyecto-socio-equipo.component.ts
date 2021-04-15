import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { DialogService } from '@core/services/dialog.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProyectoEquipoSocioModalData, ProyectoSocioEquipoModalComponent } from '../../modals/proyecto-socio-equipo-modal/proyecto-socio-equipo-modal.component';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { ProyectoSocioEquipoFragment } from './proyecto-socio-equipo.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_SOCIO_KEY = marker('csp.proyecto-socio');

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

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocioEquipo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private actionService: ProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO, actionService);
    this.formPart = this.fragment as ProyectoSocioEquipoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.proyectoSocioEquipos$.subscribe(
      (proyectoEquipos) => this.dataSource.data = proyectoEquipos
    );
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(wrapper?: StatusWrapper<IProyectoSocioEquipo>, position?: number): void {
    const fechaInicioProyectoSocio = this.actionService.proyectoSocio?.fechaInicio;
    const fechaFinProyectoSocio = this.actionService.proyectoSocio?.fechaFin;
    const data: ProyectoEquipoSocioModalData = {
      proyectoSocioEquipo: wrapper?.value ?? {} as IProyectoSocioEquipo,
      selectedProyectoSocioEquipos: this.dataSource.data.map(element => element.value),
      fechaInicioProyectoSocio,
      fechaFinProyectoSocio,
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedProyectoSocioEquipos);
      filtered.splice(position, 1);
      data.selectedProyectoSocioEquipos = filtered;
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
      }
    );
  }

  deleteProyectoSocioEquipo(wrapper: StatusWrapper<IProyectoSocioEquipo>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoSocioEquipo(wrapper);
          }
        }
      )
    );
  }
}
