import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ProyectoActionService } from '../../proyecto.action.service';
import { EntidadFinanciadoraDataModal, EntidadFinanciadoraModalComponent } from '../../../modals/entidad-financiadora-modal/entidad-financiadora-modal.component';
import { ProyectoEntidadesFinanciadorasFragment } from './proyecto-entidades-financiadoras.fragment';

const MODAL_ENTIDAD_FINANCIADORA_TITLE = marker('csp.proyecto.entidades.financiadoras.modal.title');
const MSG_DELETE = marker('csp.convocatoria.entidad.financiadora.listado.borrar');

@Component({
  selector: 'sgi-proyecto-entidades-financiadoras',
  templateUrl: './proyecto-entidades-financiadoras.component.html',
  styleUrls: ['./proyecto-entidades-financiadoras.component.scss']
})
export class ProyectoEntidadesFinanciadorasComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoEntidadesFinanciadorasFragment;
  private subscriptions: Subscription[] = [];

  private columns = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
    'porcentajeFinanciacion', 'acciones'];
  private elementsPage = [5, 10, 25, 100];

  columnsPropias = [...this.columns];
  columnsAjenas = [...this.columns];
  elementsPagePropias = [...this.elementsPage];
  elementsPageAjenas = [...this.elementsPage];

  dataSourcePropias = new MatTableDataSource<StatusWrapper<IProyectoEntidadFinanciadora>>();
  dataSourceAjenas = new MatTableDataSource<StatusWrapper<IProyectoEntidadFinanciadora>>();
  @ViewChild('paginatorPropias', { static: true }) paginatorPropias: MatPaginator;
  @ViewChild('sortPropias', { static: true }) sortPropias: MatSort;
  @ViewChild('paginatorAjenas', { static: true }) paginatorAjenas: MatPaginator;
  @ViewChild('sortAjenas', { static: true }) sortAjenas: MatSort;

  constructor(
    protected logger: NGXLogger,
    protected actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoEntidadesFinanciadorasFragment;
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.dataSourcePropias.paginator = this.paginatorPropias;
    this.dataSourcePropias.sort = this.sortPropias;
    this.dataSourceAjenas.paginator = this.paginatorAjenas;
    this.dataSourceAjenas.sort = this.sortAjenas;
    this.subscriptions.push(
      this.formPart.entidadesPropias$.subscribe((elements) => this.dataSourcePropias.data = elements)
    );
    this.subscriptions.push(
      this.formPart.entidadesAjenas$.subscribe((elements) => this.dataSourceAjenas.data = elements)
    );
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(targetPropias: boolean, wrapper?: StatusWrapper<IProyectoEntidadFinanciadora>): void {
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, `openModal()`, 'start');
    const data: EntidadFinanciadoraDataModal = {
      title: MODAL_ENTIDAD_FINANCIADORA_TITLE,
      entidad: wrapper ? wrapper.value : {} as IProyectoEntidadFinanciadora,
      selectedEmpresas: targetPropias
        ? this.dataSourcePropias.data.map(entidad => entidad.value.empresa)
        : this.dataSourceAjenas.data.map(entidad => entidad.value.empresa),
      readonly: this.formPart.readonly
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(EntidadFinanciadoraModalComponent, config);
    dialogRef.afterClosed().subscribe(entidadFinanciadora => {
      if (entidadFinanciadora) {
        if (!wrapper) {
          this.formPart.addEntidadFinanciadora(entidadFinanciadora, targetPropias);
        } else if (!wrapper.created) {
          const entidad = new StatusWrapper<IProyectoEntidadFinanciadora>(wrapper.value);
          this.formPart.updateEntidadFinanciadora(entidad, targetPropias);
        }
      }
      this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name, `openModal()`, 'end');
    }
    );
  }

  deleteEntidadFinanciadora(targetPropias: boolean, wrapper: StatusWrapper<IProyectoEntidadFinanciadora>) {
    this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name,
      `deleteEntidadFinanciadora(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteEntidadFinanciadora(wrapper, targetPropias);
          }
          this.logger.debug(ProyectoEntidadesFinanciadorasComponent.name,
            `deleteEntidadFinanciadora(${wrapper})`, 'end');
        }
      )
    );
  }
}
