import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoProteccionSubtipoModalComponent } from '../../modals/tipo-proteccion-subtipo-modal/tipo-proteccion-subtipo-modal.component';
import { TipoProteccionActionService } from '../../tipo-proteccion.action.service';
import { TipoProteccionSubtiposFragment } from './tipo-proteccion-subtipos.fragment';

const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const SUBTIPO_PROTECCION_KEY = marker('pii.subtipo-proteccion');

@Component({
  selector: 'sgi-tipo-proteccion-subtipos',
  templateUrl: './tipo-proteccion-subtipos.component.html',
  styleUrls: ['./tipo-proteccion-subtipos.component.scss']
})
export class TipoProteccionSubtiposComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: TipoProteccionSubtiposFragment;

  elementosPagina = [5, 10, 25, 100];
  columnas = ['nombre', 'descripcion', 'activo', 'acciones'];

  msgParamEntity = {};
  textoDesactivar: string;
  textoReactivar: string;

  dataSource = new MatTableDataSource<StatusWrapper<ITipoProteccion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private actionService: TipoProteccionActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.SUBTIPOS, actionService);

    this.formPart = this.fragment as TipoProteccionSubtiposFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.subtiposProteccion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);
  }

  /**
   * Apertura de modal de {@link ITipoProteccion}
   */
  openModal(subtipoProteccion: StatusWrapper<ITipoProteccion>): void {
    const config = {
      panelClass: 'sgi-dialog-container',
      data: subtipoProteccion
    };
    const dialogRef = this.matDialog.open(TipoProteccionSubtipoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: StatusWrapper<ITipoProteccion>) => {
        if (result) {
          this.formPart.agregarSubtiposProteccion([result]);
          this.formPart.setChanges(true);
        }
      });
  }

  /**
   * Elimina el {@link ITipoProteccion}
   *
   * @param wrapper del {@link ITipoProteccion}
   */
  deactivateTipoProteccion(wrapper: StatusWrapper<ITipoProteccion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDesactivar).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.desactivarSubtipoProteccion(wrapper);
          }
        }
      )
    );
  }

  /**
   * Recupera el {@link ITipoProteccion}
   *
   * @param wrapper del {@link ITipoProteccion}
   */
  activateTipoProteccion(wrapper: StatusWrapper<ITipoProteccion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoReactivar).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.activarSubtipoProteccion(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
