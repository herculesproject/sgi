import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IComentario } from '@core/models/eti/comentario';
import { TipoComentario } from '@core/models/eti/tipo-comentario';
import { DialogService } from '@core/services/dialog.service';
import { TipoComentarioService } from '@core/services/eti/tipo-comentario.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Observable, Subscription } from 'rxjs';
import { ComentarioCrearModalComponent } from '../../comentario/comentario-crear-modal/comentario-crear-modal.component';
import { ComentarioEditarModalComponent } from '../../comentario/comentario-editar-modal/comentario-editar-modal.component';
import { Gestion, SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoComentarioFragment } from './seguimiento-comentarios.fragment';


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
    private readonly dialogService: DialogService,
    private tipoComentarioService: TipoComentarioService,
    private matDialog: MatDialog,
    private actionService: SeguimientoFormularioActionService
  ) {
    super(actionService.FRAGMENT.COMENTARIOS, actionService);
    this.dataSource = new MatTableDataSource<StatusWrapper<IComentario>>();
    this.formPart = this.fragment as SeguimientoComentarioFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['apartado.bloque', 'apartado.padre',
      'apartado', 'texto', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(this.formPart.comentarios$.subscribe(elements => {
      this.dataSource.data = elements;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getApartadoNombre(comentario: IComentario): string {
    const nombre = comentario.apartado?.padre ?
      comentario.apartado?.padre?.nombre : comentario.apartado?.nombre;
    return nombre;
  }

  getSubApartadoNombre(comentario: IComentario): string {
    const nombre = comentario.apartado?.padre ? comentario.apartado?.nombre : '';
    return nombre;
  }

  /**
   * Abre la ventana modal para añadir un comentario
   */
  openCreateModal(): void {
    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: this.actionService.getEvaluacion(),
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(ComentarioCrearModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (comentario: IComentario) => {
        if (comentario) {
          this.formPart.addComentario(comentario);
        }
      }
    );
  }

  /**
   * Abre la ventana modal para modificar un comentario
   *
   * @param comentario Comentario a modificar
   */
  openEditModal(comentario: StatusWrapper<IComentario>): void {
    const wrapperRef = comentario;

    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: wrapperRef.value,
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
      }
    );
  }

  /**
   * Elimina un comentario del listado
   *
   * @param comentario Comentario a eliminar
   */
  deleteComentario(comentario: StatusWrapper<IComentario>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteComentario(comentario);
          }
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
