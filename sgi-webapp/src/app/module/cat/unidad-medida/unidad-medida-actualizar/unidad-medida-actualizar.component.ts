import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { SnackBarService } from '@core/services/snack-bar.service';
import { UnidadMedidaService } from '@core/services/cat/unidad-medida.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

const MSG_ERROR_NOT_FOUND = marker('cat.unidad-medida.actualizar.no-encontrado');
const MSG_ERROR_FORM = marker('form-group.error');
const MSG_SUCCESS = marker('cat.unidad-medida.actualizar.correcto');
const MSG_ERROR = marker('cat.unidad-medida.actualizar.error');

@Component({
  selector: 'sgi-unidad-medida-actualizar',
  templateUrl: './unidad-medida-actualizar.component.html',
  styleUrls: ['./unidad-medida-actualizar.component.scss'],
})
export class UnidadMedidaActualizarComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  unidadMedida: UnidadMedida;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  unidadMedidaServiceGetSubscription: Subscription;
  unidadMedidaServiceUpdateSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly unidadMedidaService: UnidadMedidaService,
    private readonly snackBarService: SnackBarService
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
      UnidadMedidaActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      abreviatura: new FormControl('', [
        Validators.maxLength(10),
        Validators.minLength(2),
      ]),
      descripcion: new FormControl('', [Validators.maxLength(100)]),
    });
    this.getUnidadMedida(); // TODO usar mejor un resolver
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnDestroy()',
      'start'
    );
    this.unidadMedidaServiceGetSubscription?.unsubscribe();
    this.unidadMedidaServiceUpdateSubscription?.unsubscribe();
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnDestroy()',
      'end'
    );
  }

  /**
   * Obtiene los datos de la unidad de medida a actualizar si existe
   */
  getUnidadMedida(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'getUnidadMedida()',
      'start'
    );
    this.unidadMedida = new UnidadMedida();
    const id = this.route.snapshot.params.id;
    if (id && !isNaN(id)) {
      this.unidadMedidaServiceGetSubscription = this.unidadMedidaService
        .findById(Number(id))
        .subscribe(
          (unidadMedida: UnidadMedida) => {
            this.unidadMedida = unidadMedida;
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
            this.logger.debug(
              UnidadMedidaActualizarComponent.name,
              'getUnidadMedida()',
              'start'
            );
          },
          () => {
            this.snackBarService.showError(MSG_ERROR_NOT_FOUND);
            this.router.navigate(['../'], { relativeTo: this.route });
            this.logger.debug(
              UnidadMedidaActualizarComponent.name,
              'getUnidadMedida()',
              'end'
            );
          }
        );
    }
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  enviarForm(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'enviarForm()',
      'start'
    );
    if (FormGroupUtil.valid(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'enviarForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para crear o actualizar una unidad de medida
   */
  private enviarApi(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'enviarApi()',
      'start'
    );
    this.desactivarAceptar = true;
    this.getDatosForm();
    this.unidadMedidaServiceUpdateSubscription = this.unidadMedidaService
      .update(this.unidadMedida.id, this.unidadMedida)
      .subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(
            UnidadMedidaActualizarComponent.name,
            'enviarApi()',
            'end'
          );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR);
          this.desactivarAceptar = false;
          this.logger.debug(
            UnidadMedidaActualizarComponent.name,
            'enviarApi()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'getDatosForm()',
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
      UnidadMedidaActualizarComponent.name,
      'getDatosForm()',
      'end'
    );
  }
}
