import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IApartadoFormulario } from '@core/models/eti/apartado-formulario';
import { IBloqueFormulario } from '@core/models/eti/bloque-formulario';
import { IComentario } from '@core/models/eti/comentario';
import { TipoComentario } from '@core/models/eti/tipo-comentario';
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

const MSG_ERROR_BLOQUE = marker('eti.comentario.editar.bloque.error.cargar');
const MSG_ERROR_TIPO_COMENTARIO = marker('eti.comentario.editar.tipoComentario.error');
const MSG_ERROR_APARTADO = marker('eti.comentario.editar.apartado.error.cargar');
const MSG_ERROR_SUBAPARTADO = marker('eti.comentario.editar.subapartado.error.cargar');
const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './comentario-editar-modal.component.html',
  styleUrls: ['./comentario-editar-modal.component.scss']
})
export class ComentarioEditarModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  bloquesFormulario: IBloqueFormulario[];
  apartadosFormulario: IApartadoFormulario[];
  subapartados: IApartadoFormulario[];
  suscripciones: Subscription[];
  tipoComentario: TipoComentario;

  cargaDatosInicial: boolean;

  constructor(
    private readonly logger: NGXLogger,
    private readonly bloqueFormularioService: BloqueFormularioService,
    private readonly apartadoFormularioService: ApartadoFormularioService,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ComentarioEditarModalComponent>,
    @Inject(MAT_DIALOG_DATA) public comentario: IComentario,
  ) {
    this.logger.debug(ComentarioEditarModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
    this.fxLayoutProperties.layoutAlign = 'space-around start';
    this.bloquesFormulario = [];
    this.apartadosFormulario = [];
    this.suscripciones = [];
    this.logger.debug(ComentarioEditarModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'ngOnInit()', 'start');
    this.cargaDatosInicial = true;
    this.suscripciones = [];
    this.subapartados = [];
    this.inicialFormGroup();
    this.loadAllBloques();
    this.loadApartados();
    this.loadHijos();
    this.logger.debug(ComentarioEditarModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Inicializa el formGroup
   */
  private inicialFormGroup(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'inicialFormGroup()', 'start');
    let apartado: IApartadoFormulario;
    let subapartado: IApartadoFormulario;
    if (this.comentario?.apartadoFormulario?.apartadoFormularioPadre) {
      apartado = this.comentario.apartadoFormulario.apartadoFormularioPadre;
      subapartado = this.comentario.apartadoFormulario;
    }
    else {
      apartado = this.comentario.apartadoFormulario;
      subapartado = null;
    }
    this.formGroup = new FormGroup({
      bloqueFormulario: new FormControl(this.comentario?.apartadoFormulario?.bloqueFormulario, [IsBloqueFormulario.isValid()]),
      apartadoFormulario: new FormControl(apartado, [IsApartadoFormulario.isValid()]),
      subapartado: new FormControl(subapartado, []),
      comentario: new FormControl(this.comentario.texto, [])
    });
    this.logger.debug(ComentarioEditarModalComponent.name, 'inicialFormGroup()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ComentarioEditarModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Carga todos los bloques de la aplicación
   */
  private loadAllBloques(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'loadAllBloques()', 'start');
    this.suscripciones.push(
      this.bloqueFormularioService.findAll().subscribe(
        (res: SgiRestListResult<IBloqueFormulario>) => {
          this.bloquesFormulario = res.items;
          this.logger.debug(ComentarioEditarModalComponent.name, 'loadAllBloques()', 'end');
        },
        () => {
          this.closeModalError(MSG_ERROR_BLOQUE);
          this.logger.error(ComentarioEditarModalComponent.name, 'loadAllBloques()', 'error');
        }
      )
    );
  }

  /**
   * Carga todos los apartados del bloque seleccionado
   */
  loadApartados(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'loadApartados()', 'start');
    this.initDatosApartados();
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'bloqueFormulario')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.bloqueFormularioService.getApartados(id).subscribe(
          (res: SgiRestListResult<IApartadoFormulario>) => {
            this.apartadosFormulario = res.items;
            this.logger.debug(ComentarioEditarModalComponent.name, 'loadApartados()', 'end');
          },
          () => {
            this.closeModalError(MSG_ERROR_APARTADO);
            this.logger.error(ComentarioEditarModalComponent.name, 'loadApartados()', 'error');
          }
        )
      );
    }
  }

  /**
   * Reinicia los datos de los apartados cuando se cambia de bloque
   */
  private initDatosApartados(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'initDatosApartados()', 'start');
    if (!this.cargaDatosInicial) {
      this.apartadosFormulario = [];
      this.subapartados = [];
      FormGroupUtil.setValue(this.formGroup, 'apartadoFormulario', '');
      FormGroupUtil.setValue(this.formGroup, 'subapartado', '');
    } else {
      this.cargaDatosInicial = false;
    }
    this.logger.debug(ComentarioEditarModalComponent.name, 'initDatosApartados()', 'end');
  }

  /**
   * Carga todos los subapartados del apartado seleccionado en el formulario
   */
  loadHijos(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'loadHijos()', 'start');
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'apartadoFormulario')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.apartadoFormularioService.getHijos(id).subscribe(
          (res: SgiRestListResult<IApartadoFormulario>) => {
            this.subapartados = res.items;
            this.logger.debug(ComentarioEditarModalComponent.name, 'loadHijos()', 'end');
          },
          () => {
            this.closeModalError(MSG_ERROR_SUBAPARTADO);
            this.logger.error(ComentarioEditarModalComponent.name, 'loadHijos()', 'error');
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
    this.logger.debug(ComentarioEditarModalComponent.name, `closeModalError(${texto})`, 'start');
    this.snackBarService.showError(texto);
    this.closeModal();
    this.logger.debug(ComentarioEditarModalComponent.name, `closeModalError(${texto})`, 'end');
  }

  /**
   * Cierra la ventana modal y devuelve el comentario si se ha creado
   *
   * @param comentario Comentario creado
   */
  closeModal(comentario?: IComentario): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(comentario);
    this.logger.debug(ComentarioEditarModalComponent.name, 'closeModal()', 'end');
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
  updateComentario(): void {
    this.logger.debug(ComentarioEditarModalComponent.name, 'updateComentario()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ComentarioEditarModalComponent.name, 'updateComentario()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private getDatosForm(): IComentario {
    this.logger.debug(ComentarioEditarModalComponent.name, 'getDatosForm()', 'start');
    const comentario = this.comentario;
    const subapartado = FormGroupUtil.getValue(this.formGroup, 'subapartado');
    if (subapartado) {
      comentario.apartadoFormulario = subapartado;
    } else {
      comentario.apartadoFormulario = FormGroupUtil.getValue(this.formGroup, 'apartadoFormulario');
    }
    comentario.texto = FormGroupUtil.getValue(this.formGroup, 'comentario');
    comentario.tipoComentario = this.tipoComentario;
    this.logger.debug(ComentarioEditarModalComponent.name, 'getDatosForm()', 'end');
    return comentario;
  }
}
