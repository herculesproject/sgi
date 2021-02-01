import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { IComentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ActionService } from '@core/services/action-service';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { EvaluacionFormularioActionService } from '../../evaluacion-formulario/evaluacion-formulario.action.service';

const MSG_ERROR_BLOQUE = marker('eti.comentario.crear.bloque.error.cargar');
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

  bloques: IBloque[];
  apartados: IApartado[];
  subapartados: IApartado[];
  suscripciones: Subscription[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly formularioService: FormularioService,
    private readonly bloqueService: BloqueService,
    private readonly apartadoService: ApartadoService,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ComentarioCrearModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IEvaluacion,
  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
    this.fxLayoutProperties.layoutAlign = 'space-around start';
    this.bloques = [];
    this.apartados = [];
    this.suscripciones = [];
  }

  ngOnInit(): void {
    this.suscripciones = [];
    this.formGroup = new FormGroup({
      bloque: new FormControl('', [IsEntityValidator.isValid()]),
      apartado: new FormControl('', [IsEntityValidator.isValid()]),
      subapartado: new FormControl('', []),
      comentario: new FormControl('', [])
    });
    this.initDatosApartados();
    this.loadBloquesComite();
  }

  /**
   * Reinicia los datos de los apartados cuando se cambia de bloque
   */
  private initDatosApartados(): void {
    this.apartados = [];
    this.subapartados = [];
    FormGroupUtil.setValue(this.formGroup, 'apartado', '');
    FormGroupUtil.setValue(this.formGroup, 'subapartado', '');
  }

  ngOnDestroy(): void {
    this.suscripciones?.forEach(x => x.unsubscribe());
  }

  /**
   * Carga todos los bloques de la aplicación
   */
  private loadBloquesComite(): void {
    this.suscripciones.push(
      this.formularioService.getBloques(resolveFormularioByTipoEvaluacionAndComite(this.data.tipoEvaluacion, this.data.memoria.comite)).
        subscribe(
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
  saveComentario() {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): IComentario {
    const comentario = {} as IComentario;
    const subapartado: IApartado = FormGroupUtil.getValue(this.formGroup, 'subapartado');
    if (subapartado) {
      comentario.apartado = subapartado;
    } else {
      comentario.apartado = FormGroupUtil.getValue(this.formGroup, 'apartado');
    }
    comentario.texto = FormGroupUtil.getValue(this.formGroup, 'comentario');
    comentario.memoria = this.data.memoria;
    return comentario;
  }
}
