import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ApartadoFormulario } from '@core/models/eti/apartado-formulario';
import { BloqueFormulario } from '@core/models/eti/bloque-formulario';
import { Comentario } from '@core/models/eti/comentario';
import { TipoComentario } from '@core/models/eti/tipo-comentario';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ApartadoFormularioService } from '@core/services/eti/apartado-formulario.service';
import { BloqueFormularioService } from '@core/services/eti/bloque-formulario.service';
import { TipoComentarioService } from '@core/services/eti/tipo-comentario.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const MSG_ERROR_BLOQUE = marker('eti.comentario.crear.bloque.error.cargar');
const MSG_ERROR_TIPO_COMENTARIO = marker('eti.comentario.crear.tipoComentario.error');
const MSG_ERROR_APARTADO = marker('eti.comentario.crear.apartado.error.cargar');
const MSG_ERROR_SUBAPARTADO = marker('eti.comentario.crear.subapartado.error.cargar');
const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './comentario-crear-modal.component.html',
  styleUrls: ['./comentario-crear-modal.component.scss']
})
export class ComentarioCrearModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  bloquesFormulario: BloqueFormulario[];
  apartadosFormulario: ApartadoFormulario[];
  subapartados: ApartadoFormulario[];
  suscripciones: Subscription[];
  tipoComentario: TipoComentario;

  constructor(
    private readonly logger: NGXLogger,
    private readonly bloqueFormularioService: BloqueFormularioService,
    private readonly apartadoFormularioService: ApartadoFormularioService,
    private readonly snackBarService: SnackBarService,
    private readonly tipoComentarioService: TipoComentarioService,
    public readonly matDialogRef: MatDialogRef<ComentarioCrearModalComponent>
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
    this.subapartados = [];
    this.formGroup = new FormGroup({
      bloqueFormulario: new FormControl('', [Validators.required]),
      apartadoFormulario: new FormControl('', [Validators.required]),
      subapartado: new FormControl('', []),
      comentario: new FormControl('', [Validators.required])
    });
    this.loadAllBloques();
    this.loadAllTipoComentario();
    this.logger.debug(ComentarioCrearModalComponent.name, 'ngOnInit()', 'end');
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
        (res: SgiRestListResult<BloqueFormulario>) => {
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
   * Carga todos los tipos de comentarios existentes para luego seleccionar el de tipo 'EVALUADOR'
   */
  private loadAllTipoComentario() {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cargarTiposComentarios()', 'start');
    this.suscripciones.push(
      this.tipoComentarioService.findAll().subscribe(
        (res: SgiRestListResult<TipoComentario>) => {
          res.items.forEach(tipoComentario => {
            if (tipoComentario.nombre.toUpperCase() === 'EVALUADOR') {
              this.tipoComentario = tipoComentario;
            }
          });
          this.logger.debug(ComentarioCrearModalComponent.name, 'cargarTiposComentarios()', 'end');
        },
        () => {
          this.logger.error(ComentarioCrearModalComponent.name, 'cargarTiposComentarios()', 'error');
          this.closeModalError(MSG_ERROR_TIPO_COMENTARIO);
        }
      )
    );
  }

  /**
   * Carga todos los apartados del bloque seleccionado
   */
  loadApartados(): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cargarApartadosBloque()', 'start');
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'bloqueFormulario')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.bloqueFormularioService.getApartados(id).subscribe(
          (res: SgiRestListResult<ApartadoFormulario>) => {
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
          (res: SgiRestListResult<ApartadoFormulario>) => {
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
  closeModal(comentario?: Comentario): void {
    this.logger.debug(ComentarioCrearModalComponent.name, 'cerrarModal()', 'start');
    this.matDialogRef.close(comentario);
    this.logger.debug(ComentarioCrearModalComponent.name, 'cerrarModal()', 'end');
  }

  getNombreBloqueFormulario(bloqueFormulario: BloqueFormulario): string {
    return bloqueFormulario?.nombre;
  }

  getNombreApartadoFormulario(apartadoFormulario: ApartadoFormulario): string {
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
  private getDatosForm(): Comentario {
    this.logger.debug(ComentarioCrearModalComponent.name, 'getDatosForm()', 'start');
    const comentario = new Comentario();
    const subapartado = FormGroupUtil.getValue(this.formGroup, 'subapartado');
    if (subapartado) {
      comentario.apartadoFormulario = subapartado;
    } else {
      comentario.apartadoFormulario = FormGroupUtil.getValue(this.formGroup, 'apartadoFormulario');
    }
    comentario.texto = FormGroupUtil.getValue(this.formGroup, 'comentario');
    comentario.tipoComentario = this.tipoComentario;
    this.logger.debug(ComentarioCrearModalComponent.name, 'getDatosForm()', 'end');
    return comentario;
  }
}
