import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { SearchProyectoEconomicoModalComponent, SearchProyectoEconomicoModalData } from 'src/app/esb/sge/shared/search-proyecto-economico-modal/search-proyecto-economico-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoProyectosSgeFragment } from './proyecto-proyectos-sge.fragment';
import { ConfigCsp } from 'src/app/module/adm/config-csp/config-csp.component';
import { ConfigService } from '@core/services/csp/config.service';
import { IProyectoEconomicoFormlyData, ProyectoEconomicoFormlyModalComponent, IProyectoEconomicoFormlyResponse } from 'src/app/esb/sge/formly-forms/proyecto-economico-formly-modal/proyecto-economico-formly-modal.component';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';

const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-proyecto-sge.identificador-sge');

@Component({
  selector: 'sgi-proyecto-proyectos-sge',
  templateUrl: './proyecto-proyectos-sge.component.html',
  styleUrls: ['./proyecto-proyectos-sge.component.scss']
})
export class ProyectoProyectosSgeComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoProyectosSgeFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['proyectoSgeRef', 'sectorIva'];

  msgParamEntity = {};

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoProyectoSge>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  canDeleteProyectosSge: boolean;
  private _altaBuscadorSgeEnabled: boolean = true;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private readonly cspConfigService: ConfigService
  ) {
    super(actionService.FRAGMENT.PROYECTOS_SGE, actionService);

    this.formPart = this.fragment as ProyectoProyectosSgeFragment;

    this.subscriptions.push(
      this.cspConfigService.isAltaBuscadorSgeEnabled().subscribe(altaBuscadorSgeEnabled => {
        this._altaBuscadorSgeEnabled = altaBuscadorSgeEnabled;
      })
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoProyectoSge>, property: string) => {
        switch (property) {
          case 'proyectoSgeRef':
            return wrapper.value.proyectoSge.id;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.proyectosSge$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

  }

  private setupI18N(): void {
    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }

  openModal(): void {
    if (!this._altaBuscadorSgeEnabled) {
      const proyectoData: IProyectoEconomicoFormlyData = {
        proyectoSgiId: this.formPart.getKey() as number,
        action: ACTION_MODAL_MODE.NEW,
        proyectoSge: null,
        grupoInvestigacion: null
      };

      const config = {
        panelClass: 'sgi-dialog-container',
        data: proyectoData
      };
      const dialogRef = this.matDialog.open(ProyectoEconomicoFormlyModalComponent, config);

      dialogRef.afterClosed().subscribe(
        (response: IProyectoEconomicoFormlyResponse) => {
          if (response) {
            this.formPart.addProyectoSge(response.proyectoSge);
          }
        }
      );
    } else {
      const data: SearchProyectoEconomicoModalData = {
        searchTerm: null,
        extended: true,
        selectedProyectos: this.dataSource.data.map((proyectoProyectoSge) => proyectoProyectoSge.value.proyectoSge),
        proyectoSgiId: this.formPart.getKey() as number,
        selectAndNotify: true,
        grupoInvestigacion: null
      };

      const config = {
        data
      };
      const dialogRef = this.matDialog.open(SearchProyectoEconomicoModalComponent, config);
      dialogRef.afterClosed().subscribe(
        (proyectoSge) => {
          if (proyectoSge) {
            this.formPart.addProyectoSge(proyectoSge);
          }
        }
      );
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
