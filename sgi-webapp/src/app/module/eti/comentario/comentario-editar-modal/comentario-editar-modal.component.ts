import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { IComentario } from '@core/models/eti/comentario';
import { resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

const MSG_ERROR_BLOQUE = marker('eti.comentario.editar.bloque.error.cargar');
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

  bloques: IBloque[];
  apartados: IApartado[];
  subapartados: IApartado[];
  suscripciones: Subscription[];

  cargaDatosInicial: boolean;

  constructor(
    private readonly logger: NGXLogger,
    private readonly formularioService: FormularioService,
    private readonly bloqueService: BloqueService,
    private readonly apartadoService: ApartadoService,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ComentarioEditarModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IComentario,
  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
    this.fxLayoutProperties.layoutAlign = 'space-around start';
    this.bloques = [];
    this.apartados = [];
    this.suscripciones = [];
  }

  ngOnInit(): void {
    this.cargaDatosInicial = true;
    this.suscripciones = [];
    this.subapartados = [];
    this.inicialFormGroup();
    this.loadBloquesComite();
    this.loadApartados();
    this.loadHijos();
  }

  /**
   * Inicializa el formGroup
   */
  private inicialFormGroup(): void {
    let apartado: IApartado;
    let subapartado: IApartado;
    if (this.data.apartado?.padre) {
      apartado = this.data.apartado.padre;
      subapartado = this.data.apartado;
    }
    else {
      apartado = this.data.apartado;
      subapartado = null;
    }
    this.formGroup = new FormGroup({
      bloque: new FormControl(this.data.apartado?.bloque, [IsEntityValidator.isValid()]),
      apartado: new FormControl(apartado, [IsEntityValidator.isValid()]),
      subapartado: new FormControl(subapartado, []),
      comentario: new FormControl(this.data.texto, [])
    });
  }

  ngOnDestroy(): void {
    this.suscripciones?.forEach(x => x.unsubscribe());
  }

  /**
   * Carga todos los bloques de la aplicación
   */
  private loadBloquesComite(): void {
    this.suscripciones.push(
      this.formularioService.getBloques(
        resolveFormularioByTipoEvaluacionAndComite(this.data.evaluacion.tipoEvaluacion, this.data.memoria.comite)).subscribe(
          (res: SgiRestListResult<IBloque>) => {
            this.bloques = res.items;
          },
          (error) => {
            this.logger.error(error);
            this.closeModalError(MSG_ERROR_BLOQUE);
          }
        )
    );
  }

  /**
   * Carga todos los apartados del bloque seleccionado
   */
  loadApartados(): void {
    this.initDatosApartados();
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'bloque')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.bloqueService.getApartados(id).subscribe(
          (res: SgiRestListResult<IApartado>) => {
            this.apartados = res.items;
          },
          (error) => {
            this.logger.error(error);
            this.closeModalError(MSG_ERROR_APARTADO);
          }
        )
      );
    }
  }

  /**
   * Reinicia los datos de los apartados cuando se cambia de bloque
   */
  private initDatosApartados(): void {
    if (!this.cargaDatosInicial) {
      this.apartados = [];
      this.subapartados = [];
      FormGroupUtil.setValue(this.formGroup, 'apartado', '');
      FormGroupUtil.setValue(this.formGroup, 'subapartado', '');
    } else {
      this.cargaDatosInicial = false;
    }
  }

  /**
   * Carga todos los subapartados del apartado seleccionado en el formulario
   */
  loadHijos(): void {
    const id = Number(FormGroupUtil.getValue(this.formGroup, 'apartado')?.id);
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.apartadoService.getHijos(id).subscribe(
          (res: SgiRestListResult<IApartado>) => {
            this.subapartados = res.items;
          },
          (error) => {
            this.logger.error(error);
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
    this.snackBarService.showError(texto);
    this.closeModal();
  }

  /**
   * Cierra la ventana modal y devuelve el comentario si se ha creado
   *
   * @param comentario Comentario creado
   */
  closeModal(comentario?: IComentario): void {
    this.matDialogRef.close(comentario);
  }

  getNombreBloque(bloque: IBloque): string {
    return bloque?.nombre;
  }

  getNombreApartado(apartado: IApartado): string {
    return apartado?.nombre;
  }

  /**
   * Comprueba el formulario y envia el comentario resultante
   */
  updateComentario(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private getDatosForm(): IComentario {
    const comentario = this.data;
    const subapartado = FormGroupUtil.getValue(this.formGroup, 'subapartado');
    if (subapartado) {
      comentario.apartado = subapartado;
    } else {
      comentario.apartado = FormGroupUtil.getValue(this.formGroup, 'apartado');
    }
    comentario.texto = FormGroupUtil.getValue(this.formGroup, 'comentario');
    return comentario;
  }
}
