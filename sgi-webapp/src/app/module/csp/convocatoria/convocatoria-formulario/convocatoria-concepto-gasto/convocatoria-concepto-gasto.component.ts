import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ConvocatoriaConceptoGastoFragment } from './convocatoria-concepto-gasto.fragment';
import { Subscription, Observable, of } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { map, startWith } from 'rxjs/operators';
import { ConvocatoriaConceptoGastoModalComponent, IConvocatoriaConceptoGastoModalComponent } from '../../modals/convocatoria-concepto-gasto-modal/convocatoria-concepto-gasto-modal.component';
import { IConceptoGasto } from '@core/models/csp/tipos-configuracion';

const MSG_DELETE = marker('csp.convocatoria.concepto-gasto.listado.borrar');

@Component({
  selector: 'sgi-convocatoria-concepto-gasto',
  templateUrl: './convocatoria-concepto-gasto.component.html',
  styleUrls: ['./convocatoria-concepto-gasto.component.scss']
})

export class ConvocatoriaConceptoGastoComponent extends FormFragmentComponent<IConvocatoriaConceptoGasto[]> implements OnInit, OnDestroy {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ConvocatoriaConceptoGastoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumnsPermitidos = ['conceptoGasto.nombre', 'conceptoGasto.descripcion', 'importeMaximo', 'numMeses', 'observaciones', 'acciones'];
  displayedColumnsNoPermitidos = ['conceptoGasto.nombre', 'conceptoGasto.descripcion', 'observaciones', 'acciones'];

  dataSourcePermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
  dataSourceNoPermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
  @ViewChild('paginatorPermitidos', { static: true }) paginatorPermitidos: MatPaginator;
  @ViewChild('paginatorNoPermitidos', { static: true }) paginatorNoPermitidos: MatPaginator;
  @ViewChild('sortPermitidos', { static: true }) sortPermitidos: MatSort;
  @ViewChild('sortNoPermitidos', { static: true }) sortNoPermitidos: MatSort;

  filteredCostesIndirectos: Observable<IConceptoGasto[]>;

