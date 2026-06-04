import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoUnidadVinculacionModalComponent, ProyectoUnidadVinculacionModalData } from '../../modals/proyecto-unidad-vinculacion-modal/proyecto-unidad-vinculacion-modal.component';
import { IProyectoUnidadVinculacionListado, ProyectoUnidadesVinculacionFragment } from './proyecto-unidades-vinculacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_UNIDAD_KEY = marker('csp.proyecto-unidad-vinculacion');

@Component({
  templateUrl: './proyecto-unidades-vinculacion.component.html',
  styleUrls: ['./proyecto-unidades-vinculacion.component.scss']
})
export class ProyectoUnidadesVinculacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: ProyectoUnidadesVinculacionFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['tipoUnidad', 'unidadVinculacion', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoUnidadVinculacionListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.UNIDADES_VINCULACION, actionService, translate);
    this.formPart = this.fragment as ProyectoUnidadesVinculacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoUnidadVinculacionListado>, property: string) => {
        switch (property) {
          case 'tipoUnidad':
            return wrapper.value.tipoUnidad?.nombre;
          case 'unidadVinculacion':
            return wrapper.value.unidadVinculacion?.nombre;
          default:
            return wrapper.value[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.unidadesVinculacion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  protected setupI18N(): void {
    this.translate.get(
      PROYECTO_UNIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => {
      this.msgParamEntity = { entity: value };
      this.modalTitleEntity = value;
    });

    this.translate.get(
      PROYECTO_UNIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  openModal(): void {
    const config: MatDialogConfig<ProyectoUnidadVinculacionModalData> = {
      data: {
        titleEntity: this.modalTitleEntity,
        selectedUnidadesVinculacionRefs: this.dataSource.data.map(element => element.value.unidadVinculacion?.id).filter(id => !!id)
      }
    };

    const dialogRef = this.matDialog.open(ProyectoUnidadVinculacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoUnidadVinculacionModalData) => {
        if (modalData?.proyectoUnidad) {
          modalData.proyectoUnidad.proyectoId = this.formPart.getKey() as number;
          this.formPart.addProyectoUnidad(modalData.proyectoUnidad);
        }
      }
    );
  }

  deleteUnidadVinculacion(wrapper: StatusWrapper<IProyectoUnidadVinculacionListado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete)
        .pipe(filter(Boolean))
        .subscribe(() => this.formPart.deleteProyectoUnidad(wrapper))
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
