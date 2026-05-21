import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { DialogService } from '@core/services/dialog.service';
import { LanguageService } from '@core/services/language.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoDescriptorModalComponent, GrupoDescriptorModalData } from '../../modals/grupo-descriptor-modal/grupo-descriptor-modal.component';
import { GrupoDescriptorFragment } from './grupo-descriptor.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_DESCRIPTOR_KEY = marker('csp.grupo-descriptor');
const MODAL_TITLE_KEY = marker('csp.grupo-descriptor');

@Component({
  selector: 'sgi-grupo-descriptor',
  templateUrl: './grupo-descriptor.component.html',
  styleUrls: ['./grupo-descriptor.component.scss']
})
export class GrupoDescriptorComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoDescriptorFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['tipoDescriptorGrupo', 'texto', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoDescriptor>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private readonly languageService: LanguageService
  ) {
    super(actionService.FRAGMENT.DESCRIPTOR, actionService, translate);
    this.formPart = this.fragment as GrupoDescriptorFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;

    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoDescriptor>, property: string) => {
        switch (property) {
          case 'tipoDescriptorGrupo':
            return this.languageService.getFieldValue(wrapper.value.tipoDescriptorGrupo?.nombre);
          case 'texto':
            return this.languageService.getFieldValue(wrapper.value.texto);
          default:
            return wrapper.value[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.descriptores$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  protected setupI18N(): void {
    this.translate.get(
      GRUPO_DESCRIPTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_DESCRIPTOR_KEY,
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

  openModal(wrapper?: StatusWrapper<IGrupoDescriptor>): void {

    const config: MatDialogConfig<GrupoDescriptorModalData> = {
      data: {
        titleEntity: this.modalTitleEntity,
        descriptor: wrapper?.value ?? {} as IGrupoDescriptor,
        isEdit: Boolean(wrapper)
      }
    };

    const dialogRef = this.matDialog.open(GrupoDescriptorModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoDescriptorModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.descriptor.grupoId = this.actionService.id;
            this.formPart.addGrupoDescriptor(modalData.descriptor);
          } else if (!wrapper.created) {
            const updated = new StatusWrapper<IGrupoDescriptor>(modalData.descriptor);
            this.formPart.updateGrupoDescriptor(updated);
          }
        }
      }
    );
  }

  deleteDescriptor(wrapper: StatusWrapper<IGrupoDescriptor>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete)
        .pipe(
          filter(Boolean)
        ).subscribe(() => {
          this.formPart.deleteGrupoDescriptor(wrapper);
        })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

}