  constructor(
    protected logger: NGXLogger,
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.ELEGIBILIDAD, actionService);
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaConceptoGastoFragment;
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, 'constructor()', 'end');

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.dataSourcePermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
    this.dataSourcePermitidos.paginator = this.paginatorPermitidos;
    this.dataSourcePermitidos.sort = this.sortPermitidos;
    this.dataSourceNoPermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
    this.dataSourceNoPermitidos.paginator = this.paginatorNoPermitidos;
    this.dataSourceNoPermitidos.sort = this.sortNoPermitidos;
    this.subscriptions.push(this.formPart?.convocatoriaConceptoGastoPermitido$.subscribe(elements => {
      this.dataSourcePermitidos.data = elements;
      this.filteredCostesIndirectos = this.formPart.getFormGroup().controls.costeIndirecto.valueChanges
        .pipe(
          startWith(''),
          map(value => {
            return this.filterCosteIndirecto(value);
          })
        );
    }));
    this.subscriptions.push(this.formPart?.convocatoriaConceptoGastoNoPermitido$.subscribe(elements => {
      this.dataSourceNoPermitidos.data = elements;
    }));

    this.dataSourcePermitidos.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaConceptoGasto>, property: string) => {
        switch (property) {
          case 'conceptoGasto.nombre':
            return wrapper.value.conceptoGasto.nombre;
          case 'conceptoGasto.descripcion':
            return wrapper.value.conceptoGasto.descripcion;
          case 'importeMaximo':
            return wrapper.value.importeMaximo;
          case 'numMeses':
            return wrapper.value.numMeses;
          case 'observaciones':
            return wrapper.value.observaciones;
          default:
            return wrapper[property];
        }
      };

    this.dataSourceNoPermitidos.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaConceptoGasto>, property: string) => {
        switch (property) {
          case 'conceptoGasto.nombre':
            return wrapper.value.conceptoGasto.nombre;
          case 'conceptoGasto.descripcion':
            return wrapper.value.conceptoGasto.descripcion;
          case 'observaciones':
            return wrapper.value.observaciones;
          default:
            return wrapper[property];
        }
      };
  }

  openModal(wrapper?: StatusWrapper<IConvocatoriaConceptoGasto>): void {
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, `openModal()`, 'start');

    const convocatoriaConceptoGastosTabla = wrapper.value.permitido ? this.dataSourcePermitidos.data.map(
      wrapperPermitidos => wrapperPermitidos.value)
      : this.dataSourceNoPermitidos.data.map(wrapperNoPermitidos => wrapperNoPermitidos.value);

    const data: IConvocatoriaConceptoGastoModalComponent = {
      convocatoriaConceptoGasto: wrapper.value,
      convocatoriaConceptoGastosTabla,
      editModal: true,
      readonly: this.formPart.readonly
    };

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data
    };
    const dialogRef = this.matDialog.open(ConvocatoriaConceptoGastoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (convocatoriaConceptoGasto: IConvocatoriaConceptoGasto) => {
        if (convocatoriaConceptoGasto) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addConvocatoriaConceptoGasto(convocatoriaConceptoGasto);
          }
        }
        this.logger.debug(ConvocatoriaConceptoGastoModalComponent.name, `openModal()`, 'end');
      }
    );
  }

  openModalCrear(permitido: boolean): void {
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, `openModalCrear()`, 'start');
    const convocatoriaConceptoGastosTabla = permitido ? this.dataSourcePermitidos.data.map(
      wrapperPermitidos => wrapperPermitidos.value)
      : this.dataSourceNoPermitidos.data.map(wrapperNoPermitidos => wrapperNoPermitidos.value);

    const convocatoriaConceptoGasto: IConvocatoriaConceptoGasto = {
      conceptoGasto: null,
      convocatoria: null,
      id: null,
      importeMaximo: null,
      numMeses: null,
      observaciones: null,
      permitido,
      porcentajeCosteIndirecto: null,
      enableAccion: true
    };

    const data: IConvocatoriaConceptoGastoModalComponent = {
      convocatoriaConceptoGasto,
      convocatoriaConceptoGastosTabla,
      editModal: false,
      readonly: false
    };

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data
    };

    const dialogRef = this.matDialog.open(ConvocatoriaConceptoGastoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IConvocatoriaConceptoGasto) => {
        if (result) {
          this.formPart.addConvocatoriaConceptoGasto(result);
        }
        this.logger.debug(ConvocatoriaConceptoGastoModalComponent.name, `openModalCrear()`, 'end');
      }
    );
  }

  deleteConvocatoriaConceptoGasto(wrapper: StatusWrapper<IConvocatoriaConceptoGasto>) {
    this.logger.debug(ConvocatoriaConceptoGastoModalComponent.name,
      `deleteConvocatoriaConceptoGasto(${wrapper})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteConvocatoriaConceptoGasto(wrapper);
            this.actionService.deleteCodigoEconomico(wrapper.value);
          }
          this.logger.debug(ConvocatoriaConceptoGastoModalComponent.name,
            `deleteConvocatoriaConceptoGasto(${wrapper})`, 'end');
        }
      )
    );
  }

  private filterCosteIndirecto(value: string | IConceptoGasto): IConceptoGasto[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      if (value === null) {
        filterValue = '';
      } else {
        filterValue = value.nombre.toLowerCase();
      }
    }

    return this.dataSourcePermitidos.data.filter
      (wrapper => wrapper.value.conceptoGasto.nombre.toLowerCase().includes(filterValue)).map(wrapper => wrapper.value.conceptoGasto);
  }

  getCosteIndirecto(costeIndirecto: IConceptoGasto): string {
    if (costeIndirecto !== null) {
      return costeIndirecto.nombre;
    }
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaConceptoGastoComponent.name, 'ngOnDestroy()', 'end');
  }

}

