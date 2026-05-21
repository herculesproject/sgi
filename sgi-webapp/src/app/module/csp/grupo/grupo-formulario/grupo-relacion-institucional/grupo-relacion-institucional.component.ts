import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { GrupoActionService } from '../../grupo.action.service';
import {
  GrupoRelacionInstitucionalModalComponent,
  GrupoRelacionInstitucionalModalData
} from '../../modals/grupo-relacion-institucional-modal/grupo-relacion-institucional-modal.component';
import { GrupoRelacionInstitucionalFragment } from './grupo-relacion-institucional.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_RELACION_INSTITUCIONAL_KEY = marker('csp.grupo-relacion-institucional.entidad');
const MODAL_TITLE_KEY = marker('csp.grupo-relacion-institucional');

@Component({
  selector: 'sgi-grupo-relacion-institucional',
  templateUrl: './grupo-relacion-institucional.component.html',
  styleUrls: ['./grupo-relacion-institucional.component.scss']
})
export class GrupoRelacionInstitucionalComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoRelacionInstitucionalFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['nombreEntidad', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoRelacionInstitucional>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.RELACION_INSTITUCIONAL, actionService, translate);
    this.formPart = this.fragment as GrupoRelacionInstitucionalFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;

    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoRelacionInstitucional>, property: string) => {
        switch (property) {
          case 'nombreEntidad':
            return wrapper.value.entidad?.nombre ?? wrapper.value.institucion ?? '';
          default:
            return wrapper.value[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.relacionesInstitucionales$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  protected setupI18N(): void {

    this.translate.get(
      GRUPO_RELACION_INSTITUCIONAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE
    });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_RELACION_INSTITUCIONAL_KEY,
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

  openModal(wrapper?: StatusWrapper<IGrupoRelacionInstitucional>): void {

    const relacionesExistentes = this.formPart.relacionesInstitucionales$.value
      .filter(current => current !== wrapper)
      .map(current => current.value);

    const config: MatDialogConfig<GrupoRelacionInstitucionalModalData> = {
      data: {
        titleEntity: this.modalTitleEntity,
        relacionInstitucional: wrapper?.value ?? {} as IGrupoRelacionInstitucional,
        relacionesExistentes,
        isEdit: Boolean(wrapper)
      }
    };

    const dialogRef = this.matDialog.open(GrupoRelacionInstitucionalModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoRelacionInstitucionalModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.relacionInstitucional.grupo = this.actionService.grupo;
            this.formPart.addGrupoRelacionInstitucional(modalData.relacionInstitucional);
          } else if (!wrapper.created) {
            const updated = new StatusWrapper<IGrupoRelacionInstitucional>(modalData.relacionInstitucional);
            this.formPart.updateGrupoRelacionInstitucional(updated);
          }
        }
      }
    );
  }

  deleteRelacion(wrapper: StatusWrapper<IGrupoRelacionInstitucional>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete)
        .pipe(
          filter(Boolean)
        ).subscribe(() => {
          this.formPart.deleteGrupoRelacionInstitucional(wrapper);
        })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
