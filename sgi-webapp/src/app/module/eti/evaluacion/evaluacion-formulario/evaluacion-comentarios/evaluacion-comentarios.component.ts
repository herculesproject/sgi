import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';
import { Comentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { DialogService } from '@core/services/dialog.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';

import { ComentarioCrearModalComponent } from '../../../comentario/comentario-crear-modal/comentario-crear-modal.component';
import {
  ComentarioEditarModalComponent,
} from '../../../comentario/comentario-editar-modal/comentario-editar-modal.component';

const MSG_DELETE = marker('eti.comentario.listado.borrar.titulo');

@Component({
  selector: 'sgi-evaluacion-comentarios',
  templateUrl: './evaluacion-comentarios.component.html',
  styleUrls: ['./evaluacion-comentarios.component.scss']
})
export class EvaluacionComentariosComponent extends AbstractTabComponent<Comentario[]> implements OnInit, OnDestroy {
  comentarios: Comentario[];
  comentariosEliminados: number[];
  evaluacion: IEvaluacion;
  suscripcionesComentarios: Subscription[] = [];
  evaluacionId: number;

  columnas: string[];
  dataSource: MatTableDataSource<Comentario>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  elementosPagina: number[];

  constructor(
    protected readonly logger: NGXLogger,
    private readonly evaluacionService: EvaluacionService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly dialogService: DialogService,
    public matDialog: MatDialog
  ) {
    super(logger);
    this.logger.debug(EvaluacionComentariosComponent.name, 'constructor()', 'start');
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['apartadoFormulario.bloqueFormulario', 'apartadoFormulario.apartadoFormularioPadre',
      'apartadoFormulario', 'texto', 'acciones'];
    this.logger.debug(EvaluacionComentariosComponent.name, 'constructor()', 'end');
  }

  ngOnInit() {
    super.ngOnInit();
    this.logger.debug(EvaluacionComentariosComponent.name, 'ngOnInit()', 'start');
    this.suscripcionesComentarios = [];
    this.comentarios = [];
    this.comentariosEliminados = [];
    this.evaluacionId = this.activatedRoute.snapshot.params.id;
    if (this.evaluacionId && !isNaN(this.evaluacionId)) {
      this.suscripcionesComentarios.push(
        this.evaluacionService.getComentarios(this.evaluacionId).subscribe(
          (res: SgiRestListResult<Comentario>) => {
            if (res) {
              this.comentarios = res.items ? res.items : [];
              this.datosIniciales = [];
              this.comentarios.forEach(x => this.datosIniciales.push(new Comentario(x)));
              this.initTabla();
            }
            this.logger.debug(EvaluacionComentariosComponent.name, 'ngOnInit()', 'end');
          }
        )
      );
    }
  }

  ngOnDestroy() {
    this.logger.debug(EvaluacionComentariosComponent.name, 'ngOnDestroy()', 'start');
    super.ngOnDestroy();
    this.suscripcionesComentarios?.forEach(x => x?.unsubscribe());
    this.logger.debug(EvaluacionComentariosComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Introduce los comentarios en la tabla
   */
  initTabla(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'initTabla()', 'start');
    this.dataSource = new MatTableDataSource<Comentario>(this.comentarios);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.logger.debug(ComentarioCrearModalComponent.name, 'initTabla()', 'end');
  }

  createFormGroup(): FormGroup {
    this.logger.debug(EvaluacionComentariosComponent.name, 'crearFormGroup()', 'start');
    this.logger.debug(EvaluacionComentariosComponent.name, 'crearFormGroup()', 'end');
    return null;
  }

  getDatosFormulario(): Comentario[] {
    this.logger.debug(EvaluacionComentariosComponent.name, 'getDatosFormulario()', 'start');
    this.logger.debug(EvaluacionComentariosComponent.name, 'getDatosFormulario()', 'end');
    return this.comentarios;
  }

  /**
   * Guarda los nuevos comentario en el servidor
   */
  createComentariosObservable(): Observable<Comentario[]> {
    this.logger.debug(EvaluacionComentariosComponent.name, 'crearComentarios()', 'start');
    const nuevosComentarios = this.comentarios.filter(x => !x.id);
    const observable = this.evaluacionService.createComentarios(this.evaluacionId, nuevosComentarios);
    this.logger.debug(EvaluacionComentariosComponent.name, 'crearComentarios()', 'end');
    return observable;
  }

  /**
   * Actualiza los comentarios en el servidor
   */
  editComentariosObservable(): Observable<Comentario[]> {
    this.logger.debug(EvaluacionComentariosComponent.name, 'actualizarComentarios()', 'start');
    const actualizarComentarios = this.comentarios.filter(x => x.id);
    const observable = this.evaluacionService.updateComentarios(this.evaluacionId, actualizarComentarios);
    this.logger.debug(EvaluacionComentariosComponent.name, 'actualizarComentarios()', 'end');
    return observable;
  }

  /**
   * Borra los comentarios en el servidor
   */
  deleteComentariosObservable(): Observable<void> {
    this.logger.debug(EvaluacionComentariosComponent.name, 'borrarComentarios()', 'start');
    const observable = this.evaluacionService.deleteComentarios(this.evaluacionId, this.comentariosEliminados);
    this.logger.debug(EvaluacionComentariosComponent.name, 'borrarComentarios()', 'end');
    return observable;
  }

  getApartadoFormularioNombre(comentario: Comentario): string {
    return comentario.apartadoFormulario.apartadoFormularioPadre ?
      comentario.apartadoFormulario.apartadoFormularioPadre.nombre : comentario.apartadoFormulario.nombre;
  }

  getSubApartadoFormularioNombre(comentario: Comentario): string {
    return comentario.apartadoFormulario.apartadoFormularioPadre ? comentario.apartadoFormulario.nombre : '';
  }

  /**
   * Abre la ventana modal para añadir un comentario
   */
  openCreateModal(): void {
    this.logger.debug(EvaluacionComentariosComponent.name, 'abrirModalCrear()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal
    };
    const dialogRef = this.matDialog.open(ComentarioCrearModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (comentario: Comentario) => {
        if (comentario) {
          comentario.evaluacion = this.evaluacion;
          this.comentarios.push(comentario);
          this.initTabla();
        }
        this.logger.debug(EvaluacionComentariosComponent.name, 'abrirModalCrear()', 'end');
      }
    );
  }

  /**
   * Abre la ventana modal para modificar un comentario
   *
   * @param comentario Comentario a modificar
   */
  openEditModal(comentario: Comentario): void {
    this.logger.debug(EvaluacionComentariosComponent.name, 'openEditModal()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: comentario
    };
    const dialogRef = this.matDialog.open(ComentarioEditarModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (resultado: Comentario) => {
        if (resultado) {
          const index = this.comentarios.indexOf(comentario, 0);
          if (index >= 0) {
            this.comentarios.splice(index, 1);
          }
          this.comentarios.push(resultado);
          this.initTabla();
        }
        this.logger.debug(EvaluacionComentariosComponent.name, 'openEditModal()', 'end');
      }
    );
  }

  /**
   * Actualiza un comentario existente en el listado
   *
   * @param comentario Comentario
   */
  editComentario(comentario: Comentario): void {
    this.logger.debug(EvaluacionComentariosComponent.name, `editComentario(${comentario})`, 'start');
    for (let aux of this.comentarios) {
      if (aux.id === comentario.id) {
        aux = comentario;
        break;
      }
    }
    this.logger.debug(EvaluacionComentariosComponent.name, `editComentario(${comentario})`, 'end');
  }

  /**
   * Elimina un comentario del listado
   *
   * @param comentario Comentario a eliminar
   */
  deleteComentario(comentario: Comentario) {
    this.logger.debug(EvaluacionComentariosComponent.name, `eliminarComentario(${comentario})`, 'start');
    this.suscripcionesComentarios.push(
      this.dialogService.showConfirmation(MSG_DELETE).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            const index = this.comentarios.indexOf(comentario, 0);
            if (index > -1) {
              if (comentario.id) {
                this.comentariosEliminados.push(comentario.id);
              }
              this.comentarios.splice(index, 1);
            }
            this.initTabla();
          }
          this.logger.debug(EvaluacionComentariosComponent.name, `eliminarComentario(${comentario})`, 'end');
        }
      )
    );
  }
}
