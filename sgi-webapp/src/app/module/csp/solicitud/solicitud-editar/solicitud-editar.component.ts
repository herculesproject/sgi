import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { TipoFormularioSolicitud } from '@core/enums/tipo-formulario-solicitud';
import { TipoEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { NGXLogger } from 'ngx-logger';
import { CambioEstadoModalComponent, SolicitudCambioEstadoModalComponentData } from '../modals/cambio-estado-modal/cambio-estado-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../solicitud-route-names';
import { SolicitudActionService } from '../solicitud.action.service';

const MSG_BUTTON_EDIT = marker('botones.guardar');
const MSG_SUCCESS = marker('csp.solicitud.editar.correcto');
const MSG_ERROR = marker('csp.solicitud.editar.error');


// CAMBIO DE ESTADO
const MSG_CAMBIO_ESTADO_SUCCESS = marker('csp.solicitud.cambio-estado.correcto');
const MSG_CAMBIO_ESTADO_ERROR = marker('csp.solicitud.cambio-estado.error');

const MSG_BUTTON_ADMITIR_DEFINITIVAMENTE = marker('csp.solicitud.admitir-definitivamente');
const MSG_BUTTON_ADMITIR_PROVISIONALMENTE = marker('csp.solicitud.admitir-provisionalmente');
const MSG_BUTTON_ALEGAR_ADMISION = marker('csp.solicitud.alegar-admision');
const MSG_BUTTON_ALEGAR_CONCESION = marker('csp.solicitud.alegar-concesion');
const MSG_BUTTON_CONCEDER = marker('csp.solicitud.conceder');
const MSG_BUTTON_CONCEDER_PROVISIONALMENTE = marker('csp.solicitud.conceder-provisionalmente');
const MSG_BUTTON_DENEGAR = marker('csp.solicitud.denegar');
const MSG_BUTTON_DENEGAR_PROVISIONALMENTE = marker('csp.solicitud.denegar-provisionalmente');
const MSG_BUTTON_DESISTIR = marker('csp.solicitud.desistir');
const MSG_BUTTON_EXCLUIR = marker('csp.solicitud.excluir');
const MSG_BUTTON_EXCLUIR_PROVISIONALMENTE = marker('csp.solicitud.excluir-provisionalmente');
const MSG_BUTTON_PRESENTAR = marker('csp.solicitud.presentar');

@Component({
  selector: 'sgi-solicitud-editar',
  templateUrl: './solicitud-editar.component.html',
  styleUrls: ['./solicitud-editar.component.scss'],
  viewProviders: [
    SolicitudActionService
  ]
})
export class SolicitudEditarComponent extends ActionComponent implements OnInit {
  SOLICITUD_ROUTE_NAMES = SOLICITUD_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_EDIT;

  textoAdmitirDefinitivamente = MSG_BUTTON_ADMITIR_DEFINITIVAMENTE;
  textoAdmitirProvisionalmente = MSG_BUTTON_ADMITIR_PROVISIONALMENTE;
  textoAlegarAdmision = MSG_BUTTON_ALEGAR_ADMISION;
  textoAlegarConcesion = MSG_BUTTON_ALEGAR_CONCESION;
  textoConceder = MSG_BUTTON_CONCEDER;
  textoConcederProvisionalmente = MSG_BUTTON_CONCEDER_PROVISIONALMENTE;
  textoDenegar = MSG_BUTTON_DENEGAR;
  textoDenegarProvisionalmente = MSG_BUTTON_DENEGAR_PROVISIONALMENTE;
  textoDesistir = MSG_BUTTON_DESISTIR;
  textoExcluir = MSG_BUTTON_EXCLUIR;
  textoExcluirProvisionalmente = MSG_BUTTON_EXCLUIR_PROVISIONALMENTE;
  textoPresentar = MSG_BUTTON_PRESENTAR;


  tipoEstandar = false;

  disableCambioEstado = false;
  isPresentable: Boolean = false;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudActionService,
    dialogService: DialogService,
    private matDialog: MatDialog) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.isTipoEstandar();

    this.subscriptions.push(this.actionService.isPresentable$.subscribe(
      isPresentable => {
        this.isPresentable = isPresentable;
      }
    ));

    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableCambioEstado = status.changes || status.errors;
      }
    ));
  }


  /**
   * Comprueba que el tipo de la solcitud sea 'Estándar'
   */
  isTipoEstandar(): void {
    const formularioSolicitud = this.actionService.getDatosGeneralesSolicitud().formularioSolicitud;
    this.tipoEstandar = formularioSolicitud === TipoFormularioSolicitud.ESTANDAR;
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }



  hasEstadoCambio(...estadosSolicitud: TipoEstadoSolicitud[]): boolean {


    let estadoCorrecto: boolean = false;

    estadosSolicitud.forEach((estadoSolicitud) => {
      if (this.actionService.getDatosGeneralesSolicitud()?.estado?.estado === estadoSolicitud) {
        estadoCorrecto = true;
        return;
      }

    });

    return estadoCorrecto;

  }

  public get TipoEstadoSolicitud() {
    return TipoEstadoSolicitud;
  }



  cambioEstado(accion: string) {
    switch (accion) {
      case MSG_BUTTON_PRESENTAR: {
        this.actionService.presentar().subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case MSG_BUTTON_ADMITIR_PROVISIONALMENTE: {
        this.actionService.admitirProvisionalmente().subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case MSG_BUTTON_ADMITIR_DEFINITIVAMENTE: {
        this.actionService.admitirDefinitivamente().subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case MSG_BUTTON_CONCEDER_PROVISIONALMENTE:
        {
          this.actionService.concederProvisionalmente().subscribe(
            () => { },
            (error) => {
              this.logger.error(error);
              this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
            },
            () => {
              this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
              this.router.navigate(['../'], { relativeTo: this.activatedRoute });
            }
          );
          break;
        }
      case MSG_BUTTON_CONCEDER: {
        this.actionService.conceder().subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case MSG_BUTTON_EXCLUIR_PROVISIONALMENTE: {
        this.openModal(TipoEstadoSolicitud.EXCLUIDA_PROVISIONAL);
        break;
      }
      case MSG_BUTTON_ALEGAR_ADMISION: {
        this.openModal(TipoEstadoSolicitud.ALEGADA_ADMISION);
        break;
      }
      case MSG_BUTTON_EXCLUIR: {
        this.openModal(TipoEstadoSolicitud.EXCLUIDA);
        break;
      }
      case MSG_BUTTON_DENEGAR_PROVISIONALMENTE: {
        this.openModal(TipoEstadoSolicitud.DENEGADA_PROVISIONAL);
        break;
      }
      case MSG_BUTTON_ALEGAR_CONCESION: {
        this.openModal(TipoEstadoSolicitud.ALEGADA_CONCESION);
        break;
      }
      case MSG_BUTTON_DENEGAR: {
        this.openModal(TipoEstadoSolicitud.DENEGADA);
        break;
      }
      case MSG_BUTTON_DESISTIR: {
        this.openModal(TipoEstadoSolicitud.DESISTIDA);
        break;
      }
      default: {
        this.logger.error("Cambio de estado de solicitud no soportado.");
        break;
      }
    }
  }


  /**
  * Apertura de modal cambio de estado para insertar comentario
  */
  openModal(estadoNuevo: TipoEstadoSolicitud): void {
    const data: SolicitudCambioEstadoModalComponentData = {
      estadoActual: this.actionService.getDatosGeneralesSolicitud()?.estado?.estado,
      estadoNuevo: estadoNuevo,
      comentario: null,
    };
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(CambioEstadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (comentario) => {
        if (comentario) {
          this.cambioEstadoComentario(comentario, estadoNuevo);
        }

      }
    );
  }

  /**
   * Invoca los cambios de estado que requieren comentario.
   * @param comentario  comentario de cambio de estado.
   * @param estadoNuevo Nuevo estado de la solicitud.
   */
  cambioEstadoComentario(comentario: string, estadoNuevo: TipoEstadoSolicitud) {
    switch (estadoNuevo) {
      case TipoEstadoSolicitud.EXCLUIDA_PROVISIONAL: {
        this.actionService.excluirProvisionalmente(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case TipoEstadoSolicitud.ALEGADA_ADMISION: {
        this.actionService.alegarAdmision(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case TipoEstadoSolicitud.EXCLUIDA: {
        this.actionService.excluir(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case TipoEstadoSolicitud.DENEGADA_PROVISIONAL: {
        this.actionService.denegarProvisionalmente(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case TipoEstadoSolicitud.ALEGADA_CONCESION: {
        this.actionService.alegarConcesion(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }
      case TipoEstadoSolicitud.DENEGADA: {
        this.actionService.denegar(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }

      case TipoEstadoSolicitud.DESISTIDA: {
        this.actionService.desistir(comentario).subscribe(
          () => { },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
          },
          () => {
            this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
            this.router.navigate(['../'], { relativeTo: this.activatedRoute });
          }
        );
        break;
      }

      default: {
        this.logger.error("Cambio de estado de solicitud no soportado.");
        break;
      }
    }

  }
}
