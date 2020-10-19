import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IComentario } from '@core/models/eti/comentario';
import { DialogService } from '@core/services/dialog.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription, Observable } from 'rxjs';

import { SeguimientoComentarioFragment } from './seguimiento-comentarios.fragment';
import { ComentarioCrearModalComponent, ComentarioCrearModalData } from '../../comentario/comentario-crear-modal/comentario-crear-modal.component';
import { ComentarioEditarModalComponent, ComentarioEditarModalData } from '../../comentario/comentario-editar-modal/comentario-editar-modal.component';
import { Gestion, SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { TipoComentarioService } from '@core/services/eti/tipo-comentario.service';
import { TipoComentario } from '@core/models/eti/tipo-comentario';

const MSG_DELETE = marker('eti.comentario.listado.borrar.titulo');

@Component({
  selector: 'sgi-seguimiento-comentarios',
  templateUrl: './seguimiento-comentarios.component.html',
  styleUrls: ['./seguimiento-comentarios.component.scss']
})
export class SeguimientoComentariosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: SeguimientoComentarioFragment;
  private subscriptions: Subscription[] = [];
  tipoComentario$: Observable<TipoComentario>;

  columnas: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IComentario>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly dialogService: DialogService,
    private tipoComentarioService: TipoComentarioService,
    private matDialog: MatDialog,
    private actionService: SeguimientoFormularioActionService
  ) {
    super(actionService.FRAGMENT.COMENTARIOS, actionService);
    this.logger.debug(SeguimientoComentariosComponent.name, 'constructor()', 'start');
    this.dataSource = new MatTableDataSource<StatusWrapper<IComentario>>();
    this.formPart = this.fragment as SeguimientoComentarioFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['apartadoFormulario.bloqueFormulario', 'apartadoFormulario.apartadoFormularioPadre',
      'apartadoFormulario', 'texto', 'acciones'];
    this.logger.debug(SeguimientoComentariosComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.logger.debug(SeguimientoComentariosComponent.name, 'ngOnInit()', 'start');
    this.subscriptions.push(this.formPart.comentarios$.subscribe(elements => {
      this.dataSource.data = elements;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.logger.debug(SeguimientoComentariosComponent.name, 'ngOnInit()', 'end');
    }));
  }

  ngOnDestroy(): void {
    this.logger.debug(SeguimientoComentariosComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SeguimientoComentariosComponent.name, 'ngOnDestroy()', 'end');
  }

  getApartadoFormularioNombre(comentario: IComentario): string {
    this.logger.debug(SeguimientoComentariosComponent.name, `getApartadoFormularioNombre(comentario: ${comentario})`, 'start');
    const nombre = comentario.apartadoFormulario?.apartadoFormularioPadre ?
      comentario.apartadoFormulario?.apartadoFormularioPadre?.nombre : comentario.apartadoFormulario?.nombre;
    this.logger.debug(SeguimientoComentariosComponent.name, `getApartadoFormularioNombre(comentario: ${comentario})`, 'start');
    return nombre;
  }

  getSubApartadoFormularioNombre(comentario: IComentario): string {
    this.logger.debug(SeguimientoComentariosComponent.name, `getSubApartadoFormularioNombre(comentario: ${comentario})`, 'start');
    const nombre = comentario.apartadoFormulario?.apartadoFormularioPadre ? comentario.apartadoFormulario?.nombre : '';
    this.logger.debug(SeguimientoComentariosComponent.name, `getSubApartadoFormularioNombre(comentario: ${comentario})`, 'end');
    return nombre;
  }

  /**
   * Abre la ventana modal para añadir un comentario
   */
  openCreateModal(): void {
    this.logger.debug(SeguimientoComentariosComponent.name, 'abrirModalCrear()', 'start');

    const comentarioCrearModalData: ComentarioCrearModalData = {
      idComite: this.actionService.getEvaluacion().memoria.comite.id,
      idTipoEvaluacion: this.actionService.getEvaluacion().tipoEvaluacion.id,
    };

    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: comentarioCrearModalData,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(ComentarioCrearModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (comentario: IComentario) => {
        if (comentario) {
          this.formPart.addComentario(comentario);
        }
        this.logger.debug(SeguimientoComentariosComponent.name, 'abrirModalCrear()', 'end');
      }
    );
  }

  /**
   * Abre la ventana modal para modificar un comentario
   *
   * @param comentario Comentario a modificar
   */
  openEditModal(comentario: StatusWrapper<IComentario>): void {
    this.logger.debug(SeguimientoComentariosComponent.name, 'openEditModal()', 'start');
    const wrapperRef = comentario;

    const comentarioEditarModalData: ComentarioEditarModalData = {
      comentario: wrapperRef.value,
      idComite: this.actionService.getEvaluacion().memoria.comite.id,
      idTipoEvaluacion: this.actionService.getEvaluacion().tipoEvaluacion.id,
    };

    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: comentarioEditarModalData,
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(ComentarioEditarModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (resultado: IComentario) => {
        if (resultado) {
          this.subscriptions.push(this.getTipoComentario().subscribe(
            (tipoComentario) => {
              resultado.tipoComentario = tipoComentario;
            }
          ));
          if (!wrapperRef.created) {
            wrapperRef.setEdited();
          }
          this.formPart.setChanges(true);
        }
        this.logger.debug(SeguimientoComentariosComponent.name, 'openEditModal()', 'end');
      }
    );
  }

  /**
   * Elimina un comentario del listado
   *
   * @param comentario Comentario a eliminar
   */
  deleteComentario(comentario: StatusWrapper<IComentario>) {
    this.logger.debug(SeguimientoComentariosComponent.name, `eliminarComentario(${comentario})`, 'start');
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteComentario(comentario);
          }
          this.logger.debug(SeguimientoComentariosComponent.name, `eliminarComentario(${comentario})`, 'end');
        }
      )
    );
  }

  getTipoComentario(): Observable<TipoComentario> {
    if (this.actionService.getGestion() === Gestion.GESTOR) {
      this.tipoComentario$ = this.tipoComentarioService.findById(1);
    } else {
      this.tipoComentario$ = this.tipoComentarioService.findById(2);
    }
    return this.tipoComentario$;
  }
}
