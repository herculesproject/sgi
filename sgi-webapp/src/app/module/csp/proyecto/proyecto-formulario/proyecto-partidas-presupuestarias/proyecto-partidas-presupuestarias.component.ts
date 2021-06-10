import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PartidaPresupuestariaModalComponent, PartidaPresupuestariaModalComponentData } from '../../../shared/partida-presupuestaria-modal/partida-presupuestaria-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoPartidasPresupuestariasFragment } from './proyecto-partidas-presupuestarias.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_PARTIDA_PRESUPUESTARIA_KEY = marker('csp.proyecto-partida-presupuestaria');

@Component({
  selector: 'sgi-proyecto-partidas-presupuestarias',
  templateUrl: './proyecto-partidas-presupuestarias.component.html',
  styleUrls: ['./proyecto-partidas-presupuestarias.component.scss']
})
export class ProyectoPartidasPresupuestariasComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: ProyectoPartidasPresupuestariasFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['codigo', 'tipoPartida', 'descripcion', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoPartida>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_PARTIDA_MAP() {
    return TIPO_PARTIDA_MAP;
  }

  constructor(
    protected proyectoService: ProyectoService,
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PARTIDAS_PRESUPUESTARIAS, actionService);
    this.formPart = this.fragment as ProyectoPartidasPresupuestariasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoPartida>, property: string) => {
        switch (property) {
          case 'codigo':
            return wrapper.value.codigo;
          case 'tipoPartida':
            return wrapper.value.tipoPartida;
          case 'descripcion':
            return wrapper.value.descripcion;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.partidasPresupuestarias$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PARTIDA_PRESUPUESTARIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_PARTIDA_PRESUPUESTARIA_KEY,
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

  /**
   * Apertura de modal de equipos (edición/creación)
   *
   * @param idEquipo Identificador de equipo a editar.
   */
  openModal(wrapper?: StatusWrapper<IProyectoPartida>): void {
    const data: PartidaPresupuestariaModalComponentData = {
      partidaPresupuestaria: wrapper?.value ?? {} as IProyectoPartida,
      partidasPresupuestarias: this.dataSource.data.map(element => element.value),
      readonly: this.formPart.readonly,
      canEdit: !this.formPart.readonly
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(PartidaPresupuestariaModalComponent, config);
    dialogRef.afterClosed().subscribe((partidaPresupuestaria) => {
      if (partidaPresupuestaria) {
        if (!wrapper) {
          this.formPart.addPartidaPresupuestaria(partidaPresupuestaria);
        } else if (!wrapper.created) {
          const wrapperUpdated = new StatusWrapper<IProyectoPartida>(wrapper.value);
          this.formPart.updatePartidaPresupuestaria(wrapperUpdated);
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Elimina la partida presupuestaria
   *
   * @param wrapper una partida presupuestaria
   */
  deletePartidaPresupuestaria(wrapper: StatusWrapper<IProyectoPartida>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePartidaPresupuestaria(wrapper);
          }
        }
      )
    );
  }

}
