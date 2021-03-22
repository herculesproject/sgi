import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { ISolicitudProyectoPresupuestoTotales } from '@core/models/csp/solicitud-proyecto-presupuesto-totales';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { EntidadFinanciadoraDesglosePresupuesto, SolicitudProyectoPresupuestoEntidadesFragment } from './solicitud-proyecto-presupuesto-entidades.fragment';

export interface ISolicitudProyectoPresupuestoState {
  solicitudId: number;
  convocatoriaId: number;
  entidadFinanciadora: IEntidadFinanciadora;
  isEntidadFinanciadoraConvocatoria: boolean;
}

const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY = marker('csp.solicitud-entidad-financiadora');

@Component({
  selector: 'sgi-solicitud-proyecto-presupuesto-entidades',
  templateUrl: './solicitud-proyecto-presupuesto-entidades.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-entidades.component.scss']
})
export class SolicitudProyectoPresupuestoEntidadesComponent extends FormFragmentComponent<ISolicitudProyectoPresupuestoTotales> implements OnInit, OnDestroy {
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoPresupuestoEntidadesFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = [
    'nombre',
    'cif',
    'ajena',
    'acciones'
  ];
  elementsPage = [5, 10, 25, 100];

  msgParamEntidadFinanciadorasEntity = {};

  dataSource = new MatTableDataSource<EntidadFinanciadoraDesglosePresupuesto>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    public actionService: SolicitudActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DESGLOSE_PRESUPUESTO_ENTIDADES, actionService);
    this.formPart = this.fragment as SolicitudProyectoPresupuestoEntidadesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(33%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.actionService.existsDatosProyectos();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor =
      (entidadFinanciadoraDesglose: EntidadFinanciadoraDesglosePresupuesto, property: string) => {
        switch (property) {
          case 'nombre':
            return entidadFinanciadoraDesglose.entidadFinanciadora.empresa.razonSocial;
          case 'cif':
            return entidadFinanciadoraDesglose.entidadFinanciadora.empresa.numeroDocumento;
          case 'ajena':
            return entidadFinanciadoraDesglose.ajena;
          default:
            return entidadFinanciadoraDesglose[property];
        }
      };

    const subscription = this.formPart.entidadesFinanciadoras$.subscribe(
      (entidadesFinanciadoras) => {
        this.dataSource.data = entidadesFinanciadoras;
      }
    );
    this.subscriptions.push(subscription);
    this.actionService.hasDesglosePresupuestoEntidades();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntidadFinanciadorasEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  createState(entidadFinanciadoraDesglose: EntidadFinanciadoraDesglosePresupuesto): ISolicitudProyectoPresupuestoState {
    const state: ISolicitudProyectoPresupuestoState = {
      solicitudId: this.fragment.getKey() as number,
      convocatoriaId: this.formPart.convocatoriaId,
      entidadFinanciadora: entidadFinanciadoraDesglose.entidadFinanciadora,
      isEntidadFinanciadoraConvocatoria: !entidadFinanciadoraDesglose.ajena
    };

    return state;
  }



}
