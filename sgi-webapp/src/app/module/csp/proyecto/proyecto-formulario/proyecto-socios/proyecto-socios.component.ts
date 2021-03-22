import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoSociosFragment } from './proyecto-socios.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_DELETE_CASCADE = marker('msg.csp.proyecto-socios.relations.delete');
const MSG_ERROR = marker('error.csp.proyecto-socios.relations.delete');
const PROYECTO_SOCIO_KEY = marker('csp.proyecto-socio');

export interface IProyectoSocioState {
  proyectoId: number;
  coordinadorExterno: boolean;
  proyectoSocio: IProyectoSocio;
  selectedProyectoSocios: IProyectoSocio[];
  urlProyecto: string;
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

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocio>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    public actionService: ProyectoActionService,
    private dialogService: DialogService,
    private router: Router,
    private proyectoSocioService: ProyectoSocioService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.SOCIOS, actionService);
    this.formPart = this.fragment as ProyectoSociosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subscription = this.formPart.proyectoSocios$.subscribe(
      (proyectoSocios) => {
        this.dataSource.data = proyectoSocios;
      }
    );
    this.subscriptions.push(subscription);
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

  deleteProyectoSocio(wrapper: StatusWrapper<IProyectoSocio>): void {
    this.subscriptions.push(
      this.proyectoSocioService.vinculaciones(wrapper.value.id).pipe(
        map(res => {
          return res ? MSG_DELETE_CASCADE : this.textoDelete;
        }),
        switchMap(message => {
          return this.dialogService.showConfirmation(message);
        }),
      ).subscribe(
        aceptado => {
          if (aceptado) {
            this.formPart.deleteProyectoSocio(wrapper);
          }
        },
        () => {
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  createState(wrapper?: StatusWrapper<IProyectoSocio>): IProyectoSocioState {
    const state: IProyectoSocioState = {
      proyectoId: this.fragment.getKey() as number,
      coordinadorExterno: this.actionService.coordinadorExterno,
      proyectoSocio: wrapper ? wrapper.value : {} as IProyectoSocio,
      selectedProyectoSocios: this.dataSource.data.map(element => element.value),
      urlProyecto: this.router.url
    };
    return state;
  }

}
