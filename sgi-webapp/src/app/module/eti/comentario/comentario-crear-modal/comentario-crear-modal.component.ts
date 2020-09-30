import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IApartadoFormulario } from '@core/models/eti/apartado-formulario';
import { IBloqueFormulario } from '@core/models/eti/bloque-formulario';
import { IComentario } from '@core/models/eti/comentario';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ApartadoFormularioService } from '@core/services/eti/apartado-formulario.service';
import { BloqueFormularioService } from '@core/services/eti/bloque-formulario.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IsApartadoFormulario } from '@core/validators/is-apartado-formulario-validador';
import { IsBloqueFormulario } from '@core/validators/is-bloque-formulario-validador';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { EvaluacionFormularioActionService } from '../../evaluacion-formulario/evaluacion-formulario.action.service';
import { ActionService } from '@core/services/action-service';

const MSG_ERROR_BLOQUE = marker('eti.comentario.crear.bloque.error.cargar');
const MSG_ERROR_TIPO_COMENTARIO = marker('eti.comentario.crear.tipoComentario.error');
const MSG_ERROR_APARTADO = marker('eti.comentario.crear.apartado.error.cargar');
const MSG_ERROR_SUBAPARTADO = marker('eti.comentario.crear.subapartado.error.cargar');
const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './comentario-crear-modal.component.html',
  styleUrls: ['./comentario-crear-modal.component.scss'],
  providers: [
    {
      provide: ActionService,
      useExisting: EvaluacionFormularioActionService
    }
  ]
})
export class ComentarioCrearModalComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  bloquesFormulario: IBloqueFormulario[];
  apartadosFormulario: IApartadoFormulario[];
  subapartados: IApartadoFormulario[];
  suscripciones: Subscription[];
  private subscriptions: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private readonly bloqueFormularioService: BloqueFormularioService,
    private readonly apartadoFormularioService: ApartadoFormularioService,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ComentarioCrearModalComponent>,

  ) {
    this.logger.debug(ComentarioCrearModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
    this.fxLayoutProperties.layoutAlign = 'space-around start';
    this.bloquesFormulario = [];
    this.apartadosFormulario = [];
    this.suscripciones = [];
    this.logger.debug(ComentarioCrearModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'ngOnInit()', 'start');
    this.suscripciones = [];
    this.formGroup = new FormGroup({
      bloqueFormulario: new FormControl('', [IsBloqueFormulario.isValid()]),
      apartadoFormulario: new FormControl('', [IsApartadoFormulario.isValid()]),
      subapartado: new FormControl('', []),
      comentario: new FormControl('', [])
    });
    this.initDatosApartados();
    this.loadAllBloques();
    this.logger.debug(ComentarioCrearModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Reinicia los datos de los apartados cuando se cambia de bloque
   */
  private initDatosApartados(): void {
    this.apartadosFormulario = [];
    this.subapartados = [];
    FormGroupUtil.setValue(this.formGroup, 'apartadoFormulario', '');
    FormGroupUtil.setValue(this.formGroup, 'subapartado', '');
  }

  ngOnDestroy(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ComentarioCrearModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Carga todos los bloques de la aplicación
   */
  private loadAllBloques(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cargarBloques()', 'start');
    this.suscripciones.push(
      this.bloqueFormularioService.findAll().subscribe(
        (res: SgiRestListResult<IBloqueFormulario>) => {
          this.bloquesFormulario = res.items;
          this.logger.debug(ComentarioCrearModalComponent.name, 'cargarBloques()', 'end');
        },
        () => {
          this.closeModalError(MSG_ERROR_BLOQUE);
          this.logger.error(ComentarioCrearModalComponent.name, 'cargarBloques()', 'error');
        }
      )
    );
  }

  /**
   * Carga todos los apartados del bloque seleccionado
   */
  loadApartados(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cargarApartadosBloque()', 'start');
    this.initDatosApartados();
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'bloqueFormulario')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.bloqueFormularioService.getApartados(id).subscribe(
          (res: SgiRestListResult<IApartadoFormulario>) => {
            this.apartadosFormulario = res.items;
            this.logger.debug(ComentarioCrearModalComponent.name, 'cargarApartadosBloque()', 'end');
          },
          () => {
            this.logger.error(ComentarioCrearModalComponent.name, 'cargarApartadosBloque()', 'error');
            this.closeModalError(MSG_ERROR_APARTADO);
          }
        )
      );
    }
  }

  /**
   * Carga todos los subapartados del apartado seleccionado en el formulario
   */
  loadHijos(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cargarSubapartados()', 'start');
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'apartadoFormulario')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.apartadoFormularioService.getHijos(id).subscribe(
          (res: SgiRestListResult<IApartadoFormulario>) => {
            this.subapartados = res.items;
            this.logger.debug(ComentarioCrearModalComponent.name, 'cargarSubapartados()', 'end');
          },
          () => {
            this.logger.error(ComentarioCrearModalComponent.name, 'cargarSubapartados()', 'error');
            this.closeModalError(MSG_ERROR_SUBAPARTADO);
          }
        )
      );
    }
  }

  /**
   * Muestra un mensaje de error y cierra la ventana modal
   *
   * @param texto Texto de error a mostrar
   */
  private closeModalError(texto: string): void {
    this.logger.debug(ComentarioCrearModalComponent.name, `cerrarModalError(${texto})`, 'start');
    this.snackBarService.showError(texto);
    this.closeModal();
    this.logger.debug(ComentarioCrearModalComponent.name, `cerrarModalError(${texto})`, 'end');
  }

  /**
   * Cierra la ventana modal y devuelve el comentario si se ha creado
   *
   * @param comentario Comentario creado
   */
  closeModal(comentario?: IComentario): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cerrarModal()', 'start');
    this.matDialogRef.close(comentario);
    this.logger.debug(ComentarioCrearModalComponent.name, 'cerrarModal()', 'end');
  }

  getNombreBloqueFormulario(bloqueFormulario: IBloqueFormulario): string {
    return bloqueFormulario?.nombre;
  }

  getNombreApartadoFormulario(apartadoFormulario: IApartadoFormulario): string {
    return apartadoFormulario?.nombre;
  }

  /**
   * Comprueba el formulario y envia el comentario resultante
   */
  saveComentario() {
    this.logger.debug(ComentarioCrearModalComponent.name, 'guardar()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ComentarioCrearModalComponent.name, 'guardar()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): IComentario {
    this.logger.debug(ComentarioCrearModalComponent.name, 'getDatosForm()', 'start');
    const comentario = {} as IComentario;
    const subapartado: IApartadoFormulario = FormGroupUtil.getValue(this.formGroup, 'subapartado');
    if (subapartado) {
      comentario.apartadoFormulario = subapartado;
    } else {
      comentario.apartadoFormulario = FormGroupUtil.getValue(this.formGroup, 'apartadoFormulario');
    }
    comentario.texto = FormGroupUtil.getValue(this.formGroup, 'comentario');
    this.logger.debug(ComentarioCrearModalComponent.name, 'getDatosForm()', 'end');
    return comentario;
  }
}
