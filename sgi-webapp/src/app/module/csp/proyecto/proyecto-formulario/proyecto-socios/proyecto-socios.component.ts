import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoSociosFragment } from './proyecto-socios.fragment';


const MSG_DELETE = marker('csp.proyecto.socios.borrar');


interface IProyectoSocioState {
  proyectoId: number;
  proyectoSocio: IProyectoSocio;
  selectedProyectoSocios: IProyectoSocio[];
}

@Component({
  selector: 'sgi-proyecto-socios',
  templateUrl: './proyecto-socios.component.html',
  styleUrls: ['./proyecto-socios.component.scss']
})
export class ProyectoSociosComponent extends FragmentComponent implements OnInit, OnDestroy {
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;
  PROYECTO_SOCIO_ROUTE = CSP_ROUTE_NAMES.PROYECTO_SOCIO;
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  formPart: ProyectoSociosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['empresa.numeroDocumento', 'empresa.razonSocial', 'rolSocio.nombre', 'numInvestigadores', 'fechaInicio', 'fechaFin', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocio>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    public actionService: ProyectoActionService,
    private dialogService: DialogService,
  ) {
    super(actionService.FRAGMENT.SOCIOS, actionService);
    this.logger.debug(ProyectoSociosComponent.name, `ngOnInit()`, 'start');
    this.formPart = this.fragment as ProyectoSociosFragment;
    this.logger.debug(ProyectoSociosComponent.name, `ngOnInit()`, 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoSociosComponent.name, `ngOnInit()`, 'start');
    super.ngOnInit();
    const subscription = this.formPart.proyectoSocios$.subscribe(
      (proyectoSocios) => {
        this.dataSource.data = proyectoSocios;
      }
    );
    this.subscriptions.push(subscription);
    this.logger.debug(ProyectoSociosComponent.name, `ngOnInit()`, 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSociosComponent.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSociosComponent.name, `ngOnDestroy()`, 'end');
  }

  deleteProyectoSocio(wrapper: StatusWrapper<IProyectoSocio>) {
    this.logger.debug(ProyectoSociosComponent.name, `deleteProyectoSocio(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoSocio(wrapper);
          }
          this.logger.debug(ProyectoSociosComponent.name, `deleteProyectoSocio(${wrapper})`, 'end');
        }
      )
    );
  }


  createState(wrapper?: StatusWrapper<IProyectoSocio>): IProyectoSocioState {
    this.logger.debug(ProyectoSociosComponent.name, `createState(${wrapper})`, 'start');

    const state: IProyectoSocioState = {
      proyectoId: this.fragment.getKey() as number,
      proyectoSocio: wrapper ? wrapper.value : {} as IProyectoSocio,
      selectedProyectoSocios: this.dataSource.data.map(element => element.value)
    };

    this.logger.debug(ProyectoSociosComponent.name, `createState(${wrapper})`, 'end');

    return state;
  }

}
