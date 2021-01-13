import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { SolicitudHitosFragment } from './solicitud-hitos.fragment';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { MatTableDataSource } from '@angular/material/table';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { NGXLogger } from 'ngx-logger';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { Subscription } from 'rxjs';
import { SolicitudActionService } from '../../solicitud.action.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SolicitudHitosModalComponentData, SolicitiudHitosModalComponent } from '../../modals/solicitud-hitos-modal/solicitud-hitos-modal.component';


const MSG_DELETE = marker('csp.solicitud.hito.listado.borrar');
@Component({
  selector: 'sgi-solicitud-hitos',
  templateUrl: './solicitud-hitos.component.html',
  styleUrls: ['./solicitud-hitos.component.scss']
})
export class SolicitudHitosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudHitosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['fechaInicio', 'tipoHito', 'comentario', 'aviso', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudHito>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    protected convocatoriaReunionService: ConvocatoriaService,
    private actionService: SolicitudActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService) {

    super(actionService.FRAGMENT.HITOS, actionService);

    this.formPart = this.fragment as SolicitudHitosFragment;
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudHitosComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ISolicitudHito>, property: string) => {
        switch (property) {
          case 'fechaInicio':
            return wrapper.value.fecha;
          case 'tipoHito':
            return wrapper.value.tipoHito.nombre;
          case 'comentario':
            return wrapper.value.comentario;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.hitos$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(SolicitudHitosComponent.name, 'ngOnInit()', 'end');
    }));
    this.logger.debug(SolicitudHitosComponent.name, `ngOnInit()`, 'end');

  }


  /**
   * Apertura de modal de hitos (edición/creación)
   * @param idHito Identificador de hito a editar.
   */
  openModal(wrapper?: StatusWrapper<ISolicitudHito>): void {
    this.logger.debug(SolicitudHitosComponent.name, `openModal()`, 'start');

    const data: SolicitudHitosModalComponentData = {
      hito: wrapper ? wrapper.value : {} as ISolicitudHito,
      idModeloEjecucion: this.formPart.solicitud.convocatoria.modeloEjecucion.id,
    };
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(SolicitiudHitosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (convocatoriaHito) => {
        if (convocatoriaHito) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addHito(convocatoriaHito);
          }
        }
        this.logger.debug(SolicitudHitosComponent.name, `openModal()`, 'end');
      }
    );

  }

  /**
   * Desactivar solicitud hito
   */
  deleteHito(wrapper: StatusWrapper<ISolicitudHito>) {
    this.logger.debug(SolicitudHitosComponent.name,
      `deleteHito(${wrapper})`, 'start');

    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteHito(wrapper);
          }
          this.logger.debug(SolicitudHitosComponent.name,
            `deleteHito(${wrapper})`, 'end');
        }
      )
    );

  }


  ngOnDestroy(): void {
    this.logger.debug(SolicitudHitosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudHitosComponent.name, 'ngOnDestroy()', 'end');

  }

}
