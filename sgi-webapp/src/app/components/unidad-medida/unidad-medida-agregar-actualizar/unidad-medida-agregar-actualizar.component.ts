import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormGroupUtil } from '@shared/config/form-group-util';
import { UnidadMedida } from '@core/models/unidad-medida';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { UnidadMedidaService } from '@core/services/unidad-medida.service';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { UrlUtils } from '@core/utils/url-utils';
import { SnackBarService } from '@core/services/snack-bar.service';

@Component({
  selector: 'app-unidad-medida-agregar-actualizar',
  templateUrl: './unidad-medida-agregar-actualizar.component.html',
  styleUrls: ['./unidad-medida-agregar-actualizar.component.scss'],
})
export class UnidadMedidaAgregarActualizarComponent implements OnInit {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  unidadMedida: UnidadMedida;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    private readonly logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
    private readonly router: Router,
    private readonly unidadMedidaService: UnidadMedidaService,
    private readonly traductor: TraductorService,
    private snackBarService: SnackBarService
  ) {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.unidadMedida = new UnidadMedida();
    this.formGroup = new FormGroup({
      abreviatura: new FormControl(this.unidadMedida.abreviatura, [
        Validators.required,
        Validators.maxLength(10),
        Validators.minLength(2),
      ]),
      descripcion: new FormControl(this.unidadMedida.descripcion, [
        Validators.maxLength(100),
      ]),
    });
    this.getUnidadMedida();
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  /**
   * Obtiene los datos de la unidad de medida a actualizar si existe
   */
  getUnidadMedida(): void {
    // Obtiene los parámetros de la url
    this.activatedRoute.params.subscribe((params: Params) => {
      // Combrueba que exista el parámetro id
      if (params.id) {
        const id = Number(params.id);
        // Obtiene los datos del back
        this.unidadMedidaService.getOne(id).subscribe(
          (unidadMedida: UnidadMedida) => {
            this.unidadMedida = unidadMedida;
            // Actualiza el formGroup
            FormGroupUtil.setValue(
              this.formGroup,
              'abreviatura',
              this.unidadMedida.abreviatura
            );
            FormGroupUtil.setValue(
              this.formGroup,
              'descripcion',
              this.unidadMedida.descripcion
            );
          },
          () => {
            this.snackBarService
              .mostrarMensajeSuccess(this.traductor.getTexto('unidad-medida.actualizar.no-encontrado'));
            this.router.navigateByUrl(UrlUtils.unidadMedida).then();
          }
        );
      }
    });
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  sendForm(): void {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendForm()',
      'start'
    );
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.sendApi();
    } else {
      this.snackBarService
        .mostrarMensajeError(this.traductor.getTexto('form-group.error'));
    }
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para agregar o actualizar una unidad de medida
   */
  private sendApi(): void {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendApi()',
      'start'
    );
    this.createData();
    this.desactivarAceptar = true;

    // Si no tiene id, lo creamos
    if (this.unidadMedida.id === null) {
      this.agregarUnidadMedida();
    }
    // Si tiene id, lo actualizamos
    else {
      this.actualizarUnidadMedida();
    }
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendApi()',
      'end'
    );
  }

  /**
   * Crea una nueva unidad de medidad en el back
   */
  private agregarUnidadMedida() {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'agregarUnidadMedida()',
      'start'
    );
    this.unidadMedidaService.create(this.unidadMedida).subscribe(
      () => {
        this.snackBarService.mostrarMensajeSuccess(this.traductor.getTexto('unidad-medida.agregar.correcto'));
        this.router.navigateByUrl(UrlUtils.unidadMedida).then();
        this.logger.debug(
          UnidadMedidaAgregarActualizarComponent.name,
          'agregarUnidadMedida()',
          'end'
        );
      },
      () => {
        this.snackBarService.mostrarMensajeError(this.traductor.getTexto('unidad-medida.agregar.error'));
        this.desactivarAceptar = false;
        this.logger.debug(
          UnidadMedidaAgregarActualizarComponent.name,
          'agregarUnidadMedida()',
          'end'
        );
      }
    );
  }

  /**
   * Actualiza una unidad de medida existente en el back
   */
  private actualizarUnidadMedida() {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'actualizarUnidadMedida()',
      'start'
    );
    this.unidadMedidaService
      .update(this.unidadMedida, this.unidadMedida.id)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(this.traductor.getTexto('unidad-medida.actualizar.correcto'));
          this.router.navigateByUrl(UrlUtils.unidadMedida).then();
          this.logger.debug(
            UnidadMedidaAgregarActualizarComponent.name,
            'actualizarUnidadMedida()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(this.traductor.getTexto('unidad-medida.actualizar.error'));
          this.desactivarAceptar = false;
          this.logger.debug(
            UnidadMedidaAgregarActualizarComponent.name,
            'actualizarUnidadMedida()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private createData(): void {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'createData()',
      'start'
    );
    this.unidadMedida.abreviatura = FormGroupUtil.getValue(
      this.formGroup,
      'abreviatura'
    );
    this.unidadMedida.descripcion = FormGroupUtil.getValue(
      this.formGroup,
      'descripcion'
    );
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'createData()',
      'end'
    );
  }
}
